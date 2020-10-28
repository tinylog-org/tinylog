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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.providers.LoggingProvider;
import org.tinylog.core.runtime.StackTraceLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InternalLoggerTest {

	private Framework framework;
	private LoggingProvider provider;

	/**
	 * Initialize the internal logger.
	 */
	@BeforeEach
	void init() {
		provider = mock(LoggingProvider.class);
		framework = new Framework(false, false) {
			@Override
			public LoggingProvider getLoggingProvider() {
				return provider;
			}
		};
	}

	/**
	 * Verifies that a trace log entry with a plain text message can be issued.
	 */
	@Test
	void traceMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.trace(null, "Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.TRACE), isNull(),
			eq("Hello World!"), isNull(), any());
	}

	/**
	 * Verifies that a trace log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void traceMessageWithArguments() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.trace(null, "Hello {}!", "Alice");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.TRACE), isNull(), eq("Hello {}!"),
			aryEq(new Object[] {"Alice"}), isNotNull());
	}

	/**
	 * Verifies that a trace log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void traceExceptionAndMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);

		Exception exception = new Exception();
		logger.trace(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.TRACE), same(exception),
			eq("Oops!"), isNull(), any());
	}

	/**
	 * Verifies that a debug log entry with a plain text message can be issued.
	 */
	@Test
	void debugMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.debug(null, "Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.DEBUG), isNull(),
			eq("Hello World!"), isNull(), any());
	}

	/**
	 * Verifies that a debug log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void debugMessageWithArguments() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.debug(null, "Hello {}!", "Alice");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.DEBUG), isNull(), eq("Hello {}!"),
			aryEq(new Object[] {"Alice"}), isNotNull());
	}

	/**
	 * Verifies that a debug log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void debugExceptionAndMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);

		Exception exception = new Exception();
		logger.debug(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.DEBUG), same(exception),
			eq("Oops!"), isNull(), any());
	}

	/**
	 * Verifies that an info log entry with a plain text message can be issued.
	 */
	@Test
	void infoMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.info(null, "Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.INFO), isNull(),
			eq("Hello World!"), isNull(), any());
	}

	/**
	 * Verifies that an info log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void infoMessageWithArguments() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.info(null, "Hello {}!", "Alice");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.INFO), isNull(), eq("Hello {}!"),
			aryEq(new Object[] {"Alice"}), isNotNull());
	}

	/**
	 * Verifies that an info log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void infoExceptionAndMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);

		Exception exception = new Exception();
		logger.info(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.INFO), same(exception),
			eq("Oops!"), isNull(), any());
	}

	/**
	 * Verifies that a warn log entry with a plain text message can be issued.
	 */
	@Test
	void warnMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.warn(null, "Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.WARN), isNull(),
			eq("Hello World!"), isNull(), any());
	}

	/**
	 * Verifies that a warn log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void warnMessageWithArguments() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.warn(null, "Hello {}!", "Alice");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.WARN), isNull(), eq("Hello {}!"),
			aryEq(new Object[] {"Alice"}), isNotNull());
	}

	/**
	 * Verifies that a warn log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void warnExceptionAndMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);

		Exception exception = new Exception();
		logger.warn(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.WARN), same(exception),
			eq("Oops!"), isNull(), any());
	}

	/**
	 * Verifies that an error log entry with a plain text message can be issued.
	 */
	@Test
	void errorMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.error(null, "Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.ERROR), isNull(),
			eq("Hello World!"), isNull(), any());
	}

	/**
	 * Verifies that an error log entry with a placeholder message and arguments can be issued.
	 */
	@Test
	void errorMessageWithArguments() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.error(null, "Hello {}!", "Alice");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.ERROR), isNull(), eq("Hello {}!"),
			aryEq(new Object[] {"Alice"}), isNotNull());
	}

	/**
	 * Verifies that an error log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void errorExceptionAndMessage() {
		InternalLogger logger = new InternalLogger();
		logger.init(framework);

		Exception exception = new Exception();
		logger.error(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.ERROR), same(exception),
			eq("Oops!"), isNull(), any());
	}

	/**
	 * Verifies that log entries can be issued belated when the internal logger will be initialized.
	 */
	@Test
	void delayedIssuing() {
		InternalLogger logger = new InternalLogger();
		logger.info(null, "Hello World!");
		logger.init(framework);

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.INFO), isNull(), eq("Hello World!"),
			isNull(), any());
	}

	/**
	 * Verifies that the correct stack trace location is provided.
	 */
	@Test
	void stackTraceLocation() {
		provider = new LoggingProvider() {
			@Override
			public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
					Object[] arguments, MessageFormatter formatter) {
				StackTraceElement element = location.getCallerStackTraceElement();
				assertThat(element.getClassName()).isEqualTo(InternalLoggerTest.class.getName());
				assertThat(element.getMethodName()).isEqualTo("stackTraceLocation");
				assertThat(element.getLineNumber()).isEqualTo(289);
			}
		};

		InternalLogger logger = new InternalLogger();
		logger.init(framework);
		logger.info(null, "Hello World!");
	}

}
