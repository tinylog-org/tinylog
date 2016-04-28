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

package org.pmw.tinylog.writers;

import java.util.EnumSet;
import java.util.Set;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.InternalLogger;
import org.pmw.tinylog.LogEntry;

import android.util.Log;

/**
 * Writes log entries via Android's logcat.
 */
@PropertiesSupport(name = "logcat", properties = { @Property(name = "tag", type = String.class, optional = true) })
public final class LogcatWriter implements Writer {

	private static final int TAG_MAX_LENGTH = 23;

	private final String tag;

	/**
	 * Class name without package part will be used as tag (automatically generated for each log entry).
	 */
	public LogcatWriter() {
		this.tag = null;
	}

	/**
	 * @param tag
	 *            Static tag for logcat
	 */
	public LogcatWriter(final String tag) {
		this.tag = trim(tag);
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		if (tag == null) {
			return EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY);
		} else {
			return EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.RENDERED_LOG_ENTRY);
		}
	}

	@Override
	public void init(final Configuration configuration) {
		try {
			Class.forName("android.util.Log");
		} catch (ClassNotFoundException ex) {
			InternalLogger.error("Logcat writer works only on Android");
		}
	}

	@Override
	public void write(final LogEntry logEntry) {
		String identifier = tag == null ? trim(getNameOfClass(logEntry.getClassName())) : tag;

		switch (logEntry.getLevel()) {
			case TRACE:
				Log.v(identifier, logEntry.getRenderedLogEntry());
				break;

			case DEBUG:
				Log.d(identifier, logEntry.getRenderedLogEntry());
				break;

			case INFO:
				Log.i(identifier, logEntry.getRenderedLogEntry());
				break;

			case WARNING:
				Log.w(identifier, logEntry.getRenderedLogEntry());
				break;

			case ERROR:
				Log.e(identifier, logEntry.getRenderedLogEntry());
				break;

			default:
				InternalLogger.warn("Unexpected logging level: {}", logEntry.getLevel());
				break;
		}
	}

	@Override
	public void flush() {
		// Do nothing
	}

	@Override
	public void close() {
		// Do nothing
	}

	private static String getNameOfClass(final String fullyQualifiedClassName) {
		int dotIndex = fullyQualifiedClassName.lastIndexOf('.');
		if (dotIndex < 0) {
			return fullyQualifiedClassName;
		} else {
			return fullyQualifiedClassName.substring(dotIndex + 1);
		}
	}

	private static String trim(final String tag) {
		if (tag.length() > TAG_MAX_LENGTH) {
			return tag.substring(0, TAG_MAX_LENGTH - 3) + "...";
		} else {
			return tag;
		}
	}

}
