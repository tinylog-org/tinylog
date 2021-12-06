/*
 * Copyright 2021 Gerrit Rode
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

package org.tinylog.jsl;

import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import org.tinylog.configuration.Configuration;
import org.tinylog.format.JavaTextMessageFormatFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * Java System.Logger compatible logger that uses tinylog's {@link LoggingProvider}.
 */
public class TinylogLogger implements System.Logger {

	private static final int STACKTRACE_DEPTH = 2;

	private static final MessageFormatter MESSAGE_FORMATTER = new JavaTextMessageFormatFormatter(Configuration.getLocale());
	private static final LoggingProvider LOGGING_PROVIDER = ProviderRegistry.getLoggingProvider();

	private final String name;

	/**
	 * @param name   the name of the logger
	 */
	public TinylogLogger(final String name) {
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isLoggable(final Level level) {
		Objects.requireNonNull(level);

		// Setting the log level of a message to OFF makes little sense and should result in the message not being logged.
		if (level == Level.OFF) {
			return false;
		} else {
			return LOGGING_PROVIDER.isEnabled(STACKTRACE_DEPTH, null, translate(level));
		}
	}

	@Override
	public void log(final Level level, final String msg) {
		Objects.requireNonNull(level);

		if (isLoggable(level)) {
			LOGGING_PROVIDER.log(STACKTRACE_DEPTH, null, translate(level), null, null, msg, (Object[]) null);
		}
	}

	@Override
	public void log(final Level level, final Supplier<String> msgSupplier) {
		Objects.requireNonNull(level);
		Objects.requireNonNull(msgSupplier);

		if (isLoggable(level)) {
			LOGGING_PROVIDER.log(
				STACKTRACE_DEPTH, null, translate(level), null, null, msgSupplier, (Object[]) null
			);
		}
	}

	@Override
	public void log(final Level level, final Object obj) {
		Objects.requireNonNull(level);
		Objects.requireNonNull(obj);

		if (isLoggable(level)) {
			LOGGING_PROVIDER.log(STACKTRACE_DEPTH, null, translate(level), null, null, obj, (Object[]) null);
		}
	}

	@Override
	public void log(final Level level, final String msg, final Throwable thrown) {
		Objects.requireNonNull(level);

		if (isLoggable(level)) {
			LOGGING_PROVIDER.log(STACKTRACE_DEPTH, null, translate(level), thrown, null, msg, (Object[]) null);
		}
	}

	@Override
	public void log(final Level level, final Supplier<String> msgSupplier, final Throwable thrown) {
		Objects.requireNonNull(level);
		Objects.requireNonNull(msgSupplier);

		if (isLoggable(level)) {
			LOGGING_PROVIDER.log(STACKTRACE_DEPTH, null, translate(level), thrown, null, msgSupplier, (Object[]) null);
		}
	}

	@Override
	public void log(final Level level, final String format, final Object... params) {
		Objects.requireNonNull(level);

		if (isLoggable(level)) {
			LOGGING_PROVIDER.log(STACKTRACE_DEPTH, null, translate(level), null, MESSAGE_FORMATTER, format, params);
		}
	}

	@Override
	public void log(final Level level, final ResourceBundle bundle, final String msg, final Throwable thrown) {
		Objects.requireNonNull(level);

		if (isLoggable(level)) {
			if (bundle != null && msg != null) {
				LOGGING_PROVIDER.log(
					STACKTRACE_DEPTH, null, translate(level), thrown, null, bundle.getString(msg), (Object[]) null);
			} else {
				LOGGING_PROVIDER.log(
					STACKTRACE_DEPTH, null, translate(level), thrown, null, msg, (Object[]) null);
			}
		}
	}

	@Override
	public void log(final Level level, final ResourceBundle bundle, final String format, final Object... params) {
		Objects.requireNonNull(level);

		if (isLoggable(level)) {
			if (bundle != null && format != null) {
				LOGGING_PROVIDER.log(
					STACKTRACE_DEPTH, null, translate(level), null, MESSAGE_FORMATTER, bundle.getString(format), params);
			} else {
				LOGGING_PROVIDER.log(
					STACKTRACE_DEPTH, null, translate(level), null, MESSAGE_FORMATTER, format, params);
			}
		}
	}

	/**
	 * Translate Java System.Logger severity levels.
	 *
	 * @param level Severity level from Java System.Logger
	 * @return Responding severity level of tinylog
	 * @throws IllegalArgumentException Unknown Java System.Logger Logging severity level
	 */
	private static org.tinylog.Level translate(final System.Logger.Level level) {
		switch (level) {
			case ALL:
			case TRACE:
				return org.tinylog.Level.TRACE;
			case DEBUG:
				return org.tinylog.Level.DEBUG;
			case INFO:
				return org.tinylog.Level.INFO;
			case WARNING:
				return org.tinylog.Level.WARN;
			case ERROR:
				return org.tinylog.Level.ERROR;
			case OFF:
				return org.tinylog.Level.OFF;
			default:
				throw new IllegalArgumentException("Unknown Java System.Logger severity level \"" + level + "\"");
		}
	}
}
