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

package org.tinylog.core.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.providers.LoggingProvider;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Static logger for issuing internal log entries.
 */
public final class InternalLogger {

	private static final String TAG = "tinylog";
	private static final int INTERNAL_STACK_TRACE_DEPTH = 1;
	private static final int CALLER_STACK_TRACE_DEPTH = 3;

	private static final Object mutex = new Object();
	private static volatile State state;
	private static List<LogEntry> entries = new ArrayList<>();

	/** */
	private InternalLogger() {
	}

	/**
	 * Initializes the internal logger.
	 *
	 * @param framework Fully initialized framework for setting this logger up
	 */
	public static void init(Framework framework) {
		if (framework == null) {
			synchronized (mutex) {
				state = null;
				entries = new ArrayList<>();
			}
		} else {
			RuntimeFlavor runtime = framework.getRuntime();
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(INTERNAL_STACK_TRACE_DEPTH);
			LoggingProvider provider = framework.getLoggingProvider();
			Locale locale = framework.getConfiguration().getLocale();
			MessageFormatter formatter = new EnhancedMessageFormatter(framework, locale);

			synchronized (mutex) {
				for (LogEntry entry : entries) {
					provider.log(location, TAG, entry.getLevel(), entry.getThrowable(), entry.getMessage(),
						entry.getArguments(), formatter);
				}

				state = new State(runtime, provider, formatter);
				entries = new ArrayList<>();
			}
		}
	}

	/**
	 * Issues a trace log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void trace(Throwable ex, String message) {
		log(Level.TRACE, ex, message, null);
	}

	/**
	 * Issues a trace log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void trace(Throwable ex, String message, Object... arguments) {
		log(Level.TRACE, ex, message, arguments);
	}

	/**
	 * Issues a debug log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void debug(Throwable ex, String message) {
		log(Level.DEBUG, ex, message, null);
	}

	/**
	 * Issues a debug log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void debug(Throwable ex, String message, Object... arguments) {
		log(Level.DEBUG, ex, message, arguments);
	}

	/**
	 * Issues an info log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void info(Throwable ex, String message) {
		log(Level.INFO, ex, message, null);
	}

	/**
	 * Issues an info log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void info(Throwable ex, String message, Object... arguments) {
		log(Level.INFO, ex, message, arguments);
	}

	/**
	 * Issues a warn log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void warn(Throwable ex, String message) {
		log(Level.WARN, ex, message, null);
	}

	/**
	 * Issues a warn log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void warn(Throwable ex, String message, Object... arguments) {
		log(Level.WARN, ex, message, arguments);
	}

	/**
	 * Issues an error log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void error(Throwable ex, String message) {
		log(Level.ERROR, ex, message, null);
	}

	/**
	 * Issues an error log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message with placeholders
	 * @param arguments Argument values for placeholders in the text message
	 */
	public static void error(Throwable ex, String message, Object... arguments) {
		log(Level.ERROR, ex, message, arguments);
	}

	/**
	 * Issues a log entry at the defined severity level.
	 *
	 * @param level Severity level
	 * @param throwable Exception or any other kind of throwable
	 * @param message Human-readable text message
	 * @param arguments Argument values for placeholders in the text message
	 */
	private static void log(Level level, Throwable throwable, String message, Object[] arguments) {
		State state = InternalLogger.state;

		if (state == null) {
			synchronized (mutex) {
				state = InternalLogger.state;
				if (state == null) {
					entries.add(new LogEntry(level, throwable, message, arguments));
				}
			}
		}

		if (state != null) {
			StackTraceLocation location = state.runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			state.provider.log(location, TAG, level, throwable, message, arguments, state.formatter);
		}
	}

	/**
	 * Internal logger state with {@link RuntimeFlavor}, {@link LoggingProvider}, and {@link MessageFormatter}.
	 */
	private static final class State {
		private final RuntimeFlavor runtime;
		private final LoggingProvider provider;
		private final MessageFormatter formatter;

		/**
		 * @param runtime Runtime flavor for extraction of stack trace location
		 * @param provider Logging provider for output log entries
		 * @param formatter Message formatter for replacing placeholders by real values
		 */
		private State(RuntimeFlavor runtime, LoggingProvider provider, MessageFormatter formatter) {
			this.runtime = runtime;
			this.provider = provider;
			this.formatter = formatter;
		}
	}

}
