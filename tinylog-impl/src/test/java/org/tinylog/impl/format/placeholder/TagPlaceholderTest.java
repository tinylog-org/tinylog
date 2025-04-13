package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class TagPlaceholderTest {

    /**
     * Verifies that the tag placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        TagPlaceholder placeholder = new TagPlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the assigned tag of tagged log entries is resolved.
     */
    @Test
    void resolveWithTag() {
        TagPlaceholder placeholder = new TagPlaceholder();
        LogEntry logEntry = new LogEntryBuilder().tag("foo").create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that {@code null} is resolved for untagged log entries.
     */
    @Test
    void resolveWithoutTag() {
        TagPlaceholder placeholder = new TagPlaceholder();
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the assigned tag of tagged log entries is output.
     */
    @Test
    void renderWithTag() {
        TagPlaceholder placeholder = new TagPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().tag("foo").create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that an empty sting is output for untagged log entries.
     */
    @Test
    void renderWithoutTag() {
        TagPlaceholder placeholder = new TagPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEmpty();
    }

}
