package org.tinylog.core.variable;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VariableResolverTest {

    /**
     * Verifies that all variable resolvers are loaded correctly.
     */
    @Test
    void load() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assertThat(VariableResolver.load(classLoader))
            .isNotEmpty()
            .hasOnlyElementsOfType(VariableResolver.class)
            .doesNotContainNull()
            .doesNotHaveDuplicates();
    }

}
