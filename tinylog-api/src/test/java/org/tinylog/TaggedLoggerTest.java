/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link TaggedLogger}.
 */
@RunWith(Parameterized.class)
@PrepareForTest(TaggedLogger.class)
public final class TaggedLoggerTest {

	private static final String TAG = "test";

	/**
	 * Activates PowerMock (alternative to {@link PowerMockRunner}).
	 */
	@Rule
	public PowerMockRule rule = new PowerMockRule();

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(false);

	private Level level;

	private boolean traceEnabled;
	private boolean debugEnabled;
	private boolean infoEnabled;
	private boolean warnEnabled;
	private boolean errorEnabled;

	private LoggingProvider loggingProvider;
	private TaggedLogger logger;

	/**
	 * @param level
	 *            Actual severity level under test
	 * @param traceEnabled
	 *            Determines if {@link Level#TRACE TRACE} level is enabled
	 * @param debugEnabled
	 *            Determines if {@link Level#DEBUG DEBUG} level is enabled
	 * @param infoEnabled
	 *            Determines if {@link Level#INFO INFO} level is enabled
	 * @param warnEnabled
	 *            Determines if {@link Level#WARN WARN} level is enabled
	 * @param errorEnabled
	 *            Determines if {@link Level#ERROR ERROR} level is enabled
	 */
	public TaggedLoggerTest(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
		final boolean warnEnabled, final boolean errorEnabled) {
		this.level = level;
		this.traceEnabled = traceEnabled;
		this.debugEnabled = debugEnabled;
		this.infoEnabled = infoEnabled;
		this.warnEnabled = warnEnabled;
		this.errorEnabled = errorEnabled;
	}

	/**
	 * Returns for all severity levels which severity levels are enabled.
	 *
	 * @return Each object array contains the severity level itself and five booleans for {@link Level#TRACE TRACE} ...
	 *         {@link Level#ERROR ERROR} to determine whether these severity levels are enabled
	 */
	@Parameters(name = "{0}")
	public static Collection<Object[]> getLevels() {
		List<Object[]> levels = new ArrayList<>();

		// @formatter:off
		levels.add(new Object[] { Level.TRACE, true,  true,  true,  true,  true  });
		levels.add(new Object[] { Level.DEBUG, false, true,  true,  true,  true  });
		levels.add(new Object[] { Level.INFO,  false, false, true,  true,  true  });
		levels.add(new Object[] { Level.WARN,  false, false, false, true,  true  });
		levels.add(new Object[] { Level.ERROR, false, false, false, false, true  });
		levels.add(new Object[] { Level.OFF,   false, false, false, false, false });
		// @formatter:on

		return levels;
	}

	/**
	 * Mocks the underlying logging provider and creates a new tagged logger instance.
	 */
	@Before
	public void init() {
		loggingProvider = mockLoggingProvider();
		logger = new TaggedLogger(TAG);
	}

	/**
	 * Resets the underlying logging provider.
	 *
	 * @throws Exception
	 *             Failed resetting logging provider
	 */
	@After
	public void reset() throws Exception {
		resetLoggingProvider();
	}

	/**
	 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
	 */
	@Test
	public void isTraceEnabled() {
		assertThat(logger.isTraceEnabled()).isEqualTo(traceEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceObject() {
		logger.trace("Hello World!");

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.trace(supplier);
		verify(supplier, never()).get();

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndArguments() {
		logger.trace("Hello {}!", "World");

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.trace("The number is {}", supplier);
		verify(supplier, never()).get();

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceException() {
		Exception exception = new NullPointerException();

		logger.trace(exception);

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.trace(exception, "Hello World!");

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), same(exception), isNull(), eq("Hello World!"),
					isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#TRACE
	 * TRACE} level.
	 */
	@Test
	public void traceExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.trace(exception, supplier);

		verify(supplier, never()).get();

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.trace(exception, "Hello {}!", "World");

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.trace(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (traceEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.TRACE), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#DEBUG DEBUG} level is enabled.
	 */
	@Test
	public void isDebugEnabled() {
		assertThat(logger.isDebugEnabled()).isEqualTo(debugEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugObject() {
		logger.debug("Hello World!");

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.debug(supplier);
		verify(supplier, never()).get();

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndArguments() {
		logger.debug("Hello {}!", "World");

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.debug("The number is {}", supplier);
		verify(supplier, never()).get();

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugException() {
		Exception exception = new NullPointerException();

		logger.debug(exception);

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.debug(exception, "Hello World!");

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), same(exception), isNull(), eq("Hello World!"),
					isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#DEBUG
	 * DEBUG} level.
	 */
	@Test
	public void debugExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.debug(exception, supplier);

		verify(supplier, never()).get();

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.debug(exception, "Hello {}!", "World");

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.debug(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (debugEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.DEBUG), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#INFO INFO} level is enabled.
	 */
	@Test
	public void isInfoEnabled() {
		assertThat(logger.isInfoEnabled()).isEqualTo(infoEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoObject() {
		logger.info("Hello World!");

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.info(supplier);
		verify(supplier, never()).get();

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessageAndArguments() {
		logger.info("Hello {}!", "World");

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at {@link Level#INFO
	 * INFO} level.
	 */
	@Test
	public void infoMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.info("The number is {}", supplier);
		verify(supplier, never()).get();

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoException() {
		Exception exception = new NullPointerException();

		logger.info(exception);

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.info(exception, "Hello World!");

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), same(exception), isNull(), eq("Hello World!"),
					isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#INFO
	 * INFO} level.
	 */
	@Test
	public void infoExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.info(exception, supplier);

		verify(supplier, never()).get();

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.info(exception, "Hello {}!", "World");

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.info(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (infoEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.INFO), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#WARN WARN} level is enabled.
	 */
	@Test
	public void isWarnEnabled() {
		assertThat(logger.isWarnEnabled()).isEqualTo(warnEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnObject() {
		logger.warn("Hello World!");

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.warn(supplier);
		verify(supplier, never()).get();

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnMessageAndArguments() {
		logger.warn("Hello {}!", "World");

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at {@link Level#WARN
	 * WARN} level.
	 */
	@Test
	public void warnMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.warn("The number is {}", supplier);
		verify(supplier, never()).get();

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnException() {
		Exception exception = new NullPointerException();

		logger.warn(exception);

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.warn(exception, "Hello World!");

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), same(exception), isNull(), eq("Hello World!"),
					isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#WARN
	 * WARN} level.
	 */
	@Test
	public void warnExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.warn(exception, supplier);

		verify(supplier, never()).get();

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.warn(exception, "Hello {}!", "World");

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.warn(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (warnEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.WARN), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#ERROR ERROR} level is enabled.
	 */
	@Test
	public void isErrorEnabled() {
		assertThat(logger.isErrorEnabled()).isEqualTo(errorEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorObject() {
		logger.error("Hello World!");

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.error(supplier);
		verify(supplier, never()).get();

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndArguments() {
		logger.error("Hello {}!", "World");

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.error("The number is {}", supplier);
		verify(supplier, never()).get();

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorException() {
		Exception exception = new NullPointerException();

		logger.error(exception);

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.error(exception, "Hello World!");

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), same(exception), isNull(), eq("Hello World!"),
					isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void errorExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.error(exception, supplier);

		verify(supplier, never()).get();

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.error(exception, "Hello {}!", "World");

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.error(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (errorEnabled) {
			verify(loggingProvider).log(eq(2), eq(TAG), eq(Level.ERROR), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Mocks the logging provider for {@link TaggedLogger} and overrides all depending fields.
	 *
	 * @return Mock instance for logging provider
	 */
	private LoggingProvider mockLoggingProvider() {
		LoggingProvider provider = mock(LoggingProvider.class);

		when(provider.getMinimumLevel(TAG)).thenReturn(level);
		when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.TRACE))).thenReturn(traceEnabled);
		when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.DEBUG))).thenReturn(debugEnabled);
		when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.INFO))).thenReturn(infoEnabled);
		when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.WARN))).thenReturn(warnEnabled);
		when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.ERROR))).thenReturn(errorEnabled);

		Whitebox.setInternalState(TaggedLogger.class, provider);

		return provider;
	}

	/**
	 * Creates a mocked supplier that returns the given value.
	 * 
	 * @param value
	 *            Value that should be returned by the created supplier
	 * @param <T>
	 *            Type of value
	 * @return A new supplier
	 */
	@SuppressWarnings("unchecked")
	private <T> Supplier<T> mockSupplier(final T value) {
		Supplier<T> supplier = mock(Supplier.class);
		when(supplier.get()).thenReturn(value);
		return supplier;
	}

	/**
	 * Resets the logging provider in {@link TaggedLogger}.
	 */
	private void resetLoggingProvider() {
		Whitebox.setInternalState(TaggedLogger.class, ProviderRegistry.getLoggingProvider());
	}

}
