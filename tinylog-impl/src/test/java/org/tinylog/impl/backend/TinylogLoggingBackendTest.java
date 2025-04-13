package org.tinylog.impl.backend;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junitpioneer.jupiter.StdErr;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import org.mockito.MockedConstruction;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.SimpleMessageFormatter;
import org.tinylog.impl.writer.console.ConsoleWriter;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;

@Tinylog
class TinylogLoggingBackendTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Log log;

    /**
     * Verifies that the provided context storage accepts context values.
     */
    @Test
    void contextStorage() {
        ContextStorage storage = new TinylogLoggingBackend(context).getContextStorage();
        storage.put("foo", "42");
        assertThat(storage.getMapping()).containsExactly(entry("foo", "42"));
    }

    /**
     * Tests for {@link TinylogLoggingBackend#getLevelVisibilityByClass(String)}.
     */
    @Nested
    class ClassVisibility {

        /**
         * Verifies that output details are enabled with full location information for all severity levels by default.
         */
        @Test
        @Tinylog(configuration = {})
        void defaultRootLevel() {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);
            LevelVisibility visibility = backend.getLevelVisibilityByClass("com.example.Foo");

            assertThat(visibility.getTrace()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getDebug()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

        /**
         * Verifies that output details are only enabled for the configured root severity levels.
         */
        @Test
        @Tinylog(configuration = "level=INFO")
        void customRootLevel() {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);
            LevelVisibility visibility = backend.getLevelVisibilityByClass("com.example.Foo");

            assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
            assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
            assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

        /**
         * Verifies that output details are only enabled for the configured package severity levels.
         */
        @Test
        @Tinylog(configuration = "level@com.example=INFO")
        void customPackageLevel() {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);
            LevelVisibility visibility = backend.getLevelVisibilityByClass("com.example.Foo");

            assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
            assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
            assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

        /**
         * Verifies that output details are only enabled for the configured class severity levels.
         */
        @Test
        @Tinylog(configuration = "level@com.example.Foo=INFO")
        void customClassLevel() {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);
            LevelVisibility visibility = backend.getLevelVisibilityByClass("com.example.Foo");

            assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
            assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
            assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

    }

    /**
     * Tests for {@link TinylogLoggingBackend#getLevelVisibilityByTag(String)}.
     */
    @Nested
    class TagVisibility {

        /**
         * Verifies that output details are enabled with full location information for all severity levels for untagged
         * log entries by default.
         *
         * @param tag The supported tag aliases for untagged log entries
        */
        @ParameterizedTest
        @NullAndEmptySource
        @Tinylog(configuration = {})
        void untagged(String tag) {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);
            LevelVisibility visibility = backend.getLevelVisibilityByTag(tag);

            assertThat(visibility.getTrace()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getDebug()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

        /**
         * Verifies that output details are enabled with full location information for all severity levels for a custom
         * category tag by default.
         */
        @Test
        @Tinylog(configuration = {})
        void customGlobalTag() {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);
            LevelVisibility visibility = backend.getLevelVisibilityByTag("foo");

            assertThat(visibility.getTrace()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getDebug()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

        /**
         * Verifies that the output details can be restricted for a custom category tag.
         */
        @Test
        @Tinylog(configuration = "level=INFO@foo")
        void customConfiguredTag() {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);
            LevelVisibility visibility = backend.getLevelVisibilityByTag("foo");

            assertThat(visibility.getTrace()).isEqualTo(OutputDetails.DISABLED);
            assertThat(visibility.getDebug()).isEqualTo(OutputDetails.DISABLED);
            assertThat(visibility.getInfo()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getWarn()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
            assertThat(visibility.getError()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

    }

    /**
     * Tests for {@link TinylogLoggingBackend#isEnabled(Object, String, Level)}.
     */
    @Nested
    class EnabledCheck {

        /**
         * Verifies that each severity level state can be correctly resolved using a {@link StackTraceElement} as
         * location information.
         *
         * @param tag The supported tag aliases for untagged log entries
         */
        @ParameterizedTest
        @NullAndEmptySource
        @Tinylog(configuration = {"level=OFF", "level@com.example.Foo=INFO"})
        void stackTraceElement(String tag) {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);
            StackTraceElement stackTraceElement = new StackTraceElement("com.example.Foo", "bar", "Foo.java", 42);

            assertThat(backend.isEnabled(stackTraceElement, tag, Level.TRACE)).isFalse();
            assertThat(backend.isEnabled(stackTraceElement, tag, Level.DEBUG)).isFalse();
            assertThat(backend.isEnabled(stackTraceElement, tag, Level.INFO)).isTrue();
            assertThat(backend.isEnabled(stackTraceElement, tag, Level.WARN)).isTrue();
            assertThat(backend.isEnabled(stackTraceElement, tag, Level.ERROR)).isTrue();
        }

        /**
         * Verifies that each severity level state can be correctly resolved using a {@link Class} as location
         * information.
         *
         * @param tag The supported tag aliases for untagged log entries
         */
        @ParameterizedTest
        @NullAndEmptySource
        @Tinylog(configuration = {"level=OFF", "level@org.tinylog=INFO"})
        void classObject(String tag) {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);

            assertThat(backend.isEnabled(TinylogLoggingBackendTest.class, tag, Level.TRACE)).isFalse();
            assertThat(backend.isEnabled(TinylogLoggingBackendTest.class, tag, Level.DEBUG)).isFalse();
            assertThat(backend.isEnabled(TinylogLoggingBackendTest.class, tag, Level.INFO)).isTrue();
            assertThat(backend.isEnabled(TinylogLoggingBackendTest.class, tag, Level.WARN)).isTrue();
            assertThat(backend.isEnabled(TinylogLoggingBackendTest.class, tag, Level.ERROR)).isTrue();
        }

        /**
         * Verifies that each severity level state can be correctly resolved using a class name as location information.
         *
         * @param tag The supported tag aliases for untagged log entries
         */
        @ParameterizedTest
        @NullAndEmptySource
        @Tinylog(configuration = {"level=OFF", "level@com.example.Foo=INFO"})
        void className(String tag) {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);

            assertThat(backend.isEnabled("com.example.Foo", tag, Level.TRACE)).isFalse();
            assertThat(backend.isEnabled("com.example.Foo", tag, Level.DEBUG)).isFalse();
            assertThat(backend.isEnabled("com.example.Foo", tag, Level.INFO)).isTrue();
            assertThat(backend.isEnabled("com.example.Foo", tag, Level.WARN)).isTrue();
            assertThat(backend.isEnabled("com.example.Foo", tag, Level.ERROR)).isTrue();
        }

        /**
         * Verifies that each severity level state can be correctly resolved using {@code null} as location information,
         * besides reporting an error.
         */
        @Test
        @Tinylog(configuration = "level=OFF@*,INFO@foo")
        void nullValue() {
            TinylogLoggingBackend backend = new TinylogLoggingBackend(context);

            assertThat(backend.isEnabled(null, "foo", Level.TRACE)).isFalse();
            assertThat(backend.isEnabled(null, "foo", Level.DEBUG)).isFalse();
            assertThat(backend.isEnabled(null, "foo", Level.INFO)).isTrue();
            assertThat(backend.isEnabled(null, "foo", Level.WARN)).isTrue();
            assertThat(backend.isEnabled(null, "foo", Level.ERROR)).isTrue();

            assertThat(log.consume()).hasSize(5).allSatisfy(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getFormattedMessage(context.getConfiguration()))
                    .containsIgnoringCase("location")
                    .contains("null");
            });
        }

    }

    /**
     * Tests for {@link TinylogLoggingBackend#output(LogEntry, boolean)}.
     */
    @Nested
    class LogEntryOutput {

        /**
         * Verifies that log entries with a plain text message are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        @Tinylog(configuration = {"writer.type=console", "writer.pattern={level}: {message}"})
        void outputPlainTextMessage(StdOut out) {
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

            new TinylogLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("INFO: Hello World!");
        }

        /**
         * Verifies that log entries with a formatted text message with placeholders are output correctly.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        @Tinylog(configuration = {"writer.type=console", "writer.pattern={level}: {message}"})
        void outputFormattedTextMessage(StdOut out) {
            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                null,
                null,
                Level.INFO,
                null,
                new SimpleMessageFormatter(),
                "Hello {}!",
                new Object[] {"Alice"}
            );

            new TinylogLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).containsExactly("INFO: Hello Alice!");
        }

        /**
         * Verifies that log entries with an exception but without any message are output correctly.
         *
         * @param err The captured output of the standard error stream
         */
        @Test
        @StdIo
        @Tinylog(configuration = {"writer.type=console", "writer.pattern={level}: {message}"})
        void outputExceptionOnly(StdErr err) {
            Exception exception = new Exception();
            exception.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
                new StackTraceElement("example.OtherClass", "bar", "OtherClass.java", 42),
            });

            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                null,
                null,
                Level.ERROR,
                exception,
                null,
                null,
                null
            );

            new TinylogLoggingBackend(context).output(entry, true);

            assertThat(err.capturedLines()).containsExactly(
                "ERROR: java.lang.Exception",
                "\tat example.MyClass.foo(MyClass.java:42)",
                "\tat example.OtherClass.bar(OtherClass.java:42)"
            );
        }

        /**
         * Verifies that log entries with an exception and a custom message are output correctly.
         *
         * @param err The captured output of the standard error stream
         */
        @Test
        @StdIo
        @Tinylog(configuration = {"writer.type=console", "writer.pattern={level}: {message}"})
        void outputExceptionWithCustomMessage(StdErr err) {
            Exception exception = new Exception();
            exception.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
            });

            LogEntry entry = new LogEntry(
                Thread.currentThread(),
                emptyMap(),
                null,
                null,
                Level.ERROR,
                exception,
                null,
                "Oops!",
                null
            );

            new TinylogLoggingBackend(context).output(entry, true);

            assertThat(err.capturedLines()).containsExactly(
                "ERROR: Oops!: java.lang.Exception",
                "\tat example.MyClass.foo(MyClass.java:42)"
            );
        }

        /**
         * Verifies that a log entry won't be output if its severity level is not enabled.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        @Tinylog(configuration = "level=WARN")
        void discardNonSevereSeverityLevel(StdOut out) {
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

            new TinylogLoggingBackend(context).output(entry, true);
            assertThat(out.capturedLines()).isEmpty();
        }

        /**
         * Verifies that an error is logged, if a log entry cannot be written by a writer.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void reportFailedOutput(StdOut out) {
            try (MockedConstruction<ConsoleWriter> ignored = mockConstruction(
                ConsoleWriter.class,
                (writer, context) -> doThrow(UnsupportedOperationException.class).when(writer).log(any()))
            ) {
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

                new TinylogLoggingBackend(context).output(entry, true);
                assertThat(out.capturedLines()).isEmpty();
            }

            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getFormattedMessage(context.getConfiguration())) .contains("write", "log entry");
            });
        }

        /**
         * Verifies that an error is logged, if a writer cannot be flushed.
         *
         * @param out The captured output of the standard output stream
         */
        @Test
        @StdIo
        void reportFailedFlush(StdOut out) {
            try (MockedConstruction<ConsoleWriter> ignored = mockConstruction(
                ConsoleWriter.class,
                (writer, context) -> doThrow(UnsupportedOperationException.class).when(writer).flush())
            ) {
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

                new TinylogLoggingBackend(context).output(entry, true);
                assertThat(out.capturedLines()).isEmpty();
            }

            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getFormattedMessage(context.getConfiguration())) .contains("flush", "writer");
            });
        }

    }

    /**
     * Tests for {@link TinylogLoggingBackend#close()}.
     */
    @Nested
    class Closing {

        /**
         * Verifies that the backend can be closed successfully by default.
         */
        @Test
        void successful() {
            new TinylogLoggingBackend(context).close();
        }

        /**
         * Verifies that the writers that cannot be closed will be reported.
         */
        @Test
        void unsuccessful() {
            try (MockedConstruction<ConsoleWriter> ignored = mockConstruction(
                ConsoleWriter.class,
                (writer, context) -> doThrow(UnsupportedOperationException.class).when(writer).close())
            ) {
                new TinylogLoggingBackend(context).close();
            }

            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getFormattedMessage(context.getConfiguration())).contains("close", "writer");
            });
        }

    }

}
