package org.tinylog.impl.format.pattern.placeholders;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class BundlePlaceholderTest {

    /**
     * Verifies that all log entry values that are required by any child placeholder are also required by the bundle
     * placeholder itself.
     */
    @Test
    void requiredLogEntryValues() {
        DatePlaceholder firstChild = new DatePlaceholder(DateTimeFormatter.ISO_INSTANT, false);
        ClassPlaceholder secondChild = new ClassPlaceholder();
        BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

        assertThat(bundlePlaceholder.getRequiredLogEntryValues())
            .containsExactlyInAnyOrder(LogEntryValue.CLASS, LogEntryValue.TIMESTAMP);
    }

    /**
     * Verifies that all child placeholders are resolved as a combined char sequence in the expected order.
     */
    @Test
    void resolve() {
        StaticTextPlaceholder firstChild = new StaticTextPlaceholder("Class: ");
        ClassPlaceholder secondChild = new ClassPlaceholder();
        BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

        LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
        assertThat(bundlePlaceholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(bundlePlaceholder.getValue(logEntry)).isEqualTo("Class: foo.MyClass");
    }

    /**
     * Verifies that all child placeholders are rendered correctly and in the expected order.
     */
    @Test
    void render() {
        StaticTextPlaceholder firstChild = new StaticTextPlaceholder("Class: ");
        ClassPlaceholder secondChild = new ClassPlaceholder();
        BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

        FormatOutputRenderer renderer = new FormatOutputRenderer(bundlePlaceholder);
        LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("Class: foo.MyClass");
    }

}
