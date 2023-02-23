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

package org.tinylog.adapter.jboss;

import org.jboss.logging.Logger;
import org.tinylog.Level;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.ContextProvider;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.runtime.RuntimeProvider;

/**
 * Logging provider that forwards all log entries to JBoss Logging.
 */
public final class JBossLoggingProvider implements LoggingProvider {

	private ContextProvider contextProvider;

	/** */
	public JBossLoggingProvider() {
		contextProvider = new JBossContextProvider();
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
		String callerClassName = RuntimeProvider.getCallerClassName(depth + 1);
		Logger jbossLogger = Logger.getLogger(callerClassName);
		org.jboss.logging.Logger.Level jbossLevel = translate(level);

		if (jbossLogger.isEnabled(jbossLevel)) {
			Object message = arguments == null || arguments.length == 0 ? obj : formatter.format(String.valueOf(obj), arguments);
			String loggerClassName = RuntimeProvider.getCallerClassName(depth);
			jbossLogger.log(jbossLevel, loggerClassName, message, exception);
		}
	}

	@Override
	public void log(final String loggerClassName, final String tag, final Level level, final Throwable exception,
		final MessageFormatter formatter, final Object obj, final Object... arguments) {
		String callerClassName = RuntimeProvider.getCallerClassName(loggerClassName);
		Logger jbossLogger = Logger.getLogger(callerClassName);
		org.jboss.logging.Logger.Level jbossLevel = translate(level);

		if (jbossLogger.isEnabled(jbossLevel)) {
			Object message = arguments == null || arguments.length == 0 ? obj : formatter.format(String.valueOf(obj), arguments);
			jbossLogger.log(jbossLevel, loggerClassName, message, exception);
		}
	}

	@Override
	public void shutdown() {
		// Should be ignored
	}

	/**
	 * Translates a tinylog severity level into a JBoss Logging level.
	 *
	 * @param level
	 *            Severity level of tinylog
	 * @return Corresponding level of JBoss Logging
	 */
	private static org.jboss.logging.Logger.Level translate(final Level level) {
		switch (level) {
			case TRACE:
				return org.jboss.logging.Logger.Level.TRACE;
			case DEBUG:
				return org.jboss.logging.Logger.Level.DEBUG;
			case INFO:
				return org.jboss.logging.Logger.Level.INFO;
			case WARN:
				return org.jboss.logging.Logger.Level.WARN;
			case ERROR:
				return org.jboss.logging.Logger.Level.ERROR;
			default:
				return org.jboss.logging.Logger.Level.FATAL;
		}
	}

	private static boolean isLoggable(final String callerClassName, final Level level) {
		return Logger.getLogger(callerClassName).isEnabled(translate(level));
	}
}
