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
class ThreadIdPlaceholderBuilderTest {

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
            assertThat(builder).isInstanceOf(ThreadIdPlaceholderBuilder.class);
            assertThat(builder.getName()).isEqualTo("thread-id");
        });
    }

    /**
     * Verifies that the builder can create an instance of {@link ThreadIdPlaceholder} without having a configuration
     * value.
     */
    @Test
    void creationWithoutConfigurationValue() {
        ThreadIdPlaceholderBuilder builder = new ThreadIdPlaceholderBuilder();
        assertThat(builder.create(context, null)).isInstanceOf(ThreadIdPlaceholder.class);
    }

    /**
     * Verifies that the builder can create an instance of {@link ThreadIdPlaceholder} when having an unexpected
     * configuration value.
     */
    @Test
    void creationWithConfigurationValue() {
        ThreadIdPlaceholderBuilder builder = new ThreadIdPlaceholderBuilder();
        assertThat(builder.create(context, "foo")).isInstanceOf(ThreadIdPlaceholder.class);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });
    }

}
