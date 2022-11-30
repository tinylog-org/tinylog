package org.tinylog.impl.writers.logcat;

import java.util.ServiceLoader;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.mockito.MockedStatic;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.Logcat;
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
    private Framework framework;

    /**
     * Verifies that the logcat writer builder is the default writer builder on Android.
     */
    @Test
    @EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    void defaultWriter() {
        String builderWriterName = new LogcatWriterBuilder().getName();
        String defaultWriterName = framework.getRuntime().getDefaultWriter();
        assertThat(builderWriterName).isEqualTo(defaultWriterName);
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

    /**
     * Tests for {@link LogcatWriter} creation with mocked {@link Log} on non-Android platforms.
     */
    @Nested
    @DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    class MockedLogging {

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

            try (Writer writer = new LogcatWriterBuilder().create(framework, new Configuration(emptyMap()))) {
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
            try (Writer writer = new LogcatWriterBuilder().create(framework, configuration)) {
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
            try (Writer writer = new LogcatWriterBuilder().create(framework, configuration)) {
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
            try (Writer writer = new LogcatWriterBuilder().create(framework, configuration)) {
                writer.log(logEntry);
                logMock.verify(() -> Log.println(Log.INFO, null, "MyClass: Hello World!"));
            }
        }

    }

    /**
     * Tests for {@link LogcatWriter} creation with real {@link Log} on Android.
     */
    @Nested
    @EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    class RealLogging {

        private Logcat logcat;

        /**
         * Creates an instance of {@link Logcat}.
         */
        @BeforeEach
        void init() {
            logcat = new Logcat();
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

            try (Writer writer = new LogcatWriterBuilder().create(framework, new Configuration(emptyMap()))) {
                writer.log(logEntry);
            }

            Thread.sleep(100);

            Pattern pattern = Pattern.compile("\\W+I\\W+(pl\\.android\\.test?\\W+)?Hello World!$");
            assertThat(logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
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
            try (Writer writer = new LogcatWriterBuilder().create(framework, configuration)) {
                writer.log(logEntry);
            }

            Thread.sleep(100);

            Pattern pattern = Pattern.compile("\\W+I\\W+foo\\W+Hello World!$");
            assertThat(logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
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
            try (Writer writer = new LogcatWriterBuilder().create(framework, configuration)) {
                writer.log(logEntry);
            }

            Thread.sleep(100);

            Pattern pattern = Pattern.compile("\\W+I\\W+12345678901234567890\\.\\.\\.\\W+Hello World!$");
            assertThat(logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
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
            try (Writer writer = new LogcatWriterBuilder().create(framework, configuration)) {
                writer.log(logEntry);
            }

            Thread.sleep(100);

            Pattern pattern = Pattern.compile("\\W+I\\W+(pl\\.android\\.test?\\W+)?MyClass: Hello World!$");
            assertThat(logcat.fetchOutput()).anySatisfy(line -> assertThat(line).containsPattern(pattern));
        }

    }

}
