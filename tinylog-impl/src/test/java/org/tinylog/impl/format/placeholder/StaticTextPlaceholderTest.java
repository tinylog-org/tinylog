package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class StaticTextPlaceholderTest {

    /**
     * Verifies that the static text placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        StaticTextPlaceholder placeholder = new StaticTextPlaceholder("Hello World!");
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the passed static text is resolved correctly.
     */
    @Test
    void resolveValue() {
        LogEntry logEntry = new LogEntryBuilder().create();
        StaticTextPlaceholder placeholder = new StaticTextPlaceholder("Hello World!");
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("Hello World!");
    }

    /**
     * Verifies that the passed static text is output unchanged.
     */
    @Test
    void renderString() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new StaticTextPlaceholder("Hello World!"));
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
    }

}
