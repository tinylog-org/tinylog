package org.tinylog.slf4j;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.impl.context.ThreadLocalContextStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class TinylogMdcAdapterTest {

    private ContextStorage contextStorage;
    private TinylogMdcAdapter mdcAdapter;

    /**
     * Initializes context storage and MDC adapter.
     */
    @BeforeEach
    void init() {
        contextStorage = new ThreadLocalContextStorage();
        mdcAdapter = new TinylogMdcAdapter(contextStorage);
    }

    /**
     * Verifies that a context value can be stored.
     */
    @Test
    void put() {
        mdcAdapter.put("foo", "bar");
        assertThat(contextStorage.get("foo")).isEqualTo("bar");
    }

    /**
     * Verifies that a context value can be received.
     */
    @Test
    void get() {
        contextStorage.put("foo", "bar");
        assertThat(mdcAdapter.get("foo")).isEqualTo("bar");
    }

    /**
     * Verifies that a context value can be removed.
     */
    @Test
    void remove() {
        contextStorage.put("foo", "bar");
        mdcAdapter.remove("foo");
        assertThat(mdcAdapter.get("foo")).isNull();
    }

    /**
     * Verifies that all context values can be cleared.
     */
    @Test
    void clear() {
        contextStorage.put("foo", "bar");
        mdcAdapter.clear();
        assertThat(mdcAdapter.get("foo")).isNull();
    }

    /**
     * Verifies that all stored context values can be received.
     */
    @Test
    void getCopyOfContextMap() {
        contextStorage.put("foo", "bar");
        assertThat(mdcAdapter.getCopyOfContextMap()).containsExactly(entry("foo", "bar"));
    }

    /**
     * Verifies that the stored context values can be replaced.
     */
    @Test
    void setContextMap() {
        mdcAdapter.setContextMap(Collections.singletonMap("foo", "bar"));
        assertThat(mdcAdapter.getCopyOfContextMap()).containsExactly(entry("foo", "bar"));
    }

    /**
     * Verifies the stored deque values can be consumed one by one.
     */
    @Test
    void popByKey() {
        mdcAdapter.pushByKey("foo", "Alice");
        mdcAdapter.pushByKey("foo", "Bob");

        assertThat(mdcAdapter.popByKey("foo")).isEqualTo("Bob");
        assertThat(mdcAdapter.popByKey("foo")).isEqualTo("Alice");
    }

    /**
     * Verifies that the stored deque values can be received as a copy.
     */
    @Test
    void getCopyOfDequeByKey() {
        mdcAdapter.pushByKey("foo", "Alice");
        mdcAdapter.pushByKey("foo", "Bob");

        assertThat(mdcAdapter.getCopyOfDequeByKey("foo")).containsExactly("Bob", "Alice");
    }

    /**
     * Verifies that all stored deque values can be cleared.
     */
    @Test
    void clearDequeByKey() {
        mdcAdapter.pushByKey("foo", "bar");
        mdcAdapter.clearDequeByKey("foo");

        assertThat(mdcAdapter.getCopyOfDequeByKey("foo")).isEmpty();
    }

}
