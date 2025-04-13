package org.tinylog.impl.backend;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class InheritableMapThreadLocalTest {

    /**
     * Verifies that the initial value is an empty map.
     */
    @Test
    void initialValue() {
        ThreadLocal<Map<Integer, String>> local = new InheritableMapThreadLocal<>();
        assertThat(local.get()).isEmpty();
    }

    /**
     * Verifies that the stored map can be replaced.
     */
    @Test
    void updateValue() {
        ThreadLocal<Map<Integer, String>> local = new InheritableMapThreadLocal<>();
        local.set(Map.of(42, "forty two"));
        assertThat(local.get()).containsExactly(entry(42, "forty two"));
    }

}
