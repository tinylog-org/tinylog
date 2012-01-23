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

package org.pmw.tinylog;

import java.io.PrintStream;

/**
 * Writes log entries to the console.
 */
public class ConsoleLoggingWriter implements ILoggingWriter {

	/** 
	 */
	public ConsoleLoggingWriter() {
		super();
	}

	@Override
	public final void write(final ELoggingLevel level, final String logEntry) {
		getPrintStream(level).print(logEntry);
	}

	private static PrintStream getPrintStream(final ELoggingLevel level) {
		if (level == ELoggingLevel.ERROR || level == ELoggingLevel.WARNING) {
			return System.err;
		} else {
			return System.out;
		}
	}

}
