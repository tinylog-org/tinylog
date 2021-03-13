package org.tinylog.impl;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ThreadLocalContextStorageTest {

	/**
	 * Verifies that the context storage has an empty mapping by default.
	 */
	@Test
	void receiveEmptyMapByDefault() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		assertThat(storage.getMapping()).isEmpty();
	}

	/**
	 * Verifies that a child thread inherits the mapping from it's parent thread.
	 */
	@Test
	void receiveMappingInChildThread() throws Throwable {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "42");

		executeInChildThread(() -> assertThat(storage.getMapping()).containsExactly(entry("foo", "42")));
	}

	/**
	 * Verifies that mapping changes from a child thread are not applied to it's parent thread.
	 */
	@Test
	void updateMappingInChildThread() throws Throwable {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "1");

		executeInChildThread(() -> {
			storage.put("foo", "2");
			assertThat(storage.getMapping()).containsExactly(entry("foo", "2"));
		});

		assertThat(storage.getMapping()).containsExactly(entry("foo", "1"));
	}

	/**
	 * Verifies that a stored value can be received.
	 */
	@Test
	void getExistingValue() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "42");

		assertThat(storage.get("foo")).isEqualTo("42");
	}

	/**
	 * Verifies that {@code null} is returned for non-existent keys.
	 */
	@Test
	void getNonExistentValue() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "42");

		assertThat(storage.get("bar")).isNull();
	}

	/**
	 * Verifies that new entries can be added.
	 */
	@Test
	void addEntry() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "1");
		storage.put("bar", "2");

		assertThat(storage.getMapping())
			.containsExactlyInAnyOrderEntriesOf(ImmutableMap.of("foo", "1", "bar", "2"));
	}

	/**
	 * Verifies that already existing entries can be overwritten.
	 */
	@Test
	void overwriteEntryWithAnotherValue() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "1");
		storage.put("foo", "2");

		assertThat(storage.getMapping()).containsExactly(entry("foo", "2"));
	}

	/**
	 * Verifies that adding {@code null} as value for an entry removes this entry.
	 */
	@Test
	void overwriteEntryWithNull() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "1");
		storage.put("foo", null);

		assertThat(storage.getMapping()).isEmpty();
	}

	/**
	 * Verifies that the existing mapping can be replaced entirely by another mapping.
	 */
	@Test
	void replaceMapping() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "1");
		storage.replace(Collections.singletonMap("bar", "2"));

		assertThat(storage.getMapping()).containsExactly(entry("bar", "2"));
	}

	/**
	 * Verifies that an existing entry can be removed.
	 */
	@Test
	void removeExistingEntry() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "1");
		storage.put("bar", "2");
		storage.remove("foo");

		assertThat(storage.getMapping()).containsExactly(entry("bar", "2"));
	}

	/**
	 * Verifies that the attempt to remove an non-existent entry is silently ignored.
	 */
	@Test
	void removeNonExistentEntry() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "42");
		storage.remove("bar");

		assertThat(storage.getMapping()).containsExactly(entry("foo", "42"));
	}

	/**
	 * Verifies that all existing entries can be removed.
	 */
	@Test
	void clearMapping() {
		ThreadLocalContextStorage storage = new ThreadLocalContextStorage();
		storage.put("foo", "1");
		storage.put("bar", "2");
		storage.clear();

		assertThat(storage.getMapping()).isEmpty();
	}

	/**
	 * Executes the passed runnable in a separate child thread.
	 *
	 * <p>
	 *     The current thread is blocked until the child thread terminates.
	 * </p>
	 *
	 * @param runnable The runnable to execute in a child thread
	 * @throws Throwable Uncaught exception from the child thread
	 */
	private static void executeInChildThread(Runnable runnable) throws Throwable {
		UncaughtExceptionRethrowHandler handler = new UncaughtExceptionRethrowHandler();

		Thread thread = new Thread(runnable);
		thread.setUncaughtExceptionHandler(handler);
		thread.start();
		thread.join();

		handler.rethrow();
	}

	/**
	 * An {@link Thread.UncaughtExceptionHandler} implementation that stores all uncaught exceptions and can rethrow
	 * them.
	 */
	private static final class UncaughtExceptionRethrowHandler implements Thread.UncaughtExceptionHandler {

		private Throwable exception;

		@Override
		public void uncaughtException(Thread thread, Throwable exception) {
			if (this.exception == null) {
				this.exception = exception;
			} else {
				this.exception.addSuppressed(exception);
			}
		}

		/**
		 * Throws the stored uncaught exceptions if there are any.
		 *
		 * @throws Throwable Stored uncaught exception
		 */
		public void rethrow() throws Throwable {
			if (exception != null) {
				throw exception;
			}
		}

	}

}
