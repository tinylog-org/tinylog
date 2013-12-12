/*
 * Copyright 2012 Martin Winandy
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

import java.io.PrintStream;

import org.pmw.tinylog.LoggingLevel;

/**
 * Writes log entries to the console.
 */
@PropertiesSupport(name = "console", properties = { })
public final class ConsoleWriter implements LoggingWriter {

	@Override
	public void init() {
		// Do nothing
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) {
		getPrintStream(level).print(logEntry);
	}

	private static PrintStream getPrintStream(final LoggingLevel level) {
		if (level == LoggingLevel.ERROR || level == LoggingLevel.WARNING) {
			return System.err;
		} else {
			return System.out;
		}
	}

}
