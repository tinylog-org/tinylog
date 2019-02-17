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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.spi.LoggingEvent;
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
 * Tests for {@link Category}.
 */
@RunWith(Enclosed.class)
public final class CategoryTest {

	/**
	 * Tests for creation and receiving of category instances.
	 */
	public static final class Creation {

		/**
		 * Verifies that a category can be received by name.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void categoryByName() {
			assertThat(Category.getInstance("test.example.MyClass")).isSameAs(LogManager.getLogger("test.example.MyClass"));
		}

		/**
		 * Verifies that a category can be received by class.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void categoryByClass() {
			assertThat(Category.getInstance(CategoryTest.class)).isSameAs(LogManager.getLogger(CategoryTest.class));
		}

		/**
		 * Verifies that an existing category can be received by name, if available.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void existingCategory() {
			assertThat(Category.exists("test.example.OtherClass")).isSameAs(LogManager.exists("test.example.OtherClass"));
		}

		/**
		 * Verifies that the root category can be received.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void rootCategory() {
			assertThat(Category.getRoot()).isSameAs(LogManager.getRootLogger());
		}

		/**
		 * Verifies that all current categories can be received.
		 */
		@SuppressWarnings({ "deprecation", "unchecked" })
		@Test
		public void currentCategories() {
			assertThat(Collections.list(Category.getCurrentCategories()))
				.containsExactlyElementsOf(Collections.list(LogManager.getCurrentLoggers()));
		}

	}

	/**
	 * Tests for issuing log entries.
	 */
	@RunWith(Parameterized.class)
	@PrepareForTest(Category.class)
	public static final class Logging {

		/**
		 * Activates PowerMock (alternative to {@link PowerMockRunner}).
		 */
		@Rule
		public PowerMockRule rule = new PowerMockRule();

		private Level level;

		private boolean debugEnabled;
		private boolean infoEnabled;
		private boolean warnEnabled;
		private boolean errorEnabled;

		private LoggingProvider provider;
		private Category category;

		/**
		 * @param level
		 *            Actual severity level under test
		 * @param debugEnabled
		 *            Determines if {@link Level#DEBUG DEBUG} level is enabled
		 * @param infoEnabled
		 *            Determines if {@link Level#INFO INFO} level is enabled
		 * @param warnEnabled
		 *            Determines if {@link Level#WARN WARN} level is enabled
		 * @param errorEnabled
		 *            Determines if {@link Level#ERROR ERROR} level is enabled
		 */
		public Logging(final Level level, final boolean debugEnabled, final boolean infoEnabled, final boolean warnEnabled,
			final boolean errorEnabled) {
			this.level = level;
			this.debugEnabled = debugEnabled;
			this.infoEnabled = infoEnabled;
			this.warnEnabled = warnEnabled;
			this.errorEnabled = errorEnabled;
		}

		/**
		 * Returns for all severity levels which severity levels are enabled.
		 *
		 * @return Each object array contains the severity level itself and five booleans for {@link Level#DEBUG DEBUG}
		 *         ... {@link Level#ERROR ERROR} to determine whether these severity levels are enabled
		 */
		@Parameters(name = "{0}")
		public static Collection<Object[]> getLevels() {
			List<Object[]> levels = new ArrayList<>();

			// @formatter:off
			levels.add(new Object[] { Level.DEBUG, true,  true,  true,  true  });
			levels.add(new Object[] { Level.INFO,  false, true,  true,  true  });
			levels.add(new Object[] { Level.WARN,  false, false, true,  true  });
			levels.add(new Object[] { Level.ERROR, false, false, false, true  });
			levels.add(new Object[] { Level.OFF,   false, false, false, false });
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
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.DEBUG))).thenReturn(debugEnabled);
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.INFO))).thenReturn(infoEnabled);
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.WARN))).thenReturn(warnEnabled);
			when(provider.isEnabled(anyInt(), eq(null), eq(Level.ERROR))).thenReturn(errorEnabled);

			category = new Category(Logging.class.getName());
			Whitebox.setInternalState(Category.class, "MINIMUM_LEVEL_COVERS_DEBUG", debugEnabled);
			Whitebox.setInternalState(Category.class, "MINIMUM_LEVEL_COVERS_INFO", infoEnabled);
			Whitebox.setInternalState(Category.class, "MINIMUM_LEVEL_COVERS_WARN", warnEnabled);
			Whitebox.setInternalState(Category.class, "MINIMUM_LEVEL_COVERS_ERROR", errorEnabled);
			Whitebox.setInternalState(Category.class, provider);
		}

		/**
		 * Resets the underlying logging provider.
		 */
		@After
		public void reset() {
			Whitebox.setInternalState(Logger.class, ProviderRegistry.getLoggingProvider());
		}

		/**
		 * Verifies evaluating whether {@link Level#DEBUG DEBUG} level is enabled.
		 */
		@Test
		public void isDebugEnabled() {
			assertThat(category.isDebugEnabled()).isEqualTo(debugEnabled);
		}

		/**
		 * Verifies evaluating whether {@link Level#INFO INFO} level is enabled.
		 */
		@Test
		public void isInfoEnabled() {
			assertThat(category.isInfoEnabled()).isEqualTo(infoEnabled);
		}

		/**
		 * Verifies that the correct enabled state can be returned for each priority.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void isEnabledFor() {
			assertThat(category.isEnabledFor(Priority.DEBUG)).isEqualTo(debugEnabled);
			assertThat(category.isEnabledFor(Priority.INFO)).isEqualTo(infoEnabled);
			assertThat(category.isEnabledFor(Priority.WARN)).isEqualTo(warnEnabled);
			assertThat(category.isEnabledFor(Priority.ERROR)).isEqualTo(errorEnabled);
			assertThat(category.isEnabledFor(Priority.FATAL)).isEqualTo(errorEnabled);
		}

		/**
		 * Verifies that the current minimum level will be returned as effective level.
		 */
		@Test
		public void getEffectiveLevel() {
			assertThat(category.getEffectiveLevel().levelStr).isEqualTo(level.toString());
		}

		/**
		 * Verifies that the current minimum level will be returned as chained priority.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void getChainedPriority() {
			assertThat(category.getChainedPriority().levelStr).isEqualTo(level.toString());
		}

		/**
		 * Verifies that the current minimum level will be returned as level.
		 */
		@Test
		public void getLevel() {
			assertThat(category.getLevel().levelStr).isEqualTo(level.toString());
		}

		/**
		 * Verifies that the current minimum level will be returned as priority.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void getPriority() {
			assertThat(category.getPriority().levelStr).isEqualTo(level.toString());
		}

		/**
		 * Verifies that an object as message will be logged correctly at {@link Level#DEBUG DEBUG} level.
		 */
		@Test
		public void debugMessage() {
			category.debug(Integer.valueOf(42));

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, null, 42, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a message with exception will be logged correctly at {@link Level#DEBUG DEBUG} level.
		 */
		@Test
		public void debugMessageAndException() {
			RuntimeException exception = new RuntimeException();

			category.debug("Boom!", exception);

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an exception without a real message will be logged correctly at {@link Level#DEBUG DEBUG}
		 * level.
		 */
		@Test
		public void debugExceptionWithoutRealMessage() {
			RuntimeException exception = new RuntimeException();

			category.debug(exception, exception);

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an object as message will be logged correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoMessage() {
			category.info(Integer.valueOf(42));

			if (infoEnabled) {
				verify(provider).log(2, null, Level.INFO, null, 42, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a message with exception will be logged correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoMessageAndException() {
			RuntimeException exception = new RuntimeException();

			category.info("Boom!", exception);

			if (infoEnabled) {
				verify(provider).log(2, null, Level.INFO, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an exception without a real message will be logged correctly at {@link Level#INFO INFO} level.
		 */
		@Test
		public void infoExceptionWithoutRealMessage() {
			RuntimeException exception = new RuntimeException();

			category.info(exception, exception);

			if (infoEnabled) {
				verify(provider).log(2, null, Level.INFO, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an object as message will be logged correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnMessage() {
			category.warn(Integer.valueOf(42));

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, null, 42, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a message with exception will be logged correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnMessageAndException() {
			RuntimeException exception = new RuntimeException();

			category.warn("Boom!", exception);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an exception without a real message will be logged correctly at {@link Level#WARN WARN} level.
		 */
		@Test
		public void warnExceptionWithoutRealMessage() {
			RuntimeException exception = new RuntimeException();

			category.warn(exception, exception);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an object as message will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void errorMessage() {
			category.error(Integer.valueOf(42));

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, null, 42, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a message with exception will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void errorMessageAndException() {
			RuntimeException exception = new RuntimeException();

			category.error("Boom!", exception);

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an exception without a real message will be logged correctly at {@link Level#ERROR ERROR}
		 * level.
		 */
		@Test
		public void errorExceptionWithoutRealMessage() {
			RuntimeException exception = new RuntimeException();

			category.error(exception, exception);

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an object as message will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void fatalMessage() {
			category.fatal(Integer.valueOf(42));

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, null, 42, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a message with exception will be logged correctly at {@link Level#ERROR ERROR} level.
		 */
		@Test
		public void fatalMessageAndException() {
			RuntimeException exception = new RuntimeException();

			category.fatal("Boom!", exception);

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that an exception without a real message will be logged correctly at {@link Level#ERROR ERROR}
		 * level.
		 */
		@Test
		public void fatalExceptionWithoutRealMessage() {
			RuntimeException exception = new RuntimeException();

			category.fatal(exception, exception);

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that no log entry will be issued, if the assertion is {@code true}.
		 */
		@Test
		public void assertLogWithPostiveAssertion() {
			category.assertLog(true, "Hello World!");
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		/**
		 * Verifies that an error log entry will be issued, if the assertion is {@code false} and the error level is
		 * enabled.
		 */
		@Test
		public void assertLogWithNegativeAssertion() {
			category.assertLog(false, "Hello World!");

			if (errorEnabled) {
				verify(provider).log(2, null, Level.ERROR, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a forced log entry with a message and an exception will be handled as a normal log entry.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void forcedLogMessageAndException() {
			Exception exception = new RuntimeException();

			category.forcedLog("MyClass", Priority.DEBUG, "Boom!", exception);

			if (debugEnabled) {
				verify(provider).log("MyClass", null, Level.DEBUG, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.forcedLog("MyClass", Priority.WARN, "Boom!", exception);

			if (warnEnabled) {
				verify(provider).log("MyClass", null, Level.WARN, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a forced log entry with an exception, but without a real message, will be handled as a normal
		 * log entry.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void forcedLogExceptionWithoutRealMessage() {
			Exception exception = new RuntimeException();

			category.forcedLog("MyClass", Priority.DEBUG, exception, exception);

			if (debugEnabled) {
				verify(provider).log("MyClass", null, Level.DEBUG, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.forcedLog("MyClass", Priority.WARN, exception, exception);

			if (warnEnabled) {
				verify(provider).log("MyClass", null, Level.WARN, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a translatable log message without any parameters will be output translated, if the assigned
		 * severity level is enabled.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void l7dlogWithoutParams() {
			Exception exception = new RuntimeException();
			category.setResourceBundle(new MapResourceBundle(Collections.singletonMap("test", "Hello World!")));

			category.l7dlog(Priority.DEBUG, "test", exception);

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, exception, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.l7dlog(Priority.WARN, "test", exception);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, exception, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a translatable log message with parameters will be output translated, if the assigned severity
		 * level is enabled.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void l7dlogWithParams() {
			Exception exception = new RuntimeException();
			category.setResourceBundle(new MapResourceBundle(Collections.singletonMap("test", "Hello {0}!")));

			category.l7dlog(Priority.DEBUG, "test", new Object[] { "Java" }, exception);

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, exception, "Hello Java!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.l7dlog(Priority.WARN, "test", new Object[] { "Java" }, exception);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, exception, "Hello Java!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a generic log entry with a message, but without an exception, can be correctly issued together
		 * with depth of the caller in the current stack trace.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void logMessageOnlyByTraceDepth() {
			category.log(Priority.DEBUG, "Hello World!");

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.log(Priority.WARN, "Hello World!", null);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, null, "Hello World!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a generic log entry with a message and an exception can be correctly issued together with depth
		 * of the caller in the current stack trace.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void logMessageAndExceptionByCallerByTraceDepth() {
			Exception exception = new RuntimeException();

			category.log(Priority.DEBUG, "Boom!", exception);

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.log(Priority.WARN, "Boom!", exception);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a generic log entry with an exception, but without a real message, can be correctly issued
		 * together with depth of the caller in the current stack trace.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void logExceptionWithoutRealMessageByTraceDepth() {
			Exception exception = new RuntimeException();

			category.log(Priority.DEBUG, exception, exception);

			if (debugEnabled) {
				verify(provider).log(2, null, Level.DEBUG, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.log(Priority.WARN, exception, exception);

			if (warnEnabled) {
				verify(provider).log(2, null, Level.WARN, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a generic log entry with a message and an exception can be correctly issued together with the
		 * fully qualified class name of the caller.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void logMessageAndExceptionByCaller() {
			Exception exception = new RuntimeException();

			category.log("MyClass", Priority.DEBUG, "Boom!", exception);

			if (debugEnabled) {
				verify(provider).log("MyClass", null, Level.DEBUG, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.log("MyClass", Priority.WARN, "Boom!", exception);

			if (warnEnabled) {
				verify(provider).log("MyClass", null, Level.WARN, exception, "Boom!", (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

		/**
		 * Verifies that a generic log entry with an exception, but without a real message, can be correctly issued
		 * together with the fully qualified class name of the caller.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void logExceptionWithoutRealMessageByCaller() {
			Exception exception = new RuntimeException();

			category.log("MyClass", Priority.DEBUG, exception, exception);

			if (debugEnabled) {
				verify(provider).log("MyClass", null, Level.DEBUG, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}

			category.log("MyClass", Priority.WARN, exception, exception);

			if (warnEnabled) {
				verify(provider).log("MyClass", null, Level.WARN, exception, null, (Object[]) null);
			} else {
				verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
			}
		}

	}

	/**
	 * Tests for methods that exists only for compatibility reasons, without real business logic.
	 */
	public static final class Compatibility {

		private Category category;

		/**
		 * Creates a new category instance for each test.
		 */
		@Before
		public void init() {
			category = new Category(Compatibility.class.getName());
		}

		/**
		 * Verifies that an appender can be added without throwing an exception.
		 */
		@Test
		public void addAppender() {
			category.addAppender(mock(Appender.class));
		}

		/**
		 * Verifies that a log event for appenders can be received without doing anything.
		 */
		@Test
		public void callAppenders() {
			category.callAppenders(new LoggingEvent());
		}

		/**
		 * Verifies that additivity is always {@code true}.
		 */
		@Test
		public void getAdditivity() {
			assertThat(category.getAdditivity()).isTrue();
		}

		/**
		 * Verifies that the list of all appenders is always empty.
		 */
		@SuppressWarnings("unchecked")
		@Test
		public void getAllAppenders() {
			assertThat(Collections.list(category.getAllAppenders())).isEmpty();
		}

		/**
		 * Verifies that {@code null} is returned as requested appender.
		 */
		@Test
		public void getAppender() {
			assertThat(category.getAppender("MyFile")).isNull();
			assertThat(category.getAppender("Async")).isNull();
		}

		/**
		 * Verifies that the class name will be returned for the current category and the package name for it's parent
		 * category.
		 */
		@Test
		public void name() {
			assertThat(category.getName()).isEqualTo(Compatibility.class.getName());
			assertThat(category.getParent().getName()).isEqualTo(Compatibility.class.getPackageName());
		}

		/**
		 * Verifies that the current category instance has a valid parent category.
		 */
		@Test
		public void parent() {
			assertThat(category.getParent()).isSameAs(LogManager.getLogger(Compatibility.class.getPackageName()));
		}

		/**
		 * Verifies that there is no resource bundle by default.
		 */
		@Test
		public void getResourceBundle() {
			assertThat(category.getResourceBundle()).isNull();
		}

		/**
		 * Verifies that a new resource bundle can be set.
		 */
		@Test
		public void setResourceBundle() {
			ResourceBundle bundle = mock(ResourceBundle.class);
			category.setResourceBundle(bundle);
			assertThat(category.getResourceBundle()).isSameAs(bundle);
		}

		/**
		 * Verifies no appender is marked as attached.
		 */
		@Test
		public void isAttached() {
			assertThat(category.isAttached(mock(Appender.class))).isFalse();
		}

		/**
		 * Verifies that all appenders can be removed without throwing an exception.
		 */
		@Test
		public void removeAllAppenders() {
			category.removeAllAppenders();
		}

		/**
		 * Verifies that an appender class can be removed without throwing an exception.
		 */
		@Test
		public void removeAppenderByClass() {
			category.removeAppender(mock(Appender.class));
		}

		/**
		 * Verifies that an appender name can be removed without throwing an exception.
		 */
		@Test
		public void removeAppenderByName() {
			category.removeAppender("MyFile");
			category.removeAppender("Async");
		}

		/**
		 * Verifies that the additivity flag cannot be changed.
		 */
		@Test
		public void setAdditivity() {
			category.setAdditivity(false);
			assertThat(category.getAdditivity()).isTrue();
		}

		/**
		 * Verifies that the level can be not changed.
		 */
		@Test
		public void setLevel() {
			org.apache.log4j.Level level = category.getLevel();

			category.setLevel(org.apache.log4j.Level.DEBUG);
			assertThat(category.getLevel()).isEqualTo(level);

			category.setLevel(org.apache.log4j.Level.WARN);
			assertThat(category.getLevel()).isEqualTo(level);
		}

		/**
		 * Verifies that the priority can be not changed.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void setPriority() {
			Priority priority = category.getPriority();

			category.setPriority(Priority.DEBUG);
			assertThat(category.getPriority()).isEqualTo(priority);

			category.setPriority(Priority.WARN);
			assertThat(category.getPriority()).isEqualTo(priority);
		}

		/**
		 * Verifies that the {@code shutdown()} method can be called without any effects.
		 */
		@SuppressWarnings("deprecation")
		@Test
		public void shutdown() {
			Category.shutdown();
		}

	}

	/**
	 * Simple resource bundle that consumes translations as a map.
	 */
	private static final class MapResourceBundle extends ResourceBundle {

		private final Map<String, String> translations;

		/**
		 * @param translations
		 *            Translations to store
		 */
		private MapResourceBundle(final Map<String, String> translations) {
			this.translations = translations;
		}

		@Override
		protected Object handleGetObject(final String key) {
			return translations.get(key);
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.enumeration(translations.keySet());
		}
	}

}
