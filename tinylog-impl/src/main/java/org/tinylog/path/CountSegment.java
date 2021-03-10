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

import org.tinylog.runtime.Timestamp;

/**
 * Path segment that represents a sequential sequence of numbers.
 */
final class CountSegment implements Segment {

	/** */
	CountSegment() {
	}

	@Override
	public String getStaticText() {
		return null;
	}

	@Override
	public String createToken(final String prefix, final Timestamp timestamp) {
		int separator = Math.max(prefix.lastIndexOf(File.separatorChar), prefix.lastIndexOf('/'));

		File directory;
		String filePrefix;
		if (separator == -1) {
			directory = new File("").getAbsoluteFile();
			filePrefix = prefix;
		} else {
			directory = new File(prefix.substring(0, separator));
			filePrefix = separator == prefix.length() - 1 ? "" : prefix.substring(separator + 1);
		}

		long count = 0;
		if (directory.isDirectory()) {
			String[] entries = directory.list();
			if (entries != null) {
				for (String entry : entries) {
					if (entry.startsWith(filePrefix)) {
						Long foundCount = parseDigits(entry, filePrefix.length());
						if (foundCount != null && foundCount + 1 > count) {
							count = foundCount + 1;
						}
					}
				}
			}
		}

		return Long.toString(count);
	}

	@Override
	public boolean validateToken(final String token) {
		try {
			Long.parseLong(token);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * Parses digits from a defined position in a text. Following non-numeric characters will be ignored.
	 *
	 * @param text
	 *            Text that potentially contains digits
	 * @param start
	 *            Position in text from which digits should be parsed
	 * @return Parsed digits or {@code null} if there is no valid number at the defined position
	 */
	private static Long parseDigits(final String text, final int start) {
		for (int i = start; i < text.length(); ++i) {
			char character = text.charAt(i);
			if (character < '0' || character > '9') {
				return parseLong(text.substring(start, i));
			}
		}

		return parseLong(text.substring(start));
	}

	/**
	 * Converts a text into a number.
	 *
	 * @param text
	 *            Text that potentially represents a number
	 * @return Parsed number or {@code null} if the passed text cannot be parsed as a number
	 */
	private static Long parseLong(final String text) {
		if (text.length() == 0) {
			return null;
		} else {
			try {
				return Long.parseLong(text);
			} catch (NumberFormatException ex) {
				return null;
			}
		}
	}

}
