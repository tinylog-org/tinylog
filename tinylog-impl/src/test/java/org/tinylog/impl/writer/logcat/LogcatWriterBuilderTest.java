package org.tinylog.impl.writer.logcat;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.writer.Writer;
import org.tinylog.impl.writer.WriterBuilder;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import android.util.Log;
import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@Tinylog
class LogcatWriterBuilderTest {

    @Inject
    private TinylogContext context;

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

    /**
     * Verifies that a {@link LogcatWriter} without tag pattern can be created.
     */
    @Tinylog(configuration = {})
    @Test
    void noTag() throws Exception {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .message("Hello World!")
            .create();

        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            try (Writer writer = new LogcatWriterBuilder().create(context)) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(Log.INFO, null, "Hello World!"));
            }
        }
    }

    /**
     * Verifies that a {@link LogcatWriter} with a custom tag pattern can be created.
     */
    @Tinylog(configuration = "tag-pattern={tag}")
    @Test
    void customTag() throws Exception {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .tag("foo")
            .message("Hello World!")
            .create();

        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            try (Writer writer = new LogcatWriterBuilder().create(context)) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(Log.INFO, "foo", "Hello World!"));
            }
        }
    }

    /**
     * Verifies that the generated tag placeholder for {@link LogcatWriter} has a maximum length of 23 characters.
     */
    @Tinylog(configuration = "tag-pattern={tag}")
    @Test
    void tooLongTag() throws Exception {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .tag("123456789012345678901234")
            .message("Hello World!")
            .create();

        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            try (Writer writer = new LogcatWriterBuilder().create(context)) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(Log.INFO, "12345678901234567890...", "Hello World!"));
            }
        }
    }

    /**
     * Verifies that a {@link LogcatWriter} with a custom message pattern can be created.
     */
    @Tinylog(configuration = "message-pattern={class-name}: {message}")
    @Test
    void customMessage() throws Exception {
        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .className("org.foo.MyClass")
            .message("Hello World!")
            .create();

        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            try (Writer writer = new LogcatWriterBuilder().create(context)) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(Log.INFO, null, "MyClass: Hello World!"));
            }
        }
    }

}
