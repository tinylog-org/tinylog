package org.tinylog.core.variable;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentVariableResolverTest {

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
        assertThat(resolver.resolve("PATH")).isNotNull().isEqualTo(System.getenv("PATH"));
    }

    /**
     * Verifies that {@code null} is returned for a non-existent environment variable.
     */
    @Test
    void resolveMissingVariable() {
        EnvironmentVariableResolver resolver = new EnvironmentVariableResolver();
        assertThat(resolver.resolve("INVALID_NON_EXISTING_VARIABLE")).isNull();
    }

    /**
     * Verifies that the resolver is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(VariableResolver.class))
            .anyMatch(loader -> loader instanceof EnvironmentVariableResolver);
    }

}
