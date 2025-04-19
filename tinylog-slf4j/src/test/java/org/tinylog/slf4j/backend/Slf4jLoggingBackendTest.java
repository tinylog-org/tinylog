package org.tinylog.slf4j.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import org.slf4j.LoggerFactory;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.SimpleMessageFormatter;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class Slf4jLoggingBackendTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    @Inject
    private Log log;

    /**
     * Resets the severity levels of all SLF4J loggers.
     */
    @AfterEach
    void reset() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : loggerContext.getLoggerList()) {
            if (Logger.ROOT_LOGGER_NAME.equals(logger.getName())) {
                logger.setLevel(ch.qos.logback.classic.Level.TRACE);
            } else {
                logger.setLevel(null);
            }
        }
    }

    /**
     * Verifies that the provided context storage is an instance of {@link Slf4jContextStorage}.
     */
    @Test
    void contextStorage() {
        ContextStorage storage = new Slf4jLoggingBackend(context).getContextStorage();
        assertThat(storage).isInstanceOf(Slf4jContextStorage.class);
    }

    /**
     * Verifies that {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} is set for all classes and severity levels.
     *
     * @param className The class name to test
     */
    @ParameterizedTest
    @ValueSource(strings = { Logger.ROOT_LOGGER_NAME, "example.Foo" })
    void visibilityByClass(String className) {
        LevelVisibility visibility = new Slf4jLoggingBackend(context).getLevelVisibilityByClass(className);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
    }

    /**
     * Verifies that {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} is set for all tags and severity levels.
     *
     * @param tag The category tag to test
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "foo")
    void visibilityByTag(String tag) {
        LevelVisibility visibility = new Slf4jLoggingBackend(context).getLevelVisibilityByTag(tag);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
    }


    /**
     * Verifies that the logging backend can be closed without throwing any exception.
     */
    @Test
    void closable() {
        new Slf4jLoggingBackend(context).close();
    }

    /**
     * Tests for {@link Slf4jLoggingBackend#isEnabled(Object, String, Level)}.
     */
    @Nested
    class EnabledCheck {

        /**
         * Verifies that a stack trace element can be used as location information to check the enabled status of an
         * enabled SLF4J logger.
         *
         * @param tinylogLevel The severity level to check
         * @param slf4jLevel The severity level of the SLF4J logger
         * @param tag The category tag to check
         */
        @ParameterizedTest
        @CsvSource({
            "TRACE, TRACE,",
            "TRACE, TRACE, foo",
            "DEBUG, DEBUG,",
            "DEBUG, DEBUG, foo",
            "INFO , INFO ,",
            "INFO , INFO , foo",
            "WARN , WARN ,",
            "WARN , WARN , foo",
            "ERROR, ERROR,",
            "ERROR, ERROR, foo"
        })
        void enabledForStackTraceElement(Level tinylogLevel, org.slf4j.event.Level slf4jLevel, String tag) {
            StackTraceElement element = new StackTraceElement("example.Foo", "bar", "Foo.java", -1);

            Logger logger = (Logger) LoggerFactory.getLogger("example.Foo");
            logger.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(slf4jLevel));

            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled(element, tag, tinylogLevel)).isTrue();
        }

        /**
         * Verifies that a stack trace element can be used as location information to check the enabled status of a
         * disabled SLF4J logger.
         *
         * @param tinylogLevel The severity level to check
         * @param slf4jLevel The severity level of the SLF4J logger
         * @param tag The category tag to check
         */
        @ParameterizedTest
        @CsvSource({
            "TRACE, DEBUG,",
            "TRACE, DEBUG, foo",
            "DEBUG, INFO ,",
            "DEBUG, INFO , foo",
            "INFO , WARN ,",
            "INFO , WARN , foo",
            "WARN , ERROR,",
            "WARN , ERROR, foo"
        })
        void disabledForStackTraceElement(Level tinylogLevel, org.slf4j.event.Level slf4jLevel, String tag) {
            StackTraceElement element = new StackTraceElement("example.Foo", "bar", "Foo.java", -1);

            Logger logger = (Logger) LoggerFactory.getLogger("example.Foo");
            logger.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(slf4jLevel));

            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled(element, tag, tinylogLevel)).isFalse();
        }

        /**
         * Verifies that a class object can be used as location information to check the enabled status of an enabled
         * SLF4J logger.
         *
         * @param tinylogLevel The severity level to check
         * @param slf4jLevel The severity level of the SLF4J logger
         * @param tag The category tag to check
         */
        @ParameterizedTest
        @CsvSource({
            "TRACE, TRACE,",
            "TRACE, TRACE, foo",
            "DEBUG, DEBUG,",
            "DEBUG, DEBUG, foo",
            "INFO , INFO ,",
            "INFO , INFO , foo",
            "WARN , WARN ,",
            "WARN , WARN , foo",
            "ERROR, ERROR,",
            "ERROR, ERROR, foo"
        })
        void enabledForClassObject(Level tinylogLevel, org.slf4j.event.Level slf4jLevel, String tag) {
            Logger logger = (Logger) LoggerFactory.getLogger(EnabledCheck.class);
            logger.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(slf4jLevel));

            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled(EnabledCheck.class, tag, tinylogLevel)).isTrue();
        }

        /**
         * Verifies that a class object can be used as location information to check the enabled status of a disabled
         * SLF4J logger.
         *
         * @param tinylogLevel The severity level to check
         * @param slf4jLevel The severity level of the SLF4J logger
         * @param tag The category tag to check
         */
        @ParameterizedTest
        @CsvSource({
            "TRACE, DEBUG,",
            "TRACE, DEBUG, foo",
            "DEBUG, INFO ,",
            "DEBUG, INFO , foo",
            "INFO , WARN ,",
            "INFO , WARN , foo",
            "WARN , ERROR,",
            "WARN , ERROR, foo"
        })
        void disabledForClassObject(Level tinylogLevel, org.slf4j.event.Level slf4jLevel, String tag) {
            Logger logger = (Logger) LoggerFactory.getLogger(EnabledCheck.class);
            logger.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(slf4jLevel));

            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled(EnabledCheck.class, tag, tinylogLevel)).isFalse();
        }

        /**
         * Verifies that a class name can be used as location information to check the enabled status of an enabled
         * SLF4J logger.
         *
         * @param tinylogLevel The severity level to check
         * @param slf4jLevel The severity level of the SLF4J logger
         * @param tag The category tag to check
         */
        @ParameterizedTest
        @CsvSource({
            "TRACE, TRACE,",
            "TRACE, TRACE, foo",
            "DEBUG, DEBUG,",
            "DEBUG, DEBUG, foo",
            "INFO , INFO ,",
            "INFO , INFO , foo",
            "WARN , WARN ,",
            "WARN , WARN , foo",
            "ERROR, ERROR,",
            "ERROR, ERROR, foo"
        })
        void enabledForClassName(Level tinylogLevel, org.slf4j.event.Level slf4jLevel, String tag) {
            Logger logger = (Logger) LoggerFactory.getLogger("example.Foo");
            logger.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(slf4jLevel));

            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled("example.Foo", tag, tinylogLevel)).isTrue();
        }

        /**
         * Verifies that a class name can be used as location information to check the enabled status of a disabled
         * SLF4J logger.
         *
         * @param tinylogLevel The severity level to check
         * @param slf4jLevel The severity level of the SLF4J logger
         * @param tag The category tag to check
         */
        @ParameterizedTest
        @CsvSource({
            "TRACE, DEBUG,",
            "TRACE, DEBUG, foo",
            "DEBUG, INFO ,",
            "DEBUG, INFO , foo",
            "INFO , WARN ,",
            "INFO , WARN , foo",
            "WARN , ERROR,",
            "WARN , ERROR, foo"
        })
        void disabledForClassName(Level tinylogLevel, org.slf4j.event.Level slf4jLevel, String tag) {
            Logger logger = (Logger) LoggerFactory.getLogger("example.Foo");
            logger.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(slf4jLevel));

            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled("example.Foo", tag, tinylogLevel)).isFalse();
        }

        /**
         * Verifies that {@code null} can be used as location information to check the enabled status of the enabled
         * SLF4J root logger.
         *
         * @param tinylogLevel The severity level to check
         * @param slf4jLevel The severity level of the SLF4J logger
         * @param tag The category tag to check
         */
        @ParameterizedTest
        @CsvSource({
            "TRACE, TRACE,",
            "TRACE, TRACE, foo",
            "DEBUG, DEBUG,",
            "DEBUG, DEBUG, foo",
            "INFO , INFO ,",
            "INFO , INFO , foo",
            "WARN , WARN ,",
            "WARN , WARN , foo",
            "ERROR, ERROR,",
            "ERROR, ERROR, foo"
        })
        void enabledForNullLocation(Level tinylogLevel, org.slf4j.event.Level slf4jLevel, String tag) {
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            logger.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(slf4jLevel));

            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled(null, tag, tinylogLevel)).isTrue();

            assertThat(log.consume()).singleElement().satisfies(element -> {
                assertThat(element.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(element.getFormattedMessage(configuration))
                    .isEqualTo("Illegal location information \"null\"");
            });
        }

        /**
         * Verifies that {@code null} can be used as location information to check the enabled status of the disabled
         * SLF4J root logger.
         *
         * @param tinylogLevel The severity level to check
         * @param slf4jLevel The severity level of the SLF4J logger
         * @param tag The category tag to check
         */
        @ParameterizedTest
        @CsvSource({
            "TRACE, DEBUG,",
            "TRACE, DEBUG, foo",
            "DEBUG, INFO ,",
            "DEBUG, INFO , foo",
            "INFO , WARN ,",
            "INFO , WARN , foo",
            "WARN , ERROR,",
            "WARN , ERROR, foo"
        })
        void disabledForNullLocation(Level tinylogLevel, org.slf4j.event.Level slf4jLevel, String tag) {
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            logger.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(slf4jLevel));

            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled(null, tag, tinylogLevel)).isFalse();

            assertThat(log.consume()).singleElement().satisfies(element -> {
                assertThat(element.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(element.getFormattedMessage(configuration))
                    .isEqualTo("Illegal location information \"null\"");
            });
        }

        /**
         * Verifies that an illegal severity level will be reported.
         */
        @Test
        void reportIllegalSeverityLevel() {
            Slf4jLoggingBackend backend = new Slf4jLoggingBackend(context);
            assertThat(backend.isEnabled("example.Foo", null, Level.OFF)).isFalse();

            assertThat(log.consume()).singleElement().satisfies(element -> {
                assertThat(element.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(element.getFormattedMessage(configuration))
                    .isEqualTo("Illegal severity level \"OFF\"");
            });
        }

    }

    /**
     * Tests for {@link Slf4jLoggingBackend#output(LogEntry, boolean)}.
     */
    @Nested
    class LogEntryOutput {

        /**
         * Verifies that trace log entries with a plain text message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputTraceMessage(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.TRACE,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("TRACE - Hello World!");
        }

        /**
         * Verifies that debug log entries with a plain text message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputDebugMessage(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.DEBUG,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("DEBUG - Hello World!");
        }

        /**
         * Verifies that info log entries with a plain text message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputInfoMessage(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.INFO,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("INFO - Hello World!");
        }

        /**
         * Verifies that warning log entries with a plain text message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputWarnMessage(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.WARN,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("WARN - Hello World!");
        }

        /**
         * Verifies that error log entries with a plain text message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputErrorMessage(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.ERROR,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("ERROR - Hello World!");
        }

        /**
         * Verifies that info log entries with a plain text message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputMarker(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                "bar",
                Level.INFO,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("INFO - bar - Hello World!");
        }

        /**
         * Verifies that log entries with a formatted text message with placeholders are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputFormattedTextMessage(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.INFO,
                null,
                new SimpleMessageFormatter(),
                "Hello {}!",
                new Object[] {"Alice"}
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("INFO - Hello Alice!");
        }

        /**
         * Verifies that log entries with an exception but without any message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputExceptionOnly(StdOut out) {
            Exception exception = new Exception("Something went wrong");
            exception.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
                new StackTraceElement("example.OtherClass", "bar", "OtherClass.java", 42),
            });

            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.ERROR,
                exception,
                null,
                null,
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);

            assertThat(out.capturedLines()).containsExactly(
                "ERROR - Something went wrong",
                "java.lang.Exception: Something went wrong",
                "\tat example.MyClass.foo(MyClass.java:42)",
                "\tat example.OtherClass.bar(OtherClass.java:42)"
            );
        }

        /**
         * Verifies that log entries with an exception and a custom message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputExceptionWithCustomMessage(StdOut out) {
            Exception exception = new Exception("Something went wrong");
            exception.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
            });

            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.ERROR,
                exception,
                null,
                "Oops!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);

            assertThat(out.capturedLines()).containsExactly(
                "ERROR - Oops!",
                "java.lang.Exception: Something went wrong",
                "\tat example.MyClass.foo(MyClass.java:42)"
            );
        }

        /**
         * Verifies that log entries without any location information can be output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void outputWithoutLocationInformation(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                null,
                null,
                Level.INFO,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("INFO - Hello World!");
        }

        /**
         * Verifies that a log entry won't be output if its severity level is not enabled.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void discardNonSevereSeverityLevel(StdOut out) {
            Logger logger = (Logger) LoggerFactory.getLogger("example.Foo");
            logger.setLevel(ch.qos.logback.classic.Level.WARN);

            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.INFO,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).isEmpty();
        }

        /**
         * Verifies that a log entry won't be output if its severity level is unsupported.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void discardIllegalSeverityLevel(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                "example.Foo",
                null,
                Level.OFF,
                null,
                null,
                "Hello World!",
                null
            );

            new Slf4jLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).isEmpty();

            assertThat(log.consume()).singleElement().satisfies(element -> {
                assertThat(element.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(element.getFormattedMessage(configuration))
                    .isEqualTo("Illegal severity level \"OFF\"");
            });
        }

    }

}
