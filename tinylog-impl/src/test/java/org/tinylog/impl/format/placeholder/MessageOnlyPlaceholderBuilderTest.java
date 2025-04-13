package org.tinylog.impl.format.placeholder;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class MessageOnlyPlaceholderBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    @Inject
    private Log log;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(MessageOnlyPlaceholderBuilder.class);
            assertThat(builder.getName()).isEqualTo("message-only");
        });
    }

    /**
     * Verifies that the builder can create an instance of {@link MessageOnlyPlaceholder} without having a configuration
     * value.
     */
    @Test
    void creationWithoutConfigurationValue() {
        MessageOnlyPlaceholderBuilder builder = new MessageOnlyPlaceholderBuilder();
        assertThat(builder.create(context, null)).isInstanceOf(MessageOnlyPlaceholder.class);
    }

    /**
     * Verifies that the builder can create an instance of {@link MessageOnlyPlaceholder} when having an unexpected
     * configuration value.
     */
    @Test
    void creationWithConfigurationValue() {
        MessageOnlyPlaceholderBuilder builder = new MessageOnlyPlaceholderBuilder();
        assertThat(builder.create(context, "foo")).isInstanceOf(MessageOnlyPlaceholder.class);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });
    }

}
