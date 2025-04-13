package org.tinylog.core.variable;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class EnvironmentVariableResolverTest {

    @Inject
    private InternalLogger logger;

    /**
     * Verifies that the resolver is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(VariableResolver.class))
            .anyMatch(loader -> loader instanceof EnvironmentVariableResolver);
    }

    /**
     * Verifies that the name is "environment variable".
     */
    @Test
    void name() {
        EnvironmentVariableResolver resolver = new EnvironmentVariableResolver();
        assertThat(resolver.getName()).isEqualTo("environment variable");
    }

    /**
     * Verifies that the prefix character is "$".
     */
    @Test
    void prefix() {
        EnvironmentVariableResolver resolver = new EnvironmentVariableResolver();
        assertThat(resolver.getPrefix()).isEqualTo("$");
    }

    /**
     * Verifies that the value of an existing environment variable can be resolved.
     */
    @Test
    void resolveExistingVariable() {
        EnvironmentVariableResolver resolver = new EnvironmentVariableResolver();
        assertThat(resolver.resolve("PATH", logger)).isNotNull().isEqualTo(System.getenv("PATH"));
    }

    /**
     * Verifies that {@code null} is returned for a non-existent environment variable.
     */
    @Test
    void resolveMissingVariable() {
        EnvironmentVariableResolver resolver = new EnvironmentVariableResolver();
        assertThat(resolver.resolve("INVALID_NON_EXISTING_VARIABLE", logger)).isNull();
    }

}
