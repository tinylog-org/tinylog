package org.tinylog.slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.LocationAwareLogger;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;
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

class TinylogLoggerWithMarkersTest {

    private static final Marker marker = new BasicMarkerFactory().getDetachedMarker("foo");

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
     * Tests for severity levels.
     */
    @Nested
    class Levels {

        /**
         * Verifies the results of the {@link TinylogLogger#isTraceEnabled()} method.
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
            when(framework.getLevelVisibilityByClass(Levels.class.getName())).thenReturn(
                new LevelVisibility(
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED
                )
            );

            when(framework.isEnabled(notNull(), eq("foo"), eq(Level.TRACE))).thenReturn(enabled);

            TinylogLogger logger = new TinylogLogger(Levels.class.getName(), framework);
            assertThat(logger.isTraceEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link TinylogLogger#isDebugEnabled()} method.
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
            when(framework.getLevelVisibilityByClass(Levels.class.getName())).thenReturn(
                new LevelVisibility(
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED
                )
            );

            when(framework.isEnabled(notNull(), eq("foo"), eq(Level.DEBUG))).thenReturn(enabled);

            TinylogLogger logger = new TinylogLogger(Levels.class.getName(), framework);
            assertThat(logger.isDebugEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link TinylogLogger#isInfoEnabled()} method.
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
            when(framework.getLevelVisibilityByClass(Levels.class.getName())).thenReturn(
                new LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED
                )
            );

            when(framework.isEnabled(notNull(), eq("foo"), eq(Level.INFO))).thenReturn(enabled);

            TinylogLogger logger = new TinylogLogger(Levels.class.getName(), framework);
            assertThat(logger.isInfoEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link TinylogLogger#isWarnEnabled()} method.
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
            when(framework.getLevelVisibilityByClass(Levels.class.getName())).thenReturn(
                new LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails,
                    OutputDetails.DISABLED
                )
            );

            when(framework.isEnabled(notNull(), eq("foo"), eq(Level.WARN))).thenReturn(enabled);

            TinylogLogger logger = new TinylogLogger(Levels.class.getName(), framework);
            assertThat(logger.isWarnEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

        /**
         * Verifies the results of the {@link TinylogLogger#isErrorEnabled()} method.
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
            when(framework.getLevelVisibilityByClass(Levels.class.getName())).thenReturn(
                new LevelVisibility(
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    OutputDetails.DISABLED,
                    outputDetails
                )
            );

            when(framework.isEnabled(notNull(), eq("foo"), eq(Level.ERROR))).thenReturn(enabled);

            TinylogLogger logger = new TinylogLogger(Levels.class.getName(), framework);
            assertThat(logger.isErrorEnabled(marker)).isEqualTo(outputDetails != OutputDetails.DISABLED && enabled);
        }

    }

    /**
     * Tests for issuing log entries.
     */
    @Nested
    class LogEntries {

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
                assertThat(entry.getClassName()).isEqualTo(LogEntries.class.getName());
                assertThat(entry.getMethodName()).isNull();
                assertThat(entry.getFileName()).isNull();
                assertThat(entry.getLineNumber()).isEqualTo(-1);
                assertThat(entry.getTag()).isEqualTo("foo");
                assertThat(entry.getSeverityLevel()).isEqualTo(level);
                assertThat(entry.getThrowable()).isSameAs(exception);
                assertThat(entry.getFormattedMessage(mock(Configuration.class))).isEqualTo(message);
            });
        }

        /**
         * Verifies that no log entry has been submitted.
         */
        private void verifyNoLogEntry() {
            verify(framework, never()).submit(any());
        }

        /**
         * Tests for issuing trace log entries if {@link Level#TRACE} is enabled.
         */
        @Nested
        class TraceEnabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED
                    )
                );
            }

            /**
             * Verifies that a trace log entry with a plain text message can be issued.
             */
            @Test
            void traceTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Hello World!");

                verifyLogEntry(Level.TRACE, null, "Hello World!");
            }

            /**
             * Verifies that a trace log entry with a plain text message and an exception can be issued.
             */
            @Test
            void traceTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Oops!", exception);

                verifyLogEntry(Level.TRACE, exception, "Oops!");
            }

            /**
             * Verifies that a trace log entry with a message and a single placeholder can be issued.
             */
            @Test
            void traceFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Hello {}!", "Alice");

                verifyLogEntry(Level.TRACE, null, "Hello Alice!");
            }

            /**
             * Verifies that a trace log entry with a message and two arguments can be issued.
             */
            @Test
            void traceFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyLogEntry(Level.TRACE, null, "Hello Alice and Bob!");
            }

            /**
             * Verifies that a trace log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void traceFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Oops {}!", "Alice", exception);

                verifyLogEntry(Level.TRACE, exception, "Oops Alice!");
            }

            /**
             * Verifies that a trace log entry with a message and three arguments can be issued.
             */
            @Test
            void traceFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyLogEntry(Level.TRACE, null, "Hello Alice, Bob, and Charlie!");
            }

            /**
             * Verifies that a trace log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void traceFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyLogEntry(Level.TRACE, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full trace log entry can be issued via the generic log method.
             */
            @Test
            void logGenericTraceEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.TRACE_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyLogEntry(Level.TRACE, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full trace log entry can be issued via the event log method.
             */
            @Test
            void logEventTraceEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.TRACE)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyLogEntry(Level.TRACE, exception, "Oops Alice and Bob!");
            }

        }

        /**
         * Tests for issuing trace log entries if {@link Level#TRACE} is disabled.
         */
        @Nested
        class TraceDisabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.DISABLED,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME
                    )
                );
            }

            /**
             * Verifies that a trace log entry with a plain text message can be issued.
             */
            @Test
            void traceTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a plain text message and an exception can be issued.
             */
            @Test
            void traceTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Oops!", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message and a single placeholder can be issued.
             */
            @Test
            void traceFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message and two arguments can be issued.
             */
            @Test
            void traceFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void traceFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Oops {}!", "Alice", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message and three arguments can be issued.
             */
            @Test
            void traceFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a trace log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void traceFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.trace(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full trace log entry can be issued via the generic log method.
             */
            @Test
            void logGenericTraceEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.TRACE_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full trace log entry can be issued via the event log method.
             */
            @Test
            void logEventTraceEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.TRACE)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyNoLogEntry();
            }

        }

        /**
         * Tests for issuing debug log entries if {@link Level#DEBUG} is enabled.
         */
        @Nested
        class DebugEnabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.DISABLED,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED
                    )
                );
            }

            /**
             * Verifies that a debug log entry with a plain text message can be issued.
             */
            @Test
            void debugTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Hello World!");

                verifyLogEntry(Level.DEBUG, null, "Hello World!");
            }

            /**
             * Verifies that a debug log entry with a plain text message and an exception can be issued.
             */
            @Test
            void debugTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Oops!", exception);

                verifyLogEntry(Level.DEBUG, exception, "Oops!");
            }

            /**
             * Verifies that a debug log entry with a message and a single placeholder can be issued.
             */
            @Test
            void debugFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Hello {}!", "Alice");

                verifyLogEntry(Level.DEBUG, null, "Hello Alice!");
            }

            /**
             * Verifies that a debug log entry with a message and two arguments can be issued.
             */
            @Test
            void debugFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyLogEntry(Level.DEBUG, null, "Hello Alice and Bob!");
            }

            /**
             * Verifies that a debug log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void debugFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Oops {}!", "Alice", exception);

                verifyLogEntry(Level.DEBUG, exception, "Oops Alice!");
            }

            /**
             * Verifies that a debug log entry with a message and three arguments can be issued.
             */
            @Test
            void debugFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyLogEntry(Level.DEBUG, null, "Hello Alice, Bob, and Charlie!");
            }

            /**
             * Verifies that a debug log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void debugFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyLogEntry(Level.DEBUG, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full debug log entry can be issued via the generic log method.
             */
            @Test
            void logGenericDebugEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.DEBUG_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyLogEntry(Level.DEBUG, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full debug log entry can be issued via the event log method.
             */
            @Test
            void logEventDebugEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.DEBUG)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyLogEntry(Level.DEBUG, exception, "Oops Alice and Bob!");
            }

        }

        /**
         * Tests for issuing debug log entries if {@link Level#DEBUG} is disabled.
         */
        @Nested
        class DebugDisabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.DISABLED,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME
                    )
                );
            }

            /**
             * Verifies that a debug log entry with a plain text message can be issued.
             */
            @Test
            void debugTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a plain text message and an exception can be issued.
             */
            @Test
            void debugTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Oops!", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message and a single placeholder can be issued.
             */
            @Test
            void debugFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message and two arguments can be issued.
             */
            @Test
            void debugFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void debugFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Oops {}!", "Alice", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message and three arguments can be issued.
             */
            @Test
            void debugFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a debug log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void debugFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.debug(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full debug log entry can be issued via the generic log method.
             */
            @Test
            void logGenericDebugEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.DEBUG_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full debug log entry can be issued via the event log method.
             */
            @Test
            void logEventDebugEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.DEBUG)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyNoLogEntry();
            }

        }

        /**
         * Tests for issuing info log entries if {@link Level#INFO} is enabled.
         */
        @Nested
        class InfoEnabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED
                    )
                );
            }

            /**
             * Verifies that an info log entry with a plain text message can be issued.
             */
            @Test
            void infoTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Hello World!");

                verifyLogEntry(Level.INFO, null, "Hello World!");
            }

            /**
             * Verifies that an info log entry with a plain text message and an exception can be issued.
             */
            @Test
            void infoTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Oops!", exception);

                verifyLogEntry(Level.INFO, exception, "Oops!");
            }

            /**
             * Verifies that an info log entry with a message and a single placeholder can be issued.
             */
            @Test
            void infoFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Hello {}!", "Alice");

                verifyLogEntry(Level.INFO, null, "Hello Alice!");
            }

            /**
             * Verifies that an info log entry with a message and two arguments can be issued.
             */
            @Test
            void infoFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyLogEntry(Level.INFO, null, "Hello Alice and Bob!");
            }

            /**
             * Verifies that an info log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void infoFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Oops {}!", "Alice", exception);

                verifyLogEntry(Level.INFO, exception, "Oops Alice!");
            }

            /**
             * Verifies that an info log entry with a message and three arguments can be issued.
             */
            @Test
            void infoFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyLogEntry(Level.INFO, null, "Hello Alice, Bob, and Charlie!");
            }

            /**
             * Verifies that an info log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void infoFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyLogEntry(Level.INFO, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full info log entry can be issued via the generic log method.
             */
            @Test
            void logGenericInfoEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.INFO_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyLogEntry(Level.INFO, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full info log entry can be issued via the event log method.
             */
            @Test
            void logEventInfoEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.INFO)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyLogEntry(Level.INFO, exception, "Oops Alice and Bob!");
            }

        }

        /**
         * Tests for issuing info log entries if {@link Level#INFO} is disabled.
         */
        @Nested
        class InfoDisabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.DISABLED,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME
                    )
                );
            }

            /**
             * Verifies that an info log entry with a plain text message can be issued.
             */
            @Test
            void infoTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a plain text message and an exception can be issued.
             */
            @Test
            void infoTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Oops!", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message and a single placeholder can be issued.
             */
            @Test
            void infoFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message and two arguments can be issued.
             */
            @Test
            void infoFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void infoFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Oops {}!", "Alice", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message and three arguments can be issued.
             */
            @Test
            void infoFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an info log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void infoFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.info(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full info log entry can be issued via the generic log method.
             */
            @Test
            void logGenericInfoEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.INFO_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full info log entry can be issued via the event log method.
             */
            @Test
            void logEventInfoEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.INFO)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyNoLogEntry();
            }

        }

        /**
         * Tests for issuing warning log entries if {@link Level#WARN} is enabled.
         */
        @Nested
        class WarnEnabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.DISABLED
                    )
                );
            }

            /**
             * Verifies that a warning log entry with a plain text message can be issued.
             */
            @Test
            void warnTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Hello World!");

                verifyLogEntry(Level.WARN, null, "Hello World!");
            }

            /**
             * Verifies that a warning log entry with a plain text message and an exception can be issued.
             */
            @Test
            void warnTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Oops!", exception);

                verifyLogEntry(Level.WARN, exception, "Oops!");
            }

            /**
             * Verifies that a warning log entry with a message and a single placeholder can be issued.
             */
            @Test
            void warnFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Hello {}!", "Alice");

                verifyLogEntry(Level.WARN, null, "Hello Alice!");
            }

            /**
             * Verifies that a warning log entry with a message and two arguments can be issued.
             */
            @Test
            void warnFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyLogEntry(Level.WARN, null, "Hello Alice and Bob!");
            }

            /**
             * Verifies that a warning log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void warnFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Oops {}!", "Alice", exception);

                verifyLogEntry(Level.WARN, exception, "Oops Alice!");
            }

            /**
             * Verifies that a warning log entry with a message and three arguments can be issued.
             */
            @Test
            void warnFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyLogEntry(Level.WARN, null, "Hello Alice, Bob, and Charlie!");
            }

            /**
             * Verifies that a warning log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void warnFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyLogEntry(Level.WARN, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full warning log entry can be issued via the generic log method.
             */
            @Test
            void logGenericWarnEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.WARN_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyLogEntry(Level.WARN, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full warning log entry can be issued via the event log method.
             */
            @Test
            void logEventWarnEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.WARN)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyLogEntry(Level.WARN, exception, "Oops Alice and Bob!");
            }

        }

        /**
         * Tests for issuing warning log entries if {@link Level#WARN} is disabled.
         */
        @Nested
        class WarnDisabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.DISABLED,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME
                    )
                );
            }

            /**
             * Verifies that a warning log entry with a plain text message can be issued.
             */
            @Test
            void warnTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a plain text message and an exception can be issued.
             */
            @Test
            void warnTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Oops!", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message and a single placeholder can be issued.
             */
            @Test
            void warnFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message and two arguments can be issued.
             */
            @Test
            void warnFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void warnFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Oops {}!", "Alice", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message and three arguments can be issued.
             */
            @Test
            void warnFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyNoLogEntry();
            }

            /**
             * Verifies that a warning log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void warnFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.warn(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full warning log entry can be issued via the generic log method.
             */
            @Test
            void logGenericWarnEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.WARN_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full warning log entry can be issued via the event log method.
             */
            @Test
            void logEventWarnEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.WARN)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyNoLogEntry();
            }

        }

        /**
         * Tests for issuing error log entries if {@link Level#ERROR} is enabled.
         */
        @Nested
        class ErrorEnabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.DISABLED,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME
                    )
                );
            }

            /**
             * Verifies that an error log entry with a plain text message can be issued.
             */
            @Test
            void errorTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Hello World!");

                verifyLogEntry(Level.ERROR, null, "Hello World!");
            }

            /**
             * Verifies that an error log entry with a plain text message and an exception can be issued.
             */
            @Test
            void errorTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Oops!", exception);

                verifyLogEntry(Level.ERROR, exception, "Oops!");
            }

            /**
             * Verifies that an error log entry with a message and a single placeholder can be issued.
             */
            @Test
            void errorFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Hello {}!", "Alice");

                verifyLogEntry(Level.ERROR, null, "Hello Alice!");
            }

            /**
             * Verifies that an error log entry with a message and two arguments can be issued.
             */
            @Test
            void errorFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyLogEntry(Level.ERROR, null, "Hello Alice and Bob!");
            }

            /**
             * Verifies that an error log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void errorFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Oops {}!", "Alice", exception);

                verifyLogEntry(Level.ERROR, exception, "Oops Alice!");
            }

            /**
             * Verifies that an error log entry with a message and three arguments can be issued.
             */
            @Test
            void errorFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyLogEntry(Level.ERROR, null, "Hello Alice, Bob, and Charlie!");
            }

            /**
             * Verifies that an error log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void errorFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyLogEntry(Level.ERROR, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full error log entry can be issued via the generic log method.
             */
            @Test
            void logGenericErrorEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.ERROR_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyLogEntry(Level.ERROR, exception, "Oops Alice and Bob!");
            }

            /**
             * Verifies that a full error log entry can be issued via the event log method.
             */
            @Test
            void logEventErrorEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.ERROR)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyLogEntry(Level.ERROR, exception, "Oops Alice and Bob!");
            }

        }

        /**
         * Tests for issuing error log entries if {@link Level#ERROR} is disabled.
         */
        @Nested
        class ErrorDisabled {

            /**
             * Initializes severity level visibility.
             */
            @BeforeEach
            void init() {
                when(framework.getLevelVisibilityByClass(LogEntries.class.getName())).thenReturn(
                    new LevelVisibility(
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME,
                        OutputDetails.DISABLED
                    )
                );
            }

            /**
             * Verifies that an error log entry with a plain text message can be issued.
             */
            @Test
            void errorTextMessage() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Hello World!");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a plain text message and an exception can be issued.
             */
            @Test
            void errorTextMessageAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Oops!", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message and a single placeholder can be issued.
             */
            @Test
            void errorFormattedMessageWithSingleArgument() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Hello {}!", "Alice");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message and two arguments can be issued.
             */
            @Test
            void errorFormattedMessageWithTwoArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Hello {} and {}!", "Alice", "Bob");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message, a single placeholder, and an exception can be issued.
             */
            @Test
            void errorFormattedMessageWithArgumentAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Oops {}!", "Alice", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message and three arguments can be issued.
             */
            @Test
            void errorFormattedMessageWithThreeArguments() {
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Hello {}, {}, and {}!", "Alice", "Bob", "Charlie");

                verifyNoLogEntry();
            }

            /**
             * Verifies that an error log entry with a message, two placeholders, and an exception can be issued.
             */
            @Test
            void errorFormattedMessageWithArgumentsAndException() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.error(marker, "Oops {} and {}!", "Alice", "Bob", exception);

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full error log entry can be issued via the generic log method.
             */
            @Test
            void logGenericErrorEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.log(
                    marker,
                    TinylogLogger.class.getName(),
                    LocationAwareLogger.ERROR_INT,
                    "Oops {} and {}!",
                    new Object[] {"Alice", "Bob"},
                    exception
                );

                verifyNoLogEntry();
            }

            /**
             * Verifies that a full error log entry can be issued via the event log method.
             */
            @Test
            void logEventErrorEntry() {
                Exception exception = new Exception();
                TinylogLogger logger = new TinylogLogger(LogEntries.class.getName(), framework);
                logger.makeLoggingEventBuilder(org.slf4j.event.Level.ERROR)
                    .addMarker(marker)
                    .setMessage("Oops {} and {}!")
                    .addArgument("Alice")
                    .addArgument("Bob")
                    .setCause(exception)
                    .log();

                verifyNoLogEntry();
            }

        }

    }

}
