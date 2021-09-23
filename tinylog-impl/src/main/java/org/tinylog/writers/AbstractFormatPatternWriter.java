/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.writers;

import java.util.Collection;
import java.util.Map;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.pattern.FormatPatternParser;
import org.tinylog.pattern.Token;

/**
 * Base writer for outputting rendered log entries. The format pattern will be read from property {@code format}.
 */
public abstract class AbstractFormatPatternWriter extends AbstractFileBasedWriter {

	private static final String DEFAULT_FORMAT_PATTERN = "{date} [{thread}] {class}.{method}()\n{level}: {message}";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final int BUILDER_CAPACITY = 1024;

	private final StringBuilder builder;
	private final Token token;

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public AbstractFormatPatternWriter(final Map<String, String> properties) {
		super(properties);

		String pattern = getStringValue("format");
		if (pattern == null) {
			pattern = DEFAULT_FORMAT_PATTERN;
		}

		token = new FormatPatternParser(getStringValue("exception")).parse(pattern + NEW_LINE);
		builder = getBooleanValue("writingthread") ? new StringBuilder(BUILDER_CAPACITY) : null;
	}

	/**
	 * Gets all log entry values that are required for rendering a log entry by the defined format pattern. If a child
	 * writer requires additional log entries, this method has to be overridden.
	 *
	 * @return Required log entry values
	 */
	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return token.getRequiredLogEntryValues();
	}

	/**
	 * Renders a log entry as string.
	 *
	 * @param logEntry
	 *            Log entry to render
	 * @return Rendered log entry
	 */
	protected final String render(final LogEntry logEntry) {
		if (builder == null) {
			StringBuilder builder = new StringBuilder(BUILDER_CAPACITY);
			token.render(logEntry, builder);
			return builder.toString();
		} else {
			builder.setLength(0);
			token.render(logEntry, builder);
			return builder.toString();
		}
	}

}
