package org.tinylog.impl.format.style;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.placeholder.ClassPlaceholder;
import org.tinylog.impl.format.placeholder.PackagePlaceholder;
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
class MaxLengthStyleBuilderTest {

    @Inject
    private TinylogContext context;

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

}
