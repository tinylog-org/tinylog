package org.tinylog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.JavaRuntime;
import org.tinylog.test.junit.isolate.IsolatedExecution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@IsolatedExecution(classes = {Logger.class, TaggedLogger.class})
class LoggerTest {

    private MockedStatic<Tinylog> tinylogMock;
    private Framework framework;
    private LevelVisibility visibility;

    /**
     * Initializes all mocks.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @BeforeEach
    void init() {
        tinylogMock = mockStatic(Tinylog.class);
        framework = mock(Framework.class);
        visibility = mock(LevelVisibility.class);

        JavaRuntime runtime = new JavaRuntime(mock(InternalLogger.class));
        ContextStorage storage = new NopContextStorage();

        when(visibility.getTrace()).thenReturn(OutputDetails.DISABLED);
        when(visibility.getDebug()).thenReturn(OutputDetails.DISABLED);
        when(visibility.getInfo()).thenReturn(OutputDetails.DISABLED);
        when(visibility.getWarn()).thenReturn(OutputDetails.DISABLED);
        when(visibility.getError()).thenReturn(OutputDetails.DISABLED);

        when(framework.getRuntime()).thenReturn(runtime);
        when(framework.getContextStorage()).thenReturn(storage);
        when(framework.getLevelVisibilityByTag(isNull())).thenReturn(visibility);
        when(framework.getLevelVisibilityByTag(notNull())).thenReturn(mock(LevelVisibility.class));

        tinylogMock.when(Tinylog::getFramework).thenReturn(framework);
    }

    /**
     * Restores the mocked tinylog class.
     */
    @AfterEach
    void reset() {
        tinylogMock.close();
    }

    /**
     * Tests for category tests.
     */
    @Nested
    class Tags {

        /**
         * Verifies that the same logger instance is returned for the same tag.
         */
        @Test
        void sameLoggerInstanceForSameTag() {
            TaggedLogger first = Logger.tag("foo");
            TaggedLogger second = Logger.tag("foo");
            assertThat(first).isNotNull().isSameAs(second);
        }

        /**
         * Verifies that different logger instances are returned for different tags.
         */
        @Test
        void differentLoggerInstanceForDifferentTag() {
            TaggedLogger first = Logger.tag("foo");
            TaggedLogger second = Logger.tag("bar");

            assertThat(first).isNotNull();
            assertThat(second).isNotNull();
            assertThat(first).isNotSameAs(second);
        }

        /**
         * Verifies that the same untagged root logger is returned for {@code null} and empty tags.
         */
        @Test
        void sameUntaggedRootLoggerForNullAndEmptyTags() {
            TaggedLogger nullTag = Logger.tag(null);
            TaggedLogger emptyTag = Logger.tag("");

            assertThat(nullTag).isNotNull();
            assertThat(nullTag.getTag()).isNull();
            assertThat(emptyTag).isNotNull();
            assertThat(emptyTag.getTag()).isNull();

            assertThat(nullTag).isSameAs(emptyTag);
        }

    }

    /**
     * Tests for severity levels.
     */
    @Nested
    class Levels {

        /**
         * Verifies the results of the {@link Logger#isTraceEnabled()} method.
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
            when(visibility.getTrace()).thenReturn(outputDetails);
            when(framework.isEnabled(notNull(), isNull(), eq(Level.TRACE))).thenReturn(enabled);

            assertThat(Logger.isTraceEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link Logger#isDebugEnabled()} method.
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
            when(visibility.getDebug()).thenReturn(outputDetails);
            when(framework.isEnabled(notNull(), isNull(), eq(Level.DEBUG))).thenReturn(enabled);

            assertThat(Logger.isDebugEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link Logger#isInfoEnabled()} method.
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
            when(visibility.getInfo()).thenReturn(outputDetails);
            when(framework.isEnabled(notNull(), isNull(), eq(Level.INFO))).thenReturn(enabled);

            assertThat(Logger.isInfoEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link Logger#isWarnEnabled()} method.
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
            when(visibility.getWarn()).thenReturn(outputDetails);
            when(framework.isEnabled(notNull(), isNull(), eq(Level.WARN))).thenReturn(enabled);

            assertThat(Logger.isWarnEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link Logger#isErrorEnabled()} method.
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
            when(visibility.getError()).thenReturn(outputDetails);
            when(framework.isEnabled(notNull(), isNull(), eq(Level.ERROR))).thenReturn(enabled);

            assertThat(Logger.isErrorEnabled()).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

    }

    /**
     * Tests for issuing log entries.
     */
    @Nested
    class LogEntries {

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

                Logger.trace("Hello World!");

                verifyLogEntry(Level.TRACE, null, "Hello World!");
            }

            /**
             * Verifies that a trace log entry with an object can be issued.
             */
            @Test
            void traceMessageObject() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.trace(42);

                verifyLogEntry(Level.TRACE, null, "42");
            }

            /**
             * Verifies that a trace log entry with a lazy text message can be issued.
             */
            @Test
            void traceLazyMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.trace(() -> "Hello World!");

                verifyLogEntry(Level.TRACE, null, "Hello World!");
            }

            /**
             * Verifies that a trace log entry with a message with placeholders can be issued.
             */
            @Test
            void traceFormattedMessageWithArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.trace("Hello {}!", "Alice");

                verifyLogEntry(Level.TRACE, null, "Hello Alice!");
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void traceFormattedMessageWithLazyArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.trace("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.TRACE, null, "Hello Alice!");
            }

            /**
             * Verifies that a trace log entry with an exception can be issued.
             */
            @Test
            void traceException() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.trace(exception);

                verifyLogEntry(Level.TRACE, exception, null);
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message can be issued.
             */
            @Test
            void traceExceptionAndTextMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.trace(exception, "Oops!");

                verifyLogEntry(Level.TRACE, exception, "Oops!");
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void traceExceptionAndLazyMessage() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.trace(exception, () -> "Oops!");

                verifyLogEntry(Level.TRACE, exception, "Oops!");
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void traceExceptionAndFormattedMessageWithArgument() {
                when(visibility.getTrace()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.trace(exception, "Hello {}!", "Alice");

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
                Logger.trace(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.TRACE, exception, "Hello Alice!");
            }

            /**
             * Verifies that a debug log entry with a plain text message can be issued.
             */
            @Test
            void debugTextMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.debug("Hello World!");

                verifyLogEntry(Level.DEBUG, null, "Hello World!");
            }

            /**
             * Verifies that a debug log entry with an object can be issued.
             */
            @Test
            void debugMessageObject() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.debug(42);

                verifyLogEntry(Level.DEBUG, null, "42");
            }

            /**
             * Verifies that a debug log entry with a lazy text message can be issued.
             */
            @Test
            void debugLazyMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.debug(() -> "Hello World!");

                verifyLogEntry(Level.DEBUG, null, "Hello World!");
            }

            /**
             * Verifies that a debug log entry with a message with placeholders can be issued.
             */
            @Test
            void debugFormattedMessageWithArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.debug("Hello {}!", "Alice");

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!");
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void debugFormattedMessageWithLazyArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.debug("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!");
            }

            /**
             * Verifies that a debug log entry with an exception can be issued.
             */
            @Test
            void debugException() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.debug(exception);

                verifyLogEntry(Level.DEBUG, exception, null);
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message can be issued.
             */
            @Test
            void debugExceptionAndTextMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.debug(exception, "Oops!");

                verifyLogEntry(Level.DEBUG, exception, "Oops!");
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void debugExceptionAndLazyMessage() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.debug(exception, () -> "Oops!");

                verifyLogEntry(Level.DEBUG, exception, "Oops!");
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void debugExceptionAndFormattedMessageWithArgument() {
                when(visibility.getDebug()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.debug(exception, "Hello {}!", "Alice");

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
                Logger.debug(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.DEBUG, exception, "Hello Alice!");
            }

            /**
             * Verifies that an info log entry with a plain text message can be issued.
             */
            @Test
            void infoTextMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.info("Hello World!");

                verifyLogEntry(Level.INFO, null, "Hello World!");
            }

            /**
             * Verifies that an info log entry with an object can be issued.
             */
            @Test
            void infoMessageObject() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.info(42);

                verifyLogEntry(Level.INFO, null, "42");
            }

            /**
             * Verifies that an info log entry with a lazy text message can be issued.
             */
            @Test
            void infoLazyMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.info(() -> "Hello World!");

                verifyLogEntry(Level.INFO, null, "Hello World!");
            }

            /**
             * Verifies that an info log entry with a message with placeholders can be issued.
             */
            @Test
            void infoFormattedMessageWithArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.info("Hello {}!", "Alice");

                verifyLogEntry(Level.INFO, null, "Hello Alice!");
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void infoFormattedMessageWithLazyArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.info("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.INFO, null, "Hello Alice!");
            }

            /**
             * Verifies that an info log entry with an exception can be issued.
             */
            @Test
            void infoException() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.info(exception);

                verifyLogEntry(Level.INFO, exception, null);
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message can be issued.
             */
            @Test
            void infoExceptionAndTextMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.info(exception, "Oops!");

                verifyLogEntry(Level.INFO, exception, "Oops!");
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void infoExceptionAndLazyMessage() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.info(exception, () -> "Oops!");

                verifyLogEntry(Level.INFO, exception, "Oops!");
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void infoExceptionAndFormattedMessageWithArgument() {
                when(visibility.getInfo()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.info(exception, "Hello {}!", "Alice");

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
                Logger.info(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.INFO, exception, "Hello Alice!");
            }

            /**
             * Verifies that a warning log entry with a plain text message can be issued.
             */
            @Test
            void warnTextMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.warn("Hello World!");

                verifyLogEntry(Level.WARN, null, "Hello World!");
            }

            /**
             * Verifies that a warning log entry with an object can be issued.
             */
            @Test
            void warnMessageObject() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.warn(42);

                verifyLogEntry(Level.WARN, null, "42");
            }

            /**
             * Verifies that a warning log entry with a lazy text message can be issued.
             */
            @Test
            void warnLazyMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.warn(() -> "Hello World!");

                verifyLogEntry(Level.WARN, null, "Hello World!");
            }

            /**
             * Verifies that a warning log entry with a message with placeholders can be issued.
             */
            @Test
            void warnFormattedMessageWithArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.warn("Hello {}!", "Alice");

                verifyLogEntry(Level.WARN, null, "Hello Alice!");
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void warnFormattedMessageWithLazyArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.warn("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.WARN, null, "Hello Alice!");
            }

            /**
             * Verifies that a warning log entry with an exception can be issued.
             */
            @Test
            void warnException() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.warn(exception);

                verifyLogEntry(Level.WARN, exception, null);
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message can be issued.
             */
            @Test
            void warnExceptionAndTextMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.warn(exception, "Oops!");

                verifyLogEntry(Level.WARN, exception, "Oops!");
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void warnExceptionAndLazyMessage() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.warn(exception, () -> "Oops!");

                verifyLogEntry(Level.WARN, exception, "Oops!");
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void warnExceptionAndFormattedMessageWithArgument() {
                when(visibility.getWarn()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.warn(exception, "Hello {}!", "Alice");

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
                Logger.warn(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.WARN, exception, "Hello Alice!");
            }

            /**
             * Verifies that an error log entry with a plain text message can be issued.
             */
            @Test
            void errorTextMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.error("Hello World!");

                verifyLogEntry(Level.ERROR, null, "Hello World!");
            }

            /**
             * Verifies that an error log entry with an object can be issued.
             */
            @Test
            void errorMessageObject() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.error(42);

                verifyLogEntry(Level.ERROR, null, "42");
            }

            /**
             * Verifies that an error log entry with a lazy text message can be issued.
             */
            @Test
            void errorLazyMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.error(() -> "Hello World!");

                verifyLogEntry(Level.ERROR, null, "Hello World!");
            }

            /**
             * Verifies that an error log entry with a message with placeholders can be issued.
             */
            @Test
            void errorFormattedMessageWithArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.error("Hello {}!", "Alice");

                verifyLogEntry(Level.ERROR, null, "Hello Alice!");
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments can be issued.
             */
            @Test
            void errorFormattedMessageWithLazyArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Logger.error("Hello {}!", () -> "Alice");

                verifyLogEntry(Level.ERROR, null, "Hello Alice!");
            }

            /**
             * Verifies that an error log entry with an exception can be issued.
             */
            @Test
            void errorException() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.error(exception);

                verifyLogEntry(Level.ERROR, exception, null);
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message can be issued.
             */
            @Test
            void errorExceptionAndTextMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.error(exception, "Oops!");

                verifyLogEntry(Level.ERROR, exception, "Oops!");
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message can be issued.
             */
            @Test
            void errorExceptionAndLazyMessage() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.error(exception, () -> "Oops!");

                verifyLogEntry(Level.ERROR, exception, "Oops!");
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders can be issued.
             */
            @Test
            void errorExceptionAndFormattedMessageWithArgument() {
                when(visibility.getError()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

                Exception exception = new Exception();
                Logger.error(exception, "Hello {}!", "Alice");

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
                Logger.error(exception, "Hello {}!", () -> "Alice");

                verifyLogEntry(Level.ERROR, exception, "Hello Alice!");
            }

            /**
             * Verifies framework mock invocation with expected log entry values.
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
                    assertThat(entry.getClassName()).isEqualTo(Enabled.class.getName());
                    assertThat(entry.getMethodName()).isNull();
                    assertThat(entry.getFileName()).isNull();
                    assertThat(entry.getLineNumber()).isEqualTo(-1);
                    assertThat(entry.getTag()).isNull();
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
                Logger.trace("Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an object is discarded if the trace severity level is disabled.
             */
            @Test
            void traceMessageObject() {
                Logger.trace(42);
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a lazy text message is discarded if the trace severity level is
             * disabled.
             */
            @Test
            void traceLazyMessage() {
                Logger.trace(() -> "Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message with placeholders is discarded if the trace severity level
             * is disabled.
             */
            @Test
            void traceFormattedMessageWithArgument() {
                Logger.trace("Hello {}!", "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message with placeholders and lazy arguments is discarded if the
             * trace severity level is disabled.
             */
            @Test
            void traceFormattedMessageWithLazyArgument() {
                Logger.trace("Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception is discarded if the trace severity level is disabled.
             */
            @Test
            void traceException() {
                Logger.trace(new Exception());
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception and a plain text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            void traceExceptionAndTextMessage() {
                Logger.trace(new Exception(), "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception and a lazy text message is discarded if the trace
             * severity level is disabled.
             */
            @Test
            void traceExceptionAndLazyMessage() {
                Logger.trace(new Exception(), () -> "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders is discarded if the
             * trace severity level is disabled.
             */
            @Test
            void traceExceptionAndFormattedMessageWithArgument() {
                Logger.trace(new Exception(), "Hello Alice!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the trace severity level is disabled.
             */
            @Test
            void traceExceptionAndFormattedMessageWithLazyArgument() {
                Logger.trace(new Exception(), "Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a plain text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            void debugTextMessage() {
                Logger.debug("Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an object is discarded if the debug severity level is disabled.
             */
            @Test
            void debugMessageObject() {
                Logger.debug(42);
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a lazy text message is discarded if the debug severity level is
             * disabled.
             */
            @Test
            void debugLazyMessage() {
                Logger.debug(() -> "Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message with placeholders is discarded if the debug severity level
             * is disabled.
             */
            @Test
            void debugFormattedMessageWithArgument() {
                Logger.debug("Hello {}!", "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message with placeholders and lazy arguments is discarded if the
             * debug severity level is disabled.
             */
            @Test
            void debugFormattedMessageWithLazyArgument() {
                Logger.debug("Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception is discarded if the debug severity level is disabled.
             */
            @Test
            void debugException() {
                Logger.debug(new Exception());
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception and a plain text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            void debugExceptionAndTextMessage() {
                Logger.debug(new Exception(), "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception and a lazy text message is discarded if the debug
             * severity level is disabled.
             */
            @Test
            void debugExceptionAndLazyMessage() {
                Logger.debug(new Exception(), () -> "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders is discarded if the
             * debug severity level is disabled.
             */
            @Test
            void debugExceptionAndFormattedMessageWithArgument() {
                Logger.debug(new Exception(), "Hello Alice!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the debug severity level is disabled.
             */
            @Test
            void debugExceptionAndFormattedMessageWithLazyArgument() {
                Logger.debug(new Exception(), "Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a plain text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            void infoTextMessage() {
                Logger.info("Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an object is discarded if the info severity level is disabled.
             */
            @Test
            void infoMessageObject() {
                Logger.info(42);
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a lazy text message is discarded if the info severity level is
             * disabled.
             */
            @Test
            void infoLazyMessage() {
                Logger.info(() -> "Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message with placeholders is discarded if the info severity level
             * is disabled.
             */
            @Test
            void infoFormattedMessageWithArgument() {
                Logger.info("Hello {}!", "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message with placeholders and lazy arguments is discarded if the
             * info severity level is disabled.
             */
            @Test
            void infoFormattedMessageWithLazyArgument() {
                Logger.info("Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception is discarded if the info severity level is disabled.
             */
            @Test
            void infoException() {
                Logger.info(new Exception());
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception and a plain text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            void infoExceptionAndTextMessage() {
                Logger.info(new Exception(), "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception and a lazy text message is discarded if the info
             * severity level is disabled.
             */
            @Test
            void infoExceptionAndLazyMessage() {
                Logger.info(new Exception(), () -> "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders is discarded if the
             * info severity level is disabled.
             */
            @Test
            void infoExceptionAndFormattedMessageWithArgument() {
                Logger.info(new Exception(), "Hello Alice!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the info severity level is disabled.
             */
            @Test
            void infoExceptionAndFormattedMessageWithLazyArgument() {
                Logger.info(new Exception(), "Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a plain text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            void warnTextMessage() {
                Logger.warn("Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an object is discarded if the warn severity level is disabled.
             */
            @Test
            void warnMessageObject() {
                Logger.warn(42);
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a lazy text message is discarded if the warn severity level is
             * disabled.
             */
            @Test
            void warnLazyMessage() {
                Logger.warn(() -> "Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message with placeholders is discarded if the warn severity
             * level is disabled.
             */
            @Test
            void warnFormattedMessageWithArgument() {
                Logger.warn("Hello {}!", "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message with placeholders and lazy arguments is discarded if the
             * warn severity level is disabled.
             */
            @Test
            void warnFormattedMessageWithLazyArgument() {
                Logger.warn("Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception is discarded if the warn severity level is disabled.
             */
            @Test
            void warnException() {
                Logger.warn(new Exception());
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception and a plain text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            void warnExceptionAndTextMessage() {
                Logger.warn(new Exception(), "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception and a lazy text message is discarded if the warn
             * severity level is disabled.
             */
            @Test
            void warnExceptionAndLazyMessage() {
                Logger.warn(new Exception(), () -> "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders is discarded if the
             * warn severity level is disabled.
             */
            @Test
            void warnExceptionAndFormattedMessageWithArgument() {
                Logger.warn(new Exception(), "Hello Alice!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the warn severity level is disabled.
             */
            @Test
            void warnExceptionAndFormattedMessageWithLazyArgument() {
                Logger.warn(new Exception(), "Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a plain text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            void errorTextMessage() {
                Logger.error("Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an object is discarded if the error severity level is disabled.
             */
            @Test
            void errorMessageObject() {
                Logger.error(42);
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a lazy text message is discarded if the error severity level is
             * disabled.
             */
            @Test
            void errorLazyMessage() {
                Logger.error(() -> "Hello World!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message with placeholders is discarded if the error severity
             * level is disabled.
             */
            @Test
            void errorFormattedMessageWithArgument() {
                Logger.error("Hello {}!", "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message with placeholders and lazy arguments is discarded if the
             * error severity level is disabled.
             */
            @Test
            void errorFormattedMessageWithLazyArgument() {
                Logger.error("Hello {}!", () -> "Alice");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception is discarded if the error severity level is disabled.
             */
            @Test
            void errorException() {
                Logger.error(new Exception());
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception and a plain text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            void errorExceptionAndTextMessage() {
                Logger.error(new Exception(), "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception and a lazy text message is discarded if the error
             * severity level is disabled.
             */
            @Test
            void errorExceptionAndLazyMessage() {
                Logger.error(new Exception(), () -> "Oops!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders is discarded if the
             * error severity level is disabled.
             */
            @Test
            void errorExceptionAndFormattedMessageWithArgument() {
                Logger.error(new Exception(), "Hello Alice!");
                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with an exception and a message with placeholders and lazy arguments is
             * discarded if the error severity level is disabled.
             */
            @Test
            void errorExceptionAndFormattedMessageWithLazyArgument() {
                Logger.error(new Exception(), "Hello {}!", () -> "Alice");
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
