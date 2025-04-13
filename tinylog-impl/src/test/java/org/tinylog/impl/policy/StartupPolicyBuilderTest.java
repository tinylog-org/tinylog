package org.tinylog.impl.policy;

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
class StartupPolicyBuilderTest {

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
        assertThat(ServiceLoader.load(PolicyBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(StartupPolicyBuilder.class);
            assertThat(builder.getName()).isEqualTo("startup");
        });
    }

    /**
     * Verifies that the builder can create an instance of {@link StartupPolicy} without having a configuration value.
     */
    @Test
    void creationWithoutConfigurationValue() {
        StartupPolicyBuilder builder = new StartupPolicyBuilder();
        assertThat(builder.create(context, null)).isInstanceOf(StartupPolicy.class);
    }

    /**
     * Verifies that the builder can create an instance of {@link StartupPolicy} when having an unexpected configuration
     * value.
     */
    @Test
    void creationWithConfigurationValue() {
        StartupPolicyBuilder builder = new StartupPolicyBuilder();
        assertThat(builder.create(context, "foo")).isInstanceOf(StartupPolicy.class);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });
    }

}
