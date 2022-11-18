package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ClassNamePlaceholderTest {

    /**
     * Verifies that the log entry value {@link LogEntryValue#CLASS} is defined as required by the class name
     * placeholder.
     */
    @Test
    void requiredLogEntryValues() {
        ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
        assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CLASS);
    }

    /**
     * Verifies that the simple source class name of a log entry will be resolved, if a simple class name is set.
     */
    @Test
    void resolveWithSimpleClassName() {
        LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
        ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("MyClass");
    }

    /**
     * Verifies that the simple source class name of a log entry will be resolved, if a fully-qualified class name is
     * set.
     */
    @Test
    void resolveWithFullyQualifiedClassName() {
        LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
        ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("MyClass");
    }

    /**
     * Verifies that {@code null} will be resolved, if the source class name is not set.
     */
    @Test
    void resolveWithoutClassName() {
        LogEntry logEntry = new LogEntryBuilder().create();
        ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the simple source class name of a log entry will be output, if a simple class name is set.
     */
    @Test
    void renderWithSimpleClassName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new ClassNamePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("MyClass");
    }

    /**
     * Verifies that the simple source class name of a log entry will be output, if a fully-qualified class name is set.
     */
    @Test
    void renderWithFullyQualifiedClassName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new ClassNamePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("MyClass");
    }

    /**
     * Verifies that {@code <class unknown>} will be output, if the class name is not set.
     */
    @Test
    void renderWithoutClassName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new ClassNamePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("<class unknown>");
    }

}
