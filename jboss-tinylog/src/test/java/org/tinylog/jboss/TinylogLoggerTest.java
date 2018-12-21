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

package org.tinylog.jboss;

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
 * Tests for {@link TinylogLogger}.
 */
@RunWith(Parameterized.class)
@PrepareForTest(TinylogLogger.class)
public final class TinylogLoggerTest {

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
	public TinylogLoggerTest(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
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

		logger = new TinylogLogger(TinylogLoggerTest.class.getName());
		Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_LEVEL_COVERS_TRACE", traceEnabled);
		Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_LEVEL_COVERS_DEBUG", debugEnabled);
		Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_LEVEL_COVERS_INFO", infoEnabled);
		Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_LEVEL_COVERS_WARN", warnEnabled);
		Whitebox.setInternalState(TinylogLogger.class, "MINIMUM_LEVEL_COVERS_ERROR", errorEnabled);
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
	 * Verifies evaluating whether a given level is enabled.
	 */
	@Test
	public void isEnabled() {
		assertThat(logger.isEnabled(org.jboss.logging.Logger.Level.TRACE)).isEqualTo(traceEnabled);
		assertThat(logger.isEnabled(org.jboss.logging.Logger.Level.DEBUG)).isEqualTo(debugEnabled);
		assertThat(logger.isEnabled(org.jboss.logging.Logger.Level.INFO)).isEqualTo(infoEnabled);
		assertThat(logger.isEnabled(org.jboss.logging.Logger.Level.WARN)).isEqualTo(warnEnabled);
		assertThat(logger.isEnabled(org.jboss.logging.Logger.Level.ERROR)).isEqualTo(errorEnabled);
		assertThat(logger.isEnabled(org.jboss.logging.Logger.Level.FATAL)).isEqualTo(errorEnabled);
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
	public void traceObjectMessage() {
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
	 * Verifies that a message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndExceptionWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.trace(TinylogLoggerTest.class.getName(), "Boom!", exception);

		if (traceEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.TRACE, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedMessageWithExceptionAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.trace(TinylogLoggerTest.class.getName(), "Hello {0}!", new Object[] { "Error" }, exception);

		if (traceEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.TRACE, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceFormattedMessageWithMultipleArguments() {
		logger.tracev("{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceFormattedMessageWithSingleArgument() {
		logger.tracev("Hello {0}!", "World");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with two arguments will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedMessageWithTwoArguments() {
		logger.tracev("{0} = {1}", "magic", 42);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with three arguments will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceFormattedMessageWithThreeArguments() {
		logger.tracev("{0}, {1} or {2}", 1, 2, 3);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with multiple arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedMessageWithMultipleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.tracev(exception, "{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with a single argument will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedMessageWithSingleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.tracev(exception, "Hello {0}!", "Error");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with two arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedMessageWithTwoArguments() {
		RuntimeException exception = new RuntimeException();

		logger.tracev(exception, "{0} = {1}", "magic", 42);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with three arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedMessageWithThreeArguments() {
		RuntimeException exception = new RuntimeException();

		logger.tracev(exception, "{0}, {1} or {2}", 1, 2, 3);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with multiple object arguments will be logged correctly at {@link Level#TRACE
	 * TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithMultipleObjects() {
		logger.tracef("%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single object argument will be logged correctly at {@link Level#TRACE
	 * TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithSingleObject() {
		logger.tracef("Hello %s!", "World");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two object arguments will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceFormattedStringWithTwoObjects() {
		logger.tracef("%s = %d", "magic", 42);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three object arguments will be logged correctly at {@link Level#TRACE
	 * TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithThreeObjects() {
		logger.tracef("%s, %s or %s", "one", "two", "three");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with multiple object arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithMultipleObjects() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single object argument will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithSingleObject() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "Hello %s!", "World");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two object arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%s = %d", "magic", 42);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three object arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithThreeObjects() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%s, %s or %s", "one", "two", "three");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single integer argument will be logged correctly at {@link Level#TRACE
	 * TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithSingleInt() {
		logger.tracef("Hello %s!", 42);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer arguments will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceFormattedStringWithTwoInts() {
		logger.tracef("%d + %d", 1, 2);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an integer and an object argument will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithIntAndObject() {
		logger.tracef("%d = %s", 42, "magic");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three integer arguments will be logged correctly at {@link Level#TRACE
	 * TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithThreeInts() {
		logger.tracef("%d + %d = %d", 1, 2, 3);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer and one object arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithTwoIntsAndOneObject() {
		logger.tracef("%d + %d = %s", 1, 2, "three");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one integer and two object arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithOneIntAndTwoObjects() {
		logger.tracef("%d = %s + %s", 3, "one", "two");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single integer argument will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithSingleInt() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "Hello %s!", 42);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithTwoInts() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d + %d", 1, 2);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an integer and an object argument will be logged correctly
	 * at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithIntAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d = %s", 42, "magic");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three integer arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithThreeInts() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d + %d = %d", 1, 2, 3);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer and one object arguments will be logged
	 * correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithTwoIntsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d + %d = %s", 1, 2, "three");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one integer and two object arguments will be logged
	 * correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithOneIntAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d = %s + %s", 3, "one", "two");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single long argument will be logged correctly at {@link Level#TRACE
	 * TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithSingleLong() {
		logger.tracef("Hello %s!", 42L);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long arguments will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceFormattedStringWithTwoLongs() {
		logger.tracef("%d + %d", 1L, 2L);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithLongAndObject() {
		logger.tracef("%d = %s", 42L, "magic");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three long arguments will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceFormattedStringWithThreeLongs() {
		logger.tracef("%d + %d = %d", 1L, 2L, 3L);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long and one object arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithTwoLongsAndOneObject() {
		logger.tracef("%d + %d = %s", 1L, 2L, "three");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one long and two object arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedStringWithOneLongAndTwoObjects() {
		logger.tracef("%d = %s + %s", 3L, "one", "two");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single long argument will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithSingleLong() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "Hello %s!", 42L);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithTwoLongs() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d + %d", 1L, 2L);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithLongAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d = %s", 42L, "magic");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three long arguments will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithThreeLongs() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d + %d = %d", 1L, 2L, 3L);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long and one object arguments will be logged correctly
	 * at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithTwoLongsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d + %d = %s", 1L, 2L, "three");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one long and two object arguments will be logged correctly
	 * at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionAndFormattedStringWithOneLongAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.tracef(exception, "%d = %s + %s", 3L, "one", "two");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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
	 * Verifies that an object as message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugObjectMessage() {
		logger.debug(Integer.valueOf(42));

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

		logger.debug("Boom!", exception);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndExceptionWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.debug(TinylogLoggerTest.class.getName(), "Boom!", exception);

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedMessageWithExceptionAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.debug(TinylogLoggerTest.class.getName(), "Hello {0}!", new Object[] { "Error" }, exception);

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugFormattedMessageWithMultipleArguments() {
		logger.debugv("{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugFormattedMessageWithSingleArgument() {
		logger.debugv("Hello {0}!", "World");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with two arguments will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedMessageWithTwoArguments() {
		logger.debugv("{0} = {1}", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with three arguments will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugFormattedMessageWithThreeArguments() {
		logger.debugv("{0}, {1} or {2}", 1, 2, 3);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with multiple arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedMessageWithMultipleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.debugv(exception, "{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with a single argument will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedMessageWithSingleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.debugv(exception, "Hello {0}!", "Error");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with two arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedMessageWithTwoArguments() {
		RuntimeException exception = new RuntimeException();

		logger.debugv(exception, "{0} = {1}", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with three arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedMessageWithThreeArguments() {
		RuntimeException exception = new RuntimeException();

		logger.debugv(exception, "{0}, {1} or {2}", 1, 2, 3);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with multiple object arguments will be logged correctly at {@link Level#DEBUG
	 * DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithMultipleObjects() {
		logger.debugf("%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single object argument will be logged correctly at {@link Level#DEBUG
	 * DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithSingleObject() {
		logger.debugf("Hello %s!", "World");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two object arguments will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugFormattedStringWithTwoObjects() {
		logger.debugf("%s = %d", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three object arguments will be logged correctly at {@link Level#DEBUG
	 * DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithThreeObjects() {
		logger.debugf("%s, %s or %s", "one", "two", "three");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with multiple object arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithMultipleObjects() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single object argument will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithSingleObject() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "Hello %s!", "World");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two object arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%s = %d", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three object arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithThreeObjects() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%s, %s or %s", "one", "two", "three");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single integer argument will be logged correctly at {@link Level#DEBUG
	 * DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithSingleInt() {
		logger.debugf("Hello %s!", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer arguments will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugFormattedStringWithTwoInts() {
		logger.debugf("%d + %d", 1, 2);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an integer and an object argument will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithIntAndObject() {
		logger.debugf("%d = %s", 42, "magic");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three integer arguments will be logged correctly at {@link Level#DEBUG
	 * DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithThreeInts() {
		logger.debugf("%d + %d = %d", 1, 2, 3);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer and one object arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithTwoIntsAndOneObject() {
		logger.debugf("%d + %d = %s", 1, 2, "three");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one integer and two object arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithOneIntAndTwoObjects() {
		logger.debugf("%d = %s + %s", 3, "one", "two");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single integer argument will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithSingleInt() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "Hello %s!", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithTwoInts() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d + %d", 1, 2);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an integer and an object argument will be logged correctly
	 * at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithIntAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d = %s", 42, "magic");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three integer arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithThreeInts() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d + %d = %d", 1, 2, 3);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer and one object arguments will be logged
	 * correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithTwoIntsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d + %d = %s", 1, 2, "three");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one integer and two object arguments will be logged
	 * correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithOneIntAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d = %s + %s", 3, "one", "two");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single long argument will be logged correctly at {@link Level#DEBUG
	 * DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithSingleLong() {
		logger.debugf("Hello %s!", 42L);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long arguments will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugFormattedStringWithTwoLongs() {
		logger.debugf("%d + %d", 1L, 2L);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithLongAndObject() {
		logger.debugf("%d = %s", 42L, "magic");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three long arguments will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugFormattedStringWithThreeLongs() {
		logger.debugf("%d + %d = %d", 1L, 2L, 3L);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long and one object arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithTwoLongsAndOneObject() {
		logger.debugf("%d + %d = %s", 1L, 2L, "three");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one long and two object arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedStringWithOneLongAndTwoObjects() {
		logger.debugf("%d = %s + %s", 3L, "one", "two");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single long argument will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithSingleLong() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "Hello %s!", 42L);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithTwoLongs() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d + %d", 1L, 2L);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithLongAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d = %s", 42L, "magic");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three long arguments will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithThreeLongs() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d + %d = %d", 1L, 2L, 3L);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long and one object arguments will be logged correctly
	 * at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithTwoLongsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d + %d = %s", 1L, 2L, "three");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one long and two object arguments will be logged correctly
	 * at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionAndFormattedStringWithOneLongAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.debugf(exception, "%d = %s + %s", 3L, "one", "two");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
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
	 * Verifies that an object as message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoObjectMessage() {
		logger.info(Integer.valueOf(42));

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

		logger.info("Boom!", exception);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessageAndExceptionWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.info(TinylogLoggerTest.class.getName(), "Boom!", exception);

		if (infoEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.INFO, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedMessageWithExceptionAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.info(TinylogLoggerTest.class.getName(), "Hello {0}!", new Object[] { "Error" }, exception);

		if (infoEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.INFO, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedMessageWithMultipleArguments() {
		logger.infov("{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedMessageWithSingleArgument() {
		logger.infov("Hello {0}!", "World");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with two arguments will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedMessageWithTwoArguments() {
		logger.infov("{0} = {1}", "magic", 42);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with three arguments will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedMessageWithThreeArguments() {
		logger.infov("{0}, {1} or {2}", 1, 2, 3);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with multiple arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedMessageWithMultipleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.infov(exception, "{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with a single argument will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedMessageWithSingleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.infov(exception, "Hello {0}!", "Error");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with two arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedMessageWithTwoArguments() {
		RuntimeException exception = new RuntimeException();

		logger.infov(exception, "{0} = {1}", "magic", 42);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with three arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedMessageWithThreeArguments() {
		RuntimeException exception = new RuntimeException();

		logger.infov(exception, "{0}, {1} or {2}", 1, 2, 3);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with multiple object arguments will be logged correctly at {@link Level#INFO
	 * INFO} level.
	 */
	@Test
	public void infoFormattedStringWithMultipleObjects() {
		logger.infof("%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single object argument will be logged correctly at {@link Level#INFO
	 * INFO} level.
	 */
	@Test
	public void infoFormattedStringWithSingleObject() {
		logger.infof("Hello %s!", "World");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two object arguments will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedStringWithTwoObjects() {
		logger.infof("%s = %d", "magic", 42);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three object arguments will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedStringWithThreeObjects() {
		logger.infof("%s, %s or %s", "one", "two", "three");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with multiple object arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithMultipleObjects() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single object argument will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithSingleObject() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "Hello %s!", "World");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two object arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%s = %d", "magic", 42);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three object arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithThreeObjects() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%s, %s or %s", "one", "two", "three");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single integer argument will be logged correctly at {@link Level#INFO
	 * INFO} level.
	 */
	@Test
	public void infoFormattedStringWithSingleInt() {
		logger.infof("Hello %s!", 42);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer arguments will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedStringWithTwoInts() {
		logger.infof("%d + %d", 1, 2);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an integer and an object argument will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedStringWithIntAndObject() {
		logger.infof("%d = %s", 42, "magic");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three integer arguments will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedStringWithThreeInts() {
		logger.infof("%d + %d = %d", 1, 2, 3);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer and one object arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedStringWithTwoIntsAndOneObject() {
		logger.infof("%d + %d = %s", 1, 2, "three");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one integer and two object arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedStringWithOneIntAndTwoObjects() {
		logger.infof("%d = %s + %s", 3, "one", "two");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single integer argument will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithSingleInt() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "Hello %s!", 42);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithTwoInts() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d + %d", 1, 2);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an integer and an object argument will be logged correctly
	 * at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithIntAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d = %s", 42, "magic");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three integer arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithThreeInts() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d + %d = %d", 1, 2, 3);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer and one object arguments will be logged
	 * correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithTwoIntsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d + %d = %s", 1, 2, "three");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one integer and two object arguments will be logged
	 * correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithOneIntAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d = %s + %s", 3, "one", "two");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single long argument will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedStringWithSingleLong() {
		logger.infof("Hello %s!", 42L);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long arguments will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedStringWithTwoLongs() {
		logger.infof("%d + %d", 1L, 2L);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedStringWithLongAndObject() {
		logger.infof("%d = %s", 42L, "magic");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three long arguments will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoFormattedStringWithThreeLongs() {
		logger.infof("%d + %d = %d", 1L, 2L, 3L);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long and one object arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedStringWithTwoLongsAndOneObject() {
		logger.infof("%d + %d = %s", 1L, 2L, "three");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one long and two object arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedStringWithOneLongAndTwoObjects() {
		logger.infof("%d = %s + %s", 3L, "one", "two");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single long argument will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithSingleLong() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "Hello %s!", 42L);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithTwoLongs() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d + %d", 1L, 2L);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithLongAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d = %s", 42L, "magic");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three long arguments will be logged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithThreeLongs() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d + %d = %d", 1L, 2L, 3L);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long and one object arguments will be logged correctly
	 * at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithTwoLongsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d + %d = %s", 1L, 2L, "three");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one long and two object arguments will be logged correctly
	 * at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionAndFormattedStringWithOneLongAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.infof(exception, "%d = %s + %s", 3L, "one", "two");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an object as message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnObjectMessage() {
		logger.warn(Integer.valueOf(42));

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

		logger.warn("Boom!", exception);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnMessageAndExceptionWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.warn(TinylogLoggerTest.class.getName(), "Boom!", exception);

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedMessageWithExceptionAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.warn(TinylogLoggerTest.class.getName(), "Hello {0}!", new Object[] { "Error" }, exception);

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedMessageWithMultipleArguments() {
		logger.warnv("{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedMessageWithSingleArgument() {
		logger.warnv("Hello {0}!", "World");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with two arguments will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedMessageWithTwoArguments() {
		logger.warnv("{0} = {1}", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with three arguments will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedMessageWithThreeArguments() {
		logger.warnv("{0}, {1} or {2}", 1, 2, 3);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with multiple arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedMessageWithMultipleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.warnv(exception, "{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with a single argument will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedMessageWithSingleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.warnv(exception, "Hello {0}!", "Error");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with two arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedMessageWithTwoArguments() {
		RuntimeException exception = new RuntimeException();

		logger.warnv(exception, "{0} = {1}", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with three arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedMessageWithThreeArguments() {
		RuntimeException exception = new RuntimeException();

		logger.warnv(exception, "{0}, {1} or {2}", 1, 2, 3);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with multiple object arguments will be logged correctly at {@link Level#WARN
	 * WARN} level.
	 */
	@Test
	public void warnFormattedStringWithMultipleObjects() {
		logger.warnf("%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single object argument will be logged correctly at {@link Level#WARN
	 * WARN} level.
	 */
	@Test
	public void warnFormattedStringWithSingleObject() {
		logger.warnf("Hello %s!", "World");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two object arguments will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedStringWithTwoObjects() {
		logger.warnf("%s = %d", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three object arguments will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedStringWithThreeObjects() {
		logger.warnf("%s, %s or %s", "one", "two", "three");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with multiple object arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithMultipleObjects() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single object argument will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithSingleObject() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "Hello %s!", "World");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two object arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%s = %d", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three object arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithThreeObjects() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%s, %s or %s", "one", "two", "three");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single integer argument will be logged correctly at {@link Level#WARN
	 * WARN} level.
	 */
	@Test
	public void warnFormattedStringWithSingleInt() {
		logger.warnf("Hello %s!", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer arguments will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedStringWithTwoInts() {
		logger.warnf("%d + %d", 1, 2);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an integer and an object argument will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedStringWithIntAndObject() {
		logger.warnf("%d = %s", 42, "magic");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three integer arguments will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedStringWithThreeInts() {
		logger.warnf("%d + %d = %d", 1, 2, 3);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer and one object arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedStringWithTwoIntsAndOneObject() {
		logger.warnf("%d + %d = %s", 1, 2, "three");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one integer and two object arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedStringWithOneIntAndTwoObjects() {
		logger.warnf("%d = %s + %s", 3, "one", "two");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single integer argument will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithSingleInt() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "Hello %s!", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithTwoInts() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d + %d", 1, 2);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an integer and an object argument will be logged correctly
	 * at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithIntAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d = %s", 42, "magic");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three integer arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithThreeInts() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d + %d = %d", 1, 2, 3);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer and one object arguments will be logged
	 * correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithTwoIntsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d + %d = %s", 1, 2, "three");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one integer and two object arguments will be logged
	 * correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithOneIntAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d = %s + %s", 3, "one", "two");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single long argument will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedStringWithSingleLong() {
		logger.warnf("Hello %s!", 42L);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long arguments will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedStringWithTwoLongs() {
		logger.warnf("%d + %d", 1L, 2L);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedStringWithLongAndObject() {
		logger.warnf("%d = %s", 42L, "magic");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three long arguments will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnFormattedStringWithThreeLongs() {
		logger.warnf("%d + %d = %d", 1L, 2L, 3L);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long and one object arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedStringWithTwoLongsAndOneObject() {
		logger.warnf("%d + %d = %s", 1L, 2L, "three");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one long and two object arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedStringWithOneLongAndTwoObjects() {
		logger.warnf("%d = %s + %s", 3L, "one", "two");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single long argument will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithSingleLong() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "Hello %s!", 42L);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithTwoLongs() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d + %d", 1L, 2L);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithLongAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d = %s", 42L, "magic");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three long arguments will be logged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithThreeLongs() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d + %d = %d", 1L, 2L, 3L);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long and one object arguments will be logged correctly
	 * at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithTwoLongsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d + %d = %s", 1L, 2L, "three");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one long and two object arguments will be logged correctly
	 * at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionAndFormattedStringWithOneLongAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.warnf(exception, "%d = %s + %s", 3L, "one", "two");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an object as message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorObjectMessage() {
		logger.error(Integer.valueOf(42));

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

		logger.error("Boom!", exception);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndExceptionWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.error(TinylogLoggerTest.class.getName(), "Boom!", exception);

		if (errorEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.ERROR, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedMessageWithExceptionAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.error(TinylogLoggerTest.class.getName(), "Hello {0}!", new Object[] { "Error" }, exception);

		if (errorEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.ERROR, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorFormattedMessageWithMultipleArguments() {
		logger.errorv("{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorFormattedMessageWithSingleArgument() {
		logger.errorv("Hello {0}!", "World");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with two arguments will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedMessageWithTwoArguments() {
		logger.errorv("{0} = {1}", "magic", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with three arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorFormattedMessageWithThreeArguments() {
		logger.errorv("{0}, {1} or {2}", 1, 2, 3);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with multiple arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedMessageWithMultipleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.errorv(exception, "{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with a single argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedMessageWithSingleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.errorv(exception, "Hello {0}!", "Error");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with two arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedMessageWithTwoArguments() {
		RuntimeException exception = new RuntimeException();

		logger.errorv(exception, "{0} = {1}", "magic", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with three arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedMessageWithThreeArguments() {
		RuntimeException exception = new RuntimeException();

		logger.errorv(exception, "{0}, {1} or {2}", 1, 2, 3);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with multiple object arguments will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithMultipleObjects() {
		logger.errorf("%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single object argument will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithSingleObject() {
		logger.errorf("Hello %s!", "World");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two object arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorFormattedStringWithTwoObjects() {
		logger.errorf("%s = %d", "magic", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three object arguments will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithThreeObjects() {
		logger.errorf("%s, %s or %s", "one", "two", "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with multiple object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithMultipleObjects() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single object argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithSingleObject() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "Hello %s!", "World");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%s = %d", "magic", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithThreeObjects() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%s, %s or %s", "one", "two", "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single integer argument will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithSingleInt() {
		logger.errorf("Hello %s!", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorFormattedStringWithTwoInts() {
		logger.errorf("%d + %d", 1, 2);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an integer and an object argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithIntAndObject() {
		logger.errorf("%d = %s", 42, "magic");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three integer arguments will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithThreeInts() {
		logger.errorf("%d + %d = %d", 1, 2, 3);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer and one object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithTwoIntsAndOneObject() {
		logger.errorf("%d + %d = %s", 1, 2, "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one integer and two object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithOneIntAndTwoObjects() {
		logger.errorf("%d = %s + %s", 3, "one", "two");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single integer argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithSingleInt() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "Hello %s!", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithTwoInts() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d + %d", 1, 2);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an integer and an object argument will be logged correctly
	 * at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithIntAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d = %s", 42, "magic");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three integer arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithThreeInts() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d + %d = %d", 1, 2, 3);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer and one object arguments will be logged
	 * correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithTwoIntsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d + %d = %s", 1, 2, "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one integer and two object arguments will be logged
	 * correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithOneIntAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d = %s + %s", 3, "one", "two");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single long argument will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithSingleLong() {
		logger.errorf("Hello %s!", 42L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorFormattedStringWithTwoLongs() {
		logger.errorf("%d + %d", 1L, 2L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithLongAndObject() {
		logger.errorf("%d = %s", 42L, "magic");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three long arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorFormattedStringWithThreeLongs() {
		logger.errorf("%d + %d = %d", 1L, 2L, 3L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long and one object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithTwoLongsAndOneObject() {
		logger.errorf("%d + %d = %s", 1L, 2L, "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one long and two object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedStringWithOneLongAndTwoObjects() {
		logger.errorf("%d = %s + %s", 3L, "one", "two");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single long argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithSingleLong() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "Hello %s!", 42L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithTwoLongs() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d + %d", 1L, 2L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithLongAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d = %s", 42L, "magic");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three long arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithThreeLongs() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d + %d = %d", 1L, 2L, 3L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long and one object arguments will be logged correctly
	 * at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithTwoLongsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d + %d = %s", 1L, 2L, "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one long and two object arguments will be logged correctly
	 * at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionAndFormattedStringWithOneLongAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.errorf(exception, "%d = %s + %s", 3L, "one", "two");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an object as message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalObjectMessage() {
		logger.fatal(Integer.valueOf(42));

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

		logger.fatal("Boom!", exception);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalMessageAndExceptionWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.fatal(TinylogLoggerTest.class.getName(), "Boom!", exception);

		if (errorEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.ERROR, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with exception will be logged correctly with a passed logger class name at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalFormattedMessageWithExceptionAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.fatal(TinylogLoggerTest.class.getName(), "Hello {0}!", new Object[] { "Error" }, exception);

		if (errorEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.ERROR, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void fatalFormattedMessageWithMultipleArguments() {
		logger.fatalv("{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void fatalFormattedMessageWithSingleArgument() {
		logger.fatalv("Hello {0}!", "World");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with two arguments will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalFormattedMessageWithTwoArguments() {
		logger.fatalv("{0} = {1}", "magic", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with three arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void fatalFormattedMessageWithThreeArguments() {
		logger.fatalv("{0}, {1} or {2}", 1, 2, 3);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with multiple arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedMessageWithMultipleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.fatalv(exception, "{0}, {1}, {2} or {3}", new Object[] { 1, 2, 3, 4 });

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with a single argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedMessageWithSingleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.fatalv(exception, "Hello {0}!", "Error");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with two arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedMessageWithTwoArguments() {
		RuntimeException exception = new RuntimeException();

		logger.fatalv(exception, "{0} = {1}", "magic", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with three arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedMessageWithThreeArguments() {
		RuntimeException exception = new RuntimeException();

		logger.fatalv(exception, "{0}, {1} or {2}", 1, 2, 3);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with multiple object arguments will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithMultipleObjects() {
		logger.fatalf("%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single object argument will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithSingleObject() {
		logger.fatalf("Hello %s!", "World");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two object arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void fatalFormattedStringWithTwoObjects() {
		logger.fatalf("%s = %d", "magic", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three object arguments will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithThreeObjects() {
		logger.fatalf("%s, %s or %s", "one", "two", "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with multiple object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithMultipleObjects() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%s, %s, %s or %s", new Object[] { "one", "two", "three", "four" });

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single object argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithSingleObject() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "Hello %s!", "World");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%s = %d", "magic", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithThreeObjects() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%s, %s or %s", "one", "two", "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single integer argument will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithSingleInt() {
		logger.fatalf("Hello %s!", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void fatalFormattedStringWithTwoInts() {
		logger.fatalf("%d + %d", 1, 2);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an integer and an object argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithIntAndObject() {
		logger.fatalf("%d = %s", 42, "magic");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three integer arguments will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithThreeInts() {
		logger.fatalf("%d + %d = %d", 1, 2, 3);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two integer and one object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithTwoIntsAndOneObject() {
		logger.fatalf("%d + %d = %s", 1, 2, "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one integer and two object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithOneIntAndTwoObjects() {
		logger.fatalf("%d = %s + %s", 3, "one", "two");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single integer argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithSingleInt() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "Hello %s!", 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithTwoInts() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d + %d", 1, 2);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an integer and an object argument will be logged correctly
	 * at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithIntAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d = %s", 42, "magic");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three integer arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithThreeInts() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d + %d = %d", 1, 2, 3);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two integer and one object arguments will be logged
	 * correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithTwoIntsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d + %d = %s", 1, 2, "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one integer and two object arguments will be logged
	 * correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithOneIntAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d = %s + %s", 3, "one", "two");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single long argument will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithSingleLong() {
		logger.fatalf("Hello %s!", 42L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void fatalFormattedStringWithTwoLongs() {
		logger.fatalf("%d + %d", 1L, 2L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithLongAndObject() {
		logger.fatalf("%d = %s", 42L, "magic");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three long arguments will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void fatalFormattedStringWithThreeLongs() {
		logger.fatalf("%d + %d = %d", 1L, 2L, 3L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two long and one object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithTwoLongsAndOneObject() {
		logger.fatalf("%d + %d = %s", 1L, 2L, "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with one long and two object arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalFormattedStringWithOneLongAndTwoObjects() {
		logger.fatalf("%d = %s + %s", 3L, "one", "two");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single long argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithSingleLong() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "Hello %s!", 42L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Hello 42!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithTwoLongs() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d + %d", 1L, 2L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with an long and an object argument will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithLongAndObject() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d = %s", 42L, "magic");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "42 = magic", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three long arguments will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithThreeLongs() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d + %d = %d", 1L, 2L, 3L);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2 = 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two long and one object arguments will be logged correctly
	 * at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithTwoLongsAndOneObject() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d + %d = %s", 1L, 2L, "three");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "1 + 2 = three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with one long and two object arguments will be logged correctly
	 * at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalExceptionAndFormattedStringWithOneLongAndTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.fatalf(exception, "%d = %s + %s", 3L, "one", "two");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "3 = one + two", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an object as message will be logged correctly at any level.
	 */
	@Test
	public void logObjectMessage() {
		logger.log(org.jboss.logging.Logger.Level.DEBUG, Integer.valueOf(42));

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, 42, (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.log(org.jboss.logging.Logger.Level.WARN, Integer.valueOf(42));

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, 42, (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a message with exception will be logged correctly at any level.
	 */
	@Test
	public void logMessageAndException() {
		RuntimeException exception = new RuntimeException();

		logger.log(org.jboss.logging.Logger.Level.DEBUG, "Boom!", exception);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.log(org.jboss.logging.Logger.Level.WARN, "Boom!", exception);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a message with exception will be logged correctly with a passed logger class name at any level.
	 */
	@Test
	public void logMessageAndExceptionWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.log(org.jboss.logging.Logger.Level.DEBUG, TinylogLoggerTest.class.getName(), "Boom!", exception);

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.log(org.jboss.logging.Logger.Level.WARN, TinylogLoggerTest.class.getName(), "Boom!", exception);

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "Boom!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message and an exception will be logged correctly with a passed logger class name at
	 * any level.
	 */
	@Test
	public void logFormattedMessageAndExceptionWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.log(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, "Hello {0}!", new Object[] { "Error" },
			exception);

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.log(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, "Hello {0}!", new Object[] { "Error" },
			exception);

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at any level.
	 */
	@Test
	public void logFormattedMessageWithMultipleArguments() {
		logger.logv(org.jboss.logging.Logger.Level.DEBUG, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(org.jboss.logging.Logger.Level.WARN, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at any level.
	 */
	@Test
	public void logFormattedMessageWithSingleArgument() {
		logger.logv(org.jboss.logging.Logger.Level.DEBUG, "Hello {0}!", "World");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(org.jboss.logging.Logger.Level.WARN, "Hello {0}!", "World");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with two arguments will be logged correctly at any level.
	 */
	@Test
	public void logFormattedMessageWithTwoArguments() {
		logger.logv(org.jboss.logging.Logger.Level.DEBUG, "{0} = {1}", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(org.jboss.logging.Logger.Level.WARN, "{0} = {1}", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted message with three arguments will be logged correctly at any level.
	 */
	@Test
	public void logFormattedMessageWithThreeArguments() {
		logger.logv(org.jboss.logging.Logger.Level.DEBUG, "{0}, {1} or {2}", 1, 2, 3);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(org.jboss.logging.Logger.Level.WARN, "{0}, {1} or {2}", 1, 2, 3);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with multiple arguments will be logged correctly at any level.
	 */
	@Test
	public void logExceptionAndFormattedMessageWithMultipleArguments() {
		RuntimeException exception = new RuntimeException();

		logger.logv(org.jboss.logging.Logger.Level.DEBUG, exception, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(org.jboss.logging.Logger.Level.WARN, exception, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with a single argument will be logged correctly at any level.
	 */
	@Test
	public void logExceptionAndFormattedMessageWithSingleArgument() {
		RuntimeException exception = new RuntimeException();

		logger.logv(org.jboss.logging.Logger.Level.DEBUG, exception, "Hello {0}!", "World");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(org.jboss.logging.Logger.Level.WARN, exception, "Hello {0}!", "World");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with two arguments will be logged correctly at any level.
	 */
	@Test
	public void logExceptionAndFormattedMessageWithTwoArguments() {
		RuntimeException exception = new RuntimeException();

		logger.logv(org.jboss.logging.Logger.Level.DEBUG, exception, "{0} = {1}", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(org.jboss.logging.Logger.Level.WARN, exception, "{0} = {1}", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with three arguments will be logged correctly at any level.
	 */
	@Test
	public void logExceptionAndFormattedMessageWithThreeArguments() {
		RuntimeException exception = new RuntimeException();

		logger.logv(org.jboss.logging.Logger.Level.DEBUG, exception, "{0}, {1} or {2}", 1, 2, 3);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(org.jboss.logging.Logger.Level.WARN, exception, "{0}, {1} or {2}", 1, 2, 3);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with multiple arguments will be logged correctly with a passed
	 * logger class name at any level.
	 */
	@Test
	public void logExceptionAndFormattedMessageWithMultipleArgumentsAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.logv(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, exception, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, exception, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "1, 2, 3 or 4", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with a single argument will be logged correctly with a passed
	 * logger class name at any level.
	 */
	@Test
	public void logExceptionAndFormattedMessageWithSingleArgumentAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.logv(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, exception, "Hello {0}!", "World");

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, exception, "Hello {0}!", "World");

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with two arguments will be logged correctly with a passed
	 * logger class name at any level.
	 */
	@Test
	public void logExceptionAndFormattedMessageWithTwoArgumentsAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.logv(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, exception, "{0} = {1}", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, exception, "{0} = {1}", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted message with three arguments will be logged correctly with a passed
	 * logger class name at any level.
	 */
	@Test
	public void logExceptionAndFormattedMessageWithThreeArgumentsAndLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.logv(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, exception, "{0}, {1} or {2}", 1, 2, 3);

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logv(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, exception, "{0}, {1} or {2}", 1, 2, 3);

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "1, 2 or 3", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with multiple object arguments will be logged correctly at any level.
	 */
	@Test
	public void logFormattedStringWithMultipleObjects() {
		logger.logf(org.jboss.logging.Logger.Level.DEBUG, "%s, %s, %s or %s", "one", "two", "three", "four");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(org.jboss.logging.Logger.Level.WARN, "%s, %s, %s or %s", "one", "two", "three", "four");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with a single object argument will be logged correctly at any level.
	 */
	@Test
	public void logFormattedStringWithSingleObject() {
		logger.logf(org.jboss.logging.Logger.Level.DEBUG, "Hello %s!", "World");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(org.jboss.logging.Logger.Level.WARN, "Hello %s!", "World");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with two object arguments will be logged correctly at any level.
	 */
	@Test
	public void logFormattedStringWithTwoObjects() {
		logger.logf(org.jboss.logging.Logger.Level.DEBUG, "%s = %d", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(org.jboss.logging.Logger.Level.WARN, "%s = %d", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that a formatted string with three object arguments will be logged correctly at any level.
	 */
	@Test
	public void logFormattedStringWithThreeObject() {
		logger.logf(org.jboss.logging.Logger.Level.DEBUG, "%s, %s or %s", "one", "two", "three");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(org.jboss.logging.Logger.Level.WARN, "%s, %s or %s", "one", "two", "three");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with multiple object arguments will be logged correctly at any
	 * level.
	 */
	@Test
	public void logExceptionAndFormattedStringWithMultipleObjects() {
		RuntimeException exception = new RuntimeException();

		logger.logf(org.jboss.logging.Logger.Level.DEBUG, exception, "%s, %s, %s or %s", "one", "two", "three", "four");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(org.jboss.logging.Logger.Level.WARN, exception, "%s, %s, %s or %s", "one", "two", "three", "four");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "one, two, three or four", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single object argument will be logged correctly at any
	 * level.
	 */
	@Test
	public void logExceptionAndFormattedStringWithSingleObject() {
		RuntimeException exception = new RuntimeException();

		logger.logf(org.jboss.logging.Logger.Level.DEBUG, exception, "Hello %s!", "Error");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(org.jboss.logging.Logger.Level.WARN, exception, "Hello %s!", "Error");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two object arguments will be logged correctly at any
	 * level.
	 */
	@Test
	public void logExceptionAndFormattedStringWithTwoObjects() {
		RuntimeException exception = new RuntimeException();

		logger.logf(org.jboss.logging.Logger.Level.DEBUG, exception, "%s = %d", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(org.jboss.logging.Logger.Level.WARN, exception, "%s = %d", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three object arguments will be logged correctly at any
	 * level.
	 */
	@Test
	public void logExceptionAndFormattedStringWithThreeObject() {
		RuntimeException exception = new RuntimeException();

		logger.logf(org.jboss.logging.Logger.Level.DEBUG, exception, "%s, %s or %s", "one", "two", "three");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(org.jboss.logging.Logger.Level.WARN, exception, "%s, %s or %s", "one", "two", "three");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with multiple object arguments will be logged correctly with a
	 * passed logger class name at any level.
	 */
	@Test
	public void logExceptionAndFormattedStringWithMultipleObjectsWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.logf(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, exception, "%s, %s, %s or %s", "one", "two",
			"three", "four");

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "one, two, three or four",
				(Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, exception, "%s, %s, %s or %s", "one", "two",
			"three", "four");

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "one, two, three or four",
				(Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with a single object argument will be logged correctly with a
	 * passed logger class name at any level.
	 */
	@Test
	public void logExceptionAndFormattedStringWithSingleObjectWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.logf(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, exception, "Hello %s!", "Error");

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, exception, "Hello %s!", "Error");

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "Hello Error!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with two object arguments will be logged correctly with a
	 * passed logger class name at any level.
	 */
	@Test
	public void logExceptionAndFormattedStringWithTwoObjectsWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.logf(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, exception, "%s = %d", "magic", 42);

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, exception, "%s = %d", "magic", 42);

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "magic = 42", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

	/**
	 * Verifies that an exception and a formatted string with three object arguments will be logged correctly with a
	 * passed logger class name at any level.
	 */
	@Test
	public void logExceptionAndFormattedStringWithThreeObjectWithLoggerClassName() {
		RuntimeException exception = new RuntimeException();

		logger.logf(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.DEBUG, exception, "%s, %s or %s", "one", "two",
			"three");

		if (debugEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.DEBUG, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}

		logger.logf(TinylogLoggerTest.class.getName(), org.jboss.logging.Logger.Level.WARN, exception, "%s, %s or %s", "one", "two",
			"three");

		if (warnEnabled) {
			verify(provider).log(TinylogLoggerTest.class.getName(), null, Level.WARN, exception, "one, two or three", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), (Object[]) any());
		}
	}

}
