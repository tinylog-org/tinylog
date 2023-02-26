/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.adapter.jul;

import java.util.logging.Logger;

import org.tinylog.Level;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.ContextProvider;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.NopContextProvider;
import org.tinylog.runtime.RuntimeProvider;

/**
 * Logging provider that forwards all log entries to {@code java.util.logging}.
 */
public final class JavaUtilLoggingProvider implements LoggingProvider {

	private ContextProvider contextProvider;

	/** */
	public JavaUtilLoggingProvider() {
		contextProvider = new NopContextProvider();
	}

	@Override
	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	@Override
	public Level getMinimumLevel() {
		return Level.TRACE;
	}

	@Override
	public Level getMinimumLevel(final String tag) {
		return Level.TRACE;
	}

	@Override
	public boolean isEnabled(final int depth, final String tag, final Level level) {
		return isLoggable(RuntimeProvider.getCallerClassName(depth + 1), level);
	}

	@Override
	public boolean isEnabled(final String loggerClassName, final String tag, final Level level) {
		return isLoggable(RuntimeProvider.getCallerClassName(loggerClassName), level);
	}

	@Override
	public void log(final int depth, final String tag, final Level level, final Throwable exception, final MessageFormatter formatter,
		final Object obj, final Object... arguments) {
		StackTraceElement caller = RuntimeProvider.getCallerStackTraceElement(depth + 1);
		Logger julLogger = Logger.getLogger(caller.getClassName());
		java.util.logging.Level julLevel = translate(level);

		if (julLogger.isLoggable(julLevel)) {
			String message = String.valueOf(obj);
			if (arguments != null && arguments.length > 0) {
				message = formatter.format(message, arguments);
			}
			julLogger.logp(julLevel, caller.getClassName(), caller.getMethodName(), message, exception);
		}
	}

	@Override
	public void log(final String loggerClassName, final String tag, final Level level, final Throwable exception,
		final MessageFormatter formatter, final Object obj, final Object... arguments) {
		StackTraceElement caller = RuntimeProvider.getCallerStackTraceElement(loggerClassName);
		Logger julLogger = Logger.getLogger(caller.getClassName());
		java.util.logging.Level julLevel = translate(level);

		if (julLogger.isLoggable(julLevel)) {
			String message = String.valueOf(obj);
			if (arguments != null && arguments.length > 0) {
				message = formatter.format(message, arguments);
			}
			julLogger.logp(julLevel, caller.getClassName(), caller.getMethodName(), message, exception);
		}
	}

	@Override
	public void shutdown() {
		// Should be ignored
	}

	/**
	 * Translates a tinylog severity level into a {@code java.util.logging} level.
	 *
	 * @param level
	 *            Severity level of tinylog
	 * @return Corresponding level of {@code java.util.logging}
	 */
	private static java.util.logging.Level translate(final Level level) {
		switch (level) {
			case TRACE:
				return java.util.logging.Level.FINER;
			case DEBUG:
				return java.util.logging.Level.FINE;
			case INFO:
				return java.util.logging.Level.INFO;
			case WARN:
				return java.util.logging.Level.WARNING;
			case ERROR:
				return java.util.logging.Level.SEVERE;
			default:
				return java.util.logging.Level.OFF;
		}
	}

	private static boolean isLoggable(final String callerClassName, final Level level) {
		return Logger.getLogger(callerClassName).isLoggable(translate(level));
	}
}
