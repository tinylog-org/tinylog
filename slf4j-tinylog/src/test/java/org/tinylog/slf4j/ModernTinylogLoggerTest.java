/*
 * Copyright 2023 Martin Winandy
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
 * Tests for {@link ModernTinylogLogger}.
 */
@RunWith(Enclosed.class)
public final class ModernTinylogLoggerTest {

	/**
	 * Test logging without using a {@link Marker}.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(ModernTinylogLogger.class)
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
		private ModernTinylogLogger logger;

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

			logger = new ModernTinylogLogger(ModernTinylogLoggerTest.class.getName());

			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_ERROR", errorEnabled);

			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_ERROR", errorEnabled);

			Whitebox.setInternalState(AbstractTinylogLogger.class, provider);
		}

		/**
		 * Resets the underlying logging provider.
		 */
		@After
		public void reset() {
			Whitebox.setInternalState(AbstractTinylogLogger.class, ProviderRegistry.getLoggingProvider());
		}

		/**
		 * Verifies that the configured name is returned.
		 */
		@Test
		public void getName() {
			assertThat(logger.getName()).isEqualTo(ModernTinylogLoggerTest.class.getName());
		}

		/**
		 * Verifies that event aware logging is fully supported.
		 */
		@Test
		public void logEventAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			RuntimeException exception = new RuntimeException();

			DefaultLoggingEvent event = new DefaultLoggingEvent(org.slf4j.event.Level.intToLevel(levelInt), logger);
			event.setCallerBoundary(ModernTinylogLogger.class.getName());
			event.setThrowable(exception);
			event.setMessage("Hello {}!");
			event.addArgument("World");

			logger.log(event);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(ModernTinylogLogger.class.getName()), isNull(), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

	}

	/**
	 * Test logging with using a real non-null {@link Marker}.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(ModernTinylogLogger.class)
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
		private ModernTinylogLogger logger;

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

			logger = new ModernTinylogLogger(ModernTinylogLoggerTest.class.getName());

			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_ERROR", errorEnabled);

			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_TRACE", false);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG", false);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_INFO", false);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_WARN", false);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_ERROR", false);

			Whitebox.setInternalState(AbstractTinylogLogger.class, provider);
		}

		/**
		 * Resets the underlying logging provider.
		 */
		@After
		public void reset() {
			Whitebox.setInternalState(AbstractTinylogLogger.class, ProviderRegistry.getLoggingProvider());
		}

		/**
		 * Verifies that the configured name is returned.
		 */
		@Test
		public void getName() {
			assertThat(logger.getName()).isEqualTo(ModernTinylogLoggerTest.class.getName());
		}

		/**
		 * Verifies that event aware logging is fully supported.
		 */
		@Test
		public void logEventAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			RuntimeException exception = new RuntimeException();

			DefaultLoggingEvent event = new DefaultLoggingEvent(org.slf4j.event.Level.intToLevel(levelInt), logger);
			event.setCallerBoundary(ModernTinylogLogger.class.getName());
			event.addMarker(marker);
			event.setThrowable(exception);
			event.setMessage("Hello {}!");
			event.addArgument("World");

			logger.log(event);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(ModernTinylogLogger.class.getName()), eq(TAG), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

	}

	/**
	 * Test logging with using a null value as {@link Marker}.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(ModernTinylogLogger.class)
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
		private ModernTinylogLogger logger;

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

			logger = new ModernTinylogLogger(ModernTinylogLoggerTest.class.getName());

			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_GLOBAL_LEVEL_COVERS_ERROR", errorEnabled);

			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_TRACE", false);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_DEBUG", false);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_INFO", false);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_WARN", false);
			Whitebox.setInternalState(AbstractTinylogLogger.class, "MINIMUM_DEFAULT_LEVEL_COVERS_ERROR", false);

			Whitebox.setInternalState(AbstractTinylogLogger.class, provider);
		}

		/**
		 * Resets the underlying logging provider.
		 */
		@After
		public void reset() {
			Whitebox.setInternalState(AbstractTinylogLogger.class, ProviderRegistry.getLoggingProvider());
		}

		/**
		 * Verifies that the configured name is returned.
		 */
		@Test
		public void getName() {
			assertThat(logger.getName()).isEqualTo(ModernTinylogLoggerTest.class.getName());
		}

		/**
		 * Verifies that event aware logging is fully supported.
		 */
		@Test
		public void logEventAwareWithLoggerClassName() {
			int levelInt = Math.min(org.slf4j.event.Level.ERROR.toInt(), level.ordinal() * 10);
			RuntimeException exception = new RuntimeException();

			DefaultLoggingEvent event = new DefaultLoggingEvent(org.slf4j.event.Level.intToLevel(levelInt), logger);
			event.setCallerBoundary(ModernTinylogLogger.class.getName());
			event.addMarker(null);
			event.setThrowable(exception);
			event.setMessage("Hello {}!");
			event.addArgument("World");

			logger.log(event);

			if (level == Level.OFF) {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
			} else {
				verify(provider).log(eq(ModernTinylogLogger.class.getName()), isNull(), eq(level), same(exception),
						any(LegacyMessageFormatter.class), eq("Hello {}!"), eq("World"));
			}
		}

	}

}
