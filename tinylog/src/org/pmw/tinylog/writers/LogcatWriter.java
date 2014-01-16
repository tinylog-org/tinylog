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

package org.pmw.tinylog.writers;

import java.util.EnumSet;
import java.util.Set;

import org.pmw.tinylog.EnvironmentHelper;

import android.util.Log;

/**
 * Forward log entries to Android's logcat. Class name of log entry will be used as tag.
 */
@PropertiesSupport(name = "logcat", properties = { })
public final class LogcatWriter implements LoggingWriter {

	private static final String NEW_LINE = EnvironmentHelper.getNewLine();

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY);
	}

	@Override
	public void init() {
		// Do nothing
	}

	@Override
	public void write(final LogEntry logEntry) {
		String tag = shorten(logEntry.getClassName());
		String message = removeLastLineBreak(logEntry.getRenderedLogEntry());

		switch (logEntry.getLevel()) {
			case ERROR:
				Log.e(tag, message);
				break;
			case WARNING:
				Log.w(tag, message);
				break;
			case INFO:
				Log.i(tag, message);
				break;
			case DEBUG:
				Log.d(tag, message);
				break;
			case TRACE:
				Log.v(tag, message);
				break;
			default:
				throw new IllegalArgumentException(logEntry.getLevel().toString());
		}
	}

	private String shorten(final String className) {
		int index = className.lastIndexOf('.');
		return index >= 0 ? className.substring(index + 1) : className;
	}

	private String removeLastLineBreak(final String message) {
		if (message.endsWith(NEW_LINE)) {
			return message.substring(0, message.length() - NEW_LINE.length());
		} else {
			return message;
		}
	}

}
