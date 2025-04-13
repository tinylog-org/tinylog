package org.tinylog.impl.backend;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class TinylogContextStorageTest {

    /**
     * Verifies that a new context storage has an empty mapping.
     */
    @Test
    public void empty() {
        TinylogContextStorage storage = new TinylogContextStorage();
        assertThat(storage.getMapping()).isEmpty();
    }

    /**
     * Verifies that a new value can be added.
     */
    @Test
    public void addNewValue() {
        TinylogContextStorage storage = new TinylogContextStorage();
        storage.put("pi", "3.14");

        assertThat(storage.get("pi")).isEqualTo("3.14");
        assertThat(storage.getMapping()).containsOnly(entry("pi", "3.14"));
    }

    /**
     * Verifies that {@code null} values are not stored.
     */
    @Test
    public void addNullValue() {
        TinylogContextStorage storage = new TinylogContextStorage();
        storage.put("test", null);
        assertThat(storage.getMapping()).isEmpty();
    }

    /**
     * Verifies that an existing value can be overridden.
     */
    @Test
    public void overrideExistingValue() {
        TinylogContextStorage storage = new TinylogContextStorage();

        storage.put("test", "first");
        assertThat(storage.getMapping()).containsOnly(entry("test", "first"));

        storage.put("test", "second");
        assertThat(storage.getMapping()).containsOnly(entry("test", "second"));
    }

    /**
     * Verifies that existing values can be removed.
     */
    @Test
    public void remove() {
        TinylogContextStorage storage = new TinylogContextStorage();

        storage.put("first", "Alice");
        storage.put("second", "Bob");
        assertThat(storage.getMapping()).containsOnly(entry("first", "Alice"), entry("second", "Bob"));

        storage.remove("first");
        assertThat(storage.getMapping()).containsOnly(entry("second", "Bob"));

        storage.remove("second");
        assertThat(storage.getMapping()).isEmpty();
    }

    /**
     * Verifies that the entire mapping can be replaced with another mapping.
     */
    @Test
    public void replace() {
        TinylogContextStorage storage = new TinylogContextStorage();

        storage.put("first", "Alice");
        assertThat(storage.getMapping()).containsOnly(entry("first", "Alice"));

        storage.replace(Map.of("second", "Bob"));
        assertThat(storage.getMapping()).containsOnly(entry("second", "Bob"));
    }

    /**
     * Verifies that the context storage can be cleared.
     */
    @Test
    public void clear() {
        TinylogContextStorage storage = new TinylogContextStorage();

        storage.put("first", "Alice");
        storage.put("second", "Bob");
        assertThat(storage.getMapping()).containsOnly(entry("first", "Alice"), entry("second", "Bob"));

        storage.clear();
        assertThat(storage.getMapping()).isEmpty();
    }

    /**
     * Verifies that a child thread inherits values from its parent thread but not the way around.
     */
    @Test
    public void inheritance() throws InterruptedException {
        TinylogContextStorage storage = new TinylogContextStorage();
        storage.put("first", "Alice");

        Thread thread = new Thread(() -> {
            storage.put("second", "Bob");
            assertThat(storage.getMapping()).containsOnly(entry("first", "Alice"), entry("second", "Bob"));
        });
        thread.start();
        thread.join();

        assertThat(storage.getMapping()).containsOnly(entry("first", "Alice"));
    }

}
