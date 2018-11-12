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

package org.pmw.tinylog;

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
import org.tinylog.Level;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link Logger}.
 */
@RunWith(Parameterized.class)
@PrepareForTest(Logger.class)
public final class LoggerTest {

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
	public LoggerTest(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
		final boolean warnEnabled, final boolean errorEnabled) {
		this.level = level;
		this.traceEnabled = traceEnabled;
		this.debugEnabled = debugEnabled;
		this.infoEnabled = infoEnabled;
		this.warnEnabled = warnEnabled;
		this.errorEnabled = errorEnabled;
	}

	/**
	 * Returns for all severity levels which severity level are enabled.
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
	 * Mocks the underlying logging provider.
	 */
	@Before
	public void init() {
		loggingProvider = mockLoggingProvider();
	}

	/**
	 * Resets the underlying logging provider.
	 *
	 * @throws Exception
	 *             Failed reseting logging provider
	 */
	@After
	public void reset() throws Exception {
		resetLoggingProvider();
	}

	/**
	 * Verifies evaluating whether a specific severity level is covered by the minimum severity level.
	 *
	 * @throws Exception
	 *             Failed invoking private {@link Logger#isCoveredByMinimumLevel(Level)} method
	 */
	@Test
	public void coveredByMinimumLevel() throws Exception {
		assertThat(isCoveredByMinimumLevel(Level.TRACE)).isEqualTo(traceEnabled);
		assertThat(isCoveredByMinimumLevel(Level.DEBUG)).isEqualTo(debugEnabled);
		assertThat(isCoveredByMinimumLevel(Level.INFO)).isEqualTo(infoEnabled);
		assertThat(isCoveredByMinimumLevel(Level.WARN)).isEqualTo(warnEnabled);
		assertThat(isCoveredByMinimumLevel(Level.ERROR)).isEqualTo(errorEnabled);
	}

	/**
	 * Verifies that all severity level getters will return the minimum enabled severity level.
	 */
	@Test
	public void gettingLevel() {
		String name = level.toString().replace(Level.WARN.toString(), org.pmw.tinylog.Level.WARNING.toString());
		org.pmw.tinylog.Level level = org.pmw.tinylog.Level.valueOf(name);

		assertThat(Logger.getLevel()).isEqualTo(level);
		assertThat(Logger.getLevel(LoggerTest.class.getPackage())).isEqualTo(level);
		assertThat(Logger.getLevel(LoggerTest.class)).isEqualTo(level);
	}

	/**
	 * Verifies that any object such as an integer will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceObject() {
		Logger.trace(42);

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, null, 42, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a text message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessage() {
		Logger.trace("Hello World!");

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, null, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		Logger.trace(supplier);
		verify(supplier, never()).get();

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, null, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndArguments() {
		Logger.trace("Hello {}!", "World");

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, null, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		Logger.trace("The number is {}", supplier);
		verify(supplier, never()).get();

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, null, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceException() {
		Exception exception = new NullPointerException();

		Logger.trace(exception);

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, exception, null, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionWithMessage() {
		Exception exception = new NullPointerException();

		Logger.trace(exception, "Hello World!");

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, exception, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.trace(exception, supplier);

		verify(supplier, never()).get();

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, exception, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		Logger.trace(exception, "Hello {}!", "World");

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, exception, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.trace(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (traceEnabled) {
			verify(loggingProvider).log(2, null, Level.TRACE, exception, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that any object such as an integer will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugObject() {
		Logger.debug(42);

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, null, 42, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessage() {
		Logger.debug("Hello World!");

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, null, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		Logger.debug(supplier);
		verify(supplier, never()).get();

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, null, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndArguments() {
		Logger.debug("Hello {}!", "World");

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, null, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		Logger.debug("The number is {}", supplier);
		verify(supplier, never()).get();

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, null, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugException() {
		Exception exception = new NullPointerException();

		Logger.debug(exception);

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, exception, null, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionWithMessage() {
		Exception exception = new NullPointerException();

		Logger.debug(exception, "Hello World!");

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, exception, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.debug(exception, supplier);

		verify(supplier, never()).get();

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, exception, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		Logger.debug(exception, "Hello {}!", "World");

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, exception, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.debug(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (debugEnabled) {
			verify(loggingProvider).log(2, null, Level.DEBUG, exception, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that any object such as an integer will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoObject() {
		Logger.info(42);

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, null, 42, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a text message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessage() {
		Logger.info("Hello World!");

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, null, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		Logger.info(supplier);
		verify(supplier, never()).get();

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, null, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessageAndArguments() {
		Logger.info("Hello {}!", "World");

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, null, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at {@link Level#INFO
	 * INFO} level.
	 */
	@Test
	public void infoMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		Logger.info("The number is {}", supplier);
		verify(supplier, never()).get();

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, null, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoException() {
		Exception exception = new NullPointerException();

		Logger.info(exception);

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, exception, null, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionWithMessage() {
		Exception exception = new NullPointerException();

		Logger.info(exception, "Hello World!");

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, exception, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.info(exception, supplier);

		verify(supplier, never()).get();

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, exception, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		Logger.info(exception, "Hello {}!", "World");

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, exception, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.info(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (infoEnabled) {
			verify(loggingProvider).log(2, null, Level.INFO, exception, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that any object such as an integer will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnObject() {
		Logger.warn(42);

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, null, 42, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a text message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnMessage() {
		Logger.warn("Hello World!");

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, null, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		Logger.warn(supplier);
		verify(supplier, never()).get();

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, null, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnMessageAndArguments() {
		Logger.warn("Hello {}!", "World");

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, null, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at {@link Level#WARN
	 * WARN} level.
	 */
	@Test
	public void warnMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		Logger.warn("The number is {}", supplier);
		verify(supplier, never()).get();

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, null, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnException() {
		Exception exception = new NullPointerException();

		Logger.warn(exception);

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, exception, null, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionWithMessage() {
		Exception exception = new NullPointerException();

		Logger.warn(exception, "Hello World!");

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, exception, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.warn(exception, supplier);

		verify(supplier, never()).get();

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, exception, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		Logger.warn(exception, "Hello {}!", "World");

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, exception, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.warn(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (warnEnabled) {
			verify(loggingProvider).log(2, null, Level.WARN, exception, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that any object such as an integer will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorObject() {
		Logger.error(42);

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, null, 42, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a text message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessage() {
		Logger.error("Hello World!");

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, null, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		Logger.error(supplier);
		verify(supplier, never()).get();

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, null, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndArguments() {
		Logger.error("Hello {}!", "World");

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, null, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		Logger.error("The number is {}", supplier);
		verify(supplier, never()).get();

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, null, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorException() {
		Exception exception = new NullPointerException();

		Logger.error(exception);

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, exception, null, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionWithMessage() {
		Exception exception = new NullPointerException();

		Logger.error(exception, "Hello World!");

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, exception, "Hello World!", (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.error(exception, supplier);

		verify(supplier, never()).get();

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, exception, supplier, (Object[]) null);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		Logger.error(exception, "Hello {}!", "World");

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, exception, "Hello {}!", "World");
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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

		Logger.error(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (errorEnabled) {
			verify(loggingProvider).log(2, null, Level.ERROR, exception, "The number is {}", supplier);
		} else {
			verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Mocks the logging provider for {@link Logger} and overrides all depending fields.
	 *
	 * @return Mock instance for logging provider
	 */
	private LoggingProvider mockLoggingProvider() {
		LoggingProvider provider = mock(LoggingProvider.class);

		when(provider.getMinimumLevel(null)).thenReturn(level);
		when(provider.isEnabled(anyInt(), isNull(), eq(Level.TRACE))).thenReturn(traceEnabled);
		when(provider.isEnabled(anyInt(), isNull(), eq(Level.DEBUG))).thenReturn(debugEnabled);
		when(provider.isEnabled(anyInt(), isNull(), eq(Level.INFO))).thenReturn(infoEnabled);
		when(provider.isEnabled(anyInt(), isNull(), eq(Level.WARN))).thenReturn(warnEnabled);
		when(provider.isEnabled(anyInt(), isNull(), eq(Level.ERROR))).thenReturn(errorEnabled);

		Whitebox.setInternalState(Logger.class, provider);
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_TRACE", traceEnabled);
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_DEBUG", debugEnabled);
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_INFO", infoEnabled);
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_WARN", warnEnabled);
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_ERROR", errorEnabled);

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
	 * Resets the logging provider and all overridden fields in {@link Logger}.
	 *
	 * @throws Exception
	 *             Failed updating fields
	 */
	private void resetLoggingProvider() throws Exception {
		Whitebox.setInternalState(Logger.class, ProviderRegistry.getLoggingProvider());
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_TRACE", isCoveredByMinimumLevel(Level.TRACE));
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_DEBUG", isCoveredByMinimumLevel(Level.DEBUG));
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_INFO", isCoveredByMinimumLevel(Level.INFO));
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_WARN", isCoveredByMinimumLevel(Level.WARN));
		Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_ERROR", isCoveredByMinimumLevel(Level.ERROR));
	}

	/**
	 * Invokes the private method {@link Logger#isCoveredByMinimumLevel(Level)}.
	 *
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 * @throws Exception
	 *             Failed invoking method
	 */
	private boolean isCoveredByMinimumLevel(final Level level) throws Exception {
		return Whitebox.invokeMethod(Logger.class, "isCoveredByMinimumLevel", level);
	}

}
