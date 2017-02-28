/*
 * Copyright 2017 Martin Winandy
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

import android.util.Log;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.pattern.FormatPatternParser;
import org.tinylog.pattern.Token;
import org.tinylog.provider.InternalLogger;

/**
 * Writer for redirecting log entries to Android's logcat.
 */
public final class LogcatWriter implements Writer {

	private static final String DEFAULT_TAG_FORMAT_PATTERN = "{className}";
	private static final String DEFAULT_MESSAGE_FORMAT_PATTERN = "{message}";
	private static final String ELLIPSIS = "...";

	private static final int TAG_MAX_LENGTH = 23;
	private static final int MESSAGE_BUILDER_CAPACITY = 1024;

	private final StringBuilder tagBuilder;
	private final Token tagToken;

	private final StringBuilder messageBuilder;
	private final Token messageToken;

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public LogcatWriter(final Map<String, String> properties) {
		boolean hasWritingThread = Boolean.parseBoolean(properties.get("writingthread"));

		String tagPattern = properties.get("tag");
		if (tagPattern == null) {
			tagPattern = DEFAULT_TAG_FORMAT_PATTERN;
		}

		tagToken = FormatPatternParser.parse(tagPattern);
		tagBuilder = hasWritingThread ? new StringBuilder(TAG_MAX_LENGTH) : null;

		String messagePattern = properties.get("format");
		if (messagePattern == null) {
			messagePattern = DEFAULT_MESSAGE_FORMAT_PATTERN;
		}

		messageToken = FormatPatternParser.parse(messagePattern);
		messageBuilder = hasWritingThread ? new StringBuilder(MESSAGE_BUILDER_CAPACITY) : null;
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		Collection<LogEntryValue> values = EnumSet.of(LogEntryValue.LEVEL);
		values.addAll(tagToken.getRequiredLogEntryValues());
		values.addAll(messageToken.getRequiredLogEntryValues());
		return values;
	}

	@Override
	public void write(final LogEntry logEntry) {
		String tag = renderTag(logEntry);
		String message = renderMessage(logEntry);

		switch (logEntry.getLevel()) {
			case TRACE:
				Log.v(tag, message);
				break;

			case DEBUG:
				Log.d(tag, message);
				break;

			case INFO:
				Log.i(tag, message);
				break;

			case WARNING:
				Log.w(tag, message);
				break;

			case ERROR:
				Log.e(tag, message);
				break;

			default:
				InternalLogger.log(Level.ERROR, "Unexpected logging level: " + logEntry.getLevel());
				break;
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

	/**
	 * Renders the tag from a log entry.
	 *
	 * @param logEntry
	 *            Log entry for rendering tag
	 * @return Rendered tag
	 */
	protected String renderTag(final LogEntry logEntry) {
		StringBuilder builder = reuseOrCreate(tagBuilder, TAG_MAX_LENGTH);
		tagToken.render(logEntry, builder);

		if (builder.length() > TAG_MAX_LENGTH) {
			return builder.substring(0, TAG_MAX_LENGTH - ELLIPSIS.length()) + ELLIPSIS;
		} else {
			return builder.toString();
		}
	}

	/**
	 * Renders the log message from a log entry.
	 *
	 * @param logEntry
	 *            Log entry for rendering log message
	 * @return Rendered log message
	 */
	protected String renderMessage(final LogEntry logEntry) {
		StringBuilder builder = reuseOrCreate(messageBuilder, MESSAGE_BUILDER_CAPACITY);
		messageToken.render(logEntry, builder);
		return builder.toString();
	}

	/**
	 * Clears an existing string builder or creates a new one if given string builder is {@code null}.
	 *
	 * @param builder
	 *            String builder instance or {@code null}
	 * @param capacity
	 *            Initial capacity for new string builder if created
	 * @return Empty string builder
	 */
	private static StringBuilder reuseOrCreate(final StringBuilder builder, final int capacity) {
		if (builder == null) {
			return new StringBuilder(capacity);
		} else {
			builder.setLength(0);
			return builder;
		}
	}

}
