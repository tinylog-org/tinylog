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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries
class MinLengthStyleBuilderTest {

    private final Placeholder fooPlaceholder = new StaticTextPlaceholder("foo");

    @Inject
    private LoggingContext context;

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
        assertThatCode(() -> new MinLengthStyleBuilder().create(context, fooPlaceholder, "boo"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("boo");
    }

    /**
     * Verifies that a configuration value with an illegal position is rejected.
     */
    @Test
    void creationWithInvalidPosition() {
        assertThatCode(() -> new MinLengthStyleBuilder().create(context, fooPlaceholder, "5,boo"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("boo");
    }

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

}
