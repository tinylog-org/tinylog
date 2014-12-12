/*
 * Copyright 2014 Martin Winandy
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

package org.pmw.tinylog.policies;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pmw.tinylog.Configuration;

/**
 * Policy for limiting the number of log entries per file.
 */
@PropertiesSupport(name = "count")
public final class CountPolicy implements Policy {

	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\r\n|\\\\r\\\\n|\n|\\\\n|\r|\\\\r");

	private final long limit;
	private long lines;
	private long count;

	/**
	 * @param limit
	 *            Maximum number of log entries per file (must be &gt; 0)
	 * @throws IllegalArgumentException
	 *             if limit is &lt;= 0
	 */
	public CountPolicy(final long limit) throws IllegalArgumentException {
		if (limit <= 0L) {
			throw new IllegalArgumentException("limit must be > 0");
		}

		this.limit = limit;
		this.count = 0L;
	}

	/**
	 * String parameter for {@link org.pmw.tinylog.PropertiesLoader PropertiesLoader}.
	 *
	 * @param limit
	 *            Maximum number of log entries per file (must be > 0)
	 * @throws IllegalArgumentException
	 *             if limit is <= 0
	 */
	CountPolicy(final String limit) throws IllegalArgumentException {
		try {
			this.limit = Long.parseLong(limit);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Size \"" + limit + "\" is not numberic");
		}
		if (this.limit <= 0L) {
			throw new IllegalArgumentException("Size must be > 0, but is " + this.limit);
		}
		this.count = 0L;
	}

	@Override
	public void init(final Configuration configuration) {
		String formatPattern = configuration.getFormatPattern();
		Matcher matcher = NEW_LINE_PATTERN.matcher(formatPattern);

		lines = 1L;
		while (matcher.find()) {
			++lines;
		}
	}

	@Override
	public boolean check(final File logFile) throws IOException {
		if (logFile.exists()) {
			LineNumberReader reader = new LineNumberReader(new FileReader(logFile));
			try {
				reader.skip(Long.MAX_VALUE);
				count = reader.getLineNumber() / lines;
				return count <= limit;
			} finally {
				reader.close();
			}
		} else {
			return true;
		}
	}

	@Override
	public boolean check(final String logEntry) {
		++count;
		return count <= limit;
	}

	@Override
	public void reset() {
		count = 0L;
	}

}
