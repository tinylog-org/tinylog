package org.tinylog.impl.writer.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.ServiceLoader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.json.NewlineDelimitedJson;
import org.tinylog.impl.writer.Writer;
import org.tinylog.impl.writer.WriterBuilder;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Tinylog
class FileWriterBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Log log;

    private Path file;

    /**
     * Creates a temporary log file.
     *
     * @throws IOException If failed to create a temporary log file
     */
    @BeforeEach
    void init() throws IOException {
        file = Files.createTempFile("tinylog", ".log");
        file.toFile().deleteOnExit();
    }

    /**
     * Deletes the created temporary log file.
     *
     * @throws IOException If failed to delete the temporary log file
     */
    @AfterEach
    void release() throws IOException {
        Files.deleteIfExists(file);
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(WriterBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(FileWriterBuilder.class);
            assertThat(builder.getName()).isEqualTo("file");
        });
    }

    /**
     * Verifies that the default format pattern will be used, if no custom format pattern is set.
     */
    @Test
    void defaultPattern() throws Exception {
        applyConfiguration(Map.of(
            "locale", "en_US",
            "zone", "UTC",
            "file", file.toString()
        ));

        try (Writer writer = new FileWriterBuilder().create(context)) {
            LogEntry logEntry = new LogEntryBuilder()
                .timestamp(Instant.EPOCH)
                .thread(new Thread(() -> { }, "main"))
                .severityLevel(Level.INFO)
                .stackTraceElement("org.MyClass", "foo", null, -1)
                .message("Hello World!")
                .create();

            writer.log(logEntry);
        }

        assertThat(file)
            .hasContent("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!" + System.lineSeparator());
    }

    /**
     * Verifies that custom output formats like {@link NewlineDelimitedJson} are supported.
     */
    @Test
    void customJsonFormat() throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "format", "ndjson",
            "fields.msg", "message"
        ));

        try (Writer writer = new FileWriterBuilder().create(context)) {
            LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
            writer.log(logEntry);
        }

        assertThat(file).hasContent("{\"msg\": \"Hello World!\"}" + System.lineSeparator());
    }

    /**
     * Verifies that illegal output formats are reported and the writer will use the default pattern output format
     * instead.
     */
    @Test
    void illegalOutputFormat() throws Exception {
        applyConfiguration(Map.of(
            "locale", "en_US",
            "zone", "UTC",
            "file", file.toString(),
            "format", "foo"
        ));

        try (Writer writer = new FileWriterBuilder().create(context)) {
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getFormattedMessage(context.getConfiguration())).contains("format", "foo");
            });

            LogEntry logEntry = new LogEntryBuilder()
                .timestamp(Instant.EPOCH)
                .thread(new Thread(() -> { }, "main"))
                .severityLevel(Level.INFO)
                .stackTraceElement("org.MyClass", "foo", null, -1)
                .message("Hello World!")
                .create();

            writer.log(logEntry);
        }

        assertThat(file)
            .hasContent("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!" + System.lineSeparator());
    }

    /**
     * Verifies that a new line is automatically appended to a custom format pattern.
     */
    @Test
    void appendNewLineToCustomPattern() throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "pattern", "{message}"
        ));

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("Hello World!").create());
        }

        assertThat(file).hasContent("Hello World!" + System.lineSeparator());
    }

    /**
     * Verifies that an exception with a meaningful message will be thrown, if file name is undefined.
     */
    @Test
    void missingFileName() {
        applyConfiguration(emptyMap());
        Throwable throwable = catchThrowable(() -> new FileWriterBuilder().create(context).close());

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).containsIgnoringCase("file");
    }

    /**
     * Verifies that UTF-8 can be defined as custom charset, regardless of the spelling.
     *
     * @param charsetName The UTF-8 spelling to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"utf8", "utf-8", "UTF8", "UTF-8"})
    void utf8Charset(String charsetName) throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "charset", charsetName,
            "pattern", "{message}"
        ));

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("abc - äöüß - áéíóúüñ - 한글").create());
        }

        assertThat(file)
            .usingCharset(StandardCharsets.UTF_8)
            .hasContent("abc - äöüß - áéíóúüñ - 한글" + System.lineSeparator());
    }

    /**
     * Verifies that ASCII can be defined as custom charset, regardless of the spelling.
     *
     * @param charsetName The ASCII spelling to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"ascii", "us-ascii", "ASCII", "US-ASCII"})
    void asciiCharset(String charsetName) throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "charset", charsetName,
            "pattern", "{message}"
        ));

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("abc - äöüß - áéíóúüñ - 한글").create());
        }

        assertThat(file)
            .usingCharset(StandardCharsets.US_ASCII)
            .hasContent("abc - ???? - ??????? - ??" + System.lineSeparator());
    }

    /**
     * Verifies that an invalid charset name is reported, but does not prevent the file writer from outputting log
     * entries.
     */
    @Test
    void invalidCharset() throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "charset", "dummy",
            "pattern", "{message}"
        ));

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("Hello World!").create());
        }

        assertThat(file).hasContent("Hello World!" + System.lineSeparator());

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(context.getConfiguration())).contains("charset", "dummy");
        });
    }

    /**
     * Verifies that a file writer can be created without defining a policy.
     */
    @Test
    void noPolicy() throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "chatset", StandardCharsets.US_ASCII.name(),
            "pattern", "{message}"
        ));

        Files.write(file, singleton("foo"), StandardCharsets.US_ASCII);

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("bar").create());
        }

        assertThat(file).hasContent("foo" + System.lineSeparator() + "bar" + System.lineSeparator());
    }

    /**
     * Verifies that a file writer can be created with a single policy.
     */
    @Test
    void singlePolicy() throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "chatset", StandardCharsets.US_ASCII.name(),
            "pattern", "{message}",
            "policies", "startup"
        ));

        Files.write(file, singleton("foo"), StandardCharsets.US_ASCII);

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("bar").create());
        }

        assertThat(file).hasContent("bar" + System.lineSeparator());
    }

    /**
     * Verifies that a file writer can be created with multiple policies.
     */
    @Test
    void multiplePolicies() throws Exception {
        int size = (1 + System.lineSeparator().length()) * 2; // two lines (letter + line separator)
        applyConfiguration(Map.of(
            "file", file.toString(),
            "chatset", StandardCharsets.US_ASCII.name(),
            "pattern", "{message}",
            "policies", "startup, size: " + size
        ));

        Files.write(file, singleton("a"), StandardCharsets.US_ASCII);

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("b").create());
            writer.log(new LogEntryBuilder().message("c").create());
            writer.log(new LogEntryBuilder().message("d").create());
        }

        assertThat(file).usingCharset(StandardCharsets.US_ASCII).hasContent("d" + System.lineSeparator());
    }

    /**
     * Verifies that unknown policies are reported, but other known policies work nevertheless.
     */
    @Test
    void reportUnknownPolicy() throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "chatset", StandardCharsets.US_ASCII.name(),
            "pattern", "{message}",
            "policies", "foo, startup"
        ));

        Files.write(file, singleton("foo"), StandardCharsets.US_ASCII);

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("bar").create());
        }

        assertThat(file).hasContent("bar" + System.lineSeparator());
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(context.getConfiguration())).contains("foo");
        });
    }

    /**
     * Verifies that invalid configured policies are reported, but other valid configured policies work nevertheless.
     */
    @Test
    void reportInvalidPolicy() throws Exception {
        applyConfiguration(Map.of(
            "file", file.toString(),
            "chatset", StandardCharsets.US_ASCII.name(),
            "pattern", "{message}",
            "policies", "size: AB, startup"
        ));

        Files.write(file, singleton("foo"), StandardCharsets.US_ASCII);

        try (Writer writer = new FileWriterBuilder().create(context)) {
            writer.log(new LogEntryBuilder().message("bar").create());
        }

        assertThat(file).hasContent("bar" + System.lineSeparator());
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(context.getConfiguration())).contains("size");
            assertThat(entry.getThrowable()).hasMessageContaining("AB");
        });
    }

    /**
     * Overwrites the current tinylog context configuration with the passed properties.
     *
     * @param properties The properties for the new configuration to apply
     */
    private void applyConfiguration(Map<String, String> properties) {
        context = context.withConfiguration(new Configuration(properties, context.getLogger()));
    }

}
