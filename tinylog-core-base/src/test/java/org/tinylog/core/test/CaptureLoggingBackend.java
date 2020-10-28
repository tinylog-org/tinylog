/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.test;

import org.tinylog.core.Level;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Logging backend for storing all issued log entries in a passed {@link Log}.
 */
class CaptureLoggingBackend implements LoggingBackend {

	private final Log log;

	/**
	 * @param log All issued log entries will be stored in this {@link Log}
	 */
	CaptureLoggingBackend(Log log) {
		this.log = log;
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		String output = arguments == null ? String.valueOf(message) : formatter.format(message.toString(), arguments);
		log.add(new LogEntry(location.getCallerClassName(), tag, level, throwable, output));
	}

}
