package org.tinylog.slf4j.backend;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class Slf4jContextStorageTest {

    /**
     * Removes all entries from {@link MDC}.
     */
    @AfterEach
    void clean() {
        MDC.clear();
    }

    /**
     * Verifies that an empty mapping is returned as empty map.
     */
    @Test
    public void getEmptyMapping() {
        Slf4jContextStorage storage = new Slf4jContextStorage();
        assertThat(storage.getMapping()).isEmpty();
    }

    /**
     * Verifies that all entries from {@link MDC} are returned correctly.
     */
    @Test
    public void getFilledMapping() {
        MDC.setContextMap(Map.of("foo", "A", "bar", "B"));

        Slf4jContextStorage storage = new Slf4jContextStorage();
        assertThat(storage.getMapping()).containsOnly(entry("foo", "A"), entry("bar", "B"));
    }

    /**
     * Verifies that a single entry is returned correctly.
     */
    @Test
    public void getEntry() {
        MDC.setContextMap(Map.of("foo", "A", "bar", "B"));

        Slf4jContextStorage storage = new Slf4jContextStorage();
        assertThat(storage.get("foo")).isEqualTo("A");
    }

    /**
     * Verifies that a new entry can be added.
     */
    @Test
    public void putEntry() {
        new Slf4jContextStorage().put("foo", "A");
        assertThat(MDC.getCopyOfContextMap()).containsOnly(entry("foo", "A"));
    }

    /**
     * Verifies that the entire mapping can be replaced.
     */
    @Test
    public void replaceMapping() {
        MDC.setContextMap(Map.of("foo", "A", "bar", "B"));

        new Slf4jContextStorage().replace(Map.of("foo", "C", "baz", "D"));
        assertThat(MDC.getCopyOfContextMap()).containsOnly(entry("foo", "C"), entry("baz", "D"));
    }

    /**
     * Verifies that an entry can be removed.
     */
    @Test
    public void removeEntry() {
        MDC.setContextMap(Map.of("foo", "A", "bar", "B"));

        new Slf4jContextStorage().remove("foo");
        assertThat(MDC.getCopyOfContextMap()).containsOnly(entry("bar", "B"));
    }

    /**
     * Verifies that the entire mapping can be cleared.
     */
    @Test
    public void clearMapping() {
        MDC.setContextMap(Map.of("foo", "A", "bar", "B"));

        new Slf4jContextStorage().clear();
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

}
