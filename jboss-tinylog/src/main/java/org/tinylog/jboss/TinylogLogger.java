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

package org.tinylog.jboss;

import org.jboss.logging.Logger;
import org.tinylog.configuration.Configuration;
import org.tinylog.format.JavaTextMessageFormatFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.format.PrintfStyleFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * JBoss compatible logger that uses tinylog's {@link LoggingProvider}.
 */
public final class TinylogLogger extends Logger {

	private static final long serialVersionUID = 1L;

	private static final int STACKTRACE_DEPTH = 2;

	private static final MessageFormatter messageFormatter = new JavaTextMessageFormatFormatter(Configuration.getLocale());
	private static final MessageFormatter printfFormatter = new PrintfStyleFormatter(Configuration.getLocale());
	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	// @formatter:off
	private static final boolean MINIMUM_LEVEL_COVERS_TRACE = isCoveredByMinimumLevel(org.tinylog.Level.TRACE);
	private static final boolean MINIMUM_LEVEL_COVERS_DEBUG = isCoveredByMinimumLevel(org.tinylog.Level.DEBUG);
	private static final boolean MINIMUM_LEVEL_COVERS_INFO  = isCoveredByMinimumLevel(org.tinylog.Level.INFO);
	private static final boolean MINIMUM_LEVEL_COVERS_WARN  = isCoveredByMinimumLevel(org.tinylog.Level.WARN);
	private static final boolean MINIMUM_LEVEL_COVERS_ERROR = isCoveredByMinimumLevel(org.tinylog.Level.ERROR);
	// @formatter:on

	/**
	 * @param name
	 *            Logger category name
	 */
	public TinylogLogger(final String name) {
		super(name);
	}

	@Override
	public boolean isEnabled(final Level level) {
		return provider.isEnabled(STACKTRACE_DEPTH, null, translateLevel(level));
	}

	@Override
	public boolean isTraceEnabled() {
		return MINIMUM_LEVEL_COVERS_TRACE && provider.isEnabled(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE);
	}

	@Override
	public void trace(final Object message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, null, message, (Object[]) null);
		}
	}

	@Override
	public void trace(final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void trace(final String loggerClassName, final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(loggerClassName, null, org.tinylog.Level.TRACE, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void trace(final String loggerClassName, final Object message, final Object[] arguments, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(loggerClassName, null, org.tinylog.Level.TRACE, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void tracev(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, messageFormatter, message, arguments);
		}
	}

	@Override
	public void tracev(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, messageFormatter, message, argument);
		}
	}

	@Override
	public void tracev(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void tracev(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracev(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void tracev(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, messageFormatter, message, argument);
		}

	}

	@Override
	public void tracev(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void tracev(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracef(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, arguments);
		}
	}

	@Override
	public void tracef(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void tracef(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void tracef(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, arguments);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracef(final String message, final int argument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void tracef(final String message, final int firstArgument, final int secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument);
		}
	}

	@Override
	public void tracef(final String message, final int firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument);
		}
	}

	@Override
	public void tracef(final String message, final int firstArgument, final int secondArgument, final int thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void tracef(final String message, final int firstArgument, final int secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void tracef(final String message, final int firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final int argument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final int firstArgument, final int secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final int firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final int firstArgument, final int secondArgument,
		final int thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final int firstArgument, final int secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final int firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracef(final String message, final long argument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void tracef(final String message, final long firstArgument, final long secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument);
		}
	}

	@Override
	public void tracef(final String message, final long firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument);
		}
	}

	@Override
	public void tracef(final String message, final long firstArgument, final long secondArgument, final long thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void tracef(final String message, final long firstArgument, final long secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void tracef(final String message, final long firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final long argument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final long firstArgument, final long secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final long firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final long firstArgument, final long secondArgument,
		final long thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final long firstArgument, final long secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void tracef(final Throwable exception, final String message, final long firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return MINIMUM_LEVEL_COVERS_DEBUG && provider.isEnabled(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG);
	}

	@Override
	public void debug(final Object message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, null, message, (Object[]) null);
		}
	}

	@Override
	public void debug(final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void debug(final String loggerClassName, final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(loggerClassName, null, org.tinylog.Level.DEBUG, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void debug(final String loggerClassName, final Object message, final Object[] arguments, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(loggerClassName, null, org.tinylog.Level.DEBUG, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void debugv(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, messageFormatter, message, arguments);
		}
	}

	@Override
	public void debugv(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, messageFormatter, message, argument);
		}
	}

	@Override
	public void debugv(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void debugv(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugv(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void debugv(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, messageFormatter, message, argument);
		}

	}

	@Override
	public void debugv(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void debugv(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugf(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, arguments);
		}
	}

	@Override
	public void debugf(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void debugf(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void debugf(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, arguments);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugf(final String message, final int argument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void debugf(final String message, final int firstArgument, final int secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument);
		}
	}

	@Override
	public void debugf(final String message, final int firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument);
		}
	}

	@Override
	public void debugf(final String message, final int firstArgument, final int secondArgument, final int thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void debugf(final String message, final int firstArgument, final int secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void debugf(final String message, final int firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final int argument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final int firstArgument, final int secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final int firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final int firstArgument, final int secondArgument,
		final int thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final int firstArgument, final int secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final int firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugf(final String message, final long argument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void debugf(final String message, final long firstArgument, final long secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument);
		}
	}

	@Override
	public void debugf(final String message, final long firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument);
		}
	}

	@Override
	public void debugf(final String message, final long firstArgument, final long secondArgument, final long thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void debugf(final String message, final long firstArgument, final long secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void debugf(final String message, final long firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final long argument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final long firstArgument, final long secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final long firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final long firstArgument, final long secondArgument,
		final long thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final long firstArgument, final long secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void debugf(final Throwable exception, final String message, final long firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return MINIMUM_LEVEL_COVERS_INFO && provider.isEnabled(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO);
	}

	@Override
	public void info(final Object message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, null, message, (Object[]) null);
		}
	}

	@Override
	public void info(final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void info(final String loggerClassName, final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(loggerClassName, null, org.tinylog.Level.INFO, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void info(final String loggerClassName, final Object message, final Object[] arguments, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(loggerClassName, null, org.tinylog.Level.INFO, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void infov(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, messageFormatter, message, arguments);
		}
	}

	@Override
	public void infov(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, messageFormatter, message, argument);
		}
	}

	@Override
	public void infov(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void infov(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void infov(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void infov(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, messageFormatter, message, argument);
		}

	}

	@Override
	public void infov(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void infov(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void infof(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, printfFormatter, message, arguments);
		}
	}

	@Override
	public void infof(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void infof(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void infof(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void infof(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, printfFormatter, message, arguments);
		}
	}

	@Override
	public void infof(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void infof(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void infof(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void warn(final Object message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, null, message, (Object[]) null);
		}
	}

	@Override
	public void warn(final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void warn(final String loggerClassName, final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(loggerClassName, null, org.tinylog.Level.WARN, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void warn(final String loggerClassName, final Object message, final Object[] arguments, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(loggerClassName, null, org.tinylog.Level.WARN, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void warnv(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, messageFormatter, message, arguments);
		}
	}

	@Override
	public void warnv(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, messageFormatter, message, argument);
		}
	}

	@Override
	public void warnv(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void warnv(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void warnv(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void warnv(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, messageFormatter, message, argument);
		}

	}

	@Override
	public void warnv(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void warnv(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void warnf(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, printfFormatter, message, arguments);
		}
	}

	@Override
	public void warnf(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void warnf(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void warnf(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void warnf(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, printfFormatter, message, arguments);
		}
	}

	@Override
	public void warnf(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void warnf(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void warnf(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, printfFormatter, message, firstArgument, secondArgument,
					thirdArgument);
		}
	}

	@Override
	public void error(final Object message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, null, message, (Object[]) null);
		}
	}

	@Override
	public void error(final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void error(final String loggerClassName, final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(loggerClassName, null, org.tinylog.Level.ERROR, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void error(final String loggerClassName, final Object message, final Object[] arguments, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(loggerClassName, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void errorv(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, messageFormatter, message, arguments);
		}
	}

	@Override
	public void errorv(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, messageFormatter, message, argument);
		}
	}

	@Override
	public void errorv(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void errorv(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void errorv(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void errorv(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, argument);
		}

	}

	@Override
	public void errorv(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void errorv(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void errorf(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, printfFormatter, message, arguments);
		}
	}

	@Override
	public void errorf(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void errorf(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void errorf(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void errorf(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, printfFormatter, message, arguments);
		}
	}

	@Override
	public void errorf(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void errorf(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void errorf(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void fatal(final Object message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, null, message, (Object[]) null);
		}
	}

	@Override
	public void fatal(final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void fatal(final String loggerClassName, final Object message, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(loggerClassName, null, org.tinylog.Level.ERROR, exception, null, message, (Object[]) null);
		}
	}

	@Override
	public void fatal(final String loggerClassName, final Object message, final Object[] arguments, final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(loggerClassName, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void fatalv(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, messageFormatter, message, arguments);
		}
	}

	@Override
	public void fatalv(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, messageFormatter, message, argument);
		}
	}

	@Override
	public void fatalv(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void fatalv(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void fatalv(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, arguments);
		}
	}

	@Override
	public void fatalv(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, argument);
		}

	}

	@Override
	public void fatalv(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void fatalv(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, messageFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void fatalf(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, printfFormatter, message, arguments);
		}
	}

	@Override
	public void fatalf(final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, printfFormatter, message, argument);
		}
	}

	@Override
	public void fatalf(final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void fatalf(final String message, final Object firstArgument, final Object secondArgument, final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void fatalf(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, printfFormatter, message, arguments);
		}
	}

	@Override
	public void fatalf(final Throwable exception, final String message, final Object argument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, printfFormatter, message, argument);
		}
	}

	@Override
	public void fatalf(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, printfFormatter, message, firstArgument,
					secondArgument);
		}
	}

	@Override
	public void fatalf(final Throwable exception, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, printfFormatter, message, firstArgument,
					secondArgument, thirdArgument);
		}
	}

	@Override
	public void log(final Level level, final Object message) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, null, message, (Object[]) null);
	}

	@Override
	public void log(final Level level, final Object message, final Throwable exception) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, null, message, (Object[]) null);
	}

	@Override
	public void logv(final Level level, final String message, final Object... arguments) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, messageFormatter, message, arguments);
	}

	@Override
	public void logv(final Level level, final String message, final Object argument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, messageFormatter, message, argument);
	}

	@Override
	public void logv(final Level level, final String message, final Object firstArgument, final Object secondArgument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, messageFormatter, message, firstArgument, secondArgument);
	}

	@Override
	public void logv(final Level level, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, messageFormatter, message, firstArgument, secondArgument,
			thirdArgument);
	}

	@Override
	public void logv(final Level level, final Throwable exception, final String message, final Object... arguments) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, messageFormatter, message, arguments);
	}

	@Override
	public void logv(final Level level, final Throwable exception, final String message, final Object argument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, messageFormatter, message, argument);
	}

	@Override
	public void logv(final Level level, final Throwable exception, final String message, final Object firstArgument,
		final Object secondArgument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, messageFormatter, message, firstArgument, secondArgument);
	}

	@Override
	public void logv(final Level level, final Throwable exception, final String message, final Object firstArgument,
		final Object secondArgument, final Object thirdArgument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, messageFormatter, message, firstArgument, secondArgument,
			thirdArgument);
	}

	@Override
	public void logf(final Level level, final String message, final Object... arguments) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, printfFormatter, message, arguments);
	}

	@Override
	public void logf(final Level level, final String message, final Object argument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, printfFormatter, message, argument);
	}

	@Override
	public void logf(final Level level, final String message, final Object firstArgument, final Object secondArgument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, printfFormatter, message, firstArgument, secondArgument);
	}

	@Override
	public void logf(final Level level, final String message, final Object firstArgument, final Object secondArgument,
		final Object thirdArgument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), null, printfFormatter, message, firstArgument, secondArgument,
			thirdArgument);
	}

	@Override
	public void logf(final Level level, final Throwable exception, final String message, final Object... arguments) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, printfFormatter, message, arguments);
	}

	@Override
	public void logf(final Level level, final Throwable exception, final String message, final Object argument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, printfFormatter, message, argument);
	}

	@Override
	public void logf(final Level level, final Throwable exception, final String message, final Object firstArgument,
		final Object secondArgument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, printfFormatter, message, firstArgument, secondArgument);
	}

	@Override
	public void logf(final Level level, final Throwable exception, final String message, final Object firstArgument,
		final Object secondArgument, final Object thirdArgument) {
		provider.log(STACKTRACE_DEPTH, null, translateLevel(level), exception, printfFormatter, message, firstArgument, secondArgument,
			thirdArgument);
	}

	@Override
	protected void doLog(final Level level, final String loggerClassName, final Object message, final Object[] arguments,
		final Throwable exception) {
		provider.log(loggerClassName, null, translateLevel(level), exception, messageFormatter, message, arguments);
	}

	@Override
	protected void doLogf(final Level level, final String loggerClassName, final String message, final Object[] arguments,
		final Throwable exception) {
		provider.log(loggerClassName, null, translateLevel(level), exception, printfFormatter, message, arguments);
	}

	/**
	 * Checks if a given severity level is covered by the logging provider's minimum level.
	 *
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 */
	private static boolean isCoveredByMinimumLevel(final org.tinylog.Level level) {
		return provider.getMinimumLevel(null).ordinal() <= level.ordinal();
	}

	/**
	 * Translate JBoss Logging severity levels.
	 * 
	 * @param level
	 *            Severity level from JBoss Logging
	 * @return Responding severity level of tinylog
	 */
	private static org.tinylog.Level translateLevel(final Level level) {
		switch (level) {
			case TRACE:
				return org.tinylog.Level.TRACE;
			case DEBUG:
				return org.tinylog.Level.DEBUG;
			case INFO:
				return org.tinylog.Level.INFO;
			case WARN:
				return org.tinylog.Level.WARN;
			case ERROR:
			case FATAL:
				return org.tinylog.Level.ERROR;
			default:
				throw new IllegalArgumentException("Unknown JBoss Logging severity level \"" + level + "\"");
		}
	}

}
