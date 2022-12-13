package org.tinylog.impl.format.pattern.placeholders;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class FilePlaceholderBuilderTest {

    @Inject
    private LoggingContext context;

    @Inject
    private Log log;

    /**
     * Verifies that the builder can create an instance of {@link FilePlaceholder} without having a configuration
     * value.
     */
    @Test
    void creationWithoutConfigurationValue() {
        FilePlaceholderBuilder builder = new FilePlaceholderBuilder();
        assertThat(builder.create(context, null)).isInstanceOf(FilePlaceholder.class);
        assertThat(log.consume()).isEmpty();
    }

    /**
     * Verifies that the builder can create an instance of {@link FilePlaceholder} when having an unexpected
     * configuration value.
     */
    @Test
    void creationWithConfigurationValue() {
        FilePlaceholderBuilder builder = new FilePlaceholderBuilder();
        assertThat(builder.create(context, "foo")).isInstanceOf(FilePlaceholder.class);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getMessage()).contains("foo");
        });
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(FilePlaceholderBuilder.class);
            assertThat(builder.getName()).isEqualTo("file");
        });
    }

}