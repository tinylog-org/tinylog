package org.tinylog.impl.format.style;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.LogEntry;
import org.tinylog.impl.format.placeholder.BundlePlaceholder;
import org.tinylog.impl.format.placeholder.LinePlaceholder;
import org.tinylog.impl.format.placeholder.MessageOnlyPlaceholder;
import org.tinylog.impl.format.placeholder.MessagePlaceholder;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.impl.format.placeholder.ValueType;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class AbstractStylePlaceholderTest {

    @Inject
    private Configuration configuration;

    /**
     * Verifies that a style placeholder requires the same output details as the actual wrapped placeholder.
     */
    @Test
    void getOutputDetails() {
        Placeholder actual = new MessagePlaceholder(configuration);
        Placeholder styled = new StylePlaceholder(actual);
        assertThat(styled.getOutputDetails()).isEqualTo(actual.getOutputDetails());
    }

    /**
     * Verifies that a {@link ValueType#STRING} placeholder is resolved correctly.
     */
    @Test
    void resolveStringPlaceholder() {
        Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder(configuration));
        assertThat(styled.getType()).isEqualTo(ValueType.STRING);

        LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
        assertThat(styled.getValue(logEntry)).isEqualTo("[Hello World!]");

        logEntry = new LogEntryBuilder().create();
        assertThat(styled.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that a non-string placeholder is resolved as {@link ValueType#STRING}.
     */
    @Test
    void resolveNumericPlaceholder() {
        Placeholder styled = new StylePlaceholder(new LinePlaceholder());
        assertThat(styled.getType()).isEqualTo(ValueType.STRING);

        LogEntry logEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 42)
            .create();
        assertThat(styled.getValue(logEntry)).isEqualTo("[42]");

        logEntry = new LogEntryBuilder().create();
        assertThat(styled.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that a bundled placeholder is resolved as {@link ValueType#STRING}.
     */
    @Test
    void resolveBundledPlaceholder() {
        Placeholder prefix = new StaticTextPlaceholder("foo:");
        Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder(configuration));
        Placeholder bundle = new BundlePlaceholder(List.of(prefix, styled));
        assertThat(bundle.getType()).isEqualTo(ValueType.STRING);

        LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
        assertThat(bundle.getValue(logEntry)).isEqualTo("foo:[Hello World!]");

        logEntry = new LogEntryBuilder().create();
        assertThat(bundle.getValue(logEntry)).isEqualTo("foo:[]");
    }

    /**
     * Verifies that a {@link ValueType#STRING} placeholder is rendered correctly.
     */
    @Test
    void renderStringPlaceholder() {
        Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder(configuration));
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
        assertThat(renderer.render(logEntry)).isEqualTo("[Hello World!]");

        logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("[]");
    }

    /**
     * Verifies that a non-string placeholder is rendered correctly.
     */
    @Test
    void renderNumericPlaceholder() {
        Placeholder styled = new StylePlaceholder(new LinePlaceholder());
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 42)
            .create();
        assertThat(renderer.render(logEntry)).isEqualTo("[42]");

        logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("[?]");
    }

    /**
     * Verifies that a bundled placeholder is rendered correctly.
     */
    @Test
    void renderBundledPlaceholder() {
        Placeholder prefix = new StaticTextPlaceholder("foo:");
        Placeholder styled = new StylePlaceholder(new MessageOnlyPlaceholder(configuration));
        Placeholder bundle = new BundlePlaceholder(List.of(prefix, styled));
        FormatOutputRenderer renderer = new FormatOutputRenderer(bundle);

        LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo:[Hello World!]");

        logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo:[]");
    }

    /**
     * Testable non-abstract implementation of {@link AbstractStylePlaceholder}.
     */
    private static final class StylePlaceholder extends AbstractStylePlaceholder {

        /**
         * @param placeholder The actual placeholder to style
         */
        private StylePlaceholder(Placeholder placeholder) {
            super(placeholder);
        }

        @Override
        protected void apply(StringBuilder builder, int start) {
            builder.insert(start, '[');
            builder.append(']');
        }

    }

}
