package org.tinylog.core.context;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NopContextStorageTest {

	/**
	 * Verifies that {@link NopContextStorage#put(String, String)} does not modify the storage.
	 */
	@Test
	void putDoesNothing() {
		NopContextStorage storage = new NopContextStorage();

		storage.put("foo", "42");

		assertThat(storage.get("foo")).isNull();
		assertThat(storage.getMapping()).isEmpty();
	}

	/**
	 * Verifies that {@link NopContextStorage#replace(Map)} does not modify the storage.
	 */
	@Test
	void replaceDoesNothing() {
		NopContextStorage storage = new NopContextStorage();

		storage.replace(Collections.singletonMap("foo", "42"));

		assertThat(storage.get("foo")).isNull();
		assertThat(storage.getMapping()).isEmpty();
	}

	/**
	 * Verifies that {@link NopContextStorage#remove(String)} can be called without throwing any exception.
	 */
	@Test
	void removeIsCallable() {
		new NopContextStorage().remove("foo");
	}

	/**
	 * Verifies that {@link NopContextStorage#clear()} can be called without throwing any exception.
	 */
	@Test
	void clearIsCallable() {
		new NopContextStorage().clear();
	}

}
