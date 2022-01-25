package org.tinylog.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.notNull
import org.mockito.kotlin.whenever
import org.tinylog.core.Framework
import org.tinylog.core.Level
import org.tinylog.core.backend.LevelVisibility
import org.tinylog.core.backend.LoggingBackend
import org.tinylog.core.backend.OutputDetails
import org.tinylog.core.test.log.CaptureLogEntries
import org.tinylog.core.test.log.Log
import org.tinylog.core.test.log.LogEntry
import java.util.function.Consumer
import javax.inject.Inject

internal class TaggedLoggerTest {
	/**
	 * Tests for category tags.
	 */
	@CaptureLogEntries
	@Nested
	inner class Tags {
		@Inject
		private lateinit var framework: Framework

		/**
		 * Verifies that a string can be assigned as tag.
		 */
		@Test
		fun stringTag() {
			val logger = TaggedLogger("dummy", framework)
			assertThat(logger.tag).isEqualTo("dummy")
		}

		/**
		 * Verifies that `null` can be passed as tag for creating an untagged logger.
		 */
		@Test
		fun nullTag() {
			val logger = TaggedLogger(null, framework)
			assertThat(logger.tag).isNull()
		}
	}

	/**
	 * Tests for severity levels.
	 */
	@ExtendWith(MockitoExtension::class)
	@Nested
	inner class Levels {
		@Mock(lenient = true)
		private lateinit var backend: LoggingBackend

		private val framework: Framework = object : Framework(false, false) {
			override fun getLoggingBackend(): LoggingBackend {
				return backend
			}
		}

		/**
		 * Verifies the results of the [TaggedLogger.isTraceEnabled] method.
		 *
		 * @param enabled The value for [LoggingBackend.isEnabled]
		 * @param outputDetails The value for [LevelVisibility.getTrace]
		 */
		@ParameterizedTest
		@CsvSource(
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		)
		fun isTraceEnabled(enabled: Boolean, outputDetails: OutputDetails) {
			whenever(backend.getLevelVisibility("test")).thenReturn(
				LevelVisibility(
					outputDetails,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			)
	
			whenever(backend.isEnabled(notNull(), eq("test"), eq(Level.TRACE))).thenReturn(enabled)

			val logger = TaggedLogger("test", framework)
			assertThat(logger.isTraceEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
		}

		/**
		 * Verifies the results of the [TaggedLogger.isDebugEnabled] method.
		 *
		 * @param enabled The value for [LoggingBackend.isEnabled]
		 * @param outputDetails The value for [LevelVisibility.getDebug]
		 */
		@ParameterizedTest
		@CsvSource(
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		)
		fun isDebugEnabled(enabled: Boolean, outputDetails: OutputDetails) {
			whenever(backend.getLevelVisibility("test")).thenReturn(
				LevelVisibility(
					OutputDetails.DISABLED,
					outputDetails,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			)

			whenever(backend.isEnabled(notNull(), eq("test"), eq(Level.DEBUG))).thenReturn(enabled)

			val logger = TaggedLogger("test", framework)
			assertThat(logger.isDebugEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
		}

		/**
		 * Verifies the results of the [TaggedLogger.isInfoEnabled] method.
		 *
		 * @param enabled The value for [LoggingBackend.isEnabled]
		 * @param outputDetails The value for [LevelVisibility.getInfo]
		 */
		@ParameterizedTest
		@CsvSource(
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		)
		fun isInfoEnabled(enabled: Boolean, outputDetails: OutputDetails) {
			whenever(backend.getLevelVisibility("test")).thenReturn(
				LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					outputDetails,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED
				)
			)

			whenever(backend.isEnabled(notNull(), eq("test"), eq(Level.INFO))).thenReturn(enabled)

			val logger = TaggedLogger("test", framework)
			assertThat(logger.isInfoEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
		}

		/**
		 * Verifies the results of the [TaggedLogger.isWarnEnabled] method.
		 *
		 * @param enabled The value for [LoggingBackend.isEnabled]
		 * @param outputDetails The value for [LevelVisibility.getWarn]
		 */
		@ParameterizedTest
		@CsvSource(
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		)
		fun isWarnEnabled(enabled: Boolean, outputDetails: OutputDetails) {
			whenever(backend.getLevelVisibility("test")).thenReturn(
				LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					outputDetails,
					OutputDetails.DISABLED
				)
			)

			whenever(backend.isEnabled(notNull(), eq("test"), eq(Level.WARN))).thenReturn(enabled)

			val logger = TaggedLogger("test", framework)
			assertThat(logger.isWarnEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
		}

		/**
		 * Verifies the results of the [TaggedLogger.isErrorEnabled] method.
		 *
		 * @param enabled The value for [LoggingBackend.isEnabled]
		 * @param outputDetails The value for [LevelVisibility.getError]
		 */
		@ParameterizedTest
		@CsvSource(
			"false, DISABLED                              ",
			"true , DISABLED                              ",
			"false, ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"true , ENABLED_WITHOUT_LOCATION_INFORMATION  ",
			"false, ENABLED_WITH_CALLER_CLASS_NAME        ",
			"true , ENABLED_WITH_CALLER_CLASS_NAME        ",
			"false, ENABLED_WITH_FULL_LOCATION_INFORMATION",
			"true , ENABLED_WITH_FULL_LOCATION_INFORMATION"
		)
		fun isErrorEnabled(enabled: Boolean, outputDetails: OutputDetails) {
			whenever(backend.getLevelVisibility("test")).thenReturn(
				LevelVisibility(
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					OutputDetails.DISABLED,
					outputDetails
				)
			)

			whenever(backend.isEnabled(notNull(), eq("test"), eq(Level.ERROR))).thenReturn(enabled)

			val logger = TaggedLogger("test", framework)
			assertThat(logger.isErrorEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
		}
	}

	/**
	 * Tests for issuing log entries.
	 */
	@Nested
	inner class LogEntries {
		@Inject
		private lateinit var framework: Framework

		@Inject
		private lateinit var log: Log

		private lateinit var logger: TaggedLogger

		/**
		 * Creates the tagged logger instance and clears all trace and debug log entries, which have been issued while
		 * the creation.
		 */
		@BeforeEach
		fun init() {
			logger = TaggedLogger("test", framework)
			assertThat(log.consume())
				.allSatisfy(Consumer { assertThat(it.level).isGreaterThanOrEqualTo(Level.DEBUG) })
		}

		/**
		 * Tests issuing log entries if the assigned severity level is enabled.
		 */
		@Nested
		inner class Enabled {
			/**
			 * Verifies that a trace log entry with an object can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceObjectMessage() {
				logger.trace(42)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "42"))
			}

			/**
			 * Verifies that a trace log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceTextMessage() {
				logger.trace("Hello World!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "Hello World!"))
			}

			/**
			 * Verifies that a trace log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceLazyMessage() {
				logger.trace { "Hello World!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "Hello World!"))
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceFormattedMessageWithArgument() {
				logger.trace("Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "Hello Alice!"))
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceFormattedMessageWithLazyArgument() {
				logger.trace("Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, null, "Hello Alice!"))
			}

			/**
			 * Verifies that a trace log entry with an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceException() {
				val exception = Exception()
				logger.trace(exception)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, null))
			}

			/**
			 * Verifies that a trace log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceExceptionAndTextMessage() {
				val exception = Exception()
				logger.trace(exception, "Oops!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, "Oops!"))
			}

			/**
			 * Verifies that a trace log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceExceptionAndLazyMessage() {
				val exception = Exception()
				logger.trace(exception) { "Oops!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, "Oops!"))
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceExceptionAndFormattedMessageWithArgument() {
				val exception = Exception()
				logger.trace(exception, "Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(level = Level.TRACE)
			@Test
			fun traceExceptionAndFormattedMessageWithLazyArgument() {
				val exception = Exception()
				logger.trace(exception, "Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.TRACE, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that a debug log entry with an object can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugObjectMessage() {
				logger.debug(42)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "42"))
			}

			/**
			 * Verifies that a debug log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugTextMessage() {
				logger.debug("Hello World!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "Hello World!"))
			}

			/**
			 * Verifies that a debug log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugLazyMessage() {
				logger.debug { "Hello World!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "Hello World!"))
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugFormattedMessageWithArgument() {
				logger.debug("Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "Hello Alice!"))
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugFormattedMessageWithLazyArgument() {
				logger.debug("Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, null, "Hello Alice!"))
			}

			/**
			 * Verifies that a debug log entry with an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugException() {
				val exception = Exception()
				logger.debug(exception)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, null))
			}

			/**
			 * Verifies that a debug log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugExceptionAndTextMessage() {
				val exception = Exception()
				logger.debug(exception, "Oops!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, "Oops!"))
			}

			/**
			 * Verifies that a debug log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugExceptionAndLazyMessage() {
				val exception = Exception()
				logger.debug(exception) { "Oops!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, "Oops!"))
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugExceptionAndFormattedMessageWithArgument() {
				val exception = Exception()
				logger.debug(exception, "Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun debugExceptionAndFormattedMessageWithLazyArgument() {
				val exception = Exception()
				logger.debug(exception, "Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.DEBUG, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that an info log entry with an object can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoObjectMessage() {
				logger.info(42)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "42"))
			}

			/**
			 * Verifies that an info log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoTextMessage() {
				logger.info("Hello World!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "Hello World!"))
			}

			/**
			 * Verifies that an info log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoLazyMessage() {
				logger.info { "Hello World!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "Hello World!"))
			}

			/**
			 * Verifies that an info log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoFormattedMessageWithArgument() {
				logger.info("Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "Hello Alice!"))
			}

			/**
			 * Verifies that an info log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoFormattedMessageWithLazyArgument() {
				logger.info("Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, null, "Hello Alice!"))
			}

			/**
			 * Verifies that an info log entry with an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoException() {
				val exception = Exception()
				logger.info(exception)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, null))
			}

			/**
			 * Verifies that an info log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoExceptionAndTextMessage() {
				val exception = Exception()
				logger.info(exception, "Oops!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, "Oops!"))
			}

			/**
			 * Verifies that an info log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoExceptionAndLazyMessage() {
				val exception = Exception()
				logger.info(exception) { "Oops!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, "Oops!"))
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoExceptionAndFormattedMessageWithArgument() {
				val exception = Exception()
				logger.info(exception, "Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun infoExceptionAndFormattedMessageWithLazyArgument() {
				val exception = Exception()
				logger.info(exception, "Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.INFO, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that a warning log entry with an object can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnObjectMessage() {
				logger.warn(42)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "42"))
			}

			/**
			 * Verifies that a warning log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnTextMessage() {
				logger.warn("Hello World!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "Hello World!"))
			}

			/**
			 * Verifies that a warning log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnLazyMessage() {
				logger.warn { "Hello World!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "Hello World!"))
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnFormattedMessageWithArgument() {
				logger.warn("Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "Hello Alice!"))
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnFormattedMessageWithLazyArgument() {
				logger.warn("Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, null, "Hello Alice!"))
			}

			/**
			 * Verifies that a warning log entry with an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnException() {
				val exception = Exception()
				logger.warn(exception)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, null))
			}

			/**
			 * Verifies that a warning log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnExceptionAndTextMessage() {
				val exception = Exception()
				logger.warn(exception, "Oops!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, "Oops!"))
			}

			/**
			 * Verifies that a warning log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnExceptionAndLazyMessage() {
				val exception = Exception()
				logger.warn(exception) { "Oops!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, "Oops!"))
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnExceptionAndFormattedMessageWithArgument() {
				val exception = Exception()
				logger.warn(exception, "Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun warnExceptionAndFormattedMessageWithLazyArgument() {
				val exception = Exception()
				logger.warn(exception, "Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.WARN, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that an error log entry with an object can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorObjectMessage() {
				logger.error(42)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "42"))
			}

			/**
			 * Verifies that an error log entry with a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorTextMessage() {
				logger.error("Hello World!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "Hello World!"))
			}

			/**
			 * Verifies that an error log entry with a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorLazyMessage() {
				logger.error { "Hello World!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "Hello World!"))
			}

			/**
			 * Verifies that an error log entry with a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorFormattedMessageWithArgument() {
				logger.error("Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "Hello Alice!"))
			}

			/**
			 * Verifies that an error log entry with a message with placeholders and lazy arguments can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorFormattedMessageWithLazyArgument() {
				logger.error("Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, null, "Hello Alice!"))
			}

			/**
			 * Verifies that an error log entry with an exception can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorException() {
				val exception = Exception()
				logger.error(exception)
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, null))
			}

			/**
			 * Verifies that an error log entry with an exception and a plain text message can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorExceptionAndTextMessage() {
				val exception = Exception()
				logger.error(exception, "Oops!")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, "Oops!"))
			}

			/**
			 * Verifies that an error log entry with an exception and a lazy text message can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorExceptionAndLazyMessage() {
				val exception = Exception()
				logger.error(exception) { "Oops!" }
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, "Oops!"))
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders can be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorExceptionAndFormattedMessageWithArgument() {
				val exception = Exception()
				logger.error(exception, "Hello {}!", "Alice")
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, "Hello Alice!"))
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments can
			 * be issued.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun errorExceptionAndFormattedMessageWithLazyArgument() {
				val exception = Exception()
				logger.error(exception, "Hello {}!", { "Alice" })
				assertThat(log.consume())
					.containsExactly(createLogEntry(Level.ERROR, exception, "Hello Alice!"))
			}

			/**
			 * Creates a new log entry.
			 *
			 * @param level     Severity level
			 * @param exception Exception or any other kind of throwable
			 * @param message   Text message
			 * @return Created log entry
			 */
			private fun createLogEntry(level: Level, exception: Throwable?, message: String?): LogEntry {
				return LogEntry(Enabled::class.java.name, "test", level, exception, message)
			}
		}

		/**
		 * Tests discarding log entries if the assigned severity level is disabled.
		 */
		@Nested
		inner class Disabled {
			/**
			 * Verifies that a trace log entry with an object is discarded if the trace severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceObjectMessage() {
				logger.trace(42)
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with a plain text message is discarded if the trace severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceTextMessage() {
				logger.trace("Hello World!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with a lazy text message is discarded if the trace severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceLazyMessage() {
				logger.trace { "Hello World!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders is discarded if the trace severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceFormattedMessageWithArgument() {
				logger.trace("Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with a message with placeholders and lazy arguments is discarded if the
			 * trace severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceFormattedMessageWithLazyArgument() {
				logger.trace("Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with an exception is discarded if the trace severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceException() {
				logger.trace(Exception())
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with an exception and a plain text message is discarded if the trace
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceExceptionAndTextMessage() {
				logger.trace(Exception(), "Oops!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with an exception and a lazy text message is discarded if the trace
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceExceptionAndLazyMessage() {
				logger.trace(Exception()) { "Oops!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders is discarded if the
			 * trace severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceExceptionAndFormattedMessageWithArgument() {
				logger.trace(Exception(), "Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the trace severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.DEBUG)
			@Test
			fun traceExceptionAndFormattedMessageWithLazyArgument() {
				logger.trace(Exception(), "Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}
			
			/**
			 * Verifies that a debug log entry with an object is discarded if the debug severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugObjectMessage() {
				logger.debug(42)
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with a plain text message is discarded if the debug severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugTextMessage() {
				logger.debug("Hello World!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with a lazy text message is discarded if the debug severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugLazyMessage() {
				logger.debug { "Hello World!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders is discarded if the debug severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugFormattedMessageWithArgument() {
				logger.debug("Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with a message with placeholders and lazy arguments is discarded if the
			 * debug severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugFormattedMessageWithLazyArgument() {
				logger.debug("Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with an exception is discarded if the debug severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugException() {
				logger.debug(Exception())
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with an exception and a plain text message is discarded if the debug
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugExceptionAndTextMessage() {
				logger.debug(Exception(), "Oops!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with an exception and a lazy text message is discarded if the debug
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugExceptionAndLazyMessage() {
				logger.debug(Exception()) { "Oops!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders is discarded if the
			 * debug severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugExceptionAndFormattedMessageWithArgument() {
				logger.debug(Exception(), "Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the debug severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.INFO)
			@Test
			fun debugExceptionAndFormattedMessageWithLazyArgument() {
				logger.debug(Exception(), "Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}
			
			/**
			 * Verifies that an info log entry with an object is discarded if the info severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoObjectMessage() {
				logger.info(42)
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with a plain text message is discarded if the info severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoTextMessage() {
				logger.info("Hello World!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with a lazy text message is discarded if the info severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoLazyMessage() {
				logger.info { "Hello World!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with a message with placeholders is discarded if the info severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoFormattedMessageWithArgument() {
				logger.info("Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with a message with placeholders and lazy arguments is discarded if the
			 * info severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoFormattedMessageWithLazyArgument() {
				logger.info("Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with an exception is discarded if the info severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoException() {
				logger.info(Exception())
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with an exception and a plain text message is discarded if the info
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoExceptionAndTextMessage() {
				logger.info(Exception(), "Oops!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with an exception and a lazy text message is discarded if the info
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoExceptionAndLazyMessage() {
				logger.info(Exception()) { "Oops!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders is discarded if the
			 * info severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoExceptionAndFormattedMessageWithArgument() {
				logger.info(Exception(), "Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the info severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.WARN)
			@Test
			fun infoExceptionAndFormattedMessageWithLazyArgument() {
				logger.info(Exception(), "Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with an object is discarded if the warn severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnObjectMessage() {
				logger.warn(42)
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnTextMessage() {
				logger.warn("Hello World!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with a lazy text message is discarded if the warn severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnLazyMessage() {
				logger.warn { "Hello World!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders is discarded if the warn severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnFormattedMessageWithArgument() {
				logger.warn("Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with a message with placeholders and lazy arguments is discarded if the
			 * warn severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnFormattedMessageWithLazyArgument() {
				logger.warn("Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with an exception is discarded if the warn severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnException() {
				logger.warn(Exception())
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with an exception and a plain text message is discarded if the warn
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnExceptionAndTextMessage() {
				logger.warn(Exception(), "Oops!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with an exception and a lazy text message is discarded if the warn
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnExceptionAndLazyMessage() {
				logger.warn(Exception()) { "Oops!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders is discarded if the
			 * warn severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnExceptionAndFormattedMessageWithArgument() {
				logger.warn(Exception(), "Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the warn severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.ERROR)
			@Test
			fun warnExceptionAndFormattedMessageWithLazyArgument() {
				logger.warn(Exception(), "Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with an object is discarded if the error severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorObjectMessage() {
				logger.error(42)
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with a plain text message is discarded if the error severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorTextMessage() {
				logger.error("Hello World!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with a lazy text message is discarded if the error severity level is
			 * disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorLazyMessage() {
				logger.error { "Hello World!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with a message with placeholders is discarded if the error severity level
			 * is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorFormattedMessageWithArgument() {
				logger.error("Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with a message with placeholders and lazy arguments is discarded if the
			 * error severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorFormattedMessageWithLazyArgument() {
				logger.error("Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with an exception is discarded if the error severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorException() {
				logger.error(Exception())
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with an exception and a plain text message is discarded if the error
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorExceptionAndTextMessage() {
				logger.error(Exception(), "Oops!")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with an exception and a lazy text message is discarded if the error
			 * severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorExceptionAndLazyMessage() {
				logger.error(Exception()) { "Oops!" }
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders is discarded if the
			 * error severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorExceptionAndFormattedMessageWithArgument() {
				logger.error(Exception(), "Hello {}!", "Alice")
				assertThat(log.consume()).isEmpty()
			}

			/**
			 * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments is
			 * discarded if the error severity level is disabled.
			 */
			@CaptureLogEntries(level = Level.OFF)
			@Test
			fun errorExceptionAndFormattedMessageWithLazyArgument() {
				logger.error(Exception(), "Hello {}!", { "Alice" })
				assertThat(log.consume()).isEmpty()
			}
		}
	}
}
