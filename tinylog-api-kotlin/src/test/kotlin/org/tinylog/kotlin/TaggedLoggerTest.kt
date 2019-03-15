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

package org.tinylog.kotlin

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.ArrayList

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.tinylog.Level
import org.tinylog.provider.LoggingProvider
import org.tinylog.provider.ProviderRegistry
import org.tinylog.rules.SystemStreamCollector

import org.assertj.core.api.Assertions.assertThat
import org.junit.*
import org.tinylog.Supplier

/**
 * Tests for [TaggedLogger].
 *
 * @param level
 * Actual severity level under test
 * @param traceEnabled
 * Determines if [TRACE][Level.TRACE] level is enabled
 * @param debugEnabled
 * Determines if [DEBUG][Level.DEBUG] level is enabled
 * @param infoEnabled
 * Determines if [INFO][Level.INFO] level is enabled
 * @param warnEnabled
 * Determines if [WARN][Level.WARN] level is enabled
 * @param errorEnabled
 * Determines if [ERROR][Level.ERROR] level is enabled
 */
@RunWith(Parameterized::class)
class TaggedLoggerTest(private val level: Level, private val traceEnabled: Boolean, private val debugEnabled: Boolean,
	                   private val infoEnabled: Boolean, private val warnEnabled: Boolean, private val errorEnabled: Boolean) {

	companion object {

		/**
		 * Returns for all severity levels which severity levels are enabled.
		 *
		 * @return Each object array contains the severity level itself and five booleans for [TRACE][Level.TRACE]
		 * ... [ERROR][Level.ERROR] to determine whether these severity levels are enabled
		 */
		@JvmStatic
		@Parameters(name = "{0}")
		fun getLevels(): Collection<Array<Any>> {
			val levels = ArrayList<Array<Any>>()

			// @formatter:off
			levels.add(arrayOf(Level.TRACE, true,  true,  true,  true,  true))
			levels.add(arrayOf(Level.DEBUG, false, true,  true,  true,  true))
			levels.add(arrayOf(Level.INFO,  false, false, true,  true,  true))
			levels.add(arrayOf(Level.WARN,  false, false, false, true,  true))
			levels.add(arrayOf(Level.ERROR, false, false, false, false, true))
			levels.add(arrayOf(Level.OFF,   false, false, false, false, false))
			// @formatter:on

			return levels
		}

	}

	/**
	 * Redirects and collects system output streams.
	 */
	@JvmField
	@Rule
	public val systemStream = SystemStreamCollector(false)

	private val tag = "test"
	private val loggingProvider = mockk<LoggingProvider>()
	private val logger = TaggedLogger(tag)

	/**
	 * Applies the mocked logging provider and overrides all depending fields.
	 */
	@Before
	fun applyLoggingProvider() {
		every { loggingProvider.getMinimumLevel(tag) } returns level

		every { loggingProvider.isEnabled(any(), tag, Level.TRACE) } returns  traceEnabled
		every { loggingProvider.isEnabled(any(), tag, Level.DEBUG) } returns  debugEnabled
		every { loggingProvider.isEnabled(any(), tag, Level.INFO) } returns  infoEnabled
		every { loggingProvider.isEnabled(any(), tag, Level.WARN) } returns  warnEnabled
		every { loggingProvider.isEnabled(any(), tag, Level.ERROR) } returns  errorEnabled

		every { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) } returns Unit
		every { loggingProvider.log(any<String>(), any(), any(), any(), any(), *anyVararg()) } returns Unit

		Whitebox.setProperty(logger, LoggingProvider::class, loggingProvider)
		Whitebox.setProperty(logger, "minimumLevelCoversTrace", traceEnabled)
		Whitebox.setProperty(logger, "minimumLevelCoversDebug", debugEnabled)
		Whitebox.setProperty(logger, "minimumLevelCoversInfo", infoEnabled)
		Whitebox.setProperty(logger, "minimumLevelCoversWarn", warnEnabled)
		Whitebox.setProperty(logger, "minimumLevelCoversError", errorEnabled)
	}

	/**
	 * Resets the logging provider and all overridden fields in [TaggedLogger].
	 */
	@After
	fun resetLoggingProvider() {
		Whitebox.setProperty(logger, LoggingProvider::class, ProviderRegistry.getLoggingProvider())
		Whitebox.setProperty(logger, "minimumLevelCoversTrace", isCoveredByMinimumLevel(Level.TRACE))
		Whitebox.setProperty(logger, "minimumLevelCoversDebug", isCoveredByMinimumLevel(Level.DEBUG))
		Whitebox.setProperty(logger, "minimumLevelCoversInfo", isCoveredByMinimumLevel(Level.INFO))
		Whitebox.setProperty(logger, "minimumLevelCoversWarn", isCoveredByMinimumLevel(Level.WARN))
		Whitebox.setProperty(logger, "minimumLevelCoversError", isCoveredByMinimumLevel(Level.ERROR))
	}

	/**
	 * Verifies evaluating whether a specific severity level is covered by the minimum severity level.
	 */
	@Test
	fun coveredByMinimumLevel() {
		assertThat(isCoveredByMinimumLevel(Level.TRACE)).isEqualTo(traceEnabled)
		assertThat(isCoveredByMinimumLevel(Level.DEBUG)).isEqualTo(debugEnabled)
		assertThat(isCoveredByMinimumLevel(Level.INFO)).isEqualTo(infoEnabled)
		assertThat(isCoveredByMinimumLevel(Level.WARN)).isEqualTo(warnEnabled)
		assertThat(isCoveredByMinimumLevel(Level.ERROR)).isEqualTo(errorEnabled)
	}

	/**
	 * Verifies evaluating whether [TRACE][Level.TRACE] level is enabled.
	 */
	@Test
	fun isTraceEnabled() {
		assertThat(logger.isTraceEnabled()).isEqualTo(traceEnabled)
	}

	/**
	 * Verifies that an object will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceObject() {
		logger.trace(42)

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceString() {
		logger.trace("Hello World!")

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceLazyMessage() {
		logger.trace{ "Hello World!" }

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceMessageAndArguments() {
		logger.trace("Hello {}!", "World")

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, null, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceMessageAndLazyArguments() {
		logger.trace("The number is {}", { 42 })

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, null, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceException() {
		val exception = NullPointerException()

		logger.trace(exception)

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, exception,null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceExceptionWithMessage() {
		val exception = NullPointerException()

		logger.trace(exception, "Hello World!")

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, exception,"Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.trace(exception) { "Hello World!" }

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, exception, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.trace(exception, "Hello {}!", "World")

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, exception, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
	 * correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceExceptionWithMessageAndLazyArguments() {
		val exception = NullPointerException()

		logger.trace(exception, "The number is {}", { 42 })

		if (traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.TRACE, exception, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies evaluating whether [DEBUG][Level.DEBUG] level is enabled.
	 */
	@Test
	fun isDebugEnabled() {
		assertThat(logger.isDebugEnabled()).isEqualTo(debugEnabled)
	}

	/**
	 * Verifies that an object will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugObject() {
		logger.debug(42)

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugString() {
		logger.debug("Hello World!")

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugLazyMessage() {
		logger.debug{ "Hello World!" }

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugMessageAndArguments() {
		logger.debug("Hello {}!", "World")

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, null, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugMessageAndLazyArguments() {
		logger.debug("The number is {}", { 42 })

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, null, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugException() {
		val exception = NullPointerException()

		logger.debug(exception)

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, exception,null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugExceptionWithMessage() {
		val exception = NullPointerException()

		logger.debug(exception, "Hello World!")

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, exception,"Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.debug(exception) { "Hello World!" }

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, exception, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.debug(exception, "Hello {}!", "World")

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, exception, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
	 * correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugExceptionWithMessageAndLazyArguments() {
		val exception = NullPointerException()

		logger.debug(exception, "The number is {}", { 42 })

		if (debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.DEBUG, exception, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies evaluating whether [INFO][Level.INFO] level is enabled.
	 */
	@Test
	fun isInfoEnabled() {
		assertThat(logger.isInfoEnabled()).isEqualTo(infoEnabled)
	}

	/**
	 * Verifies that an object will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoObject() {
		logger.info(42)

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoString() {
		logger.info("Hello World!")

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoLazyMessage() {
		logger.info{ "Hello World!" }

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoMessageAndArguments() {
		logger.info("Hello {}!", "World")

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, null, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [INFO][Level.INFO] level.
	 */
	@Test
	fun infoMessageAndLazyArguments() {
		logger.info("The number is {}", { 42 })

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, null, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoException() {
		val exception = NullPointerException()

		logger.info(exception)

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, exception,null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoExceptionWithMessage() {
		val exception = NullPointerException()

		logger.info(exception, "Hello World!")

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, exception,"Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ INFO][Level.INFO] level.
	 */
	@Test
	fun infoExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.info(exception) { "Hello World!" }

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, exception, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.info(exception, "Hello {}!", "World")

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, exception, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
	 * correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoExceptionWithMessageAndLazyArguments() {
		val exception = NullPointerException()

		logger.info(exception, "The number is {}", { 42 })

		if (infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.INFO, exception, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies evaluating whether [WARN][Level.WARN] level is enabled.
	 */
	@Test
	fun isWarnEnabled() {
		assertThat(logger.isWarnEnabled()).isEqualTo(warnEnabled)
	}

	/**
	 * Verifies that an object will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnObject() {
		logger.warn(42)

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnString() {
		logger.warn("Hello World!")

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnLazyMessage() {
		logger.warn{ "Hello World!" }

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnMessageAndArguments() {
		logger.warn("Hello {}!", "World")

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, null, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [WARN][Level.WARN] level.
	 */
	@Test
	fun warnMessageAndLazyArguments() {
		logger.warn("The number is {}", { 42 })

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, null, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnException() {
		val exception = NullPointerException()

		logger.warn(exception)

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, exception,null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnExceptionWithMessage() {
		val exception = NullPointerException()

		logger.warn(exception, "Hello World!")

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, exception,"Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ WARN][Level.WARN] level.
	 */
	@Test
	fun warnExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.warn(exception) { "Hello World!" }

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, exception, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.warn(exception, "Hello {}!", "World")

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, exception, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
	 * correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnExceptionWithMessageAndLazyArguments() {
		val exception = NullPointerException()

		logger.warn(exception, "The number is {}", { 42 })

		if (warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.WARN, exception, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies evaluating whether [ERROR][Level.ERROR] level is enabled.
	 */
	@Test
	fun isErrorEnabled() {
		assertThat(logger.isErrorEnabled()).isEqualTo(errorEnabled)
	}

	/**
	 * Verifies that an object will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorObject() {
		logger.error(42)

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorString() {
		logger.error("Hello World!")

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorLazyMessage() {
		logger.error{ "Hello World!" }

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorMessageAndArguments() {
		logger.error("Hello {}!", "World")

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, null, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorMessageAndLazyArguments() {
		logger.error("The number is {}", { 42 })

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, null, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorException() {
		val exception = NullPointerException()

		logger.error(exception)

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, exception,null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorExceptionWithMessage() {
		val exception = NullPointerException()

		logger.error(exception, "Hello World!")

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, exception,"Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.error(exception) { "Hello World!" }

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, exception, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.error(exception, "Hello {}!", "World")

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, exception, "Hello {}!", "World") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
	 * correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorExceptionWithMessageAndLazyArguments() {
		val exception = NullPointerException()

		logger.error(exception, "The number is {}", { 42 })

		if (errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag, Level.ERROR, exception, "The number is {}", match(provide(42))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Creates a functions that checks whether a passed object is a [Supplier] that returns a defined value.
	 *
	 * @param
	 * Expected return value from supplier
	 * @return
	 * Match function
	 */
	private inline fun <reified T : Any> provide(value: Any?): (T) -> Boolean {
		return { it is Supplier<*> && value == it.get() }
	}

	/**
	 * Invokes the private method [TaggedLogger.isCoveredByMinimumLevel].
	 *
	 * @param level
	 * Severity level to check
	 * @return `true` if given severity level is covered, otherwise `false`
	 */
	private fun isCoveredByMinimumLevel(level: Level): Boolean {
		return Whitebox.callMethod(logger, "isCoveredByMinimumLevel", tag, level) as Boolean
	}

}
