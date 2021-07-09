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
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
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
import org.tinylog.util.LevelConfiguration;

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
 * Tests for {@link Logger}.
 */
@RunWith(Enclosed.class)
public final class LoggerTest {

	/**
	 * Tests for logging methods.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(Logger.class)
	public static final class Logging {

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

		/**
		 * Information about the severity level under test.
		 */
		@Parameterized.Parameter
		public LevelConfiguration levelConfiguration;

		private LoggingProvider loggingProvider;

		/**
		 * Returns for all severity levels which severity levels are enabled.
		 *
		 * @return Each object array contains the severity level itself and five booleans for {@link Level#TRACE TRACE}
		 *         ... {@link Level#ERROR ERROR} to determine whether these severity levels are enabled
		 */
		@Parameters(name = "{0}")
		public static Collection<Object[]> getLevels() {
			List<Object[]> levels = new ArrayList<>();
			for (LevelConfiguration configuration: LevelConfiguration.AVAILABLE_LEVELS) {
				levels.add(new Object[] {configuration});
			}

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
		 *             Failed resetting logging provider
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
			assertThat(isCoveredByMinimumLevel(Level.TRACE)).isEqualTo(levelConfiguration.isTraceEnabled());
			assertThat(isCoveredByMinimumLevel(Level.DEBUG)).isEqualTo(levelConfiguration.isDebugEnabled());
			assertThat(isCoveredByMinimumLevel(Level.INFO)).isEqualTo(levelConfiguration.isInfoEnabled());
			assertThat(isCoveredByMinimumLevel(Level.WARN)).isEqualTo(levelConfiguration.isWarnEnabled());
			assertThat(isCoveredByMinimumLevel(Level.ERROR)).isEqualTo(levelConfiguration.isErrorEnabled());
		}

		/**
		 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
		 */
		@Test
		public void isTraceEnabled() {
			assertThat(Logger.isTraceEnabled()).isEqualTo(levelConfiguration.isTraceEnabled());
		}

		/**
		 * Verifies that a plain message object will be logged correctly at {@link Level#TRACE TRACE} level.
		 */
		@Test
		public void traceObject() {
			Logger.trace("Hello World!");

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), isNull(), eq("Hello World!"), isNull());
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
			Logger.trace(supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), isNull(), same(supplier), isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at {@link Level#TRACE TRACE} level.
		 */
		@Test
		public void traceMessageAndArguments() {
			Logger.trace("Hello {}!", "World");

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(AdvancedMessageFormatter.class),
					eq("Hello {}!"), eq("World"));
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
			Logger.trace("The number is {}", supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(AdvancedMessageFormatter.class),
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

			Logger.trace(exception);

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), isNull(), isNull(), isNull());
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

			Logger.trace(exception, "Hello World!");

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), isNull(), eq("Hello World!"),
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

			Logger.trace(exception, supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), isNull(), same(supplier),
						isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceExceptionWithMessageAndArguments() {
			Exception exception = new NullPointerException();

			Logger.trace(exception, "Hello {}!", "World");

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), any(AdvancedMessageFormatter.class),
					eq("Hello {}!"), eq("World"));
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at {@link Level#TRACE TRACE} level.
		 */
		@Test
		public void traceExceptionWithMessageAndLazyArguments() {
			Exception exception = new NullPointerException();
			Supplier<Integer> supplier = mockSupplier(42);

			Logger.trace(exception, "The number is {}", supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isTraceEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), any(AdvancedMessageFormatter.class),
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
			assertThat(Logger.isDebugEnabled()).isEqualTo(levelConfiguration.isDebugEnabled());
		}

		/**
		 * Verifies that a plain message object will be logged correctly at {@link Level#DEBUG DEBUG} level.
		 */
		@Test
		public void debugObject() {
			Logger.debug("Hello World!");

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), isNull(), eq("Hello World!"), isNull());
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
			Logger.debug(supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), isNull(), same(supplier), isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
		 */
		@Test
		public void debugMessageAndArguments() {
			Logger.debug("Hello {}!", "World");

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(AdvancedMessageFormatter.class),
					eq("Hello {}!"), eq("World"));
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
			Logger.debug("The number is {}", supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(AdvancedMessageFormatter.class),
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

			Logger.debug(exception);

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), isNull(), isNull(), isNull());
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

			Logger.debug(exception, "Hello World!");

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), isNull(), eq("Hello World!"),
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

			Logger.debug(exception, supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), isNull(), same(supplier),
						isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugExceptionWithMessageAndArguments() {
			Exception exception = new NullPointerException();

			Logger.debug(exception, "Hello {}!", "World");

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), any(AdvancedMessageFormatter.class),
					eq("Hello {}!"), eq("World"));
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at {@link Level#DEBUG DEBUG} level.
		 */
		@Test
		public void debugExceptionWithMessageAndLazyArguments() {
			Exception exception = new NullPointerException();
			Supplier<Integer> supplier = mockSupplier(42);

			Logger.debug(exception, "The number is {}", supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isDebugEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), any(AdvancedMessageFormatter.class),
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
			assertThat(Logger.isInfoEnabled()).isEqualTo(levelConfiguration.isInfoEnabled());
		}

		/**
		 * Verifies that a plain message object will be logged correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoObject() {
			Logger.info("Hello World!");

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), isNull(), isNull(), eq("Hello World!"), isNull());
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
			Logger.info(supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), isNull(), isNull(), same(supplier), isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoMessageAndArguments() {
			Logger.info("Hello {}!", "World");

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
					eq("World"));
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
		 * {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoMessageAndLazyArguments() {
			Supplier<Integer> supplier = mockSupplier(42);
			Logger.info("The number is {}", supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(AdvancedMessageFormatter.class),
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

			Logger.info(exception);

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), same(exception), isNull(), isNull(), isNull());
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

			Logger.info(exception, "Hello World!");

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), same(exception), isNull(), eq("Hello World!"),
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

			Logger.info(exception, supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), same(exception), isNull(), same(supplier),
						isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#INFO
		 * INFO} level.
		 */
		@Test
		public void infoExceptionWithMessageAndArguments() {
			Exception exception = new NullPointerException();

			Logger.info(exception, "Hello {}!", "World");

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), same(exception), any(AdvancedMessageFormatter.class),
					eq("Hello {}!"), eq("World"));
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoExceptionWithMessageAndLazyArguments() {
			Exception exception = new NullPointerException();
			Supplier<Integer> supplier = mockSupplier(42);

			Logger.info(exception, "The number is {}", supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isInfoEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.INFO), same(exception), any(AdvancedMessageFormatter.class),
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
			assertThat(Logger.isWarnEnabled()).isEqualTo(levelConfiguration.isWarnEnabled());
		}

		/**
		 * Verifies that a plain message object will be logged correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnObject() {
			Logger.warn("Hello World!");

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), isNull(), isNull(), eq("Hello World!"), isNull());
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
			Logger.warn(supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), isNull(), isNull(), same(supplier), isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnMessageAndArguments() {
			Logger.warn("Hello {}!", "World");

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
					eq("World"));
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
		 * {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnMessageAndLazyArguments() {
			Supplier<Integer> supplier = mockSupplier(42);
			Logger.warn("The number is {}", supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(AdvancedMessageFormatter.class),
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

			Logger.warn(exception);

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), same(exception), isNull(), isNull(), isNull());
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

			Logger.warn(exception, "Hello World!");

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), same(exception), isNull(), eq("Hello World!"),
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

			Logger.warn(exception, supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), same(exception), isNull(), same(supplier),
						isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#WARN
		 * WARN} level.
		 */
		@Test
		public void warnExceptionWithMessageAndArguments() {
			Exception exception = new NullPointerException();

			Logger.warn(exception, "Hello {}!", "World");

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), same(exception), any(AdvancedMessageFormatter.class),
					eq("Hello {}!"), eq("World"));
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnExceptionWithMessageAndLazyArguments() {
			Exception exception = new NullPointerException();
			Supplier<Integer> supplier = mockSupplier(42);

			Logger.warn(exception, "The number is {}", supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isWarnEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.WARN), same(exception), any(AdvancedMessageFormatter.class),
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
			assertThat(Logger.isErrorEnabled()).isEqualTo(levelConfiguration.isErrorEnabled());
		}

		/**
		 * Verifies that a plain message object will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void errorObject() {
			Logger.error("Hello World!");

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), isNull(), eq("Hello World!"), isNull());
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
			Logger.error(supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), isNull(), same(supplier), isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void errorMessageAndArguments() {
			Logger.error("Hello {}!", "World");

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(AdvancedMessageFormatter.class),
					eq("Hello {}!"), eq("World"));
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
			Logger.error("The number is {}", supplier);
			verify(supplier, never()).get();

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(AdvancedMessageFormatter.class),
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

			Logger.error(exception);

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), isNull(), isNull(), isNull());
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

			Logger.error(exception, "Hello World!");

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), isNull(), eq("Hello World!"),
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

			Logger.error(exception, supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), isNull(), same(supplier),
						isNull());
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorExceptionWithMessageAndArguments() {
			Exception exception = new NullPointerException();

			Logger.error(exception, "Hello {}!", "World");

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), any(AdvancedMessageFormatter.class),
					eq("Hello {}!"), eq("World"));
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void errorExceptionWithMessageAndLazyArguments() {
			Exception exception = new NullPointerException();
			Supplier<Integer> supplier = mockSupplier(42);

			Logger.error(exception, "The number is {}", supplier);

			verify(supplier, never()).get();

			if (levelConfiguration.isErrorEnabled()) {
				verify(loggingProvider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), any(AdvancedMessageFormatter.class),
					eq("The number is {}"), same(supplier));
			} else {
				verify(loggingProvider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Mocks the logging provider for {@link Logger} and overrides all depending fields.
		 *
		 * @return Mock instance for logging provider
		 */
		private LoggingProvider mockLoggingProvider() {
			LoggingProvider provider = mock(LoggingProvider.class);

			when(provider.getMinimumLevel(null)).thenReturn(levelConfiguration.getLevel());
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.TRACE))).thenReturn(levelConfiguration.isTraceEnabled());
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.DEBUG))).thenReturn(levelConfiguration.isDebugEnabled());
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.INFO))).thenReturn(levelConfiguration.isInfoEnabled());
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.WARN))).thenReturn(levelConfiguration.isWarnEnabled());
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.ERROR))).thenReturn(levelConfiguration.isErrorEnabled());

			Whitebox.setInternalState(Logger.class, provider);
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_TRACE", levelConfiguration.isTraceEnabled());
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_DEBUG", levelConfiguration.isDebugEnabled());
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_INFO", levelConfiguration.isInfoEnabled());
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_WARN", levelConfiguration.isWarnEnabled());
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_ERROR", levelConfiguration.isErrorEnabled());

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

	/**
	 * Tests for receiving tagged logger instances.
	 */
	@RunWith(PowerMockRunner.class)
	@PrepareForTest(TaggedLogger.class)
	public static final class Tagging {

		/**
		 * Redirects and collects system output streams.
		 */
		@Rule
		public final SystemStreamCollector systemStream = new SystemStreamCollector(false);

		/**
		 * Verifies that {@link Logger#tag(String)} returns the same untagged instance of {@link TaggedLogger} for
		 * {@code null} and empty strings.
		 */
		@Test
		public void untagged() {
			TaggedLogger logger = Logger.tag(null);

			assertThat(logger).isNotNull()
				.isSameAs(Logger.tag(""))
				.isSameAs(Logger.tags())
				.isSameAs(Logger.tags(null))
				.isSameAs(Logger.tags(null, null))
				.isSameAs(Logger.tags(null, ""))
				.isSameAs(Logger.tags("", ""));
			assertThat(Whitebox.<Set<String>>getInternalState(logger, "tags")).hasSize(1).containsOnlyNulls();
		}

		/**
		 * Verifies that {@link Logger#tag(String)} returns the same tagged instance of {@link TaggedLogger} for each
		 * tag.
		 */
		@Test
		public void tagged() {
			TaggedLogger logger = Logger.tag("test");

			assertThat(logger).isNotNull().isSameAs(Logger.tag("test")).isSameAs(Logger.tags("test")).isNotSameAs(Logger.tag("other"));
			assertThat(Whitebox.<Set<String>>getInternalState(logger, "tags")).containsOnly("test");
		}

		/**
		 * Verifies that {@link Logger#tags(String...)} returns the same tagged instance of {@link TaggedLogger} for each
		 * set of tags with more than one tag or if the same tag is repeated multiple times.
		 */
		@Test
		public void taggedMultiple() {
			TaggedLogger logger = Logger.tags("test", "more", "extra");

			assertThat(logger).isNotNull()
				.isSameAs(Logger.tags("extra", "more", "test"))
				.isSameAs(Logger.tags("more", "test", "extra", "more", "extra", "test"))
				.isNotSameAs(Logger.tag("other"))
				.isNotSameAs(Logger.tags("test", "more"));
			assertThat(Whitebox.<Set<String>>getInternalState(logger, "tags")).containsOnly("test", "more", "extra");
		}

		/**
		 * Verifies that {@link Logger#tags(String...)} with "untagged" mixed with other tags, returns the same tagged instance of
		 * {@link TaggedLogger}.
		 */
		@Test
		public void taggedMultipleWithUntagged() {
			TaggedLogger logger = Logger.tags("test", null, "more");

			assertThat(logger).isNotNull()
				.isSameAs(Logger.tags(null, "more", "test"))
				.isSameAs(Logger.tags("", "more", "test"))
				.isSameAs(Logger.tags("more", "test", null, "more", null, "test"))
				.isSameAs(Logger.tags("more", "test", null, "more", "", "test"))
				.isSameAs(Logger.tags("more", "test", "", "more", "", "test"))
				.isNotSameAs(Logger.tag("other"))
				.isNotSameAs(Logger.tags("test", "more"))
				.isNotSameAs(Logger.tag(null));
			assertThat(Whitebox.<Set<String>>getInternalState(logger, "tags")).containsOnly("test", null, "more");
		}
	}

}
