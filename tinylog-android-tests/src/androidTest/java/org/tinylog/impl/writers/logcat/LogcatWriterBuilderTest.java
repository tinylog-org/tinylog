package org.tinylog.impl.writers.logcat;

import java.io.IOException;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.Logcat;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.WriterBuilder;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class LogcatWriterBuilderTest {

    @Inject
    private Framework framework;

    @Inject
    private LoggingContext context;

    /**
     * Clears all existing Logcat output.
     */
    @BeforeEach
    void init() throws IOException, InterruptedException {
        Logcat.clear();
    }

    /**
     * Verifies that the logcat writer builder is the default writer builder on Android.
     */
    @Test
    void defaultWriter() {
        String builderWriterName = new LogcatWriterBuilder().getName();
        String defaultWriterName = framework.getRuntime().getDefaultWriter();
        assertThat(builderWriterName).isEqualTo(defaultWriterName);
    }

    /**
     * Verifies that a {@link LogcatWriter} without tag pattern can be created.
     */
    @Test
    void noTag() throws Exception {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .message("Hello World!")
            .create();

        try (Writer writer = new LogcatWriterBuilder().create(context, new Configuration(emptyMap()))) {
            writer.log(logEntry);
        }

        Thread.sleep(100);

        Pattern pattern = Pattern.compile("\\W+I\\W+(tinylog\\.test\\W+)?Hello World!$");
        assertThat(Logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
    }

    /**
     * Verifies that a {@link LogcatWriter} with a custom tag pattern can be created.
     */
    @Test
    void customTag() throws Exception {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .tag("foo")
            .message("Hello World!")
            .create();

        Configuration configuration = new Configuration(singletonMap("tag-pattern", "{tag}"));
        try (Writer writer = new LogcatWriterBuilder().create(context, configuration)) {
            writer.log(logEntry);
        }

        Thread.sleep(100);

        Pattern pattern = Pattern.compile("\\W+I\\W+foo\\W+Hello World!$");
        assertThat(Logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
    }

    /**
     * Verifies that the generated tag placeholder for {@link LogcatWriter} has a maximum length of 23 characters.
     */
    @Test
    void tooLongTag() throws Exception {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .tag("123456789012345678901234")
            .message("Hello World!")
            .create();

        Configuration configuration = new Configuration(singletonMap("tag-pattern", "{tag}"));
        try (Writer writer = new LogcatWriterBuilder().create(context, configuration)) {
            writer.log(logEntry);
        }

        Thread.sleep(100);

        Pattern pattern = Pattern.compile("\\W+I\\W+12345678901234567890\\.\\.\\.\\W+Hello World!$");
        assertThat(Logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
    }

    /**
     * Verifies that a {@link LogcatWriter} with a custom message pattern can be created.
     */
    @Test
    void customMessage() throws Exception {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .className("org.foo.MyClass")
            .message("Hello World!")
            .create();

        Configuration configuration = new Configuration(singletonMap("message-pattern", "{class-name}: {message}"));
        try (Writer writer = new LogcatWriterBuilder().create(context, configuration)) {
            writer.log(logEntry);
        }

        Thread.sleep(100);

        Pattern pattern = Pattern.compile("\\W+I\\W+(tinylog\\.test\\W+)?MyClass: Hello World!$");
        assertThat(Logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(WriterBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(LogcatWriterBuilder.class);
            assertThat(builder.getName()).isEqualTo("logcat");
        });
    }

}
