package org.tinylog.impl.format.style;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultValueStyleTest {

    /**
     * Verifies that empty strings will be replaced with the given default value.
     */
    @Test
    void handleEmptyString() {
        Placeholder placeholder = new DefaultValueStyle(new StaticTextPlaceholder(""), "bar");
        assertThat(render(placeholder)).isEqualTo("bar");
    }

    /**
     * Verifies that non-empty strings will be output unchanged.
     */
    @Test
    void handleExistingString() {
        Placeholder placeholder = new DefaultValueStyle(new StaticTextPlaceholder("foo"), "bar");
        assertThat(render(placeholder)).isEqualTo("foo");
    }

    /**
     * Renders the passed placeholder.
     *
     * @param placeholder The placeholder to render
     * @return The output of the passed placeholder
     */
    private static String render(Placeholder placeholder) {
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        return renderer.render(new LogEntryBuilder().create());
    }

}
