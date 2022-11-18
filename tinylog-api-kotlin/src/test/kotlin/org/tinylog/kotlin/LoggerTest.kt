package org.tinylog.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.MockedStatic
import org.mockito.Mockito.atMostOnce
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.isA
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.notNull
import org.mockito.kotlin.same
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.tinylog.core.Framework
import org.tinylog.core.Level
import org.tinylog.core.Tinylog
import org.tinylog.core.backend.LevelVisibility
import org.tinylog.core.backend.LoggingBackend
import org.tinylog.core.backend.OutputDetails
import org.tinylog.core.format.message.EnhancedMessageFormatter
import org.tinylog.core.test.isolate.IsolatedExecution
import java.util.function.Supplier

@IsolatedExecution(classes = [Logger::class, TaggedLogger::class])
internal class LoggerTest {
    private lateinit var tinylogMock: MockedStatic<Tinylog>
    private lateinit var backend: LoggingBackend
    private lateinit var visibility: LevelVisibility

    /**
     * Initializes all mocks.
     */
    @BeforeEach
    fun create() {
        tinylogMock = mockStatic(Tinylog::class.java)
        backend = mock()

        tinylogMock.`when`<Framework> {
            Tinylog.getFramework()
        }.thenReturn(object : Framework(false, false) {
            override fun getLoggingBackend() = backend
        })

        visibility = mock()
        whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)
        whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)
        whenever(visibility.info).thenReturn(OutputDetails.DISABLED)
        whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)
        whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

        val visibilityForTags = mock<LevelVisibility>()
        whenever(visibilityForTags.trace).thenReturn(OutputDetails.DISABLED)
        whenever(visibilityForTags.debug).thenReturn(OutputDetails.DISABLED)
        whenever(visibilityForTags.info).thenReturn(OutputDetails.DISABLED)
        whenever(visibilityForTags.warn).thenReturn(OutputDetails.DISABLED)
        whenever(visibilityForTags.error).thenReturn(OutputDetails.DISABLED)

        whenever(backend.getLevelVisibilityByTag(anyOrNull())).thenAnswer {
            if (it.getArgument<Any?>(0) == null) visibility else visibilityForTags
        }
    }

    /**
     * Restores the mocked tinylog class.
     */
    @AfterEach
    fun dispose() {
        tinylogMock.close()
    }

    /**
     * Tests for category tests.
     */
    @Nested
    inner class Tags {
        /**
         * Verifies that the same logger instance is returned for the same tag.
         */
        @Test
        fun sameLoggerInstanceForSameTag() {
            val first = Logger.tag("foo")
            val second = Logger.tag("foo")
            assertThat(first).isNotNull.isSameAs(second)
        }

        /**
         * Verifies that different logger instances are returned for different tags.
         */
        @Test
        fun differentLoggerInstanceForDifferentTag() {
            val first = Logger.tag("foo")
            val second = Logger.tag("boo")
            assertThat(first).isNotNull
            assertThat(second).isNotNull
            assertThat(first).isNotSameAs(second)
        }

        /**
         * Verifies that the same untagged root logger is returned for `null` and empty tags.
         */
        @Test
        fun sameUntaggedRootLoggerForNullAndEmptyTags() {
            val nullTag = Logger.tag(null)
            val emptyTag = Logger.tag("")
            assertThat(nullTag).isNotNull
            assertThat(nullTag.tag).isNull()
            assertThat(emptyTag).isNotNull
            assertThat(emptyTag.tag).isNull()
            assertThat(nullTag).isSameAs(emptyTag)
        }
    }

    /**
     * Tests for severity levels.
     */
    @Nested
    inner class Levels {
        /**
         * Verifies the results of the [Logger.isTraceEnabled] method.
         *
         * @param enabled The value for [LoggingBackend.isEnabled]
         * @param outputDetails The value for [LevelVisibility.trace]
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
            whenever(visibility.trace).thenReturn(outputDetails)
            whenever(backend.isEnabled(notNull(), isNull(), eq(Level.TRACE))).thenReturn(enabled)

            assertThat(Logger.isTraceEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }

        /**
         * Verifies the results of the [Logger.isDebugEnabled] method.
         *
         * @param enabled The value for [LoggingBackend.isEnabled]
         * @param outputDetails The value for [LevelVisibility.debug]
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
            whenever(visibility.debug).thenReturn(outputDetails)
            whenever(backend.isEnabled(notNull(), isNull(), eq(Level.DEBUG))).thenReturn(enabled)

            assertThat(Logger.isDebugEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }

        /**
         * Verifies the results of the [Logger.isInfoEnabled] method.
         *
         * @param enabled The value for [LoggingBackend.isEnabled]
         * @param outputDetails The value for [LevelVisibility.info]
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
            whenever(visibility.info).thenReturn(outputDetails)
            whenever(backend.isEnabled(notNull(), isNull(), eq(Level.INFO))).thenReturn(enabled)

            assertThat(Logger.isInfoEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }

        /**
         * Verifies the results of the [Logger.isWarnEnabled] method.
         *
         * @param enabled The value for [LoggingBackend.isEnabled]
         * @param outputDetails The value for [LevelVisibility.warn]
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
            whenever(visibility.warn).thenReturn(outputDetails)
            whenever(backend.isEnabled(notNull(), isNull(), eq(Level.WARN))).thenReturn(enabled)

            assertThat(Logger.isWarnEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }

        /**
         * Verifies the results of the [Logger.isErrorEnabled] method.
         *
         * @param enabled The value for [LoggingBackend.isEnabled]
         * @param outputDetails The value for [LevelVisibility.error]
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
            whenever(visibility.error).thenReturn(outputDetails)
            whenever(backend.isEnabled(notNull(), isNull(), eq(Level.ERROR))).thenReturn(enabled)

            assertThat(Logger.isErrorEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }
    }

    /**
     * Tests for issuing log entries.
     */
    @Nested
    inner class LogEntries {
        /**
         * Tests issuing log entries if the assigned severity level is enabled.
         */
        @Nested
        inner class Enabled {
            /**
             * Verifies that a trace log entry with an object can be issued.
             */
            @Test
            fun traceObjectMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.trace(42)
                verifyLogEntry(Level.TRACE, null, 42)
            }

            /**
             * Verifies that a trace log entry with a plain text message can be issued.
             */
            @Test
            fun traceTextMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.trace("Hello World!")
                verifyLogEntry(Level.TRACE, null, "Hello World!")
            }

            /**
             * Verifies that a trace log entry with a lazy text message can be issued.
             */
            @Test
            fun traceLazyMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.trace { "Hello World!" }
                verifyLogEntry(Level.TRACE, null, { "Hello World!" })
            }

            /**
             * Verifies that a trace log entry with a message with placeholders can be issued.
             */
            @Test
            fun traceFormattedMessageWithArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.trace("Hello {}!", "Alice")
                verifyLogEntry(Level.TRACE, null, "Hello {}!", "Alice")
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun traceFormattedMessageWithLazyArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.trace("Hello {}!", { "Alice" })
                verifyLogEntry(Level.TRACE, null, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that a trace log entry with an exception can be issued.
             */
            @Test
            fun traceException() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.trace(exception)
                verifyLogEntry(Level.TRACE, exception, null)
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun traceExceptionAndTextMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.trace(exception, "Oops!")
                verifyLogEntry(Level.TRACE, exception, "Oops!")
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun traceExceptionAndLazyMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.trace(exception) { "Oops!" }
                verifyLogEntry(Level.TRACE, exception, { "Oops!" })
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.trace(exception, "Hello {}!", "Alice")
                verifyLogEntry(Level.TRACE, exception, "Hello {}!", "Alice")
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.trace(exception, "Hello {}!", { "Alice" })
                verifyLogEntry(Level.TRACE, exception, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that a debug log entry with an object can be issued.
             */
            @Test
            fun debugObjectMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.debug(42)
                verifyLogEntry(Level.DEBUG, null, 42)
            }

            /**
             * Verifies that a debug log entry with a plain text message can be issued.
             */
            @Test
            fun debugTextMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.debug("Hello World!")
                verifyLogEntry(Level.DEBUG, null, "Hello World!")
            }

            /**
             * Verifies that a debug log entry with a lazy text message can be issued.
             */
            @Test
            fun debugLazyMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.debug { "Hello World!" }
                verifyLogEntry(Level.DEBUG, null, { "Hello World!" })
            }

            /**
             * Verifies that a debug log entry with a message with placeholders can be issued.
             */
            @Test
            fun debugFormattedMessageWithArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.debug("Hello {}!", "Alice")
                verifyLogEntry(Level.DEBUG, null, "Hello {}!", "Alice")
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun debugFormattedMessageWithLazyArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.debug("Hello {}!", { "Alice" })
                verifyLogEntry(Level.DEBUG, null, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that a debug log entry with an exception can be issued.
             */
            @Test
            fun debugException() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.debug(exception)
                verifyLogEntry(Level.DEBUG, exception, null)
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun debugExceptionAndTextMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.debug(exception, "Oops!")
                verifyLogEntry(Level.DEBUG, exception, "Oops!")
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun debugExceptionAndLazyMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.debug(exception) { "Oops!" }
                verifyLogEntry(Level.DEBUG, exception, { "Oops!" })
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.debug(exception, "Hello {}!", "Alice")
                verifyLogEntry(Level.DEBUG, exception, "Hello {}!", "Alice")
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.debug(exception, "Hello {}!", { "Alice" })
                verifyLogEntry(Level.DEBUG, exception, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that an info log entry with an object can be issued.
             */
            @Test
            fun infoObjectMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.info(42)
                verifyLogEntry(Level.INFO, null, 42)
            }

            /**
             * Verifies that an info log entry with a plain text message can be issued.
             */
            @Test
            fun infoTextMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.info("Hello World!")
                verifyLogEntry(Level.INFO, null, "Hello World!")
            }

            /**
             * Verifies that an info log entry with a lazy text message can be issued.
             */
            @Test
            fun infoLazyMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.info { "Hello World!" }
                verifyLogEntry(Level.INFO, null, { "Hello World!" })
            }

            /**
             * Verifies that an info log entry with a message with placeholders can be issued.
             */
            @Test
            fun infoFormattedMessageWithArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.info("Hello {}!", "Alice")
                verifyLogEntry(Level.INFO, null, "Hello {}!", "Alice")
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun infoFormattedMessageWithLazyArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.info("Hello {}!", { "Alice" })
                verifyLogEntry(Level.INFO, null, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that an info log entry with an exception can be issued.
             */
            @Test
            fun infoException() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.info(exception)
                verifyLogEntry(Level.INFO, exception, null)
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun infoExceptionAndTextMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.info(exception, "Oops!")
                verifyLogEntry(Level.INFO, exception, "Oops!")
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun infoExceptionAndLazyMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.info(exception) { "Oops!" }
                verifyLogEntry(Level.INFO, exception, { "Oops!" })
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.info(exception, "Hello {}!", "Alice")
                verifyLogEntry(Level.INFO, exception, "Hello {}!", "Alice")
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.info(exception, "Hello {}!", { "Alice" })
                verifyLogEntry(Level.INFO, exception, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that a warning log entry with an object can be issued.
             */
            @Test
            fun warnObjectMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.warn(42)
                verifyLogEntry(Level.WARN, null, 42)
            }

            /**
             * Verifies that a warning log entry with a plain text message can be issued.
             */
            @Test
            fun warnTextMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.warn("Hello World!")
                verifyLogEntry(Level.WARN, null, "Hello World!")
            }

            /**
             * Verifies that a warning log entry with a lazy text message can be issued.
             */
            @Test
            fun warnLazyMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.warn { "Hello World!" }
                verifyLogEntry(Level.WARN, null, { "Hello World!" })
            }

            /**
             * Verifies that a warning log entry with a message with placeholders can be issued.
             */
            @Test
            fun warnFormattedMessageWithArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.warn("Hello {}!", "Alice")
                verifyLogEntry(Level.WARN, null, "Hello {}!", "Alice")
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun warnFormattedMessageWithLazyArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.warn("Hello {}!", { "Alice" })
                verifyLogEntry(Level.WARN, null, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that a warning log entry with an exception can be issued.
             */
            @Test
            fun warnException() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.warn(exception)
                verifyLogEntry(Level.WARN, exception, null)
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun warnExceptionAndTextMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.warn(exception, "Oops!")
                verifyLogEntry(Level.WARN, exception, "Oops!")
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun warnExceptionAndLazyMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.warn(exception) { "Oops!" }
                verifyLogEntry(Level.WARN, exception, { "Oops!" })
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.warn(exception, "Hello {}!", "Alice")
                verifyLogEntry(Level.WARN, exception, "Hello {}!", "Alice")
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.warn(exception, "Hello {}!", { "Alice" })
                verifyLogEntry(Level.WARN, exception, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that an error log entry with an object can be issued.
             */
            @Test
            fun errorObjectMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.error(42)
                verifyLogEntry(Level.ERROR, null, 42)
            }

            /**
             * Verifies that an error log entry with a plain text message can be issued.
             */
            @Test
            fun errorTextMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.error("Hello World!")
                verifyLogEntry(Level.ERROR, null, "Hello World!")
            }

            /**
             * Verifies that an error log entry with a lazy text message can be issued.
             */
            @Test
            fun errorLazyMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.error { "Hello World!" }
                verifyLogEntry(Level.ERROR, null, { "Hello World!" })
            }

            /**
             * Verifies that an error log entry with a message with placeholders can be issued.
             */
            @Test
            fun errorFormattedMessageWithArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.error("Hello {}!", "Alice")
                verifyLogEntry(Level.ERROR, null, "Hello {}!", "Alice")
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun errorFormattedMessageWithLazyArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.error("Hello {}!", { "Alice" })
                verifyLogEntry(Level.ERROR, null, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies that an error log entry with an exception can be issued.
             */
            @Test
            fun errorException() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.error(exception)
                verifyLogEntry(Level.ERROR, exception, null)
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun errorExceptionAndTextMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.error(exception, "Oops!")
                verifyLogEntry(Level.ERROR, exception, "Oops!")
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun errorExceptionAndLazyMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.error(exception) { "Oops!" }
                verifyLogEntry(Level.ERROR, exception, { "Oops!" })
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.error(exception, "Hello {}!", "Alice")
                verifyLogEntry(Level.ERROR, exception, "Hello {}!", "Alice")
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.error(exception, "Hello {}!", { "Alice" })
                verifyLogEntry(Level.ERROR, exception, "Hello {}!", { "Alice" })
            }

            /**
             * Verifies backend mock invocation with expected log entry values.
             *
             * @param level     Severity level
             * @param exception Exception or any other kind of throwable
             * @param message   Message object
             * @param arguments Optional arguments
             */
            private fun verifyLogEntry(level: Level, exception: Throwable?, message: Any?, vararg arguments: Any) {
                verify(backend, atMostOnce()).getLevelVisibilityByTag(null)
                verify(backend).log(
                    eq(Enabled::class.java),
                    isNull(),
                    same(level),
                    same(exception),
                    same(message),
                    if (arguments.isEmpty()) isNull() else eq(arguments),
                    if (arguments.isEmpty()) isNull() else isA<EnhancedMessageFormatter>()
                )
            }

            /**
             * Verifies backend mock invocation with expected log entry values.
             *
             * @param level     Severity level
             * @param exception Exception or any other kind of throwable
             * @param message   Message object supplier
             * @param arguments Optional arguments
             */
            private fun verifyLogEntry(level: Level, exception: Throwable?, message: () -> Any, vararg arguments: Any) {
                verify(backend, atMostOnce()).getLevelVisibilityByTag(null)
                verify(backend).log(
                    eq(Enabled::class.java),
                    isNull(),
                    same(level),
                    same(exception),
                    argThat { this is Supplier<*> && this.get() == message() },
                    if (arguments.isEmpty()) isNull() else eq(arguments),
                    if (arguments.isEmpty()) isNull() else isA<EnhancedMessageFormatter>()
                )
            }

            /**
             * Verifies backend mock invocation with expected log entry values.
             *
             * @param level     Severity level
             * @param exception Exception or any other kind of throwable
             * @param message   Message object
             * @param arguments Optional argument suppliers
             */
            private fun verifyLogEntry(level: Level, exception: Throwable?, message: Any, vararg arguments: () -> Any) {
                verify(backend, atMostOnce()).getLevelVisibilityByTag(null)
                verify(backend).log(
                    eq(Enabled::class.java),
                    isNull(),
                    same(level),
                    same(exception),
                    same(message),
                    if (arguments.isEmpty()) {
                        isNull()
                    } else {
                        argThat {
                            this.size == arguments.size && this.withIndex().all { (index, value) ->
                                value is Supplier<*> && value.get() == arguments[index]()
                            }
                        }
                    },
                    if (arguments.isEmpty()) isNull() else isA<EnhancedMessageFormatter>()
                )
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
            @Test
            fun traceObjectMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a plain text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            fun traceTextMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a lazy text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            fun traceLazyMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a message with placeholders is discarded if the trace severity level
             * is disabled.
             */
            @Test
            fun traceFormattedMessageWithArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments is discarded if the
             * trace severity level is disabled.
             */
            @Test
            fun traceFormattedMessageWithLazyArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception is discarded if the trace severity level is disabled.
             */
            @Test
            fun traceException() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            fun traceExceptionAndTextMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            fun traceExceptionAndLazyMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders is discarded if the
             * trace severity level is disabled.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the trace severity level is disabled.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)

                Logger.trace(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an object is discarded if the debug severity level is disabled.
             */
            @Test
            fun debugObjectMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a plain text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            fun debugTextMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a lazy text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            fun debugLazyMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a message with placeholders is discarded if the debug severity level
             * is disabled.
             */
            @Test
            fun debugFormattedMessageWithArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments is discarded if the
             * debug severity level is disabled.
             */
            @Test
            fun debugFormattedMessageWithLazyArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception is discarded if the debug severity level is disabled.
             */
            @Test
            fun debugException() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            fun debugExceptionAndTextMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            fun debugExceptionAndLazyMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders is discarded if the
             * debug severity level is disabled.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the debug severity level is disabled.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)

                Logger.debug(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an object is discarded if the info severity level is disabled.
             */
            @Test
            fun infoObjectMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a plain text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            fun infoTextMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a lazy text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            fun infoLazyMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a message with placeholders is discarded if the info severity level
             * is disabled.
             */
            @Test
            fun infoFormattedMessageWithArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments is discarded if the
             * info severity level is disabled.
             */
            @Test
            fun infoFormattedMessageWithLazyArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception is discarded if the info severity level is disabled.
             */
            @Test
            fun infoException() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            fun infoExceptionAndTextMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            fun infoExceptionAndLazyMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders is discarded if the
             * info severity level is disabled.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the info severity level is disabled.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.DISABLED)

                Logger.info(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an object is discarded if the warn severity level is disabled.
             */
            @Test
            fun warnObjectMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            fun warnTextMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a lazy text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            fun warnLazyMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a message with placeholders is discarded if the warn severity level
             * is disabled.
             */
            @Test
            fun warnFormattedMessageWithArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments is discarded if the
             * warn severity level is disabled.
             */
            @Test
            fun warnFormattedMessageWithLazyArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception is discarded if the warn severity level is disabled.
             */
            @Test
            fun warnException() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            fun warnExceptionAndTextMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            fun warnExceptionAndLazyMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders is discarded if the
             * warn severity level is disabled.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the warn severity level is disabled.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)

                Logger.warn(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an object is discarded if the error severity level is disabled.
             */
            @Test
            fun errorObjectMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a plain text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            fun errorTextMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a lazy text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            fun errorLazyMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a message with placeholders is discarded if the error severity level
             * is disabled.
             */
            @Test
            fun errorFormattedMessageWithArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments is discarded if the
             * error severity level is disabled.
             */
            @Test
            fun errorFormattedMessageWithLazyArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception is discarded if the error severity level is disabled.
             */
            @Test
            fun errorException() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            fun errorExceptionAndTextMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            fun errorExceptionAndLazyMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders is discarded if the
             * error severity level is disabled.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the error severity level is disabled.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithLazyArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

                Logger.error(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies no invocations of logging methods for mocked backend.
             */
            private fun verifyNoLogEntry() {
                verify(backend, atMostOnce()).getLevelVisibilityByTag(null)
                verifyNoMoreInteractions(backend)
            }
        }
    }
}
