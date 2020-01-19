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

package org.tinylog.jul;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.tinylog.configuration.Configuration;
import org.tinylog.format.JavaTextMessageFormatFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * {@link Handler java.util.logging.Handler} for redirecting log entries to tinylog.
 */
final class BridgeHandler extends Handler {

	private static final String LOGGER_CLASS_NAME = Logger.class.getName();

	private static final MessageFormatter formatter = new JavaTextMessageFormatFormatter(Configuration.getLocale());
	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	/** */
	BridgeHandler() {
	}

	@Override
	public void publish(final LogRecord record) {
		provider.log(LOGGER_CLASS_NAME, null, translateLevel(record.getLevel()), record.getThrown(), formatter, record.getMessage(),
				record.getParameters());
	}

	@Override
	public void flush() {
		// Ignore
	}

	@Override
	public void close() {
		// Ignore
	}

	/**
	 * Activates this handler.
	 */
	void activate() {
		LogManager.getLogManager().reset();

		Logger logger = Logger.getLogger("");
		logger.setLevel(translateLevel(provider.getMinimumLevel(null)));
		logger.addHandler(this);
	}

	/**
	 * Translates a tinylog severity level into a {@code java.util.logging} level.
	 * 
	 * @param level
	 *            Severity level from tinylog
	 * @return Logging level for {@code java.util.logging}
	 */
	private static Level translateLevel(final org.tinylog.Level level) {
		switch (level) {
			case TRACE:
				return Level.ALL;
			case DEBUG:
				return Level.FINER;
			case INFO:
				return Level.CONFIG;
			case WARN:
				return Level.WARNING;
			case ERROR:
				return Level.SEVERE;
			case OFF:
				return Level.OFF;
			default:
				throw new IllegalArgumentException("Unknown JUL severity level \"" + level + "\"");
		}
	}

	/**
	 * Translates a {@code java.util.logging} level into a tinylog severity level.
	 * 
	 * @param level
	 *            Logging level from {@code java.util.logging}
	 * @return Severity level for tinylog
	 */
	private static org.tinylog.Level translateLevel(final Level level) {
		if (level.intValue() <= Level.FINEST.intValue()) {
			return org.tinylog.Level.TRACE;
		} else if (level.intValue() <= Level.FINE.intValue()) {
			return org.tinylog.Level.DEBUG;
		} else if (level.intValue() <= Level.INFO.intValue()) {
			return org.tinylog.Level.INFO;
		} else if (level.intValue() <= Level.WARNING.intValue()) {
			return org.tinylog.Level.WARN;
		} else {
			return org.tinylog.Level.ERROR;
		}
	}

}
