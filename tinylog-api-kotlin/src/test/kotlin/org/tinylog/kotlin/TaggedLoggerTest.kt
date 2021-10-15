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
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify

import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
 * Tests for [TaggedLogger].
 *
 * @param tag1Configuration
 * The logging level configuration for the first tag
 * @param tag2Configuration
 * The logging level configuration
 */
@RunWith(Parameterized::class)
class TaggedLoggerTest(private val tag1Configuration: LevelConfiguration, private val tag2Configuration: LevelConfiguration?) {

	companion object {

		/**
		 * Returns all different combinations of logging levels for up two tags for the tests.
		 *
		 * @return Each object array represents a combination. A value of null means the tag isn't present in the combination.
		 */
		@JvmStatic
		@Parameters(name = "{0}, {1}")
		fun getLevels(): Collection<Array<Any?>> {
			val levels = ArrayList<Array<Any?>>()

			LevelConfiguration.AVAILABLE_LEVELS.forEach { tag1 ->
				levels.add(arrayOf(tag1, null))
				LevelConfiguration.AVAILABLE_LEVELS.forEach { tag2 ->
					levels.add(arrayOf(tag1, tag2))
				}
			}

			return levels
		}

		/**
		 * Undoes mocking of [ProviderRegistry].
		 */
		@JvmStatic
		@AfterClass
		fun unmockProviderRegistry() {
			unmockkStatic(ProviderRegistry::class)
		}

	}

	/**
	 * Redirects and collects system output streams.
	 */
	@JvmField
	@Rule
	val systemStream = SystemStreamCollector(false)

	private val tag1 = "test"
	private val tag2 = "other tag"
	private val loggingProvider = mockk<LoggingProvider>()

	private lateinit var logger: TaggedLogger

	/**
	 * Applies the mocked logging provider and overrides all dependent fields.
	 */
	@Before
	fun applyLoggingProvider() {
		mockkStatic(ProviderRegistry::class)
		every { ProviderRegistry.getLoggingProvider() } returns loggingProvider

		every { loggingProvider.getMinimumLevel(tag1) } returns tag1Configuration.level

		every { loggingProvider.isEnabled(any(), tag1, Level.TRACE) } returns tag1Configuration.traceEnabled
		every { loggingProvider.isEnabled(any(), tag1, Level.DEBUG) } returns tag1Configuration.debugEnabled
		every { loggingProvider.isEnabled(any(), tag1, Level.INFO) } returns tag1Configuration.infoEnabled
		every { loggingProvider.isEnabled(any(), tag1, Level.WARN) } returns tag1Configuration.warnEnabled
		every { loggingProvider.isEnabled(any(), tag1, Level.ERROR) } returns tag1Configuration.errorEnabled

		logger = if (tag2Configuration == null) {
			TaggedLogger(setOf(tag1))
		} else {
			every { loggingProvider.getMinimumLevel(tag2) } returns tag2Configuration.level

			every { loggingProvider.isEnabled(any(), tag2, Level.TRACE) } returns tag2Configuration.traceEnabled
			every { loggingProvider.isEnabled(any(), tag2, Level.DEBUG) } returns tag2Configuration.debugEnabled
			every { loggingProvider.isEnabled(any(), tag2, Level.INFO) } returns tag2Configuration.infoEnabled
			every { loggingProvider.isEnabled(any(), tag2, Level.WARN) } returns tag2Configuration.warnEnabled
			every { loggingProvider.isEnabled(any(), tag2, Level.ERROR) } returns tag2Configuration.errorEnabled
			TaggedLogger(setOf(tag1, tag2))
		}

		every { loggingProvider.log(any<Int>(), any(), any(), any(), any(), any(), *anyVararg()) } returns Unit
		every { loggingProvider.log(any<String>(), any(), any(), any(), any(), any(), *anyVararg()) } returns Unit
	}

	/**
	 * Verifies evaluating whether [TRACE][Level.TRACE] level is enabled.
	 */
	@Test
	fun isTraceEnabled() {
		assertThat(logger.isTraceEnabled()).isEqualTo(
			if (tag2Configuration == null) {
				tag1Configuration.traceEnabled
			} else {
				tag1Configuration.traceEnabled || tag2Configuration.traceEnabled
			}
		)
	}

	/**
	 * Verifies that an object will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceObject() {
		logger.trace(42)

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.TRACE, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.TRACE, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceString() {
		logger.trace("Hello World!")

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.TRACE, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.TRACE, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceLazyMessage() {
		logger.trace { "Hello World!" }

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.TRACE, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.TRACE, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceMessageAndArguments() {
		logger.trace("Hello {}!", "World")

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.TRACE,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.TRACE,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceMessageAndLazyArguments() {
		logger.trace("The number is {}", { 42 })

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.TRACE,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.TRACE,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceException() {
		val exception = NullPointerException()

		logger.trace(exception)

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.TRACE, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.TRACE, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceExceptionWithMessage() {
		val exception = NullPointerException()

		logger.trace(exception, "Hello World!")

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.TRACE, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.TRACE, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.trace(exception) { "Hello World!" }

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.TRACE, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.TRACE, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [TRACE][Level.TRACE] level.
	 */
	@Test
	fun traceExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.trace(exception, "Hello {}!", "World")

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.TRACE,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.TRACE,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
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

		if (tag1Configuration.traceEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.TRACE,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.traceEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.TRACE,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies evaluating whether [DEBUG][Level.DEBUG] level is enabled.
	 */
	@Test
	fun isDebugEnabled() {
		assertThat(logger.isDebugEnabled()).isEqualTo(
			if (tag2Configuration == null) {
				tag1Configuration.debugEnabled
			} else {
				tag1Configuration.debugEnabled || tag2Configuration.debugEnabled
			}
		)
	}

	/**
	 * Verifies that an object will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugObject() {
		logger.debug(42)

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.DEBUG, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.DEBUG, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugString() {
		logger.debug("Hello World!")

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.DEBUG, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.DEBUG, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugLazyMessage() {
		logger.debug { "Hello World!" }

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.DEBUG, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.DEBUG, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugMessageAndArguments() {
		logger.debug("Hello {}!", "World")

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.DEBUG,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.DEBUG,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugMessageAndLazyArguments() {
		logger.debug("The number is {}", { 42 })

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.DEBUG,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.DEBUG,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugException() {
		val exception = NullPointerException()

		logger.debug(exception)

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.DEBUG, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.DEBUG, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugExceptionWithMessage() {
		val exception = NullPointerException()

		logger.debug(exception, "Hello World!")

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.DEBUG, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.DEBUG, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.debug(exception) { "Hello World!" }

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.DEBUG, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.DEBUG, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [DEBUG][Level.DEBUG] level.
	 */
	@Test
	fun debugExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.debug(exception, "Hello {}!", "World")

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.DEBUG,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.DEBUG,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
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

		if (tag1Configuration.debugEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.DEBUG,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.debugEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.DEBUG,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies evaluating whether [INFO][Level.INFO] level is enabled.
	 */
	@Test
	fun isInfoEnabled() {
		assertThat(logger.isInfoEnabled()).isEqualTo(
			if (tag2Configuration == null) {
				tag1Configuration.infoEnabled
			} else {
				tag1Configuration.infoEnabled || tag2Configuration.infoEnabled
			}
		)
	}

	/**
	 * Verifies that an object will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoObject() {
		logger.info(42)

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.INFO, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.INFO, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoString() {
		logger.info("Hello World!")

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.INFO, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.INFO, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoLazyMessage() {
		logger.info { "Hello World!" }

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.INFO, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.INFO, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoMessageAndArguments() {
		logger.info("Hello {}!", "World")

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.INFO,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.INFO,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [INFO][Level.INFO] level.
	 */
	@Test
	fun infoMessageAndLazyArguments() {
		logger.info("The number is {}", { 42 })

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.INFO,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.INFO,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoException() {
		val exception = NullPointerException()

		logger.info(exception)

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.INFO, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.INFO, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoExceptionWithMessage() {
		val exception = NullPointerException()

		logger.info(exception, "Hello World!")

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.INFO, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.INFO, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ INFO][Level.INFO] level.
	 */
	@Test
	fun infoExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.info(exception) { "Hello World!" }

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.INFO, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.INFO, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [INFO][Level.INFO] level.
	 */
	@Test
	fun infoExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.info(exception, "Hello {}!", "World")

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.INFO,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.INFO,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
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

		if (tag1Configuration.infoEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.INFO,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.infoEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.INFO,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies evaluating whether [WARN][Level.WARN] level is enabled.
	 */
	@Test
	fun isWarnEnabled() {
		assertThat(logger.isWarnEnabled()).isEqualTo(
			if (tag2Configuration == null) {
				tag1Configuration.warnEnabled
			} else {
				tag1Configuration.warnEnabled || tag2Configuration.warnEnabled
			}
		)
	}

	/**
	 * Verifies that an object will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnObject() {
		logger.warn(42)

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.WARN, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.WARN, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnString() {
		logger.warn("Hello World!")

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.WARN, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.WARN, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnLazyMessage() {
		logger.warn { "Hello World!" }

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.WARN, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.WARN, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnMessageAndArguments() {
		logger.warn("Hello {}!", "World")

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.WARN,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.WARN,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [WARN][Level.WARN] level.
	 */
	@Test
	fun warnMessageAndLazyArguments() {
		logger.warn("The number is {}", { 42 })

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.WARN,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.WARN,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnException() {
		val exception = NullPointerException()

		logger.warn(exception)

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.WARN, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.WARN, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnExceptionWithMessage() {
		val exception = NullPointerException()

		logger.warn(exception, "Hello World!")

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.WARN, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.WARN, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ WARN][Level.WARN] level.
	 */
	@Test
	fun warnExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.warn(exception) { "Hello World!" }

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.WARN, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.WARN, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [WARN][Level.WARN] level.
	 */
	@Test
	fun warnExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.warn(exception, "Hello {}!", "World")

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.WARN,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.WARN,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
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

		if (tag1Configuration.warnEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.WARN,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.warnEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.WARN,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies evaluating whether [ERROR][Level.ERROR] level is enabled.
	 */
	@Test
	fun isErrorEnabled() {
		assertThat(logger.isErrorEnabled()).isEqualTo(
			if (tag2Configuration == null) {
				tag1Configuration.errorEnabled
			} else {
				tag1Configuration.errorEnabled || tag2Configuration.errorEnabled
			}
		)
	}

	/**
	 * Verifies that an object will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorObject() {
		logger.error(42)

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.ERROR, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.ERROR, null, null, 42) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a plain text will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorString() {
		logger.error("Hello World!")

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.ERROR, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.ERROR, null, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorLazyMessage() {
		logger.error { "Hello World!" }

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.ERROR, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.ERROR, null, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorMessageAndArguments() {
		logger.error("Hello {}!", "World")

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.ERROR,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.ERROR,
					null,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorMessageAndLazyArguments() {
		logger.error("The number is {}", { 42 })

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.ERROR,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.ERROR,
					null,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorException() {
		val exception = NullPointerException()

		logger.error(exception)

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.ERROR, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.ERROR, exception, null, null) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorExceptionWithMessage() {
		val exception = NullPointerException()

		logger.error(exception, "Hello World!")

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.ERROR, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.ERROR, exception, null, "Hello World!") }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at [ ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorExceptionWithLazyMessage() {
		val exception = NullPointerException()

		logger.error(exception) { "Hello World!" }

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag1, Level.ERROR, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) { loggingProvider.log(2, tag2, Level.ERROR, exception, null, match(provide("Hello World!"))) }
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at [ERROR][Level.ERROR] level.
	 */
	@Test
	fun errorExceptionWithMessageAndArguments() {
		val exception = NullPointerException()

		logger.error(exception, "Hello {}!", "World")

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.ERROR,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.ERROR,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"Hello {}!",
					"World"
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
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

		if (tag1Configuration.errorEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag1,
					Level.ERROR,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag1, any(), any(), any(), any(), *anyVararg()) }
		}

		if (tag2Configuration != null && tag2Configuration.errorEnabled) {
			verify(exactly = 1) {
				loggingProvider.log(
					2,
					tag2,
					Level.ERROR,
					exception,
					ofType(AdvancedMessageFormatter::class),
					"The number is {}",
					match(provide(42))
				)
			}
		} else {
			verify(exactly = 0) { loggingProvider.log(any<Int>(), tag2, any(), any(), any(), any(), *anyVararg()) }
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
