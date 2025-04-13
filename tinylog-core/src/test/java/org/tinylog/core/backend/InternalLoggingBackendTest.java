package org.tinylog.core.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.StdErr;
import org.junitpioneer.jupiter.StdIo;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.SimpleMessageFormatter;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class InternalLoggingBackendTest {

    @Inject
    private Configuration configuration;

    /**
     * Verifies that the provided context storage does not store any context values.
     */
    @Test
    void contextStorage() {
        ContextStorage storage = new InternalLoggingBackend(configuration).getContextStorage();
        storage.put("foo", "42");
        assertThat(storage.getMapping()).isEmpty();
    }

    /**
     * Verifies that only the severity levels WARN and ERROR are enabled (but without requiring any stack trace
     * information) in the precalculated level visibility object for all classes.
     *
     * @param className The fully-qualified class name to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"Foo", "example.Foo", "org.tinylog.core.backend.InternalLoggingBackend"})
    void classesVisibility(String className) {
        LevelVisibility visibility = new InternalLoggingBackend(configuration).getLevelVisibilityByClass(className);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that all severity levels are disabled in the precalculated level visibility object for untagged log
     * entries.
     */
    @Test
    void untaggedVisibility() {
        LevelVisibility visibility = new InternalLoggingBackend(configuration).getLevelVisibilityByTag(null);
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.DISABLED);
    }

    /**
     * Verifies that all severity levels are disabled in the precalculated level visibility object for log entries with
     * another tag than "tinylog".
     */
    @Test
    void unknownTaggedVisibility() {
        LevelVisibility visibility = new InternalLoggingBackend(configuration).getLevelVisibilityByTag("foo");
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.DISABLED);
    }

    /**
     * Verifies that only the severity levels WARN and ERROR are enabled (but without requiring any stack trace
     * information) in the precalculated level visibility object for internal tinylog log entries.
     */
    @Test
    void tinylogTaggedVisibility() {
        LevelVisibility visibility = new InternalLoggingBackend(configuration).getLevelVisibilityByTag("tinylog");
        assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getInfo()).isEqualTo(OutputDetails.DISABLED);
        assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
        assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that logging is disabled for untagged log entries of any severity level.
     *
     * @param level The severity level to test
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "OFF")
    void untaggedLogEntriesDisabled(Level level) {
        InternalLoggingBackend backend = new InternalLoggingBackend(configuration);
        assertThat(backend.isEnabled(null, null, level)).isFalse();
    }

    /**
     * Verifies that logging is disabled for internal tinylog log entries with the severity levels TRACE, DEBUG, and
     * INFO.
     *
     * @param level The severity level to test
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.INCLUDE, names = {"TRACE", "DEBUG", "INFO"})
    void tinylogLogEntriesDisabled(Level level) {
        InternalLoggingBackend backend = new InternalLoggingBackend(configuration);
        assertThat(backend.isEnabled(null, "tinylog", level)).isFalse();
    }

    /**
     * Verifies that logging is enabled for internal tinylog log entries with the severity levels WARN and ERROR.
     *
     * @param level The severity level to test
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.INCLUDE, names = {"WARN", "ERROR"})
    void tinylogLogEntriesEnabled(Level level) {
        InternalLoggingBackend backend = new InternalLoggingBackend(configuration);
        assertThat(backend.isEnabled(null, "tinylog", level)).isTrue();
    }

    /**
     * Verifies that log entries with a plain text message are output correctly.
     *
     * @param level The severity level for the log entry
     * @param err The captured output of the standard error stream
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.INCLUDE, names = {"WARN", "ERROR"})
    @StdIo
    void outputPlainTextMessage(Level level, StdErr err) {
        LogEntry entry = new LogEntry(
            Thread.currentThread(),
            emptyMap(),
            null,
            "tinylog",
            level,
            null,
            null,
            "Hello World!",
            null
        );

        new InternalLoggingBackend(configuration).output(entry, true);
        assertThat(err.capturedLines()).containsExactly("TINYLOG " + level + ": Hello World!");
    }

    /**
     * Verifies that log entries with a formatted text message with placeholders are output correctly.
     *
     * @param level The severity level for the log entry
     * @param err The captured output of the standard error stream
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.INCLUDE, names = {"WARN", "ERROR"})
    @StdIo
    void outputFormattedTextMessage(Level level, StdErr err) {
        LogEntry entry = new LogEntry(
            Thread.currentThread(),
            emptyMap(),
            null,
            "tinylog",
            level,
            null,
            new SimpleMessageFormatter(),
            "Hello {}!",
            new Object[] {"Alice"}
        );

        new InternalLoggingBackend(configuration).output(entry, true);
        assertThat(err.capturedLines()).containsExactly("TINYLOG " + level + ": Hello Alice!");
    }

    /**
     * Verifies that log entries with an exception but without any message are output correctly.
     *
     * @param level The severity level for the log entry
     * @param err The captured output of the standard error stream
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.INCLUDE, names = {"WARN", "ERROR"})
    @StdIo
    void outputExceptionOnly(Level level, StdErr err) {
        Exception exception = new Exception();
        exception.setStackTrace(new StackTraceElement[] {
            new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
            new StackTraceElement("example.OtherClass", "bar", "OtherClass.java", 42),
        });

        LogEntry entry = new LogEntry(
            Thread.currentThread(),
            emptyMap(),
            null,
            "tinylog",
            level,
            exception,
            null,
            null,
            null
        );

        new InternalLoggingBackend(configuration).output(entry, true);

        assertThat(err.capturedLines()).containsExactly(
            "TINYLOG " + level + ": java.lang.Exception",
            "\tat example.MyClass.foo(MyClass.java:42)",
            "\tat example.OtherClass.bar(OtherClass.java:42)"
        );
    }

    /**
     * Verifies that log entries with an exception and a custom message are output correctly.
     *
     * @param level The severity level for the log entry
     * @param err The captured output of the standard error stream
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.INCLUDE, names = {"WARN", "ERROR"})
    @StdIo
    void outputExceptionWithCustomMessage(Level level, StdErr err) {
        Exception exception = new Exception();
        exception.setStackTrace(new StackTraceElement[] {
            new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
        });

        LogEntry entry = new LogEntry(
            Thread.currentThread(),
            emptyMap(),
            null,
            "tinylog",
            level,
            exception,
            null,
            "Oops!",
            null
        );

        new InternalLoggingBackend(configuration).output(entry, true);

        assertThat(err.capturedLines()).containsExactly(
            "TINYLOG " + level + ": Oops!: java.lang.Exception",
            "\tat example.MyClass.foo(MyClass.java:42)"
        );
    }

    /**
     * Verifies that log entries with the severity levels TRACE, DEBUG, and INFO are discarded.
     *
     * @param level The severity level for the log entry
     * @param err The captured output of the standard error stream
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.INCLUDE, names = {"TRACE", "DEBUG", "INFO"})
    @StdIo
    void discardNonSevereSeverityLevels(Level level, StdErr err) {
        LogEntry entry = new LogEntry(
            Thread.currentThread(),
            emptyMap(),
            null,
            "tinylog",
            level,
            null,
            null,
            "Hello World!",
            null
        );

        new InternalLoggingBackend(configuration).output(entry, true);
        assertThat(err.capturedLines()).isEmpty();
    }

    /**
     * Verifies that log entries with tags unequal "tinylog" are discarded.
     *
     * @param tag The category tag to test
     * @param err The captured output of the standard error stream
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"foo", "bar"})
    @StdIo
    void discardNonInternalTags(String tag, StdErr err) {
        LogEntry entry = new LogEntry(
            Thread.currentThread(),
            emptyMap(),
            null,
            tag,
            Level.ERROR,
            null,
            null,
            "Hello World!",
            null
        );

        new InternalLoggingBackend(configuration).output(entry, true);
        assertThat(err.capturedLines()).isEmpty();
    }

    /**
     * Verifies that the logging backend can be closed without throwing any exception.
     */
    @Test
    void closable() {
        new InternalLoggingBackend(configuration).close();
    }

}
