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

import org.pmw.tinylog.LoggingLevel;

import android.util.Log;

/**
 * Forward log entries to Android's logcat.
 */
@PropertiesSupport(name = "logcat", properties = { @Property(name = "tag", type = String.class) })
public final class LogcatWriter implements LoggingWriter {

	private final String tag;

	/**
	 * @param tag
	 *            String to identify the application
	 */
	public LogcatWriter(final String tag) {
		this.tag = tag;
	}

	/**
	 * Get the tag to identify the application.
	 * 
	 * @return Tag to identify the application
	 */
	public String getTag() {
		return tag;
	}

	@Override
	public void init() {
		// Do nothing
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) {
		switch (level) {
			case ERROR:
				Log.e(tag, logEntry);
				break;
			case WARNING:
				Log.w(tag, logEntry);
				break;
			case INFO:
				Log.i(tag, logEntry);
				break;
			case DEBUG:
				Log.d(tag, logEntry);
				break;
			case TRACE:
				Log.v(tag, logEntry);
				break;
			default:
				// Do nothing
				break;
		}
	}

}
