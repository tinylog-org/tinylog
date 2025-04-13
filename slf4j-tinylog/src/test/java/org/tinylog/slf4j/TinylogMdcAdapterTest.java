package org.tinylog.slf4j;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.context.ContextStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TinylogMdcAdapterTest {

    private ContextStorage storage;
    private TinylogMdcAdapter mdcAdapter;

    /**
     * Initializes context storage and MDC adapter.
     */
    @BeforeEach
    void init() {
        storage = mock(ContextStorage.class);
        mdcAdapter = new TinylogMdcAdapter(storage);
    }

    /**
     * Verifies that a context value can be stored.
     */
    @Test
    void put() {
        mdcAdapter.put("foo", "bar");
        verify(storage).put("foo", "bar");
    }

    /**
     * Verifies that a context value can be received.
     */
    @Test
    void get() {
        when(storage.get("foo")).thenReturn("bar");
        assertThat(mdcAdapter.get("foo")).isEqualTo("bar");
    }

    /**
     * Verifies that a context value can be removed.
     */
    @Test
    void remove() {
        mdcAdapter.remove("foo");
        verify(storage).remove("foo");
    }

    /**
     * Verifies that all context values can be cleared.
     */
    @Test
    void clear() {
        mdcAdapter.clear();
        verify(storage).clear();
    }

    /**
     * Verifies that all stored context values can be received.
     */
    @Test
    void getCopyOfContextMap() {
        when(storage.getMapping()).thenReturn(Collections.singletonMap("foo", "42"));
        assertThat(mdcAdapter.getCopyOfContextMap()).isEqualTo(Collections.singletonMap("foo", "42"));
    }

    /**
     * Verifies that the stored context values can be replaced.
     */
    @Test
    void setContextMap() {
        mdcAdapter.setContextMap(Collections.singletonMap("foo", "bar"));
        verify(storage).replace(Collections.singletonMap("foo", "bar"));
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
