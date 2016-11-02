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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;
import org.tinylog.util.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link Logger}.
 */
@RunWith(Parameterized.class)
@PrepareForTest(Logger.class)
public final class LoggerTest {

	/**
	 * Activates Power Mock as alternative to {@link PowerMockRunner}.
	 */
	@Rule
	public PowerMockRule rule = new PowerMockRule();

	private Level level;

	private boolean traceEnabled;
	private boolean debugEnabled;
	private boolean infoEnabled;
	private boolean warnEnabled;
	private boolean errorEnabled;

	private SystemStreamCollector systemStream;
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
	 *            Determines if {@link Level#WARNING WARNING} level is enabled
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
		levels.add(new Object[] { Level.TRACE,   true,  true,  true,  true,  true });
		levels.add(new Object[] { Level.DEBUG,   false, true,  true,  true,  true });
		levels.add(new Object[] { Level.INFO,    false, false, true,  true,  true });
		levels.add(new Object[] { Level.WARNING, false, false, false, true,  true });
		levels.add(new Object[] { Level.ERROR,   false, false, false, false, true });
		levels.add(new Object[] { Level.OFF,     false, false, false, false, false });
		// @formatter:on

		return levels;
	}

	/**
	 * Redirects {@link System#out} and {@link System#err} as well as mocks the underlying logging provider.
	 *
	 * @throws ReflectiveOperationException
	 *             Failed mocking logging provider
	 */
	@Before
	public void init() throws ReflectiveOperationException {
		systemStream = new SystemStreamCollector();
		loggingProvider = mockLoggingProvider();
	}

	/**
	 * Stops redirecting {@link System#out} and {@link System#err} and resets the underlying logging provider.
	 *
	 * @throws Exception
	 *             Failed reseting logging provider
	 */
	@After
	public void reset() throws Exception {
		systemStream.close();
		resetLoggingProvider();
	}

	/**
	 * Verifies evaluating whether a specific severity level is covered by the minimum severity level.
	 *
	 * @throws Exception
	 *             Failed invoking private {@link Logger#isCoveredByMinimumLevel()} method
	 */
	@Test
	public void coveredByMinimumLevel() throws Exception {
		assertThat(isCoveredByMinimumLevel(Level.TRACE)).isEqualTo(traceEnabled);
		assertThat(isCoveredByMinimumLevel(Level.DEBUG)).isEqualTo(debugEnabled);
		assertThat(isCoveredByMinimumLevel(Level.INFO)).isEqualTo(infoEnabled);
		assertThat(isCoveredByMinimumLevel(Level.WARNING)).isEqualTo(warnEnabled);
		assertThat(isCoveredByMinimumLevel(Level.ERROR)).isEqualTo(errorEnabled);
	}

	/**
	 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
	 */
	@Test
	public void isTraceEnabled() {
		assertThat(Logger.isTraceEnabled()).isEqualTo(traceEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceObject() {
		Logger.trace("Hello World!");

		if (traceEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.TRACE, null, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndArguments() {
		Logger.trace("Hello {}!", "World");

		if (traceEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.TRACE, null, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.TRACE, exception, null, (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.TRACE, exception, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.TRACE, exception, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#DEBUG DEBUG} level is enabled.
	 */
	@Test
	public void isDebugEnabled() {
		assertThat(Logger.isDebugEnabled()).isEqualTo(debugEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugObject() {
		Logger.debug("Hello World!");

		if (debugEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.DEBUG, null, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndArguments() {
		Logger.debug("Hello {}!", "World");

		if (debugEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.DEBUG, null, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.DEBUG, exception, null, (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.DEBUG, exception, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.DEBUG, exception, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#INFO INFO} level is enabled.
	 */
	@Test
	public void isInfoEnabled() {
		assertThat(Logger.isInfoEnabled()).isEqualTo(infoEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoObject() {
		Logger.info("Hello World!");

		if (infoEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.INFO, null, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessageAndArguments() {
		Logger.info("Hello {}!", "World");

		if (infoEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.INFO, null, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.INFO, exception, null, (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.INFO, exception, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.INFO, exception, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#WARNING WARNING} level is enabled.
	 */
	@Test
	public void isWarnEnabled() {
		assertThat(Logger.isWarnEnabled()).isEqualTo(warnEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#WARNING WARNING} level.
	 */
	@Test
	public void warnObject() {
		Logger.warn("Hello World!");

		if (warnEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.WARNING, null, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#WARNING WARNING} level.
	 */
	@Test
	public void warnMessageAndArguments() {
		Logger.warn("Hello {}!", "World");

		if (warnEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.WARNING, null, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#WARNING WARNING} level.
	 */
	@Test
	public void warnException() {
		Exception exception = new NullPointerException();

		Logger.warn(exception);

		if (warnEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.WARNING, exception, null, (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#WARNING WARNING} level.
	 */
	@Test
	public void warnExceptionWithMessage() {
		Exception exception = new NullPointerException();

		Logger.warn(exception, "Hello World!");

		if (warnEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.WARNING, exception, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#WARNING
	 * WARNING} level.
	 */
	@Test
	public void warnExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		Logger.warn(exception, "Hello {}!", "World");

		if (warnEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.WARNING, exception, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#ERROR ERROR} level is enabled.
	 */
	@Test
	public void isErrorEnabled() {
		assertThat(Logger.isErrorEnabled()).isEqualTo(errorEnabled);
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorObject() {
		Logger.error("Hello World!");

		if (errorEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.ERROR, null, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndArguments() {
		Logger.error("Hello {}!", "World");

		if (errorEnabled) {
			Mockito.verify(loggingProvider).log(1, Level.ERROR, null, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.ERROR, exception, null, (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.ERROR, exception, "Hello World!", (Object[]) null);
		} else {
			verifyZeroInteractions(loggingProvider);
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
			Mockito.verify(loggingProvider).log(1, Level.ERROR, exception, "Hello {}!", "World");
		} else {
			verifyZeroInteractions(loggingProvider);
		}
	}

	/**
	 * Mocks the logging provider for logger class and overrides all depending fields.
	 *
	 * @return Mock instance for logging provide
	 * @throws ReflectiveOperationException
	 *             Failed overriding fields in logger class
	 */
	private LoggingProvider mockLoggingProvider() throws ReflectiveOperationException {
		LoggingProvider provider = mock(LoggingProvider.class);

		when(provider.getMinimumLevel()).thenReturn(level);
		when(provider.isEnabled(anyInt(), eq(Level.TRACE))).thenReturn(traceEnabled);
		when(provider.isEnabled(anyInt(), eq(Level.DEBUG))).thenReturn(debugEnabled);
		when(provider.isEnabled(anyInt(), eq(Level.INFO))).thenReturn(infoEnabled);
		when(provider.isEnabled(anyInt(), eq(Level.WARNING))).thenReturn(warnEnabled);
		when(provider.isEnabled(anyInt(), eq(Level.ERROR))).thenReturn(errorEnabled);

		updateField(Logger.class, "LOGGING_PROVIDER", provider);
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_TRACE", traceEnabled);
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_DEBUG", debugEnabled);
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_INFO", infoEnabled);
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_WARN", warnEnabled);
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_ERROR", errorEnabled);

		return provider;
	}

	/**
	 * Updates a static field in a class.
	 *
	 * @param type
	 *            Class which contains the field to update
	 * @param name
	 *            Name of the field to update
	 * @param value
	 *            New value for the field to update
	 * @throws ReflectiveOperationException
	 *             Failed updating
	 */
	private void updateField(final Class<?> type, final String name, final Object value) throws ReflectiveOperationException {
		Field field = type.getDeclaredField(name);
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, value);
	}

	/**
	 * Resets the logging provider and all overridden fields in logger class.
	 *
	 * @throws Exception
	 *             Failed updating fields
	 */
	private void resetLoggingProvider() throws Exception {
		updateField(Logger.class, "LOGGING_PROVIDER", ProviderRegistry.getLoggingProvider());
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_TRACE", isCoveredByMinimumLevel(Level.TRACE));
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_DEBUG", isCoveredByMinimumLevel(Level.DEBUG));
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_INFO", isCoveredByMinimumLevel(Level.INFO));
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_WARN", isCoveredByMinimumLevel(Level.WARNING));
		updateField(Logger.class, "MINIMUM_LEVEL_COVERS_ERROR", isCoveredByMinimumLevel(Level.ERROR));
	}

	/**
	 * Invokes the private method {@link Logger#isCoveredByMinimumLevel()}.
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
