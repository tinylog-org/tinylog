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

package org.apache.log4j;

import org.pmw.tinylog.LogEntryForwarder;
import org.pmw.tinylog.LoggingLevel;

public class Logger extends Category {

	private static final Logger INSTANCE = new Logger();

	private Logger() {
	}

	static public Logger getLogger(final String name) {
		return INSTANCE;
	}

	static public Logger getLogger(final Class<?> clazz) {
		return INSTANCE;
	}

	public static Logger getRootLogger() {
		return INSTANCE;
	}

	public void trace(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(1, LoggingLevel.TRACE, message.toString());
		}
	}

	public void trace(final Object message, final Throwable ex) {
		if (message != null) {
			LogEntryForwarder.forward(1, LoggingLevel.TRACE, ex, message.toString());
		}
	}

	public boolean isTraceEnabled() {
		return org.pmw.tinylog.Logger.getLoggingLevel().ordinal() <= LoggingLevel.TRACE.ordinal();
	}

}
