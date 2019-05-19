/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.slf4j;

import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;
import org.tinylog.Level;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * Location aware logger for tinylog's {@link LoggingProvider}.
 */
public final class TinylogLogger implements LocationAwareLogger {

	private static final int STACKTRACE_DEPTH = 2;

	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	// @formatter:off
	private static final boolean MINIMUM_GLOBAL_LEVEL_COVERS_TRACE = isCoveredByGlobalMinimumLevel(Level.TRACE);
	private static final boolean MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG = isCoveredByGlobalMinimumLevel(Level.DEBUG);
	private static final boolean MINIMUM_GLOBAL_LEVEL_COVERS_INFO  = isCoveredByGlobalMinimumLevel(Level.INFO);
	private static final boolean MINIMUM_GLOBAL_LEVEL_COVERS_WARN  = isCoveredByGlobalMinimumLevel(Level.WARN);
	private static final boolean MINIMUM_GLOBAL_LEVEL_COVERS_ERROR = isCoveredByGlobalMinimumLevel(Level.ERROR);
	// @formatter:on

	// @formatter:off
	private static final boolean MINIMUM_DEFAULT_LEVEL_COVERS_TRACE = isCoveredByDefaultMinimumLevel(Level.TRACE);
	private static final boolean MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG = isCoveredByDefaultMinimumLevel(Level.DEBUG);
	private static final boolean MINIMUM_DEFAULT_LEVEL_COVERS_INFO  = isCoveredByDefaultMinimumLevel(Level.INFO);
	private static final boolean MINIMUM_DEFAULT_LEVEL_COVERS_WARN  = isCoveredByDefaultMinimumLevel(Level.WARN);
	private static final boolean MINIMUM_DEFAULT_LEVEL_COVERS_ERROR = isCoveredByDefaultMinimumLevel(Level.ERROR);
	// @formatter:on

	private final String name;

	/**
	 * @param name
	 *            Name for logger
	 */
	public TinylogLogger(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isTraceEnabled() {
		return MINIMUM_DEFAULT_LEVEL_COVERS_TRACE && provider.isEnabled(STACKTRACE_DEPTH, null, Level.TRACE);
	}

	@Override
	public void trace(final String message) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, message, (Object[]) null);
		}
	}

	@Override
	public void trace(final String format, final Object arg) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void trace(final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void trace(final String format, final Object... arguments) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void trace(final String message, final Throwable exception) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isTraceEnabled(final Marker marker) {
		String tag = marker == null ? null : marker.getName();
		return MINIMUM_GLOBAL_LEVEL_COVERS_TRACE && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.TRACE);
	}

	@Override
	public void trace(final Marker marker, final String message) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_TRACE) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, null, message, (Object[]) null);
		}
	}

	@Override
	public void trace(final Marker marker, final String format, final Object arg) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_TRACE) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_TRACE) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void trace(final Marker marker, final String format, final Object... arguments) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_TRACE) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void trace(final Marker marker, final String message, final Throwable exception) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_TRACE) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG && provider.isEnabled(STACKTRACE_DEPTH, null, Level.DEBUG);
	}

	@Override
	public void debug(final String message) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, message, (Object[]) null);
		}
	}

	@Override
	public void debug(final String format, final Object arg) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void debug(final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void debug(final String format, final Object... arguments) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void debug(final String message, final Throwable exception) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isDebugEnabled(final Marker marker) {
		String tag = marker == null ? null : marker.getName();
		return MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.DEBUG);
	}

	@Override
	public void debug(final Marker marker, final String message) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, null, message, (Object[]) null);
		}
	}

	@Override
	public void debug(final Marker marker, final String format, final Object arg) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void debug(final Marker marker, final String format, final Object... arguments) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void debug(final Marker marker, final String message, final Throwable exception) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return MINIMUM_DEFAULT_LEVEL_COVERS_INFO && provider.isEnabled(STACKTRACE_DEPTH, null, Level.INFO);
	}

	@Override
	public void info(final String message) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, message, (Object[]) null);
		}
	}

	@Override
	public void info(final String format, final Object arg) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void info(final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void info(final String format, final Object... arguments) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void info(final String message, final Throwable exception) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isInfoEnabled(final Marker marker) {
		String tag = marker == null ? null : marker.getName();
		return MINIMUM_GLOBAL_LEVEL_COVERS_INFO && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.INFO);
	}

	@Override
	public void info(final Marker marker, final String message) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_INFO) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, null, message, (Object[]) null);
		}
	}

	@Override
	public void info(final Marker marker, final String format, final Object arg) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_INFO) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_INFO) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void info(final Marker marker, final String format, final Object... arguments) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_INFO) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void info(final Marker marker, final String message, final Throwable exception) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_INFO) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isWarnEnabled() {
		return MINIMUM_DEFAULT_LEVEL_COVERS_WARN && provider.isEnabled(STACKTRACE_DEPTH, null, Level.WARN);
	}

	@Override
	public void warn(final String message) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, message, (Object[]) null);
		}
	}

	@Override
	public void warn(final String format, final Object arg) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void warn(final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void warn(final String format, final Object... arguments) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void warn(final String message, final Throwable exception) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isWarnEnabled(final Marker marker) {
		String tag = marker == null ? null : marker.getName();
		return MINIMUM_GLOBAL_LEVEL_COVERS_WARN && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.WARN);
	}

	@Override
	public void warn(final Marker marker, final String message) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_WARN) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.WARN, null, message, (Object[]) null);
		}
	}

	@Override
	public void warn(final Marker marker, final String format, final Object arg) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_WARN) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.WARN, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_WARN) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.WARN, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void warn(final Marker marker, final String format, final Object... arguments) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_WARN) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.WARN, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void warn(final Marker marker, final String message, final Throwable exception) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_WARN) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.WARN, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isErrorEnabled() {
		return MINIMUM_DEFAULT_LEVEL_COVERS_ERROR && provider.isEnabled(STACKTRACE_DEPTH, null, Level.ERROR);
	}

	@Override
	public void error(final String message) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, message, (Object[]) null);
		}
	}

	@Override
	public void error(final String format, final Object arg) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void error(final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void error(final String format, final Object... arguments) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void error(final String message, final Throwable exception) {
		if (MINIMUM_DEFAULT_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, message, (Object[]) null);
		}
	}

	@Override
	public boolean isErrorEnabled(final Marker marker) {
		String tag = marker == null ? null : marker.getName();
		return MINIMUM_GLOBAL_LEVEL_COVERS_ERROR && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.ERROR);
	}

	@Override
	public void error(final Marker marker, final String message) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_ERROR) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, null, message, (Object[]) null);
		}
	}

	@Override
	public void error(final Marker marker, final String format, final Object arg) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_ERROR) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, extractThrowable(arg), format, arg);
		}
	}

	@Override
	public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_ERROR) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, extractThrowable(arg2), format, arg1, arg2);
		}
	}

	@Override
	public void error(final Marker marker, final String format, final Object... arguments) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_ERROR) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, extractThrowable(arguments), format, arguments);
		}
	}

	@Override
	public void error(final Marker marker, final String message, final Throwable exception) {
		if (MINIMUM_GLOBAL_LEVEL_COVERS_ERROR) {
			String tag = marker == null ? null : marker.getName();
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, message, (Object[]) null);
		}
	}

	@Override
	public void log(final Marker marker, final String fqcn, final int level, final String message, final Object[] arguments,
		final Throwable exception) {
		Level severityLevel = translateLevel(level);
		String tag = marker == null ? null : marker.getName();
		if (provider.getMinimumLevel(tag).ordinal() <= severityLevel.ordinal()) {
			provider.log(fqcn, tag, severityLevel, exception, message, arguments);
		}
	}

	/**
	 * Checks if a given severity level is covered by the global logging provider's minimum level for all tags.
	 *
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 */
	private static boolean isCoveredByGlobalMinimumLevel(final Level level) {
		return provider.getMinimumLevel().ordinal() <= level.ordinal();
	}

	/**
	 * Checks if a given severity level is covered by the untagged logging provider's minimum level.
	 *
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 */
	private static boolean isCoveredByDefaultMinimumLevel(final Level level) {
		return provider.getMinimumLevel(null).ordinal() <= level.ordinal();
	}

	/**
	 * Translate SLF4J severity level codes.
	 * 
	 * @param level
	 *            Severity level code from SLF4J
	 * @return Responding severity level of tinylog
	 */
	private static Level translateLevel(final int level) {
		if (level <= TRACE_INT) {
			return Level.TRACE;
		} else if (level <= DEBUG_INT) {
			return Level.DEBUG;
		} else if (level <= INFO_INT) {
			return Level.INFO;
		} else if (level <= WARN_INT) {
			return Level.WARN;
		} else {
			return Level.ERROR;
		}
	}

	/**
	 * Returns a throwable if the last argument is one.
	 * 
	 * @param arguments
	 *            Passed arguments
	 * @return Last argument as throwable or {@code null}
	 */
	private static Throwable extractThrowable(final Object[] arguments) {
		return arguments.length == 0 ? null : extractThrowable(arguments[arguments.length - 1]);
	}

	/**
	 * Returns a throwable if the given argument is one.
	 * 
	 * @param argument
	 *            Passed argument
	 * @return Argument as throwable or {@code null}
	 */
	private static Throwable extractThrowable(final Object argument) {
		return argument instanceof Throwable ? (Throwable) argument : null;
	}

}
