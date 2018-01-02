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

package org.tinylog.policies;

import java.io.File;
import java.util.Locale;

/**
 * Policy for triggering a rollover when a log file reaches a defined maximum file size.
 */
public final class SizePolicy implements Policy {

	private static final long KB = 1024L;
	private static final long MB = KB * 1024L;
	private static final long GB = MB * 1024L;

	private final long maximum;
	private long count;

	/**
	 * @param argument
	 *            Maximum size for log file (e.g. "16MB")
	 */
	public SizePolicy(final String argument) {
		if (argument == null || argument.isEmpty()) {
			throw new IllegalArgumentException("No maximum size defined for size policy");
		} else {
			try {
				maximum = parse(argument.toLowerCase(Locale.ROOT));
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid size \"" + argument + "\" for size policy");
			}
			if (maximum <= 0) {
				throw new IllegalArgumentException("Invalid size \"" + argument + "\" for size policy");
			}
		}
	}

	@Override
	public boolean continueExistingFile(final String path) {
		File file = new File(path);
		count = file.length();
		return count <= maximum;
	}

	@Override
	public boolean continueCurrentFile(final byte[] entry) {
		count += entry.length;
		return count <= maximum;
	}

	@Override
	public void reset() {
		count = 0;
	}

	/**
	 * Parses file size from a string. The units GB, MB, KB and bytes are supported.
	 * 
	 * @param argument
	 *            Lower case file size
	 * @return Parsed file size
	 * @throws NumberFormatException
	 *             Failed to parse file size
	 */
	private static long parse(final String argument) throws NumberFormatException {
		if (argument.endsWith("gb")) {
			return Long.parseLong(argument.substring(0, argument.length() - "gb".length()).trim()) * GB;
		} else if (argument.endsWith("mb")) {
			return Long.parseLong(argument.substring(0, argument.length() - "mb".length()).trim()) * MB;
		} else if (argument.endsWith("kb")) {
			return Long.parseLong(argument.substring(0, argument.length() - "kb".length()).trim()) * KB;
		} else if (argument.endsWith("bytes")) {
			return Long.parseLong(argument.substring(0, argument.length() - "bytes".length()).trim());
		} else {
			return Long.parseLong(argument.trim());
		}
	}

}
