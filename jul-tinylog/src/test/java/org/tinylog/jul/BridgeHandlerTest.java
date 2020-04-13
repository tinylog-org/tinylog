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

package org.tinylog.jul;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.tinylog.Level;
import org.tinylog.format.JavaTextMessageFormatFormatter;
import org.tinylog.provider.LoggingProvider;

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
 * Tests for {@link BridgeHandler} and {@link JulTinylogBridge}.
 */
@RunWith(Parameterized.class)
public final class BridgeHandlerTest {

	/**
	 * Activates PowerMock (alternative to {@link PowerMockRunner}).
	 */
	@Rule
	public PowerMockRule rule = new PowerMockRule();

	private final Level level;
	private final boolean finestEnabled;
	private final boolean finerEnabled;
	private final boolean fineEnabled;
	private final boolean configEnabled;
	private final boolean infoEnabled;
	private final boolean warningEnabled;
	private final boolean severeEnabled;

	private LoggingProvider provider;
	private Logger logger;

	/**
	 * @param level
	 *            Actual severity level under test
	 * @param finestEnabled
	 *            Determines if {@link java.util.logging.Level#FINEST FINEST} level is enabled
	 * @param finerEnabled
	 *            Determines if {@link java.util.logging.Level#FINER FINER} level is enabled
	 * @param fineEnabled
	 *            Determines if {@link java.util.logging.Level#FINE FINE} level is enabled
	 * @param configEnabled
	 *            Determines if {@link java.util.logging.Level#CONFIG CONFIG} level is enabled
	 * @param infoEnabled
	 *            Determines if {@link java.util.logging.Level#INFO INFO} level is enabled
	 * @param warningEnabled
	 *            Determines if {@link java.util.logging.Level#WARNING WARNING} level is enabled
	 * @param severeEnabled
	 *            Determines if {@link java.util.logging.Level#SEVERE SEVERE} level is enabled
	 */
	public BridgeHandlerTest(final Level level, final boolean finestEnabled, final boolean finerEnabled,
		final boolean fineEnabled, final boolean configEnabled, final boolean infoEnabled, final boolean warningEnabled,
		final boolean severeEnabled) {
		this.level = level;
		this.finestEnabled = finestEnabled;
		this.finerEnabled = finerEnabled;
		this.fineEnabled = fineEnabled;
		this.configEnabled = configEnabled;
		this.infoEnabled = infoEnabled;
		this.warningEnabled = warningEnabled;
		this.severeEnabled = severeEnabled;
	}

	/**
	 * Returns for all severity levels which logging levels are enabled.
	 *
	 * @return Each object array contains the severity level itself and seven booleans for
	 *         {@link java.util.logging.Level#FINEST FINEST} ... {@link java.util.logging.Level#SEVERE SEVERE} to
	 *         determine whether these logging levels are enabled
	 */
	@Parameters(name = "{0}")
	public static Collection<Object[]> getLevels() {
		List<Object[]> levels = new ArrayList<>();

		// @formatter:off
		levels.add(new Object[] { Level.TRACE, true,  true,  true,  true,  true,  true,  true  });
		levels.add(new Object[] { Level.DEBUG, false, true,  true,  true,  true,  true,  true  });
		levels.add(new Object[] { Level.INFO,  false, false, false, true,  true,  true,  true  });
		levels.add(new Object[] { Level.WARN,  false, false, false, false, false, true,  true  });
		levels.add(new Object[] { Level.ERROR, false, false, false, false, false, false, true  });
		levels.add(new Object[] { Level.OFF,   false, false, false, false, false, false, false });
		// @formatter:on

		return levels;
	}

	/**
	 * Mocks the underlying logging provider, activates the bridge for {@code java.util.logging}, and creates a logger.
	 *
	 * @throws ClassNotFoundException
	 *             Failed to load {@link BridgeHandler}
	 */
	@Before
	public void init() throws ClassNotFoundException {
		Class.forName("org.tinylog.jul.BridgeHandler");

		provider = mock(LoggingProvider.class);
		when(provider.getMinimumLevel(null)).thenReturn(level);
		Whitebox.setInternalState(BridgeHandler.class, provider);

		JulTinylogBridge.activate();

		logger = Logger.getLogger(BridgeHandlerTest.class.getName());
	}

	/**
	 * Resets {@code java.util.logging}.
	 */
	@After
	public void reset() {
		LogManager.getLogManager().reset();
	}

	/**
	 * Verifies evaluating whether {@link java.util.logging.Level#FINEST FINEST} level is enabled.
	 */
	@Test
	public void isFinestLoggable() {
		assertThat(logger.isLoggable(java.util.logging.Level.FINEST)).isEqualTo(finestEnabled);
	}

	/**
	 * Verifies that a plain text message object will be logged correctly at {@link java.util.logging.Level#FINEST
	 * FINEST} level.
	 */
	@Test
	public void finestTextMessage() {
		logger.finest("Hello World!");

		if (finestEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.TRACE), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link java.util.logging.Level#FINEST FINEST}
	 * level.
	 */
	@Test
	public void finestSupplierMessage() {
		logger.finest(() -> "Hello World!");

		if (finestEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.TRACE), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with arguments will be logged correctly at
	 * {@link java.util.logging.Level#FINEST FINEST} level.
	 */
	@Test
	public void finestLogEntryWithArguments() {
		logger.log(java.util.logging.Level.FINEST, "magic = {0}", new Object[] { 42 });

		if (finestEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.TRACE), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("magic = {0}"), eq(42));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link java.util.logging.Level#FINEST FINEST} level.
	 */
	@Test
	public void finestLogEntryWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(java.util.logging.Level.FINEST, "Hello World!", exception);

		if (finestEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.TRACE), same(exception),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link java.util.logging.Level#FINER FINER} level is enabled.
	 */
	@Test
	public void isFinerLoggable() {
		assertThat(logger.isLoggable(java.util.logging.Level.FINER)).isEqualTo(finerEnabled);
	}

	/**
	 * Verifies that a plain text message object will be logged correctly at {@link java.util.logging.Level#FINER FINER}
	 * level.
	 */
	@Test
	public void finerTextMessage() {
		logger.finer("Hello World!");

		if (finerEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.DEBUG), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link java.util.logging.Level#FINER FINER}
	 * level.
	 */
	@Test
	public void finerSupplierMessage() {
		logger.finer(() -> "Hello World!");

		if (finerEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.DEBUG), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with arguments will be logged correctly at
	 * {@link java.util.logging.Level#FINER FINER} level.
	 */
	@Test
	public void finerLogEntryWithArguments() {
		logger.log(java.util.logging.Level.FINER, "magic = {0}", new Object[] { 42 });

		if (finerEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.DEBUG), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("magic = {0}"), eq(42));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link java.util.logging.Level#FINER FINER} level.
	 */
	@Test
	public void finerLogEntryWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(java.util.logging.Level.FINER, "Hello World!", exception);

		if (finerEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.DEBUG), same(exception),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link java.util.logging.Level#FINE FINE} level is enabled.
	 */
	@Test
	public void isFineLoggable() {
		assertThat(logger.isLoggable(java.util.logging.Level.FINE)).isEqualTo(fineEnabled);
	}

	/**
	 * Verifies that a plain text message object will be logged correctly at {@link java.util.logging.Level#FINE FINE}
	 * level.
	 */
	@Test
	public void fineTextMessage() {
		logger.fine("Hello World!");

		if (fineEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.DEBUG), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link java.util.logging.Level#FINE FINE}
	 * level.
	 */
	@Test
	public void fineSupplierMessage() {
		logger.fine(() -> "Hello World!");

		if (fineEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.DEBUG), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with arguments will be logged correctly at
	 * {@link java.util.logging.Level#FINE FINE} level.
	 */
	@Test
	public void fineLogEntryWithArguments() {
		logger.log(java.util.logging.Level.FINE, "magic = {0}", new Object[] { 42 });

		if (fineEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.DEBUG), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("magic = {0}"), eq(42));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link java.util.logging.Level#FINE FINE} level.
	 */
	@Test
	public void fineLogEntryWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(java.util.logging.Level.FINE, "Hello World!", exception);

		if (fineEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.DEBUG), same(exception),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link java.util.logging.Level#CONFIG CONFIG} level is enabled.
	 */
	@Test
	public void isConfigLoggable() {
		assertThat(logger.isLoggable(java.util.logging.Level.CONFIG)).isEqualTo(configEnabled);
	}

	/**
	 * Verifies that a plain text message object will be logged correctly at {@link java.util.logging.Level#CONFIG
	 * CONFIG} level.
	 */
	@Test
	public void configTextMessage() {
		logger.config("Hello World!");

		if (configEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.INFO), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link java.util.logging.Level#CONFIG CONFIG}
	 * level.
	 */
	@Test
	public void configSupplierMessage() {
		logger.config(() -> "Hello World!");

		if (configEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.INFO), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with arguments will be logged correctly at
	 * {@link java.util.logging.Level#CONFIG CONFIG} level.
	 */
	@Test
	public void configLogEntryWithArguments() {
		logger.log(java.util.logging.Level.CONFIG, "magic = {0}", new Object[] { 42 });

		if (configEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.INFO), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("magic = {0}"), eq(42));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link java.util.logging.Level#CONFIG CONFIG} level.
	 */
	@Test
	public void configLogEntryWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(java.util.logging.Level.CONFIG, "Hello World!", exception);

		if (configEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.INFO), same(exception),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link java.util.logging.Level#INFO INFO} level is enabled.
	 */
	@Test
	public void isInfoLoggable() {
		assertThat(logger.isLoggable(java.util.logging.Level.INFO)).isEqualTo(infoEnabled);
	}

	/**
	 * Verifies that a plain text message object will be logged correctly at {@link java.util.logging.Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoTextMessage() {
		logger.info("Hello World!");

		if (infoEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.INFO), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link java.util.logging.Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoSupplierMessage() {
		logger.info(() -> "Hello World!");

		if (infoEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.INFO), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with arguments will be logged correctly at
	 * {@link java.util.logging.Level#INFO INFO} level.
	 */
	@Test
	public void infoLogEntryWithArguments() {
		logger.log(java.util.logging.Level.INFO, "magic = {0}", new Object[] { 42 });

		if (infoEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.INFO), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("magic = {0}"), eq(42));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link java.util.logging.Level#INFO INFO} level.
	 */
	@Test
	public void infoLogEntryWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(java.util.logging.Level.INFO, "Hello World!", exception);

		if (infoEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.INFO), same(exception),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link java.util.logging.Level#WARNING WARNING} level is enabled.
	 */
	@Test
	public void isWarningLoggable() {
		assertThat(logger.isLoggable(java.util.logging.Level.WARNING)).isEqualTo(warningEnabled);
	}

	/**
	 * Verifies that a plain text message object will be logged correctly at {@link java.util.logging.Level#WARNING
	 * WARNING} level.
	 */
	@Test
	public void warningTextMessage() {
		logger.warning("Hello World!");

		if (warningEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.WARN), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link java.util.logging.Level#WARNING WARNING}
	 * level.
	 */
	@Test
	public void warningSupplierMessage() {
		logger.warning(() -> "Hello World!");

		if (warningEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.WARN), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with arguments will be logged correctly at
	 * {@link java.util.logging.Level#WARNING WARNING} level.
	 */
	@Test
	public void warningLogEntryWithArguments() {
		logger.log(java.util.logging.Level.WARNING, "magic = {0}", new Object[] { 42 });

		if (warningEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.WARN), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("magic = {0}"), eq(42));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link java.util.logging.Level#WARNING WARNING} level.
	 */
	@Test
	public void warningLogEntryWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(java.util.logging.Level.WARNING, "Hello World!", exception);

		if (warningEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.WARN), same(exception),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link java.util.logging.Level#SEVERE SEVERE} level is enabled.
	 */
	@Test
	public void isErrorLoggable() {
		assertThat(logger.isLoggable(java.util.logging.Level.SEVERE)).isEqualTo(severeEnabled);
	}

	/**
	 * Verifies that a plain text message object will be logged correctly at {@link java.util.logging.Level#SEVERE
	 * SEVERE} level.
	 */
	@Test
	public void severeTextMessage() {
		logger.severe("Hello World!");

		if (severeEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.ERROR), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link java.util.logging.Level#SEVERE SEVERE}
	 * level.
	 */
	@Test
	public void severeSupplierMessage() {
		logger.severe(() -> "Hello World!");

		if (severeEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.ERROR), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with arguments will be logged correctly at
	 * {@link java.util.logging.Level#SEVERE SEVERE} level.
	 */
	@Test
	public void severeLogEntryWithArguments() {
		logger.log(java.util.logging.Level.SEVERE, "magic = {0}", new Object[] { 42 });

		if (severeEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.ERROR), same(null),
					any(JavaTextMessageFormatFormatter.class), eq("magic = {0}"), eq(42));
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link java.util.logging.Level#SEVERE SEVERE} level.
	 */
	@Test
	public void severeLogEntryWithException() {
		RuntimeException exception = new RuntimeException();

		logger.log(java.util.logging.Level.SEVERE, "Hello World!", exception);

		if (severeEnabled) {
			verify(provider).log(eq(Logger.class.getName()), isNull(), eq(Level.ERROR), same(exception),
					any(JavaTextMessageFormatFormatter.class), eq("Hello World!"), isNull());
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any(), any(), any());
		}
	}

}
