package org.tinylog.impl.writer.console;

import java.time.Instant;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdErr;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.JavaRuntime;
import org.tinylog.impl.format.json.NewlineDelimitedJson;
import org.tinylog.impl.writer.Writer;
import org.tinylog.impl.writer.WriterBuilder;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class ConsoleWriterBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    @Inject
    private InternalLogger logger;

    @Inject
    private Log log;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(WriterBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(ConsoleWriterBuilder.class);
            assertThat(builder.getName()).isEqualTo("console");
        });
    }

    /**
     * Verifies that the console writer builder is the default writer builder on standard Java.
     */
    @Test
    void defaultWriter() {
        String builderWriterName = new ConsoleWriterBuilder().getName();
        String defaultWriterName = new JavaRuntime(logger).getDefaultWriter();
        assertThat(builderWriterName).isEqualTo(defaultWriterName);
    }

    /**
     * Verifies that the default format pattern will be used, if no custom format pattern is set.
     *
     * @param out The captured output of the standard output stream
     */
    @Test
    @StdIo
    @Tinylog(configuration = {"locale=en_US", "zone=UTC"})
    void defaultPattern(StdOut out) throws Exception {
        try (Writer writer = new ConsoleWriterBuilder().create(context)) {
            LogEntry logEntry = new LogEntryBuilder()
                .timestamp(Instant.EPOCH)
                .thread(new Thread(() -> { }, "main"))
                .severityLevel(Level.INFO)
                .stackTraceElement("org.MyClass", "foo", "MyClass.java", -1)
                .message("Hello World!")
                .create();

            writer.log(logEntry);

            assertThat(out.capturedLines())
                .containsExactly("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!");
        }
    }

    /**
     * Verifies that custom output formats like {@link NewlineDelimitedJson} are supported.
     *
     * @param out The captured output of the standard output stream
     */
    @Test
    @StdIo
    @Tinylog(configuration = {"format=ndjson", "fields.msg=message"})
    void customJsonFormat(StdOut out) throws Exception {
        try (Writer writer = new ConsoleWriterBuilder().create(context)) {
            LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).message("Hello World!").create();
            writer.log(logEntry);

            assertThat(out.capturedLines()).containsExactly("{\"msg\": \"Hello World!\"}");
        }
    }

    /**
     * Verifies that illegal output formats are reported and the writer will use the default pattern output format
     * instead.
     *
     * @param out The captured output of the standard output stream
     */
    @Test
    @StdIo
    @Tinylog(configuration = {"locale=en_US", "zone=UTC", "format=foo"})
    void illegalOutputFormat(StdOut out) throws Exception {
        try (Writer writer = new ConsoleWriterBuilder().create(context)) {
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getFormattedMessage(configuration)).contains("format", "foo");
            });

            LogEntry logEntry = new LogEntryBuilder()
                .timestamp(Instant.EPOCH)
                .thread(new Thread(() -> { }, "main"))
                .severityLevel(Level.INFO)
                .stackTraceElement("org.MyClass", "foo", "MyClass.java", -1)
                .message("Hello World!")
                .create();

            writer.log(logEntry);

            assertThat(out.capturedLines())
                .containsExactly("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!");
        }
    }

    /**
     * Verifies that a new line will be appended to a custom format pattern automatically.
     *
     * @param out The captured output of the standard output stream
     */
    @Test
    @StdIo
    @Tinylog(configuration = {"pattern={message}", "threshold=off"})
    void appendNewLineToCustomPattern(StdOut out) throws Exception {
        try (Writer writer = new ConsoleWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello World!").create());
            assertThat(out.capturedString()).isEqualTo("Hello World!" + System.lineSeparator());
        }
    }

    /**
     * Verifies that {@link Level#WARN} will be used as default severity level threshold, if no custom threshold is set.
     *
     * @param out The captured output of the standard output stream
     * @param err The captured output of the standard error stream
     */
    @Test
    @StdIo
    @Tinylog(configuration = "pattern={message}")
    void defaultSeverityLevelThreshold(StdOut out, StdErr err) throws Exception {
        try (Writer writer = new ConsoleWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello system out!").create());
            assertThat(out.capturedLines()).containsExactly("Hello system out!");

            writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello system err!").create());
            assertThat(err.capturedLines()).containsExactly("Hello system err!");
        }
    }

    /**
     * Verifies that a custom severity level threshold can be set.
     *
     * @param out The captured output of the standard output stream
     * @param err The captured output of the standard error stream
     */
    @Test
    @StdIo
    @Tinylog(configuration = {"pattern={message}", "threshold=error"})
    void customSeverityLevelThreshold(StdOut out, StdErr err) throws Exception {
        try (Writer writer = new ConsoleWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello system out!").create());
            assertThat(out.capturedLines()).containsExactly("Hello system out!");

            writer.log(new LogEntryBuilder().severityLevel(Level.ERROR).message("Hello system err!").create());
            assertThat(err.capturedLines()).containsExactly("Hello system err!");
        }
    }

    /**
     * Verifies that an illegal severity level as threshold is logged and the writer uses the default severity level
     * threshold {@link Level#WARN} instead.
     *
     * @param out The captured output of the standard output stream
     * @param err The captured output of the standard error stream
     */
    @Test
    @StdIo
    @Tinylog(configuration = {"pattern={message}", "threshold=foo"})
    void illegalSeverityLevelThreshold(StdOut out, StdErr err) throws Exception {
        try (Writer writer = new ConsoleWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello system out!").create());
            assertThat(out.capturedLines()).containsExactly("Hello system out!");

            writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello system err!").create());
            assertThat(err.capturedLines()).containsExactly("Hello system err!");
        }

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });
    }

}
