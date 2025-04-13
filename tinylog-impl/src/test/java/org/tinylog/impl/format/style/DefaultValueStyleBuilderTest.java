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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tinylog
class DefaultValueStyleBuilderTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(StyleBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(DefaultValueStyleBuilder.class);
            assertThat(builder.getName()).isEqualTo("default");
        });
    }

    /**
     * Verifies that the builder can create an instance of {@link DefaultValueStyle} with a given default value.
     */
    @Test
    void creationWithDefaultValue() {
        Placeholder placeholder = new StaticTextPlaceholder("");
        Placeholder styled = new DefaultValueStyleBuilder().create(context, placeholder, "foo");
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that an {@link IllegalArgumentException} with a meaningful message description will be thrown, if the
     * default value is missing.
     */
    @Test
    void creationWithoutDefaultValue() {
        Placeholder placeholder = new StaticTextPlaceholder("");
        assertThatThrownBy(() -> new DefaultValueStyleBuilder().create(context, placeholder, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("default value");
    }

}
