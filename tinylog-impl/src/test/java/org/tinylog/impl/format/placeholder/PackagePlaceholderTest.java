package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class PackagePlaceholderTest {

    /**
     * Verifies that the package placeholder enables output and requires the class name of the caller.
     */
    @Test
    void provideOutputDetails() {
        PackagePlaceholder placeholder = new PackagePlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
    }

    /**
     * Verifies that {@code null} will be resolved, if a class name without package information is set.
     */
    @Test
    void resolveWithDefaultPackage() {
        LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
        PackagePlaceholder placeholder = new PackagePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the package name of a log entry will be resolved, if a fully-qualified class name with package
     * information is set.
     */
    @Test
    void resolveWithFullyQualifiedPackage() {
        LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
        PackagePlaceholder placeholder = new PackagePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("org.foo");
    }

    /**
     * Verifies that {@code null} will be resolved, if the source class name is not set.
     */
    @Test
    void resolveWithoutPackage() {
        LogEntry logEntry = new LogEntryBuilder().create();
        PackagePlaceholder placeholder = new PackagePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that an empty string will be output, if a class name with default package is set.
     */
    @Test
    void renderWithDefaultPackage() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new PackagePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("");
    }

    /**
     * Verifies that the package name of a log entry will be output, if a fully-qualified class name with package
     * information is set.
     */
    @Test
    void renderWithFullyQualifiedPackage() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new PackagePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("org.foo");
    }

    /**
     * Verifies that {@code <package unknown>} will be output, if the class name is not set.
     */
    @Test
    void renderWithoutPackage() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new PackagePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("<package unknown>");
    }

}
