package org.tinylog.impl.format.pattern.styles;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.format.pattern.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@CaptureLogEntries
class IndentStyleBuilderTest {

    @Inject
    private Framework framework;

    /**
     * Verifies that indentation will be applied as tabs ("\t"), if indentation depth is not set.
     */
    @Test
    void defaultIndentationByTab() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        Placeholder styled = new IndentStyleBuilder().create(framework, placeholder, null);
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
        Placeholder styled = new IndentStyleBuilder().create(framework, placeholder, "2");
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
        Placeholder styled = new IndentStyleBuilder().create(framework, placeholder, "0");
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
        assertThatCode(() -> new IndentStyleBuilder().create(framework, placeholder, "boo"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("boo");
    }

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

}
