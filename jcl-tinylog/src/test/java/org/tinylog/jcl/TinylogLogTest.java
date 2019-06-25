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

package org.tinylog.jcl;

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
 * Tests for {@link TinylogLog}.
 */
@RunWith(Parameterized.class)
@PrepareForTest(TinylogLog.class)
public final class TinylogLogTest {

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
	private TinylogLog log;

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
	public TinylogLogTest(final Level level, final boolean traceEnabled, final boolean debugEnabled, final boolean infoEnabled,
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

		log = new TinylogLog();
		Whitebox.setInternalState(TinylogLog.class, "MINIMUM_LEVEL_COVERS_TRACE", traceEnabled);
		Whitebox.setInternalState(TinylogLog.class, "MINIMUM_LEVEL_COVERS_DEBUG", debugEnabled);
		Whitebox.setInternalState(TinylogLog.class, "MINIMUM_LEVEL_COVERS_INFO", infoEnabled);
		Whitebox.setInternalState(TinylogLog.class, "MINIMUM_LEVEL_COVERS_WARN", warnEnabled);
		Whitebox.setInternalState(TinylogLog.class, "MINIMUM_LEVEL_COVERS_ERROR", errorEnabled);
		Whitebox.setInternalState(TinylogLog.class, provider);
	}

	/**
	 * Resets the underlying logging provider.
	 */
	@After
	public void reset() {
		Whitebox.setInternalState(TinylogLog.class, ProviderRegistry.getLoggingProvider());
	}

	/**
	 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
	 */
	@Test
	public void isTraceEnabled() {
		assertThat(log.isTraceEnabled()).isEqualTo(traceEnabled);
	}

	/**
	 * Verifies that a message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessage() {
		log.trace("Hello World!");

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, null, "Hello World!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceException() {
		RuntimeException exception = new RuntimeException();

		log.trace(exception, exception);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a custom message with an exception will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndException() {
		RuntimeException exception = new RuntimeException();

		log.trace("Boom!", exception);

		if (traceEnabled) {
			verify(provider).log(2, null, Level.TRACE, exception, "Boom!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#DEBUG DEBUG} level is enabled.
	 */
	@Test
	public void isDebugEnabled() {
		assertThat(log.isDebugEnabled()).isEqualTo(debugEnabled);
	}

	/**
	 * Verifies that a message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessage() {
		log.debug("Hello World!");

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, null, "Hello World!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugException() {
		RuntimeException exception = new RuntimeException();

		log.debug(exception, exception);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a custom message with an exception will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndException() {
		RuntimeException exception = new RuntimeException();

		log.debug("Boom!", exception);

		if (debugEnabled) {
			verify(provider).log(2, null, Level.DEBUG, exception, "Boom!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#INFO INFO} level is enabled.
	 */
	@Test
	public void isInfoEnabled() {
		assertThat(log.isInfoEnabled()).isEqualTo(infoEnabled);
	}

	/**
	 * Verifies that a message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessage() {
		log.info("Hello World!");

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, null, "Hello World!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoException() {
		RuntimeException exception = new RuntimeException();

		log.info(exception, exception);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a custom message with an exception will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessageAndException() {
		RuntimeException exception = new RuntimeException();

		log.info("Boom!", exception);

		if (infoEnabled) {
			verify(provider).log(2, null, Level.INFO, exception, "Boom!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#WARN WARN} level is enabled.
	 */
	@Test
	public void isWarnEnabled() {
		assertThat(log.isWarnEnabled()).isEqualTo(warnEnabled);
	}

	/**
	 * Verifies that a message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnMessage() {
		log.warn("Hello World!");

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, null, "Hello World!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnException() {
		RuntimeException exception = new RuntimeException();

		log.warn(exception, exception);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a custom message with an exception will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnMessageAndException() {
		RuntimeException exception = new RuntimeException();

		log.warn("Boom!", exception);

		if (warnEnabled) {
			verify(provider).log(2, null, Level.WARN, exception, "Boom!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#ERROR ERROR} level is enabled.
	 */
	@Test
	public void isErrorEnabled() {
		assertThat(log.isErrorEnabled()).isEqualTo(errorEnabled);
	}

	/**
	 * Verifies that a message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessage() {
		log.error("Hello World!");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello World!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorException() {
		RuntimeException exception = new RuntimeException();

		log.error(exception, exception);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a custom message with an exception will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndException() {
		RuntimeException exception = new RuntimeException();

		log.error("Boom!", exception);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Boom!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#ERROR ERROR} level is enabled.
	 */
	@Test
	public void isFatalEnabled() {
		assertThat(log.isFatalEnabled()).isEqualTo(errorEnabled);
	}

	/**
	 * Verifies that a message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalMessage() {
		log.fatal("Hello World!");

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, null, "Hello World!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalException() {
		RuntimeException exception = new RuntimeException();

		log.fatal(exception, exception);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, null);
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a custom message with an exception will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void fatalMessageAndException() {
		RuntimeException exception = new RuntimeException();

		log.fatal("Boom!", exception);

		if (errorEnabled) {
			verify(provider).log(2, null, Level.ERROR, exception, "Boom!");
		} else {
			verify(provider, never()).log(anyInt(), anyString(), any(), any(), any());
		}
	}

}
