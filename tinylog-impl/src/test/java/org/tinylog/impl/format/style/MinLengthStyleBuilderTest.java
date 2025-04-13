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
import static org.assertj.core.api.Assertions.catchThrowable;

@Tinylog
class MinLengthStyleBuilderTest {

    private final Placeholder fooPlaceholder = new StaticTextPlaceholder("foo");

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(StyleBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(MinLengthStyleBuilder.class);
            assertThat(builder.getName()).isEqualTo("min-length");
        });
    }

    /**
     * Verifies that a min length style can be created with minimum length passed as configuration value.
     */
    @Test
    void creationWithMinLengthOnly() {
        Placeholder stylePlaceholder = new MinLengthStyleBuilder().create(context, fooPlaceholder, "5");
        FormatOutputRenderer renderer = new FormatOutputRenderer(stylePlaceholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo  ");
    }

    /**
     * Verifies that a min length style can be created with minimum length and position passed as configuration value.
     */
    @Test
    void creationWithMinLengthAndPosition() {
        Placeholder stylePlaceholder = new MinLengthStyleBuilder().create(context, fooPlaceholder, "5,center");
        FormatOutputRenderer renderer = new FormatOutputRenderer(stylePlaceholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo(" foo ");
    }

    /**
     * Verifies that the configuration value must not be {@code null}.
     */
    @Test
    void creationWithMissingMinLength() {
        Throwable throwable = catchThrowable(() -> new MinLengthStyleBuilder().create(context, fooPlaceholder, null));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).containsIgnoringCase("minimum length");
    }

    /**
     * Verifies that a configuration value with an illegal minimum length is rejected.
     */
    @Test
    void creationWithInvalidMinLength() {
        assertThatCode(() -> new MinLengthStyleBuilder().create(context, fooPlaceholder, "bar"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("bar");
    }

    /**
     * Verifies that a configuration value with an illegal position is rejected.
     */
    @Test
    void creationWithInvalidPosition() {
        assertThatCode(() -> new MinLengthStyleBuilder().create(context, fooPlaceholder, "5,bar"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("bar");
    }

}
