/*
 * Copyright 2021 Gerrit Rode
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

package org.tinylog.jsl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.tinylog.Level;
import org.tinylog.format.JavaTextMessageFormatFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
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
@RunWith(Parameterized.class)
@PrepareForTest(TinylogLogger.class)
public final class TinylogLoggerTest {

	/**
	 * Activates PowerMock (alternative to {@link PowerMockRunner}).
	 */
	@Rule
	public PowerMockRule rule = new PowerMockRule();

	private final Level level;

	private final boolean traceEnabled;
	private final boolean debugEnabled;
	private final boolean infoEnabled;
	private final boolean warnEnabled;
	private final boolean errorEnabled;

	private LoggingProvider provider;
	private TinylogLogger logger;
	private ResourceBundle resourceBundle;

	/**
	 * @param level        Actual severity level under test
	 * @param traceEnabled Determines if {@link Level#TRACE TRACE} level is enabled
	 * @param debugEnabled Determines if {@link Level#DEBUG DEBUG} level is enabled
	 * @param infoEnabled  Determines if {@link Level#INFO INFO} level is enabled
	 * @param warnEnabled  Determines if {@link Level#WARN WARN} level is enabled
	 * @param errorEnabled Determines if {@link Level#ERROR ERROR} level is enabled
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
	 * 		   {@link Level#ERROR ERROR} to determine whether these severity levels are enabled
	 */
	@Parameterized.Parameters(name = "{0}")
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
	 * Mocks the underlying logging provider and a resource bundle.
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

		logger = new TinylogLogger(TinylogLoggerTest.class.getName(), TinylogLoggerTest.class.getModule());
		Whitebox.setInternalState(TinylogLogger.class, provider);

		resourceBundle = PowerMockito.mock(ResourceBundle.class);
		when(resourceBundle.getString("localized_message_key")).thenReturn("I'm a localized message!");
		when(resourceBundle.getString("localized_format_key"))
			.thenReturn("I'm a localized formatted message and the argument is: \"{0}\".");
		when(resourceBundle.getString("localized_format_multiple_key"))
			.thenReturn("I'm a localized formatted message and the arguments are: \"{0}, {1}, {2}, {3}\".");
	}

	/**
	 * Resets the underlying logging provider.
	 */
	@After
	public void reset() {
		Whitebox.setInternalState(TinylogLogger.class, ProviderRegistry.getLoggingProvider());
	}

	/**
	 * Verifies that null is never allowed als log level.
	 */
	@Test
	public void nullNotAllowedAsLogLevel() {
		assertThatNullPointerException().isThrownBy(() -> logger.isLoggable(null));
		assertThatNullPointerException().isThrownBy(() -> logger.log(null, "Hello World!"));
		assertThatNullPointerException().isThrownBy(() -> logger.log(null, () -> "Hello" + " " + "World!"));
		assertThatNullPointerException().isThrownBy(() -> logger.log(null, 42));
		assertThatNullPointerException().isThrownBy(() -> logger.log(null, "fatal error", new RuntimeException()));
		assertThatNullPointerException().isThrownBy(() -> logger.log(null, () -> "fatal" + " " + "error", new RuntimeException()));
		assertThatNullPointerException().isThrownBy(() -> logger.log(null, "Hello {0}!", "World"));
		assertThatNullPointerException().isThrownBy(() -> logger.log(
			null, resourceBundle, "localized_message_key", new RuntimeException()));
		assertThatNullPointerException().isThrownBy(() -> logger.log(
			null, resourceBundle, "localized_format_key", "Hello World"));
	}

	/**
	 * Verifies evaluating whether a given level is enabled.
	 */
	@Test
	public void isLoggable() {
		assertThat(logger.isLoggable(System.Logger.Level.ALL)).isEqualTo(traceEnabled);
		assertThat(logger.isLoggable(System.Logger.Level.TRACE)).isEqualTo(traceEnabled);
		assertThat(logger.isLoggable(System.Logger.Level.DEBUG)).isEqualTo(debugEnabled);
		assertThat(logger.isLoggable(System.Logger.Level.INFO)).isEqualTo(infoEnabled);
		assertThat(logger.isLoggable(System.Logger.Level.WARNING)).isEqualTo(warnEnabled);
		assertThat(logger.isLoggable(System.Logger.Level.ERROR)).isEqualTo(errorEnabled);
		assertThat(logger.isLoggable(System.Logger.Level.OFF)).isFalse();
	}

	/**
	 * Verifies that a String as message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceStringMessage() {
		logger.log(System.Logger.Level.TRACE, "Hello World!");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceSupplierStringMessage() {
		logger.log(System.Logger.Level.TRACE, () -> "Hello" + " " + "World!");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceNullNotAllowedAsMsgSupplier() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.TRACE, (Supplier<String>) null));
	}

	/**
	 * Verifies that an object as message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceObjectMessage() {
		logger.log(System.Logger.Level.TRACE, 42);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, null, 42, (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as the message object ist not allowed at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceNullAsMsgObjectNotAllowed() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.TRACE, (Object) null));
	}

	/**
	 * Verifies that a message with exception will be loged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.TRACE, "fatal error", exception);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function combined with an exception will be loged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceSupplierStringMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.TRACE, () -> "fatal" + " " + "error", exception);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceNullNotAllowedAsMsgSupplierWithException() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(
			System.Logger.Level.TRACE, (Supplier<String>) null, new RuntimeException())
		);
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.TRACE, "Hello {0}!", "World");

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("Hello {0}!"), eq("World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.TRACE, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("{0}, {1}, {2} or {3}"), eq(1), eq(2), eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a localized message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceLocalizedMessage() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.TRACE, resourceBundle, "localized_message_key", exception);

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), isNull(),
				eq("I'm a localized message!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#TRACE TRACE} level as-is.
	 */
	@Test
	public void traceLocalizedMessageIfResourceBundleIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.TRACE, null, "localized_message_key", exception);

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), isNull(),
				eq("localized_message_key"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will be logged
	 * correctly at {@link Level#TRACE TRACE} level as-is.
	 */
	@Test
	public void traceLocalizedMessageIfKeyIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.TRACE, resourceBundle, null, exception);

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), same(exception), isNull(),
				isNull(), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with a single argument will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedLocalizedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.TRACE, resourceBundle, "localized_format_key", "Hello World");

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the argument is: \"{0}\"."), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with multiple arguments will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceFormattedLocalizedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.TRACE, resourceBundle, "localized_format_multiple_key", 1, 2, 3, 4);

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the arguments are: \"{0}, {1}, {2}, {3}\"."), eq(1), eq(2),
				eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#TRACE TRACE} level as-is.
	 */
	@Test
	public void traceFormattedLocalizedMessageIfResourceBundleIsNull() {
		logger.log(System.Logger.Level.TRACE, (ResourceBundle) null, "localized_format_key", "Hello World");

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("localized_format_key"), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will
	 * be logged correctly at {@link Level#TRACE TRACE} level as-is.
	 */
	@Test
	public void traceFormattedLocalizedMessageIfKeyIsNull() {
		logger.log(System.Logger.Level.TRACE, resourceBundle, null, "Hello World");

		if (traceEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.TRACE), isNull(), any(JavaTextMessageFormatFormatter.class),
				isNull(), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a String as message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugStringMessage() {
		logger.log(System.Logger.Level.DEBUG, "Hello World!");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugSupplierStringMessage() {
		logger.log(System.Logger.Level.DEBUG, () -> "Hello" + " " + "World!");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugNullNotAllowedAsMsgSupplier() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.DEBUG, (Supplier<String>) null));
	}

	/**
	 * Verifies that an object as message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugObjectMessage() {
		logger.log(System.Logger.Level.DEBUG, 42);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, null, 42, (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as the message object ist not allowed at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugNullAsMsgObjectNotAllowed() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.DEBUG, (Object) null));
	}

	/**
	 * Verifies that a message with exception will be loged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.DEBUG, "fatal error", exception);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function combined with an exception will be loged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugSupplierStringMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.DEBUG, () -> "fatal" + " " + "error", exception);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugNullNotAllowedAsMsgSupplierWithException() {
		assertThatNullPointerException().isThrownBy(() ->
			logger.log(System.Logger.Level.DEBUG, (Supplier<String>) null, new RuntimeException())
		);
	}

	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.DEBUG, "Hello {0}!", "World");

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("Hello {0}!"), eq("World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.DEBUG, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("{0}, {1}, {2} or {3}"), eq(1), eq(2), eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a localized message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugLocalizedMessage() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.DEBUG, resourceBundle, "localized_message_key", exception);

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), isNull(),
				eq("I'm a localized message!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#DEBUG DEBUG} level as-is.
	 */
	@Test
	public void debugLocalizedMessageIfResourceBundleIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.DEBUG, null, "localized_message_key", exception);

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), isNull(),
				eq("localized_message_key"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will be logged
	 * correctly at {@link Level#DEBUG DEBUG} level as-is.
	 */
	@Test
	public void debugLocalizedMessageIfKeyIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.DEBUG, resourceBundle, null, exception);

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), same(exception), isNull(),
				isNull(), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with a single argument will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedLocalizedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.DEBUG, resourceBundle, "localized_format_key", "Hello World");

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the argument is: \"{0}\"."), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with multiple arguments will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugFormattedLocalizedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.DEBUG, resourceBundle, "localized_format_multiple_key", 1, 2, 3, 4);

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the arguments are: \"{0}, {1}, {2}, {3}\"."), eq(1), eq(2),
				eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#DEBUG DEBUG} level as-is.
	 */
	@Test
	public void debugFormattedLocalizedMessageIfResourceBundleIsNull() {
		logger.log(System.Logger.Level.DEBUG, (ResourceBundle) null, "localized_format_key", "Hello World");

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("localized_format_key"), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will
	 * be logged correctly at {@link Level#DEBUG DEBUG} level as-is.
	 */
	@Test
	public void debugFormattedLocalizedMessageIfKeyIsNull() {
		logger.log(System.Logger.Level.DEBUG, resourceBundle, null, "Hello World");

		if (debugEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.DEBUG), isNull(), any(JavaTextMessageFormatFormatter.class),
				isNull(), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a String as message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoStringMessage() {
		logger.log(System.Logger.Level.INFO, "Hello World!");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoSupplierStringMessage() {
		logger.log(System.Logger.Level.INFO, () -> "Hello" + " " + "World!");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoNullNotAllowedAsMsgSupplier() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.INFO, (Supplier<String>) null));
	}

	/**
	 * Verifies that an object as message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoObjectMessage() {
		logger.log(System.Logger.Level.INFO, 42);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, null, 42, (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as the message object ist not allowed at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoNullAsMsgObjectNotAllowed() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.INFO, (Object) null));
	}

	/**
	 * Verifies that a message with exception will be loged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.INFO, "fatal error", exception);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function combined with an exception will be loged correctly at
	 * {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoSupplierStringMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.INFO, () -> "fatal" + " " + "error", exception);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoNullNotAllowedAsMsgSupplierWithException() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(
			System.Logger.Level.INFO, (Supplier<String>) null, new RuntimeException()));
	}


	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.INFO, "Hello {0}!", "World");

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("Hello {0}!"), eq("World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.INFO, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("{0}, {1}, {2} or {3}"), eq(1), eq(2), eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a localized message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoLocalizedMessage() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.INFO, resourceBundle, "localized_message_key", exception);

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), same(exception), isNull(),
				eq("I'm a localized message!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#INFO INFO} level as-is.
	 */
	@Test
	public void infoLocalizedMessageIfResourceBundleIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.INFO, null, "localized_message_key", exception);

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), same(exception), isNull(),
				eq("localized_message_key"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will be logged
	 * correctly at {@link Level#INFO INFO} level as-is.
	 */
	@Test
	public void infoLocalizedMessageIfKeyIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.INFO, resourceBundle, null, exception);

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), same(exception), isNull(),
				isNull(), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with a single argument will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedLocalizedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.INFO, resourceBundle, "localized_format_key", "Hello World");

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the argument is: \"{0}\"."), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with multiple arguments will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoFormattedLocalizedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.INFO, resourceBundle, "localized_format_multiple_key", 1, 2, 3, 4);

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the arguments are: \"{0}, {1}, {2}, {3}\"."), eq(1), eq(2),
				eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#INFO INFO} level as-is.
	 */
	@Test
	public void infoFormattedLocalizedMessageIfResourceBundleIsNull() {
		logger.log(System.Logger.Level.INFO, (ResourceBundle) null, "localized_format_key", "Hello World");

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("localized_format_key"), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will
	 * be logged correctly at {@link Level#INFO INFO} level as-is.
	 */
	@Test
	public void infoFormattedLocalizedMessageIfKeyIsNull() {
		logger.log(System.Logger.Level.INFO, resourceBundle, null, "Hello World");

		if (infoEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.INFO), isNull(), any(JavaTextMessageFormatFormatter.class),
				isNull(), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a String as message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnStringMessage() {
		logger.log(System.Logger.Level.WARNING, "Hello World!");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnSupplierStringMessage() {
		logger.log(System.Logger.Level.WARNING, () -> "Hello" + " " + "World!");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnNullNotAllowedAsMsgSupplier() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.WARNING, (Supplier<String>) null));
	}

	/**
	 * Verifies that an object as message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnObjectMessage() {
		logger.log(System.Logger.Level.WARNING, 42);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, null, 42, (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as the message object ist not allowed at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnNullAsMsgObjectNotAllowed() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.WARNING, (Object) null));
	}

	/**
	 * Verifies that a message with exception will be loged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.WARNING, "fatal error", exception);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function combined with an exception will be loged correctly at
	 * {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnSupplierStringMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.WARNING, () -> "fatal" + " " + "error", exception);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnNullNotAllowedAsMsgSupplierWithException() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(
			System.Logger.Level.WARNING, (Supplier<String>) null, new RuntimeException()));
	}


	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.WARNING, "Hello {0}!", "World");

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("Hello {0}!"), eq("World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.WARNING, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("{0}, {1}, {2} or {3}"), eq(1), eq(2), eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a localized message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnLocalizedMessage() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.WARNING, resourceBundle, "localized_message_key", exception);

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), same(exception), isNull(),
				eq("I'm a localized message!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#WARN WARN} level as-is.
	 */
	@Test
	public void warnLocalizedMessageIfResourceBundleIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.WARNING, null, "localized_message_key", exception);

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), same(exception), isNull(),
				eq("localized_message_key"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will be logged
	 * correctly at {@link Level#WARN WARN} level as-is.
	 */
	@Test
	public void warnLocalizedMessageIfKeyIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.WARNING, resourceBundle, null, exception);

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), same(exception), isNull(),
				isNull(), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with a single argument will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedLocalizedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.WARNING, resourceBundle, "localized_format_key", "Hello World");

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the argument is: \"{0}\"."), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with multiple arguments will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnFormattedLocalizedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.WARNING, resourceBundle, "localized_format_multiple_key", 1, 2, 3, 4);

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the arguments are: \"{0}, {1}, {2}, {3}\"."), eq(1), eq(2),
				eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#WARN WARN} level as-is.
	 */
	@Test
	public void warnFormattedLocalizedMessageIfResourceBundleIsNull() {
		logger.log(System.Logger.Level.WARNING, (ResourceBundle) null, "localized_format_key", "Hello World");

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("localized_format_key"), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will
	 * be logged correctly at {@link Level#WARN WARN} level as-is.
	 */
	@Test
	public void warnFormattedLocalizedMessageIfKeyIsNull() {
		logger.log(System.Logger.Level.WARNING, resourceBundle, null, "Hello World");

		if (warnEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.WARN), isNull(), any(JavaTextMessageFormatFormatter.class),
				isNull(), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a String as message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorStringMessage() {
		logger.log(System.Logger.Level.ERROR, "Hello World!");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorSupplierStringMessage() {
		logger.log(System.Logger.Level.ERROR, () -> "Hello" + " " + "World!");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, null, "Hello World!", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorNullNotAllowedAsMsgSupplier() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.ERROR, (Supplier<String>) null));
	}

	/**
	 * Verifies that an object as message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorObjectMessage() {
		logger.log(System.Logger.Level.ERROR, 42);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, null, 42, (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as the message object ist not allowed at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorNullAsMsgObjectNotAllowed() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(System.Logger.Level.ERROR, (Object) null));
	}

	/**
	 * Verifies that a message with exception will be loged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.ERROR, "fatal error", exception);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message produced by a supplier function combined with an exception will be loged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorSupplierStringMessageWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.ERROR, () -> "fatal" + " " + "error", exception);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, null, "fatal error", (Object[]) null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that null as a message supplier is not allowed at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorNullNotAllowedAsMsgSupplierWithException() {
		assertThatNullPointerException().isThrownBy(() -> logger.log(
			System.Logger.Level.ERROR, (Supplier<String>) null, new RuntimeException()));
	}


	/**
	 * Verifies that a formatted message with a single argument will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.ERROR, "Hello {0}!", "World");

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("Hello {0}!"), eq("World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message with multiple arguments will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.ERROR, "{0}, {1}, {2} or {3}", 1, 2, 3, 4);

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("{0}, {1}, {2} or {3}"), eq(1), eq(2), eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a localized message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorLocalizedMessage() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.ERROR, resourceBundle, "localized_message_key", exception);

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), isNull(),
				eq("I'm a localized message!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#ERROR ERROR} level as-is.
	 */
	@Test
	public void errorLocalizedMessageIfResourceBundleIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.ERROR, null, "localized_message_key", exception);

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), isNull(),
				eq("localized_message_key"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will be logged
	 * correctly at {@link Level#ERROR ERROR} level as-is.
	 */
	@Test
	public void errorLocalizedMessageIfKeyIsNull() {
		RuntimeException exception = new RuntimeException();

		logger.log(System.Logger.Level.ERROR, resourceBundle, null, exception);

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), same(exception), isNull(),
				isNull(), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with a single argument will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedLocalizedMessageWithSingleArgument() {
		logger.log(System.Logger.Level.ERROR, resourceBundle, "localized_format_key", "Hello World");

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the argument is: \"{0}\"."), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted localized message with multiple arguments will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorFormattedLocalizedMessageWithMultipleArguments() {
		logger.log(System.Logger.Level.ERROR, resourceBundle, "localized_format_multiple_key", 1, 2, 3, 4);

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("I'm a localized formatted message and the arguments are: \"{0}, {1}, {2}, {3}\"."), eq(1), eq(2),
				eq(3), eq(4));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle ResourceBundle} is null and will be logged
	 * correctly at {@link Level#ERROR ERROR} level as-is.
	 */
	@Test
	public void errorFormattedLocalizedMessageIfResourceBundleIsNull() {
		logger.log(System.Logger.Level.ERROR, (ResourceBundle) null, "localized_format_key", "Hello World");

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(JavaTextMessageFormatFormatter.class),
				eq("localized_format_key"), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}

	/**
	 * Verifies that a formatted message will not be localized if the given {@link ResourceBundle#getString(String) key} is null and will
	 * be logged correctly at {@link Level#ERROR ERROR} level as-is.
	 */
	@Test
	public void errorFormattedLocalizedMessageIfKeyIsNull() {
		logger.log(System.Logger.Level.ERROR, resourceBundle, null, "Hello World");

		if (errorEnabled) {
			verify(provider).log(eq(2), isNull(), eq(Level.ERROR), isNull(), any(JavaTextMessageFormatFormatter.class),
				isNull(), eq("Hello World"));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), anyString(), any());
		}
	}
}
