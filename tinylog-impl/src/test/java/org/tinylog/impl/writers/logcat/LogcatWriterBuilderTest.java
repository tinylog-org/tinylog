package org.tinylog.impl.writers.logcat;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.WriterBuilder;

import android.util.Log;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@CaptureLogEntries
class LogcatWriterBuilderTest {

    @Inject
    private LoggingContext context;

    private MockedStatic<Log> logMock;

    /**
     * Mocks the static {@link Log} class.
     */
    @BeforeEach
    void init() {
        logMock = mockStatic(Log.class);
    }

    /**
     * Restores the statically mocked {@link Log} class.
     */
    @AfterEach
    void dispose() {
        logMock.close();
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
            logMock.verify(() -> Log.println(Log.INFO, null, "Hello World!"));
        }
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
            logMock.verify(() -> Log.println(Log.INFO, "foo", "Hello World!"));
        }
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
            logMock.verify(() -> Log.println(Log.INFO, "12345678901234567890...", "Hello World!"));
        }
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
            logMock.verify(() -> Log.println(Log.INFO, null, "MyClass: Hello World!"));
        }
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
