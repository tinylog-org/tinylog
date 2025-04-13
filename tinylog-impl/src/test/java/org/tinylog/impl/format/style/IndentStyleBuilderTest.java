package org.tinylog.impl.format.style;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Tinylog
class IndentStyleBuilderTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(StyleBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(IndentStyleBuilder.class);
            assertThat(builder.getName()).isEqualTo("indent");
        });
    }

    /**
     * Verifies that indentation will be applied as tabs ("\t"), if indentation depth is not set.
     */
    @Test
    void defaultIndentationByTab() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        Placeholder styled = new IndentStyleBuilder().create(context, placeholder, null);
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("\tfoo");
    }

    /**
     * Verifies that the number of spaces for indentation can be defined as number via the configuration value.
     */
    @Test
    void customIndentationBySpaces() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        Placeholder styled = new IndentStyleBuilder().create(context, placeholder, "2");
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("  foo");
    }

    /**
     * Verifies that indentation can be completely removed.
     */
    @Test
    void noneIndentation() {
        Placeholder placeholder = new StaticTextPlaceholder("\tfoo");
        Placeholder styled = new IndentStyleBuilder().create(context, placeholder, "0");
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that a configuration value with an illegal indentation depth is rejected.
     */
    @Test
    void invalidIndentation() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        assertThatCode(() -> new IndentStyleBuilder().create(context, placeholder, "bar"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("bar");
    }

}
