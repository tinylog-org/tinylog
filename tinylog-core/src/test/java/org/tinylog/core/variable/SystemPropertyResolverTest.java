package org.tinylog.core.variable;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.RestoreSystemProperties;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
@RestoreSystemProperties
class SystemPropertyResolverTest {

    @Inject
    private InternalLogger logger;

    /**
     * Verifies that the resolver is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(VariableResolver.class))
            .anyMatch(loader -> loader instanceof SystemPropertyResolver);
    }

    /**
     * Verifies that the name is "system property".
     */
    @Test
    void name() {
        SystemPropertyResolver resolver = new SystemPropertyResolver();
        assertThat(resolver.getName()).isEqualTo("system property");
    }

    /**
     * Verifies that the prefix character is "#".
     */
    @Test
    void prefix() {
        SystemPropertyResolver resolver = new SystemPropertyResolver();
        assertThat(resolver.getPrefix()).isEqualTo("#");
    }

    /**
     * Verifies that the value of an existing system property can be resolved.
     */
    @Test
    void resolveExistingProperty() {
        System.setProperty("foo", "bar");

        SystemPropertyResolver resolver = new SystemPropertyResolver();
        assertThat(resolver.resolve("foo", logger)).isEqualTo("bar");
    }

    /**
     * Verifies that {@code null} is returned for a non-existent system property.
     */
    @Test
    void resolveMissingProperty() {
        System.clearProperty("foo");

        SystemPropertyResolver resolver = new SystemPropertyResolver();
        assertThat(resolver.resolve("foo", logger)).isNull();
    }

}
