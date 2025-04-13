package org.tinylog.impl.format.style;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.placeholder.ClassPlaceholder;
import org.tinylog.impl.format.placeholder.MessageOnlyPlaceholder;
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
class LengthStyleBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(StyleBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(LengthStyleBuilder.class);
            assertThat(builder.getName()).isEqualTo("length");
        });
    }

    /**
     * Verifies that a length style can be created for a placeholder with plain text output.
     */
    @Test
    void creationForPText() {
        Placeholder placeholder = new MessageOnlyPlaceholder(configuration);
        Placeholder styled = new LengthStyleBuilder().create(context, placeholder, "10");
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
        assertThat(renderer.render(logEntry)).isEqualTo("Hello W...");

        logEntry = new LogEntryBuilder().message("Hi World!").create();
        assertThat(renderer.render(logEntry)).isEqualTo("Hi World! ");
    }

    /**
     * Verifies that a length style can be created for a {@link ClassPlaceholder} with length passed as configuration
     * value.
     */
    @Test
    void creationForClass() {
        Placeholder placeholder = new ClassPlaceholder();
        Placeholder styled = new LengthStyleBuilder().create(context, placeholder, "12");
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("o.f.MyClass ");
    }

    /**
     * Verifies that a length style can be created for a {@link PackagePlaceholder} with length passed as configuration
     * value.
     */
    @Test
    void creationForPackage() {
        Placeholder placeholder = new PackagePlaceholder();
        Placeholder styled = new LengthStyleBuilder().create(context, placeholder, "4");
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("o.f ");
    }

    /**
     * Verifies that a custom position can be passed via configuration value.
     */
    @Test
    void creationWithCustomPosition() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        Placeholder styled = new LengthStyleBuilder().create(context, placeholder, "5,right");
        FormatOutputRenderer renderer = new FormatOutputRenderer(styled);

        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("  foo");
    }

    /**
     * Verifies that the configuration value must not be {@code null}.
     */
    @Test
    void creationWithMissingLength() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        Throwable throwable = catchThrowable(() -> new LengthStyleBuilder().create(context, placeholder, null));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).containsIgnoringCase("length");
    }

    /**
     * Verifies that a configuration value with an illegal length is rejected.
     */
    @Test
    void creationWithInvalidLength() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        assertThatCode(() -> new LengthStyleBuilder().create(context, placeholder, "bar"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("bar");
    }

    /**
     * Verifies that a configuration value with an illegal position is rejected.
     */
    @Test
    void creationWithInvalidPosition() {
        Placeholder placeholder = new StaticTextPlaceholder("foo");
        assertThatCode(() -> new LengthStyleBuilder().create(context, placeholder, "5,bar"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("bar");
    }

}
