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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.providers.LoggingProvider;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.RuntimeProvider;
import org.tinylog.core.runtime.StackTraceLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InternalLoggerTest {

	private final RuntimeFlavor runtime = new RuntimeProvider().getRuntime();
	private final LoggingProvider provider = mock(LoggingProvider.class);

	/**
	 * Resets the internal logger.
	 */
	@BeforeEach
	@AfterEach
	void cleanUp() {
		InternalLogger.init(null, null);
	}

	/**
	 * Verifies that a trace log entry with a plain text message can be issued.
	 */
	@Test
	void traceMessage() {
		InternalLogger.init(runtime, provider);
		InternalLogger.trace("Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.TRACE), isNull(),
			eq("Hello World!"), isNull(), isNull());
	}

	/**
	 * Verifies that a trace log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void traceExceptionAndMessage() {
		InternalLogger.init(runtime, provider);

		Exception exception = new Exception();
		InternalLogger.trace(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.TRACE), same(exception),
			eq("Oops!"), isNull(), isNull());
	}

	/**
	 * Verifies that a debug log entry with a plain text message can be issued.
	 */
	@Test
	void debugMessage() {
		InternalLogger.init(runtime, provider);
		InternalLogger.debug("Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.DEBUG), isNull(),
			eq("Hello World!"), isNull(), isNull());
	}

	/**
	 * Verifies that a debug log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void debugExceptionAndMessage() {
		InternalLogger.init(runtime, provider);

		Exception exception = new Exception();
		InternalLogger.debug(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.DEBUG), same(exception),
			eq("Oops!"), isNull(), isNull());
	}

	/**
	 * Verifies that an info log entry with a plain text message can be issued.
	 */
	@Test
	void infoMessage() {
		InternalLogger.init(runtime, provider);
		InternalLogger.info("Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.INFO), isNull(),
			eq("Hello World!"), isNull(), isNull());
	}

	/**
	 * Verifies that an info log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void infoExceptionAndMessage() {
		InternalLogger.init(runtime, provider);

		Exception exception = new Exception();
		InternalLogger.info(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.INFO), same(exception),
			eq("Oops!"), isNull(), isNull());
	}

	/**
	 * Verifies that a warn log entry with a plain text message can be issued.
	 */
	@Test
	void warnMessage() {
		InternalLogger.init(runtime, provider);
		InternalLogger.warn("Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.WARN), isNull(),
			eq("Hello World!"), isNull(), isNull());
	}

	/**
	 * Verifies that a warn log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void warnExceptionAndMessage() {
		InternalLogger.init(runtime, provider);

		Exception exception = new Exception();
		InternalLogger.warn(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.WARN), same(exception),
			eq("Oops!"), isNull(), isNull());
	}

	/**
	 * Verifies that an error log entry with a plain text message can be issued.
	 */
	@Test
	void errorMessage() {
		InternalLogger.init(runtime, provider);
		InternalLogger.error("Hello World!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.ERROR), isNull(),
			eq("Hello World!"), isNull(), isNull());
	}

	/**
	 * Verifies that an error log entry with an exception and a custom text message can be issued.
	 */
	@Test
	void errorExceptionAndMessage() {
		InternalLogger.init(runtime, provider);

		Exception exception = new Exception();
		InternalLogger.error(exception, "Oops!");

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.ERROR), same(exception),
			eq("Oops!"), isNull(), isNull());
	}

	/**
	 * Verifies that log entries can be issued belated when the internal logger will be initialized.
	 */
	@Test
	void belatedIssuing() {
		InternalLogger.info("Hello World!");
		InternalLogger.init(runtime, provider);

		verify(provider).log(isNotNull(), eq("tinylog"), eq(Level.INFO), isNull(),
			eq("Hello World!"), isNull(), isNull());
	}

	/**
	 * Verifies that the correct stack trace location is provided.
	 */
	@Test
	void stackTraceLocation() {
		LoggingProvider provider = new LoggingProvider() {
			@Override
			public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
					Object[] arguments, MessageFormatter formatter) {
				StackTraceElement element = location.getCallerStackTraceElement();
				assertThat(element.getClassName()).isEqualTo(InternalLoggerTest.class.getName());
				assertThat(element.getMethodName()).isEqualTo("stackTraceLocation");
				assertThat(element.getLineNumber()).isEqualTo(207);
			}
		};

		InternalLogger.init(runtime, provider);
		InternalLogger.info("Hello World!");
	}

}
