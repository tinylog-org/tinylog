package org.tinylog.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TinylogTest {

    /**
     * Verifies that a {@link ConfigurationBuilder} can be provided.
     *
     * @param inherit Flag for inheriting the current configuration or starting with an empty configuration
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void configuration(boolean inherit) {
        assertThat(Tinylog.getConfigurationBuilder(inherit)).isNotNull();
    }

    /**
     * Verifies that always the same non-null {@link Framework} instance is provided.
     */
    @Test
    void framework() {
        Framework first = Tinylog.getFramework();
        assertThat(first).isNotNull();

        Framework second = Tinylog.getFramework();
        assertThat(first).isSameAs(second);
    }

}
