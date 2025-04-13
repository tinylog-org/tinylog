package org.tinylog.core;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.format.message.SimpleMessageFormatter;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.mock;

@Tinylog
class LogEntryTest {

    @Inject
    private RuntimeFlavor runtime;

    @Inject
    private InternalLogger logger;

    /**
     * Verifies that a log entry provides the current timestamp.
     */
    @Test
    void receiveCurrentTimestamp() {
        LogEntry entry = new LogEntryBuilder().create();

        Instant before = Instant.now();
        Instant actual = entry.getTimestamp();
        Instant after = Instant.now();

        assertThat(actual).isBetween(before, after);
    }

    /**
     * Verifies that a log entry provides always the same timestamp.
     */
    @Test
    void receiveSameTimestamp() {
        LogEntry entry = new LogEntryBuilder().create();

        Instant first = entry.getTimestamp();
        Instant second = entry.getTimestamp();

        assertThat(first).isEqualTo(second);
    }

    /**
     * Verifies that a log entry provides the current date and time.
     */
    @Test
    void receiveCurrentUptime() {
        LogEntry entry = new LogEntryBuilder().create();

        Duration before = runtime.getUptime();
        Duration actual = entry.getUptime(runtime);
        Duration after = runtime.getUptime();

        assertThat(actual).isBetween(before, after);
    }

    /**
     * Verifies that a log entry provides always the same uptime.
     */
    @Test
    void receiveSameUptime() {
        LogEntry entry = new LogEntryBuilder().create();

        Duration first = entry.getUptime(runtime);
        Duration second = entry.getUptime(runtime);

        assertThat(first).isEqualTo(second);
    }

    /**
     * Verifies that the stored thread can be received.
     */
    @Test
    void receiveThread() {
        Thread thread = mock(Thread.class);
        LogEntry entry = new LogEntryBuilder().thread(thread).create();

        assertThat(entry.getThread()).isSameAs(thread);
    }

    /**
     * Verifies that the stored context values can be received.
     */
    @Test
    void receiveContextValues() {
        LogEntry entry = new LogEntryBuilder()
            .context("foo", "Alice")
            .context("bar", "Bob")
            .create();

        assertThat(entry.getContext()).containsOnly(entry("foo", "Alice"), entry("bar", "Bob"));
    }

    /**
     * Verifies that the fully-qualified class name can be received.
     */
    @Test
    void receiveClassName() {
        LogEntry entry = new LogEntryBuilder().className(Object.class).create();
        assertThat(entry.getClassName()).isEqualTo("java.lang.Object");
    }

    /**
     * Verifies that the method name can be received.
     */
    @Test
    void receiveMethodName() {
        LogEntry entry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", null, -1)
            .create();

        assertThat(entry.getMethodName()).isEqualTo("foo");
    }

    /**
     * Verifies that the source file name can be received.
     */
    @Test
    void receiveFileName() {
        LogEntry entry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 42)
            .create();

        assertThat(entry.getFileName()).isEqualTo("MyClass.java");
    }

    /**
     * Verifies that the line number of a source file name can be received.
     */
    @Test
    void receiveLineNumber() {
        LogEntry entry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 42)
            .create();

        assertThat(entry.getLineNumber()).isEqualTo(42);
    }

    /**
     * Verifies that the stored tag can be received.
     *
     * @param tag The tag to test
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "foo")
    void receiveTag(String tag) {
        LogEntry entry = new LogEntryBuilder().tag(tag).create();
        assertThat(entry.getTag()).isEqualTo(tag);
    }

    /**
     * Verifies that the stored severity level can be received.
     *
     * @param level The severiyt level to test
     */
    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "OFF")
    void receiveSeverityLevel(Level level) {
        LogEntry entry = new LogEntryBuilder().severityLevel(level).create();
        assertThat(entry.getSeverityLevel()).isEqualTo(level);
    }

    /**
     * Verifies that the stored throwable can be received.
     */
    @Test
    void receiveThrowable() {
        Throwable throwable = new Throwable();
        LogEntry entry = new LogEntryBuilder().throwable(throwable).create();

        assertThat(entry.getThrowable()).isSameAs(throwable);
    }

    /**
     * Verifies that the original message will be returned if the argument array is {@code null}.
     */
    @Test
    void receiveMessageWithNullAsArguments() {
        LogEntry entry = new LogEntryBuilder()
            .formatter(new SimpleMessageFormatter())
            .message("{} + {} = {}", (Object[]) null)
            .create();

        Configuration configuration = new Configuration(emptyMap(), logger);
        assertThat(entry.getFormattedMessage(configuration)).isEqualTo("{} + {} = {}");
    }

    /**
     * Verifies that the original message will be returned if the argument array is empty.
     */
    @Test
    void receiveMessageWithEmptyArguments() {
        LogEntry entry = new LogEntryBuilder()
            .formatter(new SimpleMessageFormatter())
            .message("{} + {} = {}")
            .create();

        Configuration configuration = new Configuration(emptyMap(), logger);
        assertThat(entry.getFormattedMessage(configuration)).isEqualTo("{} + {} = {}");
    }

    /**
     * Verifies that the original message will be formatted if there are arguments.
     */
    @Test
    void receiveMessageWithArguments() {
        LogEntry entry = new LogEntryBuilder()
            .formatter(new SimpleMessageFormatter())
            .message("{} + {} = {}", 1, 1, 2)
            .create();

        Configuration configuration = new Configuration(emptyMap(), logger);
        assertThat(entry.getFormattedMessage(configuration)).isEqualTo("1 + 1 = 2");
    }

}
