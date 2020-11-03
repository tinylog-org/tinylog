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

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.CaptureLogEntries;
import org.tinylog.core.test.Log;
import org.tinylog.core.test.LogEntry;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries(minLevel = Level.TRACE)
class InternalLoggerTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	/**
	 * Verifies that a trace log entry with a plain text message can be issued.
	 */
	@Test
	void traceMessage() {
		InternalLogger.trace(null, "Hello World!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.TRACE, null, "Hello World!"));
	}
	
	/**
	 * Verifies that a trace log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void traceMessageWithArguments() {
		InternalLogger.trace(null, "Hello {}!", "Alice");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.TRACE, null, "Hello Alice!"));
	}

	/**
	 * Verifies that a trace log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void traceExceptionAndMessage() {
		Exception exception = new Exception();
		InternalLogger.trace(exception, "Oops!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.TRACE, exception, "Oops!"));
	}

	/**
	 * Verifies that a debug log entry with a plain text message can be issued.
	 */
	@Test
	void debugMessage() {
		InternalLogger.debug(null, "Hello World!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.DEBUG, null, "Hello World!"));
	}

	/**
	 * Verifies that a debug log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void debugMessageWithArguments() {
		InternalLogger.debug(null, "Hello {}!", "Alice");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.DEBUG, null, "Hello Alice!"));
	}

	/**
	 * Verifies that a debug log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void debugExceptionAndMessage() {
		Exception exception = new Exception();
		InternalLogger.debug(exception, "Oops!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.DEBUG, exception, "Oops!"));
	}

	/**
	 * Verifies that an info log entry with a plain text message can be issued.
	 */
	@Test
	void infoMessage() {
		InternalLogger.info(null, "Hello World!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.INFO, null, "Hello World!"));
	}

	/**
	 * Verifies that an info log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void infoMessageWithArguments() {
		InternalLogger.info(null, "Hello {}!", "Alice");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.INFO, null, "Hello Alice!"));
	}

	/**
	 * Verifies that an info log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void infoExceptionAndMessage() {
		Exception exception = new Exception();
		InternalLogger.info(exception, "Oops!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.INFO, exception, "Oops!"));
	}

	/**
	 * Verifies that a warn log entry with a plain text message can be issued.
	 */
	@Test
	void warnMessage() {
		InternalLogger.warn(null, "Hello World!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.WARN, null, "Hello World!"));
	}

	/**
	 * Verifies that a warn log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void warnMessageWithArguments() {
		InternalLogger.warn(null, "Hello {}!", "Alice");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.WARN, null, "Hello Alice!"));
	}

	/**
	 * Verifies that a warn log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void warnExceptionAndMessage() {
		Exception exception = new Exception();
		InternalLogger.warn(exception, "Oops!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.WARN, exception, "Oops!"));
	}

	/**
	 * Verifies that an error log entry with a plain text message can be issued.
	 */
	@Test
	void errorMessage() {
		InternalLogger.error(null, "Hello World!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.ERROR, null, "Hello World!"));
	}

	/**
	 * Verifies that an error log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void errorMessageWithArguments() {
		InternalLogger.error(null, "Hello {}!", "Alice");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.ERROR, null, "Hello Alice!"));
	}

	/**
	 * Verifies that an error log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void errorExceptionAndMessage() {
		Exception exception = new Exception();
		InternalLogger.error(exception, "Oops!");
		assertThat(log.consume()).containsExactly(createLogEntry(Level.ERROR, exception, "Oops!"));
	}

	/**
	 * Verifies that log entries can be issued belated when the internal logger will be initialized.
	 */
	@CaptureLogEntries(minLevel = Level.INFO, autostart = false)
	@Test
	void delayedIssuing() {
		InternalLogger.info(null, "Hello World!");
		assertThat(log.consume()).isEmpty();

		InternalLogger.init(framework);
		assertThat(log.consume()).containsExactly(
			new LogEntry(InternalLogger.class.getName(), "tinylog", Level.INFO, null, "Hello World!")
		);
	}

	/**
	 * Creates a new log entry.
	 *
	 * @param level Severity level
	 * @param exception Exception or any other kind of throwable
	 * @param message Text message
	 * @return Created log entry
	 */
	private static LogEntry createLogEntry(Level level, Throwable exception, String message) {
		return new LogEntry(InternalLoggerTest.class.getName(), "tinylog", level, exception, message);
	}

}
