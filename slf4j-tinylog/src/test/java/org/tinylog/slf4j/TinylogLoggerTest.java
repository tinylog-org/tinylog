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

package org.tinylog.slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.slf4j.Marker;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.helpers.BasicMarkerFactory;
import org.tinylog.Level;
import org.tinylog.format.LegacyMessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

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
 * Tests for {@link TinylogLogger}.
 */
@RunWith(Enclosed.class)
public final class TinylogLoggerTest {

	/**
	 * Test logging without using a {@link Marker}.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(TinylogLogger.class)
	public static final class NoneMarker {

		/**
		 * Activates PowerMock (alternative to {@link PowerMockRunner}).
		 */
		@Rule
		public PowerMockRule rule = new PowerMockRule();

		private Level level;

		private boolean traceEnabled;
		private boolean debugEnabled;
		private boolean infoEnabled;
		private boolean warnEnabled;
		private boolean errorEnabled;

		private LoggingProvider provider;
		private TinylogLogger logger;

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
		public NoneMarker(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
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
		 * @return Each object array contains the severity level itself and five booleans for {@link Level#TRACE TRACE}
		 *         ... {@link Level#ERROR ERROR} to determine whether these severity levels are enabled
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
			provider = mock(LoggingProvider.class);
			when(provider.getMinimumLevel(null)).thenReturn(level);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.TRACE))).thenReturn(traceEnabled);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.DEBUG))).thenReturn(debugEnabled);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.INFO))).thenReturn(infoEnabled);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.WARN))).thenReturn(warnEnabled);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.ERROR))).thenReturn(errorEnabled);

			logger = new TinylogLogger(TinylogLoggerTest.class.getName());

			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_ERROR", errorEnabled);

			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_ERROR", errorEnabled);

			Whitebox.setInternalState(TinylogLogger.class, provider);
		}

		/**
		 * Resets the underlying logging provider.
		 */
		@After
		public void reset() {
			Whitebox.setInternalState(TinylogLogger.class, ProviderRegistry.getLoggingProvider());
		}

		/**
		 * Verifies that the configured name is returned.
		 */
		@Test
		public void getName() {
			assertThat(logger.getName()).isEqualTo(TinylogLoggerTest.class.getName());
		}

		/**
		 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
		 */
		@Test
		public void isTraceEnabled() {
			assertThat(logger.isTraceEnabled()).isEqualTo(traceEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#TRACE TRACE} level.
		 */
		@Test
		public void tracePlainTextMessage() {
			logger.trace("Hello World!");

			if (traceEnabled) {
				verify(provider).log(2, null, Level.TRACE, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithSingleArgument() {
			logger.trace("Hello {}!", "World");

			if (traceEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithTwoArguments() {
			logger.trace("{} = {}", "magic", 42);

			if (traceEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.trace("{} = {}", "magic", 42, exception);

			if (traceEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#TRACE TRACE}
		 * level.
		 */
		@Test
		public void tracePlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.trace("Hello World!", exception);

			if (traceEnabled) {
				verify(provider).log(2, null, Level.TRACE, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
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
		 * Verifies that a plain text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
		 */
		@Test
		public void debugPlainTextMessage() {
			logger.debug("Hello World!");

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithSingleArgument() {
			logger.debug("Hello {}!", "World");

			if (debugEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithTwoArguments() {
			logger.debug("{} = {}", "magic", 42);

			if (debugEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.debug("{} = {}", "magic", 42, exception);

			if (debugEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#DEBUG DEBUG}
		 * level.
		 */
		@Test
		public void debugPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.debug("Hello World!", exception);

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
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
		 * Verifies that a plain text message will be logged correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoPlainTextMessage() {
			logger.info("Hello World!");

			if (infoEnabled) {
				verify(provider).log(2, null, Level.INFO, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#INFO
		 * INFO} level.
		 */
		@Test
		public void infoFormattedMessageWithSingleArgument() {
			logger.info("Hello {}!", "World");

			if (infoEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#INFO INFO}
		 * level.
		 */
		@Test
		public void infoFormattedMessageWithTwoArguments() {
			logger.info("{} = {}", "magic", 42);

			if (infoEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#INFO
		 * INFO} level.
		 */
		@Test
		public void infoFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.info("{} = {}", "magic", 42, exception);

			if (infoEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.INFO), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#INFO INFO}
		 * level.
		 */
		@Test
		public void infoPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.info("Hello World!", exception);

			if (infoEnabled) {
				verify(provider).log(2, null, Level.INFO, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
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
		 * Verifies that a plain text message will be logged correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnPlainTextMessage() {
			logger.warn("Hello World!");

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#WARN
		 * WARN} level.
		 */
		@Test
		public void warnFormattedMessageWithSingleArgument() {
			logger.warn("Hello {}!", "World");

			if (warnEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#WARN WARN}
		 * level.
		 */
		@Test
		public void warnFormattedMessageWithTwoArguments() {
			logger.warn("{} = {}", "magic", 42);

			if (warnEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#WARN
		 * WARN} level.
		 */
		@Test
		public void warnFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.warn("{} = {}", "magic", 42, exception);

			if (warnEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.WARN), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#WARN WARN}
		 * level.
		 */
		@Test
		public void warnPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.warn("Hello World!", exception);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
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
		 * Verifies that a plain text message will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void errorPlainTextMessage() {
			logger.error("Hello World!");

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithSingleArgument() {
			logger.error("Hello {}!", "World");

			if (errorEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithTwoArguments() {
			logger.error("{} = {}", "magic", 42);

			if (errorEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.error("{} = {}", "magic", 42, exception);

			if (errorEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#ERROR ERROR}
		 * level.
		 */
		@Test
		public void errorPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.error("Hello World!", exception);

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that location aware logging is fully supported.
		 */
		@Test
		public void logLocationAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			Object[] arguments = new Object[] { "World" };
			RuntimeException exception = new RuntimeException();

			logger.log(null, TinylogLogger.class.getName(), levelInt, "Hello {}!", arguments, exception);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(TinylogLogger.class.getName()), isNull(), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

		/**
		 * Verifies that event aware logging is fully supported.
		 */
		@Test
		public void logEventAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			RuntimeException exception = new RuntimeException();

			DefaultLoggingEvent event = new DefaultLoggingEvent(org.slf4j.event.Level.intToLevel(levelInt), logger);
			event.setCallerBoundary(TinylogLogger.class.getName());
			event.setThrowable(exception);
			event.setMessage("Hello {}!");
			event.addArgument("World");

			logger.log(event);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(TinylogLogger.class.getName()), isNull(), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

	}

	/**
	 * Test logging with using a real non-null {@link Marker}.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(TinylogLogger.class)
	public static final class RealMarker {

		private static final String TAG = "test";

		/**
		 * Activates PowerMock (alternative to {@link PowerMockRunner}).
		 */
		@Rule
		public PowerMockRule rule = new PowerMockRule();

		private Marker marker;
		private Level level;

		private boolean traceEnabled;
		private boolean debugEnabled;
		private boolean infoEnabled;
		private boolean warnEnabled;
		private boolean errorEnabled;

		private LoggingProvider provider;
		private TinylogLogger logger;

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
		public RealMarker(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
			final boolean warnEnabled, final boolean errorEnabled) {
			this.marker = new BasicMarkerFactory().getDetachedMarker(TAG);
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
		 * @return Each object array contains the severity level itself and five booleans for {@link Level#TRACE TRACE}
		 *         ... {@link Level#ERROR ERROR} to determine whether these severity levels are enabled
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
			provider = mock(LoggingProvider.class);

			when(provider.getMinimumLevel(null)).thenReturn(Level.OFF);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.TRACE))).thenReturn(false);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.DEBUG))).thenReturn(false);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.INFO))).thenReturn(false);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.WARN))).thenReturn(false);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.ERROR))).thenReturn(false);

			when(provider.getMinimumLevel(TAG)).thenReturn(level);
			when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.TRACE))).thenReturn(traceEnabled);
			when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.DEBUG))).thenReturn(debugEnabled);
			when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.INFO))).thenReturn(infoEnabled);
			when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.WARN))).thenReturn(warnEnabled);
			when(provider.isEnabled(anyInt(), eq(TAG), eq(Level.ERROR))).thenReturn(errorEnabled);

			logger = new TinylogLogger(TinylogLoggerTest.class.getName());

			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_ERROR", errorEnabled);

			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_TRACE", false);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG", false);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_INFO", false);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_WARN", false);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_ERROR", false);

			Whitebox.setInternalState(TinylogLogger.class, provider);
		}

		/**
		 * Resets the underlying logging provider.
		 */
		@After
		public void reset() {
			Whitebox.setInternalState(TinylogLogger.class, ProviderRegistry.getLoggingProvider());
		}

		/**
		 * Verifies that the configured name is returned.
		 */
		@Test
		public void getName() {
			assertThat(logger.getName()).isEqualTo(TinylogLoggerTest.class.getName());
		}

		/**
		 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
		 */
		@Test
		public void isTraceEnabled() {
			assertThat(logger.isTraceEnabled(marker)).isEqualTo(traceEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#TRACE TRACE} level.
		 */
		@Test
		public void tracePlainTextMessage() {
			logger.trace(marker, "Hello World!");

			if (traceEnabled) {
				verify(provider).log(2, TAG, Level.TRACE, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithSingleArgument() {
			logger.trace(marker, "Hello {}!", "World");

			if (traceEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.TRACE), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithTwoArguments() {
			logger.trace(marker, "{} = {}", "magic", 42);

			if (traceEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.TRACE), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.trace(marker, "{} = {}", "magic", 42, exception);

			if (traceEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.TRACE), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#TRACE TRACE}
		 * level.
		 */
		@Test
		public void tracePlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.trace(marker, "Hello World!", exception);

			if (traceEnabled) {
				verify(provider).log(2, TAG, Level.TRACE, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies evaluating whether {@link Level#DEBUG DEBUG} level is enabled.
		 */
		@Test
		public void isDebugEnabled() {
			assertThat(logger.isDebugEnabled(marker)).isEqualTo(debugEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
		 */
		@Test
		public void debugPlainTextMessage() {
			logger.debug(marker, "Hello World!");

			if (debugEnabled) {
				verify(provider).log(2, TAG, Level.DEBUG, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithSingleArgument() {
			logger.debug(marker, "Hello {}!", "World");

			if (debugEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.DEBUG), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithTwoArguments() {
			logger.debug(marker, "{} = {}", "magic", 42);

			if (debugEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.DEBUG), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.debug(marker, "{} = {}", "magic", 42, exception);

			if (debugEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.DEBUG), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#DEBUG DEBUG}
		 * level.
		 */
		@Test
		public void debugPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.debug(marker, "Hello World!", exception);

			if (debugEnabled) {
				verify(provider).log(2, TAG, Level.DEBUG, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies evaluating whether {@link Level#INFO INFO} level is enabled.
		 */
		@Test
		public void isInfoEnabled() {
			assertThat(logger.isInfoEnabled(marker)).isEqualTo(infoEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoPlainTextMessage() {
			logger.info(marker, "Hello World!");

			if (infoEnabled) {
				verify(provider).log(2, TAG, Level.INFO, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#INFO
		 * INFO} level.
		 */
		@Test
		public void infoFormattedMessageWithSingleArgument() {
			logger.info(marker, "Hello {}!", "World");

			if (infoEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.INFO), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#INFO INFO}
		 * level.
		 */
		@Test
		public void infoFormattedMessageWithTwoArguments() {
			logger.info(marker, "{} = {}", "magic", 42);

			if (infoEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.INFO), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#INFO
		 * INFO} level.
		 */
		@Test
		public void infoFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.info(marker, "{} = {}", "magic", 42, exception);

			if (infoEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.INFO), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#INFO INFO}
		 * level.
		 */
		@Test
		public void infoPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.info(marker, "Hello World!", exception);

			if (infoEnabled) {
				verify(provider).log(2, TAG, Level.INFO, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies evaluating whether {@link Level#WARN WARN} level is enabled.
		 */
		@Test
		public void isWarnEnabled() {
			assertThat(logger.isWarnEnabled(marker)).isEqualTo(warnEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnPlainTextMessage() {
			logger.warn(marker, "Hello World!");

			if (warnEnabled) {
				verify(provider).log(2, TAG, Level.WARN, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#WARN
		 * WARN} level.
		 */
		@Test
		public void warnFormattedMessageWithSingleArgument() {
			logger.warn(marker, "Hello {}!", "World");

			if (warnEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.WARN), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#WARN WARN}
		 * level.
		 */
		@Test
		public void warnFormattedMessageWithTwoArguments() {
			logger.warn(marker, "{} = {}", "magic", 42);

			if (warnEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.WARN), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#WARN
		 * WARN} level.
		 */
		@Test
		public void warnFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.warn(marker, "{} = {}", "magic", 42, exception);

			if (warnEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.WARN), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#WARN WARN}
		 * level.
		 */
		@Test
		public void warnPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.warn(marker, "Hello World!", exception);

			if (warnEnabled) {
				verify(provider).log(2, TAG, Level.WARN, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies evaluating whether {@link Level#ERROR ERROR} level is enabled.
		 */
		@Test
		public void isErrorEnabled() {
			assertThat(logger.isErrorEnabled(marker)).isEqualTo(errorEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void errorPlainTextMessage() {
			logger.error(marker, "Hello World!");

			if (errorEnabled) {
				verify(provider).log(2, TAG, Level.ERROR, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithSingleArgument() {
			logger.error(marker, "Hello {}!", "World");

			if (errorEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.ERROR), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithTwoArguments() {
			logger.error(marker, "{} = {}", "magic", 42);

			if (errorEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.ERROR), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.error(marker, "{} = {}", "magic", 42, exception);

			if (errorEnabled) {
				verify(provider).log(eq(2), eq(TAG), eq(Level.ERROR), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#ERROR ERROR}
		 * level.
		 */
		@Test
		public void errorPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.error(marker, "Hello World!", exception);

			if (errorEnabled) {
				verify(provider).log(2, TAG, Level.ERROR, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that location aware logging is fully supported.
		 */
		@Test
		public void logLocationAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			Object[] arguments = new Object[] { "World" };
			RuntimeException exception = new RuntimeException();

			logger.log(marker, TinylogLogger.class.getName(), levelInt, "Hello {}!", arguments, exception);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(TinylogLogger.class.getName()), eq(TAG), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

		/**
		 * Verifies that event aware logging is fully supported.
		 */
		@Test
		public void logEventAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			RuntimeException exception = new RuntimeException();

			DefaultLoggingEvent event = new DefaultLoggingEvent(org.slf4j.event.Level.intToLevel(levelInt), logger);
			event.setCallerBoundary(TinylogLogger.class.getName());
			event.addMarker(marker);
			event.setThrowable(exception);
			event.setMessage("Hello {}!");
			event.addArgument("World");

			logger.log(event);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(TinylogLogger.class.getName()), eq(TAG), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

	}

	/**
	 * Test logging with using a null value as {@link Marker}.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(TinylogLogger.class)
	public static final class NullMarker {

		/**
		 * Activates PowerMock (alternative to {@link PowerMockRunner}).
		 */
		@Rule
		public PowerMockRule rule = new PowerMockRule();

		private Level level;

		private boolean traceEnabled;
		private boolean debugEnabled;
		private boolean infoEnabled;
		private boolean warnEnabled;
		private boolean errorEnabled;

		private LoggingProvider provider;
		private TinylogLogger logger;

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
		public NullMarker(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
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
		 * @return Each object array contains the severity level itself and five booleans for {@link Level#TRACE TRACE}
		 *         ... {@link Level#ERROR ERROR} to determine whether these severity levels are enabled
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
			provider = mock(LoggingProvider.class);

			when(provider.getMinimumLevel(null)).thenReturn(level);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.TRACE))).thenReturn(traceEnabled);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.DEBUG))).thenReturn(debugEnabled);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.INFO))).thenReturn(infoEnabled);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.WARN))).thenReturn(warnEnabled);
			when(provider.isEnabled(anyInt(), isNull(), eq(Level.ERROR))).thenReturn(errorEnabled);

			logger = new TinylogLogger(TinylogLoggerTest.class.getName());

			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_ERROR", errorEnabled);

			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_TRACE", false);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG", false);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_INFO", false);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_WARN", false);
			Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_ERROR", false);

			Whitebox.setInternalState(TinylogLogger.class, provider);
		}

		/**
		 * Resets the underlying logging provider.
		 */
		@After
		public void reset() {
			Whitebox.setInternalState(TinylogLogger.class, ProviderRegistry.getLoggingProvider());
		}

		/**
		 * Verifies that the configured name is returned.
		 */
		@Test
		public void getName() {
			assertThat(logger.getName()).isEqualTo(TinylogLoggerTest.class.getName());
		}

		/**
		 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
		 */
		@Test
		public void isTraceEnabled() {
			assertThat(logger.isTraceEnabled(null)).isEqualTo(traceEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#TRACE TRACE} level.
		 */
		@Test
		public void tracePlainTextMessage() {
			logger.trace((Marker) null, "Hello World!");

			if (traceEnabled) {
				verify(provider).log(2, null, Level.TRACE, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithSingleArgument() {
			logger.trace((Marker) null, "Hello {}!", "World");

			if (traceEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithTwoArguments() {
			logger.trace(null, "{} = {}", "magic", 42);

			if (traceEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#TRACE
		 * TRACE} level.
		 */
		@Test
		public void traceFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.trace((Marker) null, "{} = {}", "magic", 42, exception);

			if (traceEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#TRACE TRACE}
		 * level.
		 */
		@Test
		public void tracePlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.trace((Marker) null, "Hello World!", exception);

			if (traceEnabled) {
				verify(provider).log(2, null, Level.TRACE, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies evaluating whether {@link Level#DEBUG DEBUG} level is enabled.
		 */
		@Test
		public void isDebugEnabled() {
			assertThat(logger.isDebugEnabled(null)).isEqualTo(debugEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
		 */
		@Test
		public void debugPlainTextMessage() {
			logger.debug((Marker) null, "Hello World!");

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithSingleArgument() {
			logger.debug((Marker) null, "Hello {}!", "World");

			if (debugEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithTwoArguments() {
			logger.debug(null, "{} = {}", "magic", 42);

			if (debugEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#DEBUG
		 * DEBUG} level.
		 */
		@Test
		public void debugFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.debug((Marker) null, "{} = {}", "magic", 42, exception);

			if (debugEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#DEBUG DEBUG}
		 * level.
		 */
		@Test
		public void debugPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.debug((Marker) null, "Hello World!", exception);

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies evaluating whether {@link Level#INFO INFO} level is enabled.
		 */
		@Test
		public void isInfoEnabled() {
			assertThat(logger.isInfoEnabled(null)).isEqualTo(infoEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoPlainTextMessage() {
			logger.info((Marker) null, "Hello World!");

			if (infoEnabled) {
				verify(provider).log(2, null, Level.INFO, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#INFO
		 * INFO} level.
		 */
		@Test
		public void infoFormattedMessageWithSingleArgument() {
			logger.info((Marker) null, "Hello {}!", "World");

			if (infoEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#INFO INFO}
		 * level.
		 */
		@Test
		public void infoFormattedMessageWithTwoArguments() {
			logger.info(null, "{} = {}", "magic", 42);

			if (infoEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#INFO
		 * INFO} level.
		 */
		@Test
		public void infoFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.info((Marker) null, "{} = {}", "magic", 42, exception);

			if (infoEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.INFO), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#INFO INFO}
		 * level.
		 */
		@Test
		public void infoPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.info((Marker) null, "Hello World!", exception);

			if (infoEnabled) {
				verify(provider).log(2, null, Level.INFO, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies evaluating whether {@link Level#WARN WARN} level is enabled.
		 */
		@Test
		public void isWarnEnabled() {
			assertThat(logger.isWarnEnabled(null)).isEqualTo(warnEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnPlainTextMessage() {
			logger.warn((Marker) null, "Hello World!");

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#WARN
		 * WARN} level.
		 */
		@Test
		public void warnFormattedMessageWithSingleArgument() {
			logger.warn((Marker) null, "Hello {}!", "World");

			if (warnEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#WARN WARN}
		 * level.
		 */
		@Test
		public void warnFormattedMessageWithTwoArguments() {
			logger.warn(null, "{} = {}", "magic", 42);

			if (warnEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#WARN
		 * WARN} level.
		 */
		@Test
		public void warnFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.warn((Marker) null, "{} = {}", "magic", 42, exception);

			if (warnEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.WARN), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#WARN WARN}
		 * level.
		 */
		@Test
		public void warnPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.warn((Marker) null, "Hello World!", exception);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies evaluating whether {@link Level#ERROR ERROR} level is enabled.
		 */
		@Test
		public void isErrorEnabled() {
			assertThat(logger.isErrorEnabled(null)).isEqualTo(errorEnabled);
		}

		/**
		 * Verifies that a plain text message will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void errorPlainTextMessage() {
			logger.error((Marker) null, "Hello World!");

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, null, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with a single argument will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithSingleArgument() {
			logger.error((Marker) null, "Hello {}!", "World");

			if (errorEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(LegacyMessageFormatter.class), eq("Hello {}!"),
						eq("World"));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with two arguments will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithTwoArguments() {
			logger.error(null, "{} = {}", "magic", 42);

			if (errorEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a formatted text message with multiple arguments will be logged correctly at {@link Level#ERROR
		 * ERROR} level.
		 */
		@Test
		public void errorFormattedMessageWithMultipleArguments() {
			RuntimeException exception = new RuntimeException();

			logger.error((Marker) null, "{} = {}", "magic", 42, exception);

			if (errorEnabled) {
				verify(provider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), any(LegacyMessageFormatter.class), eq("{} = {}"),
						eq("magic"), eq(42), same(exception));
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}

		/**
		 * Verifies that a plain text message with an exception will be logged correctly at {@link Level#ERROR ERROR}
		 * level.
		 */
		@Test
		public void errorPlainTextMessageWithException() {
			RuntimeException exception = new RuntimeException();

			logger.error((Marker) null, "Hello World!", exception);

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, exception, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			}
		}


		/**
		 * Verifies that location aware logging is fully supported.
		 */
		@Test
		public void logLocationAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			Object[] arguments = new Object[] { "World" };
			RuntimeException exception = new RuntimeException();

			logger.log(null, TinylogLogger.class.getName(), levelInt, "Hello {}!", arguments, exception);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(TinylogLogger.class.getName()), isNull(), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

		/**
		 * Verifies that event aware logging is fully supported.
		 */
		@Test
		public void logEventAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			RuntimeException exception = new RuntimeException();

			DefaultLoggingEvent event = new DefaultLoggingEvent(org.slf4j.event.Level.intToLevel(levelInt), logger);
			event.setCallerBoundary(TinylogLogger.class.getName());
			event.setThrowable(exception);
			event.setMessage("Hello {}!");
			event.addArgument("World");

			logger.log(event);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(TinylogLogger.class.getName()), isNull(), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

	}

}
