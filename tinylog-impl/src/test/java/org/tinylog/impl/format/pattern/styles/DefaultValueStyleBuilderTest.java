package org.tinylog.impl.format.pattern.styles;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.format.pattern.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@CaptureLogEntries
class DefaultValueStyleBuilderTest {

    @Inject
    private LoggingContext context;

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

}
