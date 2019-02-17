/*
 * Copyright 2019 Martin Winandy
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

package org.apache.log4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.spi.LoggerFactory;
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
import org.tinylog.Level;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
	 * Tests for creation and receiving of logger instances.
	 */
	public static final class Creation {

		/**
		 * Verifies that a logger can be received by name.
		 */
		@Test
		public void loggerByName() {
			assertThat(Logger.getLogger("test.example.MyClass")).isSameAs(LogManager.getLogger("test.example.MyClass"));
		}

		/**
		 * Verifies that a logger can be received by name and logger factory.
		 */
		@Test
		public void loggerByNameAndFactory() {
			LoggerFactory factory = mock(LoggerFactory.class);
			assertThat(Logger.getLogger("test.example.MyClass", factory)).isSameAs(LogManager.getLogger("test.example.MyClass", factory));
		}

		/**
		 * Verifies that a logger can be received by class.
		 */
		@Test
		public void loggerByClass() {
			assertThat(Logger.getLogger(LoggerTest.class)).isSameAs(LogManager.getLogger(LoggerTest.class));
		}

		/**
		 * Verifies that the root logger can be received.
		 */
		@Test
		public void rootLogger() {
			assertThat(Logger.getRootLogger()).isSameAs(LogManager.getRootLogger());
		}

	}

	/**
	 * Tests for issuing log entries.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(Logger.class)
	public static final class Logging {

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
		private Logger logger;

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
		public Logging(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
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
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.TRACE))).thenReturn(traceEnabled);
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.DEBUG))).thenReturn(debugEnabled);
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.INFO))).thenReturn(infoEnabled);
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.WARN))).thenReturn(warnEnabled);
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.ERROR))).thenReturn(errorEnabled);

			logger = new Logger(Logging.class.getName());
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_TRACE", traceEnabled);
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(Logger.class, "MINIMUM_LEVEL_COVERS_ERROR", errorEnabled);
			Whitebox.setInternalState(Logger.class, provider);
		}

		/**
		 * Resets the underlying logging provider.
		 */
		@After
		public void reset() {
			Whitebox.setInternalState(Logger.class, ProviderRegistry.getLoggingProvider());
		}

		/**
		 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
		 */
		@Test
		public void isTraceEnabled() {
			assertThat(logger.isTraceEnabled()).isEqualTo(traceEnabled);
		}

		/**
		 * Verifies that an object as message will be logged correctly at {@link Level#TRACE TRACE} level.
		 */
		@Test
		public void traceMessage() {
			logger.trace(Integer.valueOf(42));

			if (traceEnabled) {
				verify(provider).log(2, null, Level.TRACE, null, 42, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a message with exception will be logged correctly at {@link Level#TRACE TRACE} level.
		 */
		@Test
		public void traceMessageAndException() {
			RuntimeException exception = new RuntimeException();

			logger.trace("Boom!", exception);

			if (traceEnabled) {
				verify(provider).log(2, null, Level.TRACE, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an exception without a real message will be logged correctly at {@link Level#TRACE TRACE}
		 * level.
		 */
		@Test
		public void traceExceptionWithoutRealMessage() {
			RuntimeException exception = new RuntimeException();

			logger.trace(exception, exception);

			if (traceEnabled) {
				verify(provider).log(2, null, Level.TRACE, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

	}

}
