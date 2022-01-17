package org.tinylog;

import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.tinylog.core.Framework;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.context.ContextStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ThreadContextTest {

	private static MockedStatic<Tinylog> tinylogMock;
	private static LoggingBackend backend;
	private static ContextStorage storage;

	/**
	 * Initializes all mocks.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@BeforeAll
	static void create() {
		tinylogMock = mockStatic(Tinylog.class);
		backend = mock(LoggingBackend.class);
		storage = mock(ContextStorage.class);

		tinylogMock.when(Tinylog::getFramework).thenReturn(new Framework(false, false) {
			@Override
			public LoggingBackend getLoggingBackend() {
				return backend;
			}
		});

		when(backend.getContextStorage()).thenReturn(storage);
	}

	/**
	 * Restores the mocked tinylog class.
	 */
	@AfterAll
	static void dispose() {
		tinylogMock.close();
	}

	/**
	 * Resets the context storage.
	 */
	@AfterEach
	void reset() {
		Mockito.reset(storage);
	}

	/**
	 * Verifies that all stored kwy value pairs can be received.
	 */
	@Test
	void receiveMapping() {
		when(storage.getMapping()).thenReturn(Collections.singletonMap("foo", "42"));
		assertThat(ThreadContext.getMapping()).isEqualTo(Collections.singletonMap("foo", "42"));
	}

	/**
	 * Verifies that a value can be received by its associated key.
	 */
	@Test
	void receiveValue() {
		when(storage.get("foo")).thenReturn("42");
		assertThat(ThreadContext.get("foo")).isEqualTo("42");
	}

	/**
	 * Verifies that {@code null} can be stored as value.
	 */
	@Test
	void putNull() {
		ThreadContext.put("foo", null);
		verify(storage).put("foo", null);
	}

	/**
	 * Verifies that an integer will be stored as string.
	 */
	@Test
	void putInteger() {
		ThreadContext.put("foo", 42);
		verify(storage).put("foo", "42");
	}

	/**
	 * Verifies that a string value can be stored.
	 */
	@Test
	void putString() {
		ThreadContext.put("foo", "bar");
		verify(storage).put("foo", "bar");
	}

	/**
	 * Verifies that a value can be removed by its associated key.
	 */
	@Test
	void removeValue() {
		ThreadContext.remove("foo");
		verify(storage).remove("foo");
	}

	/**
	 * Verifies that all stored values can be removed.
	 */
	@Test
	void clearAllValues() {
		ThreadContext.clear();
		verify(storage).clear();
	}

	/**
	 * Verifies that the original mapping will be restored after executing code with an independent context.
	 */
	@Test
	void executeIndependentContext() {
		when(storage.getMapping()).thenReturn(Collections.singletonMap("foo", "42"));

		ThreadContext.withIndependentContext(() -> {
			when(storage.getMapping()).thenThrow(UnsupportedOperationException.class);

			verify(storage, never()).clear();
			verify(storage, never()).replace(anyMap());
		});

		verify(storage).replace(Collections.singletonMap("foo", "42"));
	}

	/**
	 * Verifies that the original mapping will be restored after executing code with an empty context.
	 */
	@Test
	void executionEmptyContext() {
		when(storage.getMapping()).thenReturn(Collections.singletonMap("foo", "42"));

		ThreadContext.withEmptyContext(() -> {
			when(storage.getMapping()).thenThrow(UnsupportedOperationException.class);

			verify(storage).clear();
			verify(storage, never()).replace(anyMap());
		});

		verify(storage).replace(Collections.singletonMap("foo", "42"));
	}

}
