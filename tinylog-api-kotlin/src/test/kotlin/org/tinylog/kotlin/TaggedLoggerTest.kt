package org.tinylog.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.atMost
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.notNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.tinylog.core.Framework
import org.tinylog.core.Level
import org.tinylog.core.LogEntry
import org.tinylog.core.backend.LevelVisibility
import org.tinylog.core.backend.OutputDetails
import org.tinylog.core.context.NopContextStorage
import org.tinylog.core.format.message.SimpleMessageFormatter
import org.tinylog.core.runtime.JavaRuntime

class TaggedLoggerTest {
    private lateinit var framework: Framework

    /**
     * Creates the framework.
     */
    @BeforeEach
    fun create() {
        val javaRuntime = JavaRuntime(mock())
        val nopContextStorage = NopContextStorage()

        framework =
            mock<Framework>().apply {
                whenever(runtime).thenReturn(javaRuntime)
                whenever(contextStorage).thenReturn(nopContextStorage)
            }
    }

    /**
     * Tests for category tags.
     */
    @Nested
    inner class Tags {
        /**
         * Verifies that a string can be assigned as tag.
         */
        @Test
        fun stringTag() {
            whenever(framework.getLevelVisibilityByTag("dummy")).thenReturn(LevelVisibility(OutputDetails.DISABLED))

            val logger = TaggedLogger("dummy", framework, SimpleMessageFormatter())
            assertThat(logger.tag).isEqualTo("dummy")
        }

        /**
         * Verifies that `null` can be passed as tag for creating an untagged logger.
         */
        @Test
        fun nullTag() {
            whenever(framework.getLevelVisibilityByTag(null)).thenReturn(LevelVisibility(OutputDetails.DISABLED))

            val logger = TaggedLogger(null, framework, SimpleMessageFormatter())
            assertThat(logger.tag).isNull()
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
            whenever(framework.getLevelVisibilityByTag("test")).thenReturn(
                LevelVisibility(
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                ),
            )

            whenever(framework.isEnabled(notNull(), eq("test"), eq(Level.TRACE))).thenReturn(enabled)

            val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
            assertThat(logger.isTraceEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
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
            whenever(framework.getLevelVisibilityByTag("test")).thenReturn(
                LevelVisibility(
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                ),
            )

            whenever(framework.isEnabled(notNull(), eq("test"), eq(Level.DEBUG))).thenReturn(enabled)

            val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
            assertThat(logger.isDebugEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
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
            whenever(framework.getLevelVisibilityByTag("test")).thenReturn(
                LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                ),
            )

            whenever(framework.isEnabled(notNull(), eq("test"), eq(Level.INFO))).thenReturn(enabled)

            val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
            assertThat(logger.isInfoEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
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
            whenever(framework.getLevelVisibilityByTag("test")).thenReturn(
                LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED,
                ),
            )

            whenever(framework.isEnabled(notNull(), eq("test"), eq(Level.WARN))).thenReturn(enabled)

            val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
            assertThat(logger.isWarnEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
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
            whenever(framework.getLevelVisibilityByTag("test")).thenReturn(
                LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails,
                ),
            )

            whenever(framework.isEnabled(notNull(), eq("test"), eq(Level.ERROR))).thenReturn(enabled)

            val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
            assertThat(logger.isErrorEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled)
        }
    }

    /**
     * Tests for issuing log entries.
     */
    @Nested
    inner class LogEntries {
        private lateinit var visibility: LevelVisibility

        /**
         * Initializes mock for level visibility.
         */
        @BeforeEach
        fun init() {
            visibility = mock()

            whenever(visibility.trace).thenReturn(OutputDetails.DISABLED)
            whenever(visibility.debug).thenReturn(OutputDetails.DISABLED)
            whenever(visibility.info).thenReturn(OutputDetails.DISABLED)
            whenever(visibility.warn).thenReturn(OutputDetails.DISABLED)
            whenever(visibility.error).thenReturn(OutputDetails.DISABLED)

            whenever(framework.getLevelVisibilityByTag("test")).thenReturn(visibility)
        }

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

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(42)

                verifyLogEntry(Level.TRACE, null, "42")
            }

            /**
             * Verifies that a trace log entry with a plain text message can be issued.
             */
            @Test
            fun traceTextMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace("Hello World!")

                verifyLogEntry(Level.TRACE, null, "Hello World!")
            }

            /**
             * Verifies that a trace log entry with a lazy text message can be issued.
             */
            @Test
            fun traceLazyMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace { "Hello World!" }

                verifyLogEntry(Level.TRACE, null, "Hello World!")
            }

            /**
             * Verifies that a trace log entry with a message with placeholders can be issued.
             */
            @Test
            fun traceFormattedMessageWithArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace("Hello {}!", "Alice")

                verifyLogEntry(Level.TRACE, null, "Hello Alice!")
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun traceFormattedMessageWithLazyArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace("Hello {}!", { "Alice" })

                verifyLogEntry(Level.TRACE, null, "Hello Alice!")
            }

            /**
             * Verifies that a trace log entry with an exception can be issued.
             */
            @Test
            fun traceException() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(exception)

                verifyLogEntry(Level.TRACE, exception, null)
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun traceExceptionAndTextMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(exception, "Oops!")

                verifyLogEntry(Level.TRACE, exception, "Oops!")
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun traceExceptionAndLazyMessage() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(exception) { "Oops!" }

                verifyLogEntry(Level.TRACE, exception, "Oops!")
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.trace).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(exception, "Hello {}!", "Alice")

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
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(exception, "Hello {}!", { "Alice" })

                verifyLogEntry(Level.TRACE, exception, "Hello Alice!")
            }

            /**
             * Verifies that a debug log entry with an object can be issued.
             */
            @Test
            fun debugObjectMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(42)

                verifyLogEntry(Level.DEBUG, null, "42")
            }

            /**
             * Verifies that a debug log entry with a plain text message can be issued.
             */
            @Test
            fun debugTextMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug("Hello World!")

                verifyLogEntry(Level.DEBUG, null, "Hello World!")
            }

            /**
             * Verifies that a debug log entry with a lazy text message can be issued.
             */
            @Test
            fun debugLazyMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug { "Hello World!" }

                verifyLogEntry(Level.DEBUG, null, "Hello World!")
            }

            /**
             * Verifies that a debug log entry with a message with placeholders can be issued.
             */
            @Test
            fun debugFormattedMessageWithArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug("Hello {}!", "Alice")

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!")
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun debugFormattedMessageWithLazyArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug("Hello {}!", { "Alice" })

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!")
            }

            /**
             * Verifies that a debug log entry with an exception can be issued.
             */
            @Test
            fun debugException() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(exception)

                verifyLogEntry(Level.DEBUG, exception, null)
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun debugExceptionAndTextMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(exception, "Oops!")

                verifyLogEntry(Level.DEBUG, exception, "Oops!")
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun debugExceptionAndLazyMessage() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(exception) { "Oops!" }

                verifyLogEntry(Level.DEBUG, exception, "Oops!")
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.debug).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(exception, "Hello {}!", "Alice")

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
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(exception, "Hello {}!", { "Alice" })

                verifyLogEntry(Level.DEBUG, exception, "Hello Alice!")
            }

            /**
             * Verifies that an info log entry with an object can be issued.
             */
            @Test
            fun infoObjectMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(42)

                verifyLogEntry(Level.INFO, null, "42")
            }

            /**
             * Verifies that an info log entry with a plain text message can be issued.
             */
            @Test
            fun infoTextMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info("Hello World!")

                verifyLogEntry(Level.INFO, null, "Hello World!")
            }

            /**
             * Verifies that an info log entry with a lazy text message can be issued.
             */
            @Test
            fun infoLazyMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info { "Hello World!" }

                verifyLogEntry(Level.INFO, null, "Hello World!")
            }

            /**
             * Verifies that an info log entry with a message with placeholders can be issued.
             */
            @Test
            fun infoFormattedMessageWithArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info("Hello {}!", "Alice")

                verifyLogEntry(Level.INFO, null, "Hello Alice!")
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun infoFormattedMessageWithLazyArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info("Hello {}!", { "Alice" })

                verifyLogEntry(Level.INFO, null, "Hello Alice!")
            }

            /**
             * Verifies that an info log entry with an exception can be issued.
             */
            @Test
            fun infoException() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(exception)

                verifyLogEntry(Level.INFO, exception, null)
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun infoExceptionAndTextMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(exception, "Oops!")

                verifyLogEntry(Level.INFO, exception, "Oops!")
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun infoExceptionAndLazyMessage() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(exception) { "Oops!" }

                verifyLogEntry(Level.INFO, exception, "Oops!")
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.info).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(exception, "Hello {}!", "Alice")

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
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(exception, "Hello {}!", { "Alice" })

                verifyLogEntry(Level.INFO, exception, "Hello Alice!")
            }

            /**
             * Verifies that a warning log entry with an object can be issued.
             */
            @Test
            fun warnObjectMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(42)

                verifyLogEntry(Level.WARN, null, "42")
            }

            /**
             * Verifies that a warning log entry with a plain text message can be issued.
             */
            @Test
            fun warnTextMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn("Hello World!")

                verifyLogEntry(Level.WARN, null, "Hello World!")
            }

            /**
             * Verifies that a warning log entry with a lazy text message can be issued.
             */
            @Test
            fun warnLazyMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn { "Hello World!" }

                verifyLogEntry(Level.WARN, null, "Hello World!")
            }

            /**
             * Verifies that a warning log entry with a message with placeholders can be issued.
             */
            @Test
            fun warnFormattedMessageWithArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn("Hello {}!", "Alice")

                verifyLogEntry(Level.WARN, null, "Hello Alice!")
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun warnFormattedMessageWithLazyArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn("Hello {}!", { "Alice" })

                verifyLogEntry(Level.WARN, null, "Hello Alice!")
            }

            /**
             * Verifies that a warning log entry with an exception can be issued.
             */
            @Test
            fun warnException() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(exception)

                verifyLogEntry(Level.WARN, exception, null)
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun warnExceptionAndTextMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(exception, "Oops!")

                verifyLogEntry(Level.WARN, exception, "Oops!")
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun warnExceptionAndLazyMessage() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(exception) { "Oops!" }

                verifyLogEntry(Level.WARN, exception, "Oops!")
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.warn).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(exception, "Hello {}!", "Alice")

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
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(exception, "Hello {}!", { "Alice" })

                verifyLogEntry(Level.WARN, exception, "Hello Alice!")
            }

            /**
             * Verifies that an error log entry with an object can be issued.
             */
            @Test
            fun errorObjectMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(42)

                verifyLogEntry(Level.ERROR, null, "42")
            }

            /**
             * Verifies that an error log entry with a plain text message can be issued.
             */
            @Test
            fun errorTextMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error("Hello World!")

                verifyLogEntry(Level.ERROR, null, "Hello World!")
            }

            /**
             * Verifies that an error log entry with a lazy text message can be issued.
             */
            @Test
            fun errorLazyMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error { "Hello World!" }

                verifyLogEntry(Level.ERROR, null, "Hello World!")
            }

            /**
             * Verifies that an error log entry with a message with placeholders can be issued.
             */
            @Test
            fun errorFormattedMessageWithArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error("Hello {}!", "Alice")

                verifyLogEntry(Level.ERROR, null, "Hello Alice!")
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            fun errorFormattedMessageWithLazyArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error("Hello {}!", { "Alice" })

                verifyLogEntry(Level.ERROR, null, "Hello Alice!")
            }

            /**
             * Verifies that an error log entry with an exception can be issued.
             */
            @Test
            fun errorException() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(exception)

                verifyLogEntry(Level.ERROR, exception, null)
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message can be issued.
             */
            @Test
            fun errorExceptionAndTextMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(exception, "Oops!")

                verifyLogEntry(Level.ERROR, exception, "Oops!")
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message can be issued.
             */
            @Test
            fun errorExceptionAndLazyMessage() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(exception) { "Oops!" }

                verifyLogEntry(Level.ERROR, exception, "Oops!")
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithArgument() {
                whenever(visibility.error).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME)

                val exception = Exception()
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(exception, "Hello {}!", "Alice")

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
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(exception, "Hello {}!", { "Alice" })

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
                    assertThat(entry.tag).isEqualTo("test")
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
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(42)

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a plain text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            fun traceTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace("Hello World!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a lazy text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            fun traceLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace { "Hello World!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a message with placeholders is discarded if the trace severity level
             * is disabled.
             */
            @Test
            fun traceFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace("Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments is discarded if the
             * trace severity level is disabled.
             */
            @Test
            fun traceFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace("Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception is discarded if the trace severity level is disabled.
             */
            @Test
            fun traceException() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(Exception())

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            fun traceExceptionAndTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(Exception(), "Oops!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            fun traceExceptionAndLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(Exception()) { "Oops!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders is discarded if the
             * trace severity level is disabled.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(Exception(), "Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the trace severity level is disabled.
             */
            @Test
            fun traceExceptionAndFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.trace(Exception(), "Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an object is discarded if the debug severity level is disabled.
             */
            @Test
            fun debugObjectMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(42)

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a plain text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            fun debugTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug("Hello World!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a lazy text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            fun debugLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug { "Hello World!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a message with placeholders is discarded if the debug severity level
             * is disabled.
             */
            @Test
            fun debugFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug("Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments is discarded if the
             * debug severity level is disabled.
             */
            @Test
            fun debugFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug("Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception is discarded if the debug severity level is disabled.
             */
            @Test
            fun debugException() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(Exception())

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            fun debugExceptionAndTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(Exception(), "Oops!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            fun debugExceptionAndLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(Exception()) { "Oops!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders is discarded if the
             * debug severity level is disabled.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(Exception(), "Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the debug severity level is disabled.
             */
            @Test
            fun debugExceptionAndFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.debug(Exception(), "Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an object is discarded if the info severity level is disabled.
             */
            @Test
            fun infoObjectMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(42)

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a plain text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            fun infoTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info("Hello World!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a lazy text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            fun infoLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info { "Hello World!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a message with placeholders is discarded if the info severity level
             * is disabled.
             */
            @Test
            fun infoFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info("Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments is discarded if the
             * info severity level is disabled.
             */
            @Test
            fun infoFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info("Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception is discarded if the info severity level is disabled.
             */
            @Test
            fun infoException() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(Exception())

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            fun infoExceptionAndTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(Exception(), "Oops!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            fun infoExceptionAndLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(Exception()) { "Oops!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders is discarded if the
             * info severity level is disabled.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(Exception(), "Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the info severity level is disabled.
             */
            @Test
            fun infoExceptionAndFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.info(Exception(), "Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an object is discarded if the warn severity level is disabled.
             */
            @Test
            fun warnObjectMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(42)

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            fun warnTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn("Hello World!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a lazy text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            fun warnLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn { "Hello World!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a message with placeholders is discarded if the warn severity level
             * is disabled.
             */
            @Test
            fun warnFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn("Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments is discarded if the
             * warn severity level is disabled.
             */
            @Test
            fun warnFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn("Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception is discarded if the warn severity level is disabled.
             */
            @Test
            fun warnException() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(Exception())

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            fun warnExceptionAndTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(Exception(), "Oops!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            fun warnExceptionAndLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(Exception()) { "Oops!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders is discarded if the
             * warn severity level is disabled.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(Exception(), "Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the warn severity level is disabled.
             */
            @Test
            fun warnExceptionAndFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.warn(Exception(), "Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an object is discarded if the error severity level is disabled.
             */
            @Test
            fun errorObjectMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(42)

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a plain text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            fun errorTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error("Hello World!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a lazy text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            fun errorLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error { "Hello World!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a message with placeholders is discarded if the error severity level
             * is disabled.
             */
            @Test
            fun errorFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error("Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments is discarded if the
             * error severity level is disabled.
             */
            @Test
            fun errorFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error("Hello {}!", { "Alice" })

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception is discarded if the error severity level is disabled.
             */
            @Test
            fun errorException() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(Exception())

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            fun errorExceptionAndTextMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(Exception(), "Oops!")

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            fun errorExceptionAndLazyMessage() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(Exception()) { "Oops!" }

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders is discarded if the
             * error severity level is disabled.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(Exception(), "Hello {}!", "Alice")

                verifyNoLogEntry()
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the error severity level is disabled.
             */
            @Test
            fun errorExceptionAndFormattedMessageWithLazyArgument() {
                val logger = TaggedLogger("test", framework, SimpleMessageFormatter())
                logger.error(Exception(), "Hello {}!", { "Alice" })

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
