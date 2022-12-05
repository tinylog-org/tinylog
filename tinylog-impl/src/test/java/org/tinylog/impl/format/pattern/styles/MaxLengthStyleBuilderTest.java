package org.tinylog.impl.format.pattern.styles;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.placeholders.ClassPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.PackagePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.format.pattern.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries
class MaxLengthStyleBuilderTest {

    @Inject
    private LoggingContext context;

    /**
     * Verifies that a max length style can be created for a {@link StaticTextPlaceholder} with maximum length passed as
     * configuration value.
     */
    @Test
    void creationForText() {
        Placeholder textPlaceholder = new StaticTextPlaceholder("foo");
        Placeholder stylePlaceholder = new MaxLengthStyleBuilder().create(context, textPlaceholder, "2");

        FormatOutputRenderer renderer = new FormatOutputRenderer(stylePlaceholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("fo");
    }

    /**
     * Verifies that a max length style can be created for a {@link ClassPlaceholder} with maximum length passed as
     * configuration value.
     */
    @Test
    void creationForClass() {
        Placeholder classPlaceholder = new ClassPlaceholder();
        Placeholder stylePlaceholder = new MaxLengthStyleBuilder().create(context, classPlaceholder, "11");

        FormatOutputRenderer renderer = new FormatOutputRenderer(stylePlaceholder);
        LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("o.f.MyClass");
    }

    /**
     * Verifies that a max length style can be created for a {@link PackagePlaceholder} with maximum length passed as
     * configuration value.
     */
    @Test
    void creationForPackage() {
        Placeholder packagePlaceholder = new PackagePlaceholder();
        Placeholder stylePlaceholder = new MaxLengthStyleBuilder().create(context, packagePlaceholder, "5");

        FormatOutputRenderer renderer = new FormatOutputRenderer(stylePlaceholder);
        LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("o.foo");
    }

    /**
     * Verifies that the configuration value must not be {@code null}.
     */
    @Test
    void creationWithMissingMaxLength() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        Throwable throwable = catchThrowable(() -> new MaxLengthStyleBuilder().create(context, placeholder, null));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).containsIgnoringCase("maximum length");
    }

    /**
     * Verifies that a configuration value with an illegal maximum length is rejected.
     */
    @Test
    void creationWithInvalidMaxLength() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        assertThatCode(() -> new MaxLengthStyleBuilder().create(context, placeholder, "bar"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("bar");
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(StyleBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(MaxLengthStyleBuilder.class);
            assertThat(builder.getName()).isEqualTo("max-length");
        });
    }

}
