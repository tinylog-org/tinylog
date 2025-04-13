package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ClassPlaceholderTest {

    /**
     * Verifies that the class placeholder enables output and requires the class name of the caller.
     */
    @Test
    void provideOutputDetails() {
        ClassPlaceholder placeholder = new ClassPlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
    }

    /**
     * Verifies that the source class name of a log entry will be resolved, if set.
     */
    @Test
    void resolveWithClassName() {
        LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
        ClassPlaceholder placeholder = new ClassPlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("foo.MyClass");
    }

    /**
     * Verifies that {@code null} will be resolved, if the source class name is not set.
     */
    @Test
    void resolveWithoutClassName() {
        LogEntry logEntry = new LogEntryBuilder().create();
        ClassPlaceholder placeholder = new ClassPlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the source class name of a log entry will be output, if set.
     */
    @Test
    void renderWithClassName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new ClassPlaceholder());
        LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo.MyClass");
    }

    /**
     * Verifies that {@code <class unknown>} will be output, if the class name is not set.
     */
    @Test
    void renderWithoutClassName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new ClassPlaceholder());
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("<class unknown>");
    }

}
