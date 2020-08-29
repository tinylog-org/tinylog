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

import org.tinylog.core.Level;
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

	private static volatile State state = new State(null, null);

	private static final Object mutex = new Object();
	private static List<LogEntry> entries = new ArrayList<>();

	/** */
	private InternalLogger() {
	}

	/**
	 * Initializes the internal logger.
	 *
	 * @param runtime Runtime flavor for extraction of stack trace location
	 * @param provider Logging provider for output log entries
	 */
	public static void init(RuntimeFlavor runtime, LoggingProvider provider) {
		synchronized (mutex) {
			if (provider != null) {
				StackTraceLocation location = runtime.getStackTraceLocationAtIndex(INTERNAL_STACK_TRACE_DEPTH);
				for (LogEntry entry : entries) {
					provider.log(location, TAG, entry.getLevel(), entry.getThrowable(), entry.getMessage(),
						entry.getArguments(), null);
				}
			}

			state = new State(runtime, provider);
			entries = new ArrayList<>();
		}
	}

	/**
	 * Issues a trace log entry.
	 *
	 * @param message Human-readable text message
	 */
	public static void trace(String message) {
		log(Level.TRACE, null, message);
	}

	/**
	 * Issues a trace log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void trace(Throwable ex, String message) {
		log(Level.TRACE, ex, message);
	}

	/**
	 * Issues a debug log entry.
	 *
	 * @param message Human-readable text message
	 */
	public static void debug(String message) {
		log(Level.DEBUG, null, message);
	}

	/**
	 * Issues a debug log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void debug(Throwable ex, String message) {
		log(Level.DEBUG, ex, message);
	}

	/**
	 * Issues an info log entry.
	 *
	 * @param message Human-readable text message
	 */
	public static void info(String message) {
		log(Level.INFO, null, message);
	}

	/**
	 * Issues an info log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void info(Throwable ex, String message) {
		log(Level.INFO, ex, message);
	}

	/**
	 * Issues a warn log entry.
	 *
	 * @param message Human-readable text message
	 */
	public static void warn(String message) {
		log(Level.WARN, null, message);
	}

	/**
	 * Issues a warn log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void warn(Throwable ex, String message) {
		log(Level.WARN, ex, message);
	}

	/**
	 * Issues an error log entry.
	 *
	 * @param message Human-readable text message
	 */
	public static void error(String message) {
		log(Level.ERROR, null, message);
	}

	/**
	 * Issues an error log entry.
	 *
	 * @param ex Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public static void error(Throwable ex, String message) {
		log(Level.ERROR, ex, message);
	}

	/**
	 * Issues a log entry at the defined severity level.
	 *
	 * @param level Severity level
	 * @param throwable Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	private static void log(Level level, Throwable throwable, String message) {
		State state = InternalLogger.state;

		if (state.provider == null) {
			synchronized (mutex) {
				state = InternalLogger.state;
				if (state.provider == null) {
					entries.add(new LogEntry(level, throwable, message, null));
				}
			}
		}

		if (state.provider != null) {
			StackTraceLocation location = state.runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			state.provider.log(location, TAG, level, throwable, message, null, null);
		}
	}

	/**
	 * Internal logger state with {@link RuntimeFlavor} and {@link LoggingProvider}.
	 */
	private static final class State {
		private final RuntimeFlavor runtime;
		private final LoggingProvider provider;

		/**
		 * @param runtime Runtime flavor for extraction of stack trace location
		 * @param provider Logging provider for output log entries
		 */
		private State(RuntimeFlavor runtime, LoggingProvider provider) {
			this.runtime = runtime;
			this.provider = provider;
		}
	}

}
