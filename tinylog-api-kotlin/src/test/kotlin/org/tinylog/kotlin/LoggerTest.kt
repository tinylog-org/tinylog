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

import java.util.ArrayList

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.tinylog.Level
import org.tinylog.Supplier
import org.tinylog.format.AdvancedMessageFormatter
import org.tinylog.provider.LoggingProvider
import org.tinylog.provider.ProviderRegistry
import org.tinylog.rules.SystemStreamCollector

/**
 * Tests for [Logger].
 */
@RunWith(Enclosed::class)
class LoggerTest {

	/**
	 * Tests for logging methods.
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
	class Logging(private val level: Level, private val traceEnabled: Boolean, private val debugEnabled: Boolean,
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

			/**
			 * Resets the logging provider and all overridden fields in [Logger].
			 */
			@JvmStatic
			@AfterClass
			fun resetLoggingProvider() {
				Whitebox.setProperty(Logger, LoggingProvider::class, ProviderRegistry.getLoggingProvider())
				Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_TRACE", isCoveredByMinimumLevel(Level.TRACE))
				Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_DEBUG", isCoveredByMinimumLevel(Level.DEBUG))
				Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_INFO", isCoveredByMinimumLevel(Level.INFO))
				Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_WARN", isCoveredByMinimumLevel(Level.WARN))
				Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_ERROR", isCoveredByMinimumLevel(Level.ERROR))
			}

			/**
			 * Invokes the private method [Logger.isCoveredByMinimumLevel].
			 *
			 * @param level
			 * Severity level to check
			 * @return `true` if given severity level is covered, otherwise `false`
			 */
			private fun isCoveredByMinimumLevel(level: Level): Boolean {
				return Whitebox.callMethod(Logger, "isCoveredByMinimumLevel", level) as Boolean
			}

		}

		/**
		 * Redirects and collects system output streams.
		 */
		@JvmField
		@Rule
		val systemStream = SystemStreamCollector(false)

		private val loggingProvider = mockk<LoggingProvider>()

		/**
		 * Applies the mocked logging provider and overrides all depending fields.
		 */
		@Before
		fun applyLoggingProvider() {
			every { loggingProvider.getMinimumLevel(null) } returns level

			every { loggingProvider.isEnabled(any(), null, Level.TRACE) } returns  traceEnabled
			every { loggingProvider.isEnabled(any(), null, Level.DEBUG) } returns  debugEnabled
			every { loggingProvider.isEnabled(any(), null, Level.INFO) } returns  infoEnabled
			every { loggingProvider.isEnabled(any(), null, Level.WARN) } returns  warnEnabled
			every { loggingProvider.isEnabled(any(), null, Level.ERROR) } returns  errorEnabled

			every { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) } returns Unit
			every { loggingProvider.log(any<String>(), any(), any(), any(), any(), any(), *anyVararg()) } returns Unit

			Whitebox.setProperty(Logger, LoggingProvider::class, loggingProvider)
			Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_TRACE", traceEnabled)
			Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_DEBUG", debugEnabled)
			Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_INFO", infoEnabled)
			Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_WARN", warnEnabled)
			Whitebox.setProperty(Logger, "MINIMUM_LEVEL_COVERS_ERROR", errorEnabled)
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
			assertThat(Logger.isTraceEnabled()).isEqualTo(traceEnabled)
		}

		/**
		 * Verifies that an object will be logged correctly at [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceObject() {
			Logger.trace(42)

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, null, null, 42) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a plain text will be logged correctly at [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceString() {
			Logger.trace("Hello World!")

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, null, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a lazy message supplier will be logged correctly at [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceLazyMessage() {
			Logger.trace { "Hello World!" }

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, null, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceMessageAndArguments() {
			Logger.trace("Hello {}!", "World")

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, null, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
		 * [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceMessageAndLazyArguments() {
			Logger.trace("The number is {}", { 42 })

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, null, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception will be logged correctly at [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceException() {
			val exception = NullPointerException()

			Logger.trace(exception)

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, exception, null, null) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom message will be logged correctly at [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceExceptionWithMessage() {
			val exception = NullPointerException()

			Logger.trace(exception, "Hello World!")

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, exception, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceExceptionWithLazyMessage() {
			val exception = NullPointerException()

			Logger.trace(exception) { "Hello World!" }

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, exception, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceExceptionWithMessageAndArguments() {
			val exception = NullPointerException()

			Logger.trace(exception, "Hello {}!", "World")

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, exception, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at [TRACE][Level.TRACE] level.
		 */
		@Test
		fun traceExceptionWithMessageAndLazyArguments() {
			val exception = NullPointerException()

			Logger.trace(exception, "The number is {}", { 42 })

			if (traceEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.TRACE, exception, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies evaluating whether [DEBUG][Level.DEBUG] level is enabled.
		 */
		@Test
		fun isDebugEnabled() {
			assertThat(Logger.isDebugEnabled()).isEqualTo(debugEnabled)
		}

		/**
		 * Verifies that an object will be logged correctly at [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugObject() {
			Logger.debug(42)

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, null, null, 42) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a plain text will be logged correctly at [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugString() {
			Logger.debug("Hello World!")

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, null, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a lazy message supplier will be logged correctly at [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugLazyMessage() {
			Logger.debug { "Hello World!" }

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, null, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugMessageAndArguments() {
			Logger.debug("Hello {}!", "World")

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, null, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
		 * [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugMessageAndLazyArguments() {
			Logger.debug("The number is {}", { 42 })

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, null, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception will be logged correctly at [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugException() {
			val exception = NullPointerException()

			Logger.debug(exception)

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, exception, null, null) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom message will be logged correctly at [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugExceptionWithMessage() {
			val exception = NullPointerException()

			Logger.debug(exception, "Hello World!")

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, exception, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugExceptionWithLazyMessage() {
			val exception = NullPointerException()

			Logger.debug(exception) { "Hello World!" }

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, exception, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugExceptionWithMessageAndArguments() {
			val exception = NullPointerException()

			Logger.debug(exception, "Hello {}!", "World")

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, exception, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at [DEBUG][Level.DEBUG] level.
		 */
		@Test
		fun debugExceptionWithMessageAndLazyArguments() {
			val exception = NullPointerException()

			Logger.debug(exception, "The number is {}", { 42 })

			if (debugEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.DEBUG, exception, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies evaluating whether [INFO][Level.INFO] level is enabled.
		 */
		@Test
		fun isInfoEnabled() {
			assertThat(Logger.isInfoEnabled()).isEqualTo(infoEnabled)
		}

		/**
		 * Verifies that an object will be logged correctly at [INFO][Level.INFO] level.
		 */
		@Test
		fun infoObject() {
			Logger.info(42)

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, null, null, 42) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a plain text will be logged correctly at [INFO][Level.INFO] level.
		 */
		@Test
		fun infoString() {
			Logger.info("Hello World!")

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, null, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a lazy message supplier will be logged correctly at [INFO][Level.INFO] level.
		 */
		@Test
		fun infoLazyMessage() {
			Logger.info { "Hello World!" }

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, null, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at [INFO][Level.INFO] level.
		 */
		@Test
		fun infoMessageAndArguments() {
			Logger.info("Hello {}!", "World")

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, null, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
		 * [INFO][Level.INFO] level.
		 */
		@Test
		fun infoMessageAndLazyArguments() {
			Logger.info("The number is {}", { 42 })

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, null, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception will be logged correctly at [INFO][Level.INFO] level.
		 */
		@Test
		fun infoException() {
			val exception = NullPointerException()

			Logger.info(exception)

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, exception, null, null) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom message will be logged correctly at [INFO][Level.INFO] level.
		 */
		@Test
		fun infoExceptionWithMessage() {
			val exception = NullPointerException()

			Logger.info(exception, "Hello World!")

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, exception, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ INFO][Level.INFO] level.
		 */
		@Test
		fun infoExceptionWithLazyMessage() {
			val exception = NullPointerException()

			Logger.info(exception) { "Hello World!" }

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, exception, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at [INFO][Level.INFO] level.
		 */
		@Test
		fun infoExceptionWithMessageAndArguments() {
			val exception = NullPointerException()

			Logger.info(exception, "Hello {}!", "World")

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, exception, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at [INFO][Level.INFO] level.
		 */
		@Test
		fun infoExceptionWithMessageAndLazyArguments() {
			val exception = NullPointerException()

			Logger.info(exception, "The number is {}", { 42 })

			if (infoEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.INFO, exception, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies evaluating whether [WARN][Level.WARN] level is enabled.
		 */
		@Test
		fun isWarnEnabled() {
			assertThat(Logger.isWarnEnabled()).isEqualTo(warnEnabled)
		}

		/**
		 * Verifies that an object will be logged correctly at [WARN][Level.WARN] level.
		 */
		@Test
		fun warnObject() {
			Logger.warn(42)

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, null, null, 42) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a plain text will be logged correctly at [WARN][Level.WARN] level.
		 */
		@Test
		fun warnString() {
			Logger.warn("Hello World!")

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, null, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a lazy message supplier will be logged correctly at [WARN][Level.WARN] level.
		 */
		@Test
		fun warnLazyMessage() {
			Logger.warn { "Hello World!" }

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, null, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at [WARN][Level.WARN] level.
		 */
		@Test
		fun warnMessageAndArguments() {
			Logger.warn("Hello {}!", "World")

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, null, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
		 * [WARN][Level.WARN] level.
		 */
		@Test
		fun warnMessageAndLazyArguments() {
			Logger.warn("The number is {}", { 42 })

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, null, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception will be logged correctly at [WARN][Level.WARN] level.
		 */
		@Test
		fun warnException() {
			val exception = NullPointerException()

			Logger.warn(exception)

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, exception, null, null) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom message will be logged correctly at [WARN][Level.WARN] level.
		 */
		@Test
		fun warnExceptionWithMessage() {
			val exception = NullPointerException()

			Logger.warn(exception, "Hello World!")

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, exception, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ WARN][Level.WARN] level.
		 */
		@Test
		fun warnExceptionWithLazyMessage() {
			val exception = NullPointerException()

			Logger.warn(exception) { "Hello World!" }

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, exception, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at [WARN][Level.WARN] level.
		 */
		@Test
		fun warnExceptionWithMessageAndArguments() {
			val exception = NullPointerException()

			Logger.warn(exception, "Hello {}!", "World")

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, exception, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at [WARN][Level.WARN] level.
		 */
		@Test
		fun warnExceptionWithMessageAndLazyArguments() {
			val exception = NullPointerException()

			Logger.warn(exception, "The number is {}", { 42 })

			if (warnEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.WARN, exception, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies evaluating whether [ERROR][Level.ERROR] level is enabled.
		 */
		@Test
		fun isErrorEnabled() {
			assertThat(Logger.isErrorEnabled()).isEqualTo(errorEnabled)
		}

		/**
		 * Verifies that an object will be logged correctly at [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorObject() {
			Logger.error(42)

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, null, null, 42) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a plain text will be logged correctly at [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorString() {
			Logger.error("Hello World!")

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, null, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a lazy message supplier will be logged correctly at [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorLazyMessage() {
			Logger.error { "Hello World!" }

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, null, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message will be logged correctly at [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorMessageAndArguments() {
			Logger.error("Hello {}!", "World")

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, null, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
		 * [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorMessageAndLazyArguments() {
			Logger.error("The number is {}", { 42 })

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, null, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception will be logged correctly at [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorException() {
			val exception = NullPointerException()

			Logger.error(exception)

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, exception, null, null) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom message will be logged correctly at [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorExceptionWithMessage() {
			val exception = NullPointerException()

			Logger.error(exception, "Hello World!")

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, exception, null, "Hello World!") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorExceptionWithLazyMessage() {
			val exception = NullPointerException()

			Logger.error(exception) { "Hello World!" }

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, exception, null, match(provide("Hello World!"))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message will be logged correctly at [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorExceptionWithMessageAndArguments() {
			val exception = NullPointerException()

			Logger.error(exception, "Hello {}!", "World")

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, exception, ofType(AdvancedMessageFormatter::class), "Hello {}!", "World") }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
			}
		}

		/**
		 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged
		 * correctly at [ERROR][Level.ERROR] level.
		 */
		@Test
		fun errorExceptionWithMessageAndLazyArguments() {
			val exception = NullPointerException()

			Logger.error(exception, "The number is {}", { 42 })

			if (errorEnabled) {
				verify(exactly = 1) { loggingProvider.log(2, null, Level.ERROR, exception, ofType(AdvancedMessageFormatter::class), "The number is {}", match(provide(42))) }
			} else {
				verify(exactly = 0) { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) }
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

	}

	/**
	 * Tests for receiving tagged logger instances.
	 */
	class Tagging {

		/**
		 * Redirects and collects system output streams.
		 */
		@JvmField
		@Rule
		val systemStream = SystemStreamCollector(false)

		/**
		 * Verifies that [Logger.tag] returns the same untagged instance of [TaggedLogger] for
		 * `null` and empty strings.
		 */
		@Test
		fun untagged() {
			val logger = Logger.tag(null)

			assertThat(logger).isSameAs(Logger.tag(""))
			assertThat(Whitebox.getProperty(logger, "tag")).isNull()
		}

		/**
		 * Verifies that [Logger.tag] returns the same tagged instance of [TaggedLogger] for each
		 * tag.
		 */
		@Test
		fun tagged() {
			val logger = Logger.tag("test")

			assertThat(logger).isSameAs(Logger.tag("test")).isNotSameAs(Logger.tag("other"))
			assertThat(Whitebox.getProperty(logger, "tag")).isEqualTo("test")
		}

	}

}
