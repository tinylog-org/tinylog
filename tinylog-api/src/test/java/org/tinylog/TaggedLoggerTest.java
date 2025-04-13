package org.tinylog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;
import org.tinylog.core.format.message.SimpleMessageFormatter;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.JavaRuntime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaggedLoggerTest {

    private Framework framework;

    /**
     * Creates the framework.
     */
    @BeforeEach
    void create() {
        framework = mock(Framework.class);

        JavaRuntime runtime = new JavaRuntime(mock(InternalLogger.class));
        when(framework.getRuntime()).thenReturn(runtime);

        ContextStorage storage = new NopContextStorage();
        when(framework.getContextStorage()).thenReturn(storage);
    }

    /**
     * Tests for category tags.
     */
    @Nested
    class Tags {

        /**
         * Verifies that a string can be assigned as tag.
         */
        @Test
        void stringTag() {
            when(framework.getLevelVisibilityByTag("dummy")).thenReturn(new LevelVisibility(OutputDetails.DISABLED));

            TaggedLogger logger = new TaggedLogger("dummy", framework, new SimpleMessageFormatter());
            assertThat(logger.getTag()).isEqualTo("dummy");
        }

        /**
         * Verifies that {@code null} can be passed as tag for creating an untagged logger.
         */
        @Test
        void untagged() {
            when(framework.getLevelVisibilityByTag(null)).thenReturn(new LevelVisibility(OutputDetails.DISABLED));

            TaggedLogger logger = new TaggedLogger(null, framework, new SimpleMessageFormatter());
            assertThat(logger.getTag()).isNull();
        }

    }

    /**
     * Tests for severity levels.
     */
    @Nested
    class Levels {

        /**
         * Verifies the results of the {@link TaggedLogger#isTraceEnabled()} method.
         *
         * @param enabled The value for {@link Framework#isEnabled(Object, String, Level)}
         * @param outputDetails The value for {@link LevelVisibility#getTrace()}
         */
        @ParameterizedTest
        @CsvSource({
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO"
        })
        void isTraceEnabled(boolean enabled, OutputDetails outputDetails) {
            when(framework.getLevelVisibilityByTag("test")).thenReturn(
                new LevelVisibility(
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED
                )
            );

            when(framework.isEnabled(notNull(), eq("test"), eq(Level.TRACE))).thenReturn(enabled);

            TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
            assertThat(logger.isTraceEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link TaggedLogger#isDebugEnabled()} method.
         *
         * @param enabled The value for {@link Framework#isEnabled(Object, String, Level)}
         * @param outputDetails The value for {@link LevelVisibility#getDebug()}
         */
        @ParameterizedTest
        @CsvSource({
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO"
        })
        void isDebugEnabled(boolean enabled, OutputDetails outputDetails) {
            when(framework.getLevelVisibilityByTag("test")).thenReturn(
                new LevelVisibility(
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED
                )
            );

            when(framework.isEnabled(notNull(), eq("test"), eq(Level.DEBUG))).thenReturn(enabled);

            TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
            assertThat(logger.isDebugEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link TaggedLogger#isInfoEnabled()} method.
         *
         * @param enabled The value for {@link Framework#isEnabled(Object, String, Level)}
         * @param outputDetails The value for {@link LevelVisibility#getInfo()}
         */
        @ParameterizedTest
        @CsvSource({
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO"
        })
        void isInfoEnabled(boolean enabled, OutputDetails outputDetails) {
            when(framework.getLevelVisibilityByTag("test")).thenReturn(
                new LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED
                )
            );

            when(framework.isEnabled(notNull(), eq("test"), eq(Level.INFO))).thenReturn(enabled);

            TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
            assertThat(logger.isInfoEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link TaggedLogger#isWarnEnabled()} method.
         *
         * @param enabled The value for {@link Framework#isEnabled(Object, String, Level)}
         * @param outputDetails The value for {@link LevelVisibility#getWarn()}
         */
        @ParameterizedTest
        @CsvSource({
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO"
        })
        void isWarnEnabled(boolean enabled, OutputDetails outputDetails) {
            when(framework.getLevelVisibilityByTag("test")).thenReturn(
                new LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED
                )
            );

            when(framework.isEnabled(notNull(), eq("test"), eq(Level.WARN))).thenReturn(enabled);

            TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
            assertThat(logger.isWarnEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link TaggedLogger#isErrorEnabled()} method.
         *
         * @param enabled The value for {@link Framework#isEnabled(Object, String, Level)}
         * @param outputDetails The value for {@link LevelVisibility#getError()}
         */
        @ParameterizedTest
        @CsvSource({
            "false, DISABLED                       ",
            "true , DISABLED                       ",
            "false, ENABLED_WITHOUT_LOCATION_INFO  ",
            "true , ENABLED_WITHOUT_LOCATION_INFO  ",
            "false, ENABLED_WITH_CALLER_CLASS_NAME ",
            "true , ENABLED_WITH_CALLER_CLASS_NAME ",
            "false, ENABLED_WITH_FULL_LOCATION_INFO",
            "true , ENABLED_WITH_FULL_LOCATION_INFO"
        })
        void isErrorEnabled(boolean enabled, OutputDetails outputDetails) {
            when(framework.getLevelVisibilityByTag("test")).thenReturn(
                new LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails
                )
            );

            when(framework.isEnabled(notNull(), eq("test"), eq(Level.ERROR))).thenReturn(enabled);

            TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
            assertThat(logger.isErrorEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

    }

    /**
     * Tests for issuing log entries.
     */
    @Nested
    class LogEntries {

        private LevelVisibility visibility;

        /**
         * Initializes mock for level visibility.
         */
        @BeforeEach
        void init() {
            visibility = mock(LevelVisibility.class);
            when(framework.getLevelVisibilityByTag("test")).thenReturn(visibility);
        }

        /**
         * Tests issuing log entries if the assigned severity level is enabled.
         */
        @Nested
        class Enabled {

            /**
             * Verifies that a trace log entry with a plain text message can be issued.
             */
            @Test
            void traceTextMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace("Hello World!");

                verifyLogEntry(Level.TRACE, null, "Hello World!");
            }

            /**
             * Verifies that a trace log entry with an object can be issued.
             */
            @Test
            void traceMessageObject() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(42);

                verifyLogEntry(Level.TRACE, null, "42");
            }

            /**
             * Verifies that a trace log entry with a lazy text message can be issued.
             */
            @Test
            void traceLazyMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(() -> "Hello World!");

                verifyLogEntry(Level.TRACE, null, "Hello World!");
            }

            /**
             * Verifies that a trace log entry with a message with placeholders can be issued.
             */
            @Test
            void traceFormattedMessageWithArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace("Hello {}!", "Alice");

                verifyLogEntry(Level.TRACE, null, "Hello Alice!");
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void traceFormattedMessageWithLazyArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.TRACE, null, "Hello Alice!");
            }

            /**
             * Verifies that a trace log entry with an exception can be issued.
             */
            @Test
            void traceException() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(exception);

                verifyLogEntry(Level.TRACE, exception, null);
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message can be issued.
             */
            @Test
            void traceExceptionAndTextMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(exception, "Oops!");

                verifyLogEntry(Level.TRACE, exception, "Oops!");
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void traceExceptionAndLazyMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(exception, () -> "Oops!");

                verifyLogEntry(Level.TRACE, exception, "Oops!");
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void traceExceptionAndFormattedMessageWithArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(exception, "Hello {}!", "Alice");

                verifyLogEntry(Level.TRACE, exception, "Hello Alice!");
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            void traceExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.TRACE, exception, "Hello Alice!");
            }

            /**
             * Verifies that a debug log entry with a plain text message can be issued.
             */
            @Test
            void debugTextMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug("Hello World!");

                verifyLogEntry(Level.DEBUG, null, "Hello World!");
            }

            /**
             * Verifies that a debug log entry with an object can be issued.
             */
            @Test
            void debugMessageObject() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(42);

                verifyLogEntry(Level.DEBUG, null, "42");
            }

            /**
             * Verifies that a debug log entry with a lazy text message can be issued.
             */
            @Test
            void debugLazyMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(() -> "Hello World!");

                verifyLogEntry(Level.DEBUG, null, "Hello World!");
            }

            /**
             * Verifies that a debug log entry with a message with placeholders can be issued.
             */
            @Test
            void debugFormattedMessageWithArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug("Hello {}!", "Alice");

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!");
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void debugFormattedMessageWithLazyArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!");
            }

            /**
             * Verifies that a debug log entry with an exception can be issued.
             */
            @Test
            void debugException() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(exception);

                verifyLogEntry(Level.DEBUG, exception, null);
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message can be issued.
             */
            @Test
            void debugExceptionAndTextMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(exception, "Oops!");

                verifyLogEntry(Level.DEBUG, exception, "Oops!");
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void debugExceptionAndLazyMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(exception, () -> "Oops!");

                verifyLogEntry(Level.DEBUG, exception, "Oops!");
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void debugExceptionAndFormattedMessageWithArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(exception, "Hello {}!", "Alice");

                verifyLogEntry(Level.DEBUG, exception, "Hello Alice!");
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            void debugExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.DEBUG, exception, "Hello Alice!");
            }

            /**
             * Verifies that an info log entry with a plain text message can be issued.
             */
            @Test
            void infoTextMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info("Hello World!");

                verifyLogEntry(Level.INFO, null, "Hello World!");
            }

            /**
             * Verifies that an info log entry with an object can be issued.
             */
            @Test
            void infoMessageObject() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(42);

                verifyLogEntry(Level.INFO, null, "42");
            }

            /**
             * Verifies that an info log entry with a lazy text message can be issued.
             */
            @Test
            void infoLazyMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(() -> "Hello World!");

                verifyLogEntry(Level.INFO, null, "Hello World!");
            }

            /**
             * Verifies that an info log entry with a message with placeholders can be issued.
             */
            @Test
            void infoFormattedMessageWithArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info("Hello {}!", "Alice");

                verifyLogEntry(Level.INFO, null, "Hello Alice!");
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void infoFormattedMessageWithLazyArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.INFO, null, "Hello Alice!");
            }

            /**
             * Verifies that an info log entry with an exception can be issued.
             */
            @Test
            void infoException() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(exception);

                verifyLogEntry(Level.INFO, exception, null);
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message can be issued.
             */
            @Test
            void infoExceptionAndTextMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(exception, "Oops!");

                verifyLogEntry(Level.INFO, exception, "Oops!");
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void infoExceptionAndLazyMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(exception, () -> "Oops!");

                verifyLogEntry(Level.INFO, exception, "Oops!");
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void infoExceptionAndFormattedMessageWithArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(exception, "Hello {}!", "Alice");

                verifyLogEntry(Level.INFO, exception, "Hello Alice!");
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            void infoExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.INFO, exception, "Hello Alice!");
            }

            /**
             * Verifies that a warning log entry with a plain text message can be issued.
             */
            @Test
            void warnTextMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn("Hello World!");

                verifyLogEntry(Level.WARN, null, "Hello World!");
            }

            /**
             * Verifies that a warning log entry with an object can be issued.
             */
            @Test
            void warnMessageObject() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(42);

                verifyLogEntry(Level.WARN, null, "42");
            }

            /**
             * Verifies that a warning log entry with a lazy text message can be issued.
             */
            @Test
            void warnLazyMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(() -> "Hello World!");

                verifyLogEntry(Level.WARN, null, "Hello World!");
            }

            /**
             * Verifies that a warning log entry with a message with placeholders can be issued.
             */
            @Test
            void warnFormattedMessageWithArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn("Hello {}!", "Alice");

                verifyLogEntry(Level.WARN, null, "Hello Alice!");
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void warnFormattedMessageWithLazyArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.WARN, null, "Hello Alice!");
            }

            /**
             * Verifies that a warning log entry with an exception can be issued.
             */
            @Test
            void warnException() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(exception);

                verifyLogEntry(Level.WARN, exception, null);
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message can be issued.
             */
            @Test
            void warnExceptionAndTextMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(exception, "Oops!");

                verifyLogEntry(Level.WARN, exception, "Oops!");
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void warnExceptionAndLazyMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(exception, () -> "Oops!");

                verifyLogEntry(Level.WARN, exception, "Oops!");
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void warnExceptionAndFormattedMessageWithArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(exception, "Hello {}!", "Alice");

                verifyLogEntry(Level.WARN, exception, "Hello Alice!");
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments
             * can be issued.
             */
            @Test
            void warnExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.WARN, exception, "Hello Alice!");
            }

            /**
             * Verifies that an error log entry with a plain text message can be issued.
             */
            @Test
            void errorTextMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error("Hello World!");

                verifyLogEntry(Level.ERROR, null, "Hello World!");
            }

            /**
             * Verifies that an error log entry with an object can be issued.
             */
            @Test
            void errorMessageObject() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(42);

                verifyLogEntry(Level.ERROR, null, "42");
            }

            /**
             * Verifies that an error log entry with a lazy text message can be issued.
             */
            @Test
            void errorLazyMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(() -> "Hello World!");

                verifyLogEntry(Level.ERROR, null, "Hello World!");
            }

            /**
             * Verifies that an error log entry with a message with placeholders can be issued.
             */
            @Test
            void errorFormattedMessageWithArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error("Hello {}!", "Alice");

                verifyLogEntry(Level.ERROR, null, "Hello Alice!");
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void errorFormattedMessageWithLazyArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.ERROR, null, "Hello Alice!");
            }

            /**
             * Verifies that an error log entry with an exception can be issued.
             */
            @Test
            void errorException() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(exception);

                verifyLogEntry(Level.ERROR, exception, null);
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message can be issued.
             */
            @Test
            void errorExceptionAndTextMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(exception, "Oops!");

                verifyLogEntry(Level.ERROR, exception, "Oops!");
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void errorExceptionAndLazyMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(exception, () -> "Oops!");

                verifyLogEntry(Level.ERROR, exception, "Oops!");
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void errorExceptionAndFormattedMessageWithArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(exception, "Hello {}!", "Alice");

                verifyLogEntry(Level.ERROR, exception, "Hello Alice!");
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments can
             * be issued.
             */
            @Test
            void errorExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.ERROR, exception, "Hello Alice!");
            }

            /**
             * Verifies backend mock invocation with expected log entry values.
             *
             * @param level The expected severity level
             * @param exception The expected exception
             * @param message The expected rendered text message
             */
            private void verifyLogEntry(Level level, Exception exception, String message) {
                ArgumentCaptor<LogEntry> captor = ArgumentCaptor.forClass(LogEntry.class);

                verify(framework, atMostOnce()).getLevelVisibilityByTag(null);
                verify(framework).submit(captor.capture());

                assertThat(captor.getAllValues()).singleElement().satisfies(entry -> {
                    assertThat(entry.getThread()).isSameAs(Thread.currentThread());
                    assertThat(entry.getContext()).isEmpty();
                    assertThat(entry.getClassName()).isEqualTo(TaggedLoggerTest.LogEntries.Enabled.class.getName());
                    assertThat(entry.getMethodName()).isNull();
                    assertThat(entry.getFileName()).isNull();
                    assertThat(entry.getLineNumber()).isEqualTo(-1);
                    assertThat(entry.getTag()).isEqualTo("test");
                    assertThat(entry.getSeverityLevel()).isEqualTo(level);
                    assertThat(entry.getThrowable()).isSameAs(exception);
                    assertThat(entry.getFormattedMessage(mock(Configuration.class))).isEqualTo(message);
                });
            }

        }


        /**
         * Tests discarding log entries if the assigned severity level is disabled.
         */
        @Nested
        class Disabled {

            /**
             * Verifies that a trace log entry with a plain text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            void traceTextMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace("Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an object is discarded if the trace severity level is disabled.
             */
            @Test
            void traceMessageObject() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(42);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a lazy text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            void traceLazyMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(() -> "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message with placeholders is discarded if the trace severity level
             * is disabled.
             */
            @Test
            void traceFormattedMessageWithArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace("Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments is discarded if the
             * trace severity level is disabled.
             */
            @Test
            void traceFormattedMessageWithLazyArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace("Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception is discarded if the trace severity level is disabled.
             */
            @Test
            void traceException() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(new Exception());

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            void traceExceptionAndTextMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(new Exception(), "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            void traceExceptionAndLazyMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(new Exception(), () -> "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders is discarded if the
             * trace severity level is disabled.
             */
            @Test
            void traceExceptionAndFormattedMessageWithArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(new Exception(), "Hello Alice!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the trace severity level is disabled.
             */
            @Test
            void traceExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.trace(new Exception(), "Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a plain text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            void debugTextMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug("Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an object is discarded if the debug severity level is disabled.
             */
            @Test
            void debugMessageObject() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(42);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a lazy text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            void debugLazyMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(() -> "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message with placeholders is discarded if the debug severity level
             * is disabled.
             */
            @Test
            void debugFormattedMessageWithArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug("Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments is discarded if the
             * debug severity level is disabled.
             */
            @Test
            void debugFormattedMessageWithLazyArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug("Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception is discarded if the debug severity level is disabled.
             */
            @Test
            void debugException() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(new Exception());

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            void debugExceptionAndTextMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(new Exception(), "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            void debugExceptionAndLazyMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(new Exception(), () -> "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders is discarded if the
             * debug severity level is disabled.
             */
            @Test
            void debugExceptionAndFormattedMessageWithArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(new Exception(), "Hello Alice!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the debug severity level is disabled.
             */
            @Test
            void debugExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.debug(new Exception(), "Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a plain text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            void infoTextMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info("Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an object is discarded if the info severity level is disabled.
             */
            @Test
            void infoMessageObject() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(42);

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a lazy text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            void infoLazyMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(() -> "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message with placeholders is discarded if the info severity level
             * is disabled.
             */
            @Test
            void infoFormattedMessageWithArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info("Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments is discarded if the
             * info severity level is disabled.
             */
            @Test
            void infoFormattedMessageWithLazyArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info("Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception is discarded if the info severity level is disabled.
             */
            @Test
            void infoException() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(new Exception());

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            void infoExceptionAndTextMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(new Exception(), "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            void infoExceptionAndLazyMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(new Exception(), () -> "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders is discarded if the
             * info severity level is disabled.
             */
            @Test
            void infoExceptionAndFormattedMessageWithArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(new Exception(), "Hello Alice!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the info severity level is disabled.
             */
            @Test
            void infoExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.info(new Exception(), "Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            void warnTextMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn("Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an object is discarded if the warn severity level is disabled.
             */
            @Test
            void warnMessageObject() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(42);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a lazy text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            void warnLazyMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(() -> "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message with placeholders is discarded if the warn severity
             * level is disabled.
             */
            @Test
            void warnFormattedMessageWithArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn("Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments is discarded if the
             * warn severity level is disabled.
             */
            @Test
            void warnFormattedMessageWithLazyArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn("Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception is discarded if the warn severity level is disabled.
             */
            @Test
            void warnException() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(new Exception());

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            void warnExceptionAndTextMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(new Exception(), "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            void warnExceptionAndLazyMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(new Exception(), () -> "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders is discarded if the
             * warn severity level is disabled.
             */
            @Test
            void warnExceptionAndFormattedMessageWithArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(new Exception(), "Hello Alice!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the warn severity level is disabled.
             */
            @Test
            void warnExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.warn(new Exception(), "Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a plain text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            void errorTextMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error("Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an object is discarded if the error severity level is disabled.
             */
            @Test
            void errorMessageObject() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(42);

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a lazy text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            void errorLazyMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(() -> "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message with placeholders is discarded if the error severity
             * level is disabled.
             */
            @Test
            void errorFormattedMessageWithArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error("Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments is discarded if the
             * error severity level is disabled.
             */
            @Test
            void errorFormattedMessageWithLazyArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error("Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception is discarded if the error severity level is disabled.
             */
            @Test
            void errorException() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(new Exception());

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            void errorExceptionAndTextMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(new Exception(), "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            void errorExceptionAndLazyMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(new Exception(), () -> "Oops!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders is discarded if the
             * error severity level is disabled.
             */
            @Test
            void errorExceptionAndFormattedMessageWithArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(new Exception(), "Hello Alice!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the error severity level is disabled.
             */
            @Test
            void errorExceptionAndFormattedMessageWithLazyArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

                TaggedLogger logger = new TaggedLogger("test", framework, new SimpleMessageFormatter());
                logger.error(new Exception(), "Hello {}!", () -> "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that no log entry has been submitted.
             */
            private void verifyNoLogEntry() {
                verify(framework, never()).submit(any());
            }

        }

    }

}
