/*
 * Copyright 2018 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.path;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.runtime.Timestamp;

/**
 * A dynamic path represents a path with patterns that can be resolved as a real path to a file at runtime.
 */
public final class DynamicPath {

	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd_HH-mm-ss";

	private final List<Segment> segments;
	private final File folder;
	private final List<String> plainTexts;
	private final String suffix;

	/**
	 * @param path
	 *            Path with patterns
	 * @throws IllegalArgumentException
	 *             Path contains invalid or incomplete patterns
	 */
	public DynamicPath(final String path) {
		String normalizedPath = path.replace('/', File.separatorChar);

		segments = new ArrayList<Segment>();
		plainTexts = new ArrayList<String>();

		String prefix = "";
		int start = 0;
		boolean token = false;

		for (int i = 0; i < normalizedPath.length(); ++i) {
			char character = normalizedPath.charAt(i);

			if (character == '{') {
				if (token) {
					throw new IllegalArgumentException("Closing curly bracket is missing: '" + path + "'");
				} else if (i > 0 && normalizedPath.charAt(i - 1) == '}') {
					throw new IllegalArgumentException("Two patterns must be separated by at least one character: '" + path + "'");
				} else {
					if (i > start) {
						if (prefix.isEmpty() && segments.isEmpty()) {
							prefix = normalizedPath.substring(0, i);
						}

						String text = normalizedPath.substring(start, i);
						segments.add(new PlainTextSegment(text));
						plainTexts.add(text);
					}

					start = i + 1;
					token = true;
				}
			} else if (character == '}') {
				if (token) {
					segments.add(parseSegment(normalizedPath, normalizedPath.substring(start, i)));
					start = i + 1;
					token = false;
				} else {
					throw new IllegalArgumentException("Opening curly bracket is missing: '" + path + "'");
				}
			}
		}

		if (token) {
			throw new IllegalArgumentException("Closing curly bracket is missing: '" + path + "'");
		} else if (start < normalizedPath.length() - 1) {
			if (prefix.isEmpty() && segments.isEmpty()) {
				prefix = normalizedPath;
			}

			String text = normalizedPath.substring(start);
			int separator = Math.max(text.lastIndexOf(File.separatorChar), text.lastIndexOf('/'));
			segments.add(new PlainTextSegment(text));
			plainTexts.add(text);
			suffix = separator == -1 ? text : text.substring(separator + 1);
		} else {
			suffix = "";
		}

		int separator = Math.max(prefix.lastIndexOf(File.separatorChar), prefix.lastIndexOf('/'));
		folder = new File(separator == -1 ? "" : prefix.substring(0, separator)).getAbsoluteFile();
	}

	/**
	 * Resolves all patterns and generates a real path for a (new) file.
	 *
	 * @return Generated path
	 */
	public String resolve() {
		Timestamp timestamp = RuntimeProvider.createTimestamp();
		StringBuilder builder = new StringBuilder();
		for (Segment segment : segments) {
			builder.append(segment.createToken(builder.toString(), timestamp));
		}
		return builder.toString();
	}

	/**
	 * Gets all original files and backup files that are compatible with the dynamic path. The returned file tuples are
	 * sorted by the last modification date. The most recently modified file tuples are at the top, the oldest at the
	 * bottom of the list.
	 *
	 * @param backupSuffix
	 *            Optional file extension for backup files (can be {@code null} if there are no separate backup files)
	 * @return Found file tuples
	 */
	public List<FileTuple> getAllFiles(final String backupSuffix) {
		List<FileTuple> tuples = new ArrayList<FileTuple>();

		Collection<File> originals = backupSuffix == null ? new ArrayList<File>() : new HashSet<File>();
		collectFiles(folder, suffix, originals);

		if (backupSuffix != null) {
			Collection<File> backups = new ArrayList<File>();
			collectFiles(folder, suffix + backupSuffix, backups);

			for (File backup : backups) {
				String path = backup.getAbsolutePath();
				File original = new File(path.substring(0, path.length() - backupSuffix.length()));
				tuples.add(new FileTuple(original, backup));
				originals.remove(original);
			}
		}

		for (File original : originals) {
			String path = original.getAbsolutePath();
			File backup = new File(backupSuffix == null ? path : path + backupSuffix);
			tuples.add(new FileTuple(original, backup));
		}

		Collections.sort(tuples, LastModifiedFileTupleComparator.INSTANCE);
		return tuples;
	}

	/**
	 * Checks if an already existing file is compatible with this dynamic path.
	 *
	 * @param file
	 *            File to check
	 * @return {@code true} if passed file is compatible, {@code false} if not
	 */
	public boolean isValid(final File file) {
		return isValid(file.getAbsolutePath(), 0, 0);
	}

	/**
	 * Collects files from a folder and all nested sub folders.
	 *
	 * @param folder
	 *            Base folder for starting search
	 * @param suffix
	 *            Expected suffix for file paths
	 * @param found
	 *            All found files will be added to this list
	 */
	private void collectFiles(final File folder, final String suffix, final Collection<File> found) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					collectFiles(file, suffix, found);
				} else if (file.isFile() && file.getAbsolutePath().endsWith(suffix)) {
					int index = 0;

					for (String text : plainTexts) {
						index = file.getAbsolutePath().indexOf(text, index);
						if (index == -1) {
							break;
						} else {
							index += text.length();
						}
					}

					if (index >= 0) {
						found.add(file.getAbsoluteFile());
					}
				}
			}
		}
	}

	/**
	 * Checks if a partial path to a file is compatible with this dynamic path.
	 *
	 * @param path
	 *            Full path to file
	 * @param pathPosition
	 *            Start position for checking passed path
	 * @param segmentIndex
	 *            Index of segment that will be used for check
	 * @return {@code true} if passed path is compatible, {@code false} if not
	 */
	private boolean isValid(final String path, final int pathPosition, final int segmentIndex) {
		Segment segment = segments.get(segmentIndex);
		String expectedValue = segment.getStaticText();

		if (pathPosition == 0) {
			File file = new File(expectedValue == null ? "" : expectedValue).getAbsoluteFile();
			if (file.isDirectory()) {
				expectedValue = file.getAbsolutePath() + File.separator;
			} else {
				expectedValue = file.getAbsolutePath();
			}
		}

		if (expectedValue == null) {
			if (segmentIndex == segments.size() - 1) {
				return segment.validateToken(path.substring(pathPosition));
			} else {
				String nextValue = segments.get(segmentIndex + 1).getStaticText();
				for (int i = path.indexOf(nextValue, pathPosition); i >= 0; i = path.indexOf(nextValue, i + 1)) {
					if (segment.validateToken(path.substring(pathPosition, i)) && isValid(path, i, segmentIndex + 1)) {
						return true;
					}
				}
				return false;
			}
		} else if (path.startsWith(expectedValue, pathPosition)) {
			if (segmentIndex == segments.size() - 1) {
				return pathPosition + expectedValue.length() == path.length();
			} else {
				return isValid(path, pathPosition + expectedValue.length(), segmentIndex + 1);
			}
		} else {
			return false;
		}
	}

	/**
	 * Parses a token from a pattern as a segment.
	 *
	 * @param path
	 *            Full path with patterns
	 * @param token
	 *            Token from a pattern
	 * @return Created segment that represents the passed token
	 * @throws IllegalArgumentException
	 *             Token is unknown or contains an unexpected parameter
	 */
	private static Segment parseSegment(final String path, final String token) {
		int separator = token.indexOf(':');

		String name;
		String parameter;

		if (separator == -1) {
			name = token.trim();
			parameter = null;
		} else {
			name = token.substring(0, separator).trim();
			parameter = token.substring(separator + 1).trim();
		}

		if ("date".equals(name)) {
			return new DateSegment(parameter == null ? DEFAULT_DATE_FORMAT_PATTERN : parameter);
		} else if ("count".equals(name) && parameter == null) {
			return new CountSegment();
		} else if ("pid".equals(name) && parameter == null) {
			return new ProcessIdSegment();
		} else {
			throw new IllegalArgumentException("Invalid token '" + token + "' in '" + path + "'");
		}
	}

}
