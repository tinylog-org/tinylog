package org.tinylog.core.format.value;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValueFormatTest {

    /**
     * Verifies that all value formats are loaded correctly.
     */
    @Test
    void load() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assertThat(ValueFormat.load(classLoader))
            .isNotEmpty()
            .hasOnlyElementsOfType(ValueFormat.class)
            .doesNotContainNull()
            .doesNotHaveDuplicates();
    }

}
