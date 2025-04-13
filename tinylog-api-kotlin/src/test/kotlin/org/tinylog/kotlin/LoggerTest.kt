package org.tinylog.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentCaptor
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.atMost
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.notNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.tinylog.core.Framework
import org.tinylog.core.Level
import org.tinylog.core.LogEntry
import org.tinylog.core.Tinylog
import org.tinylog.core.backend.LevelVisibility
import org.tinylog.core.backend.OutputDetails
import org.tinylog.core.context.NopContextStorage
import org.tinylog.core.runtime.JavaRuntime
import org.tinylog.test.junit.isolate.IsolatedExecution

@IsolatedExecution(classes = [Logger::class, TaggedLogger::class])
class LoggerTest {
    private lateinit var tinylogMock: MockedStatic<Tinylog>
    private lateinit var framework: Framework
    private lateinit var visibility: LevelVisibility

    /**
     * Initializes all mocks.
     */
    @BeforeEach
    fun init() {
        val javaRuntime = JavaRuntime(mock())
        val nopContextStorage = NopContextStorage()

        visibility =
            mock<LevelVisibility>().apply {
                whenever(trace).thenReturn(OutputDetails.DISABLED)
                whenever(debug).thenReturn(OutputDetails.DISABLED)
                whenever(info).thenReturn(OutputDetails.DISABLED)
                whenever(warn).thenReturn(OutputDetails.DISABLED)
                whenever(error).thenReturn(OutputDetails.DISABLED)
            }

        framework =
            mock<Framework>().apply {
                whenever(runtime).thenReturn(javaRuntime)
                whenever(contextStorage).thenReturn(nopContextStorage)
                whenever(getLevelVisibilityByTag(isNull())).thenReturn(visibility)
            }

        tinylogMock =
            mockStatic(Tinylog::class.java).apply {
                `when`<Framework>(Tinylog::getFramework).thenReturn(framework)
            }
    }

    /**
     * Restores the mocked tinylog class.
     */
    @AfterEach
    fun reset() {
        tinylogMock.close()
    }

    /**
     * Tests for category tests.
     */
    @Nested
    inner class Tags {
        /**
         * Sets the level visibility for any kind of tagged loggers.
         */
        @BeforeEach
        fun init() {
            whenever(framework.getLevelVisibilityByTag(any())).thenReturn(visibility)
        }

        /**
         * Verifies that the same logger instance is returned for the same tag.
         */
        @Test
        fun sameLoggerInstanceForSameTag() {
            val first = Logger.tag("foo")
            val second = Logger.tag("foo")
            assertThat(first).isNotNull().isSameAs(second)
        }

        /**
         * Verifies that different logger instances are returned for different tags.
         */
        @Test
        fun differentLoggerInstanceForDifferentTag() {
            val first = Logger.tag("foo")
            val second = Logger.tag("bar")

            assertThat(first).isNotNull()
            assertThat(second).isNotNull()
            assertThat(first).isNotSameAs(second)
        }

        /**
         * Verifies that the same untagged root logger is returned for `null` and empty tags.
         */
        @Test
        fun sameUntaggedRootLoggerForNullAndEmptyTags() {
            val nullTag = Logger.tag(null)
            val emptyTag = Logger.tag("")

            assertThat(nullTag).isNotNull()
            assertThat(nullTag.tag).isNull()
            assertThat(emptyTag).isNotNull()
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
         * Verifies the results of the [TaggedLogger.isTraceEnabled] method.
         *
         * @param enabled The value for [Framework.isEnabled]
         * @param outputDetails The value for [LevelVisibility.getTrace]
         */
        @ParameterizedTest
        @CsvSource(
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO",
        )
        fun isTraceEnabled(
            enabled: Boolean,
            outputDetails: OutputDetails,
        ) {
            whenever(visibility.trace).thenReturn(outputDetails)
            whenever(framework.isEnabled(notNull(), isNull(), eq(Level.TRACE))).thenReturn(enabled)

            assertThat(Logger.isTraceEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }

        /**
         * Verifies the results of the [TaggedLogger.isDebugEnabled] method.
         *
         * @param enabled The value for [Framework.isEnabled]
         * @param outputDetails The value for [LevelVisibility.getDebug]
         */
        @ParameterizedTest
        @CsvSource(
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO",
        )
        fun isDebugEnabled(
            enabled: Boolean,
            outputDetails: OutputDetails,
        ) {
            whenever(visibility.debug).thenReturn(outputDetails)
            whenever(framework.isEnabled(notNull(), isNull(), eq(Level.DEBUG))).thenReturn(enabled)

            assertThat(Logger.isDebugEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }

        /**
         * Verifies the results of the [TaggedLogger.isInfoEnabled] method.
         *
         * @param enabled The value for [Framework.isEnabled]
         * @param outputDetails The value for [LevelVisibility.getInfo]
         */
        @ParameterizedTest
        @CsvSource(
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO",
        )
        fun isInfoEnabled(
            enabled: Boolean,
            outputDetails: OutputDetails,
        ) {
            whenever(visibility.info).thenReturn(outputDetails)
            whenever(framework.isEnabled(notNull(), isNull(), eq(Level.INFO))).thenReturn(enabled)

            assertThat(Logger.isInfoEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }

        /**
         * Verifies the results of the [TaggedLogger.isWarnEnabled] method.
         *
         * @param enabled The value for [Framework.isEnabled]
         * @param outputDetails The value for [LevelVisibility.getWarn]
         */
        @ParameterizedTest
        @CsvSource(
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO",
        )
        fun isWarnEnabled(
            enabled: Boolean,
            outputDetails: OutputDetails,
        ) {
            whenever(visibility.warn).thenReturn(outputDetails)
            whenever(framework.isEnabled(notNull(), isNull(), eq(Level.WARN))).thenReturn(enabled)

            assertThat(Logger.isWarnEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }

        /**
         * Verifies the results of the [TaggedLogger.isErrorEnabled] method.
         *
         * @param enabled The value for [Framework.isEnabled]
         * @param outputDetails The value for [LevelVisibility.getError]
         */
        @ParameterizedTest
        @CsvSource(
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO",
        )
        fun isErrorEnabled(
            enabled: Boolean,
            outputDetails: OutputDetails,
        ) {
            whenever(visibility.error).thenReturn(outputDetails)
            whenever(framework.isEnabled(notNull(), isNull(), eq(Level.ERROR))).thenReturn(enabled)

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

                verifyLogEntry(Level.TRACE, null, "42")
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

                verifyLogEntry(Level.TRACE, null, "Hello World!")
            }

            /**
             * Verifies that a trace log entry with a message with placeholders can be issued.
             */
            @Test
            fun traceFormattedMessageWithArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.trace("Hello {}!", "Alice")

                verifyLogEntry(Level.TRACE, null, "Hello Alice!")
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun traceFormattedMessageWithLazyArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.trace("Hello {}!", { "Alice" })

                verifyLogEntry(Level.TRACE, null, "Hello Alice!")
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

                verifyLogEntry(Level.TRACE, exception, "Oops!")
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.trace(exception, "Hello {}!", "Alice")

                verifyLogEntry(Level.TRACE, exception, "Hello Alice!")
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

                verifyLogEntry(Level.TRACE, exception, "Hello Alice!")
            }

            /**
             * Verifies that a debug log entry with an object can be issued.
             */
            @Test
            fun debugObjectMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.debug(42)

                verifyLogEntry(Level.DEBUG, null, "42")
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

                verifyLogEntry(Level.DEBUG, null, "Hello World!")
            }

            /**
             * Verifies that a debug log entry with a message with placeholders can be issued.
             */
            @Test
            fun debugFormattedMessageWithArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.debug("Hello {}!", "Alice")

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!")
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun debugFormattedMessageWithLazyArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.debug("Hello {}!", { "Alice" })

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!")
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

                verifyLogEntry(Level.DEBUG, exception, "Oops!")
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.debug(exception, "Hello {}!", "Alice")

                verifyLogEntry(Level.DEBUG, exception, "Hello Alice!")
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

                verifyLogEntry(Level.DEBUG, exception, "Hello Alice!")
            }

            /**
             * Verifies that an info log entry with an object can be issued.
             */
            @Test
            fun infoObjectMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.info(42)

                verifyLogEntry(Level.INFO, null, "42")
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

                verifyLogEntry(Level.INFO, null, "Hello World!")
            }

            /**
             * Verifies that an info log entry with a message with placeholders can be issued.
             */
            @Test
            fun infoFormattedMessageWithArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.info("Hello {}!", "Alice")

                verifyLogEntry(Level.INFO, null, "Hello Alice!")
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun infoFormattedMessageWithLazyArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.info("Hello {}!", { "Alice" })

                verifyLogEntry(Level.INFO, null, "Hello Alice!")
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

                verifyLogEntry(Level.INFO, exception, "Oops!")
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.info(exception, "Hello {}!", "Alice")

                verifyLogEntry(Level.INFO, exception, "Hello Alice!")
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

                verifyLogEntry(Level.INFO, exception, "Hello Alice!")
            }

            /**
             * Verifies that a warning log entry with an object can be issued.
             */
            @Test
            fun warnObjectMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.warn(42)

                verifyLogEntry(Level.WARN, null, "42")
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

                verifyLogEntry(Level.WARN, null, "Hello World!")
            }

            /**
             * Verifies that a warning log entry with a message with placeholders can be issued.
             */
            @Test
            fun warnFormattedMessageWithArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.warn("Hello {}!", "Alice")

                verifyLogEntry(Level.WARN, null, "Hello Alice!")
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun warnFormattedMessageWithLazyArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.warn("Hello {}!", { "Alice" })

                verifyLogEntry(Level.WARN, null, "Hello Alice!")
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

                verifyLogEntry(Level.WARN, exception, "Oops!")
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.warn(exception, "Hello {}!", "Alice")

                verifyLogEntry(Level.WARN, exception, "Hello Alice!")
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

                verifyLogEntry(Level.WARN, exception, "Hello Alice!")
            }

            /**
             * Verifies that an error log entry with an object can be issued.
             */
            @Test
            fun errorObjectMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.error(42)

                verifyLogEntry(Level.ERROR, null, "42")
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

                verifyLogEntry(Level.ERROR, null, "Hello World!")
            }

            /**
             * Verifies that an error log entry with a message with placeholders can be issued.
             */
            @Test
            fun errorFormattedMessageWithArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.error("Hello {}!", "Alice")

                verifyLogEntry(Level.ERROR, null, "Hello Alice!")
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun errorFormattedMessageWithLazyArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                Logger.error("Hello {}!", { "Alice" })

                verifyLogEntry(Level.ERROR, null, "Hello Alice!")
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

                verifyLogEntry(Level.ERROR, exception, "Oops!")
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                Logger.error(exception, "Hello {}!", "Alice")

                verifyLogEntry(Level.ERROR, exception, "Hello Alice!")
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

                verifyLogEntry(Level.ERROR, exception, "Hello Alice!")
            }

            /**
             * Verifies backend mock invocation with expected log entry values.
             *
             * @param level The expected severity level
             * @param exception The expected exception
             * @param message The expected rendered text message
             */
            private fun verifyLogEntry(
                level: Level,
                exception: Exception?,
                message: String?,
            ) {
                val captor = ArgumentCaptor.forClass(LogEntry::class.java)
                verify(framework, atMost(1)).getLevelVisibilityByTag(null)
                verify(framework).submit(captor.capture())

                assertThat(captor.allValues).singleElement().satisfies({ entry: LogEntry ->
                    assertThat(entry.thread).isSameAs(Thread.currentThread())
                    assertThat(entry.context).isEmpty()
                    assertThat(entry.className).isEqualTo(Enabled::class.java.name)
                    assertThat(entry.methodName).isNull()
                    assertThat(entry.fileName).isNull()
                    assertThat(entry.lineNumber).isEqualTo(-1)
                    assertThat(entry.tag).isNull()
                    assertThat(entry.severityLevel).isEqualTo(level)
                    assertThat(entry.throwable).isSameAs(exception)
                    assertThat(entry.getFormattedMessage(mock())).isEqualTo(message)
                })
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
                Logger.trace(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a plain text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            fun traceTextMessage() {
                Logger.trace("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a lazy text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            fun traceLazyMessage() {
                Logger.trace { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a message with placeholders is discarded if the trace severity level
             * is disabled.
             */
            @Test
            fun traceFormattedMessageWithArgument() {
                Logger.trace("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments is discarded if the
             * trace severity level is disabled.
             */
            @Test
            fun traceFormattedMessageWithLazyArgument() {
                Logger.trace("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception is discarded if the trace severity level is disabled.
             */
            @Test
            fun traceException() {
                Logger.trace(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            fun traceExceptionAndTextMessage() {
                Logger.trace(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            fun traceExceptionAndLazyMessage() {
                Logger.trace(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders is discarded if the
             * trace severity level is disabled.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithArgument() {
                Logger.trace(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the trace severity level is disabled.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithLazyArgument() {
                Logger.trace(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an object is discarded if the debug severity level is disabled.
             */
            @Test
            fun debugObjectMessage() {
                Logger.debug(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a plain text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            fun debugTextMessage() {
                Logger.debug("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a lazy text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            fun debugLazyMessage() {
                Logger.debug { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a message with placeholders is discarded if the debug severity level
             * is disabled.
             */
            @Test
            fun debugFormattedMessageWithArgument() {
                Logger.debug("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments is discarded if the
             * debug severity level is disabled.
             */
            @Test
            fun debugFormattedMessageWithLazyArgument() {
                Logger.debug("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception is discarded if the debug severity level is disabled.
             */
            @Test
            fun debugException() {
                Logger.debug(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            fun debugExceptionAndTextMessage() {
                Logger.debug(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            fun debugExceptionAndLazyMessage() {
                Logger.debug(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders is discarded if the
             * debug severity level is disabled.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithArgument() {
                Logger.debug(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the debug severity level is disabled.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithLazyArgument() {
                Logger.debug(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an object is discarded if the info severity level is disabled.
             */
            @Test
            fun infoObjectMessage() {
                Logger.info(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a plain text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            fun infoTextMessage() {
                Logger.info("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a lazy text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            fun infoLazyMessage() {
                Logger.info { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a message with placeholders is discarded if the info severity level
             * is disabled.
             */
            @Test
            fun infoFormattedMessageWithArgument() {
                Logger.info("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments is discarded if the
             * info severity level is disabled.
             */
            @Test
            fun infoFormattedMessageWithLazyArgument() {
                Logger.info("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception is discarded if the info severity level is disabled.
             */
            @Test
            fun infoException() {
                Logger.info(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            fun infoExceptionAndTextMessage() {
                Logger.info(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            fun infoExceptionAndLazyMessage() {
                Logger.info(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders is discarded if the
             * info severity level is disabled.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithArgument() {
                Logger.info(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the info severity level is disabled.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithLazyArgument() {
                Logger.info(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an object is discarded if the warn severity level is disabled.
             */
            @Test
            fun warnObjectMessage() {
                Logger.warn(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            fun warnTextMessage() {
                Logger.warn("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a lazy text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            fun warnLazyMessage() {
                Logger.warn { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a message with placeholders is discarded if the warn severity level
             * is disabled.
             */
            @Test
            fun warnFormattedMessageWithArgument() {
                Logger.warn("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments is discarded if the
             * warn severity level is disabled.
             */
            @Test
            fun warnFormattedMessageWithLazyArgument() {
                Logger.warn("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception is discarded if the warn severity level is disabled.
             */
            @Test
            fun warnException() {
                Logger.warn(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            fun warnExceptionAndTextMessage() {
                Logger.warn(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            fun warnExceptionAndLazyMessage() {
                Logger.warn(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders is discarded if the
             * warn severity level is disabled.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithArgument() {
                Logger.warn(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the warn severity level is disabled.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithLazyArgument() {
                Logger.warn(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an object is discarded if the error severity level is disabled.
             */
            @Test
            fun errorObjectMessage() {
                Logger.error(42)
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a plain text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            fun errorTextMessage() {
                Logger.error("Hello World!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a lazy text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            fun errorLazyMessage() {
                Logger.error { "Hello World!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a message with placeholders is discarded if the error severity level
             * is disabled.
             */
            @Test
            fun errorFormattedMessageWithArgument() {
                Logger.error("Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments is discarded if the
             * error severity level is disabled.
             */
            @Test
            fun errorFormattedMessageWithLazyArgument() {
                Logger.error("Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception is discarded if the error severity level is disabled.
             */
            @Test
            fun errorException() {
                Logger.error(Exception())
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            fun errorExceptionAndTextMessage() {
                Logger.error(Exception(), "Oops!")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            fun errorExceptionAndLazyMessage() {
                Logger.error(Exception()) { "Oops!" }
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders is discarded if the
             * error severity level is disabled.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithArgument() {
                Logger.error(Exception(), "Hello {}!", "Alice")
                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the error severity level is disabled.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithLazyArgument() {
                Logger.error(Exception(), "Hello {}!", { "Alice" })
                verifyNoLogEntry()
            }

            /**
             * Verifies that no log entry has been submitted.
             */
            private fun verifyNoLogEntry() {
                verify(framework, never()).submit(any())
            }
        }
    }
}
