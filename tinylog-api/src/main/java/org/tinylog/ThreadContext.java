package org.tinylog;

import java.util.Map;

import org.tinylog.core.Tinylog;
import org.tinylog.core.context.ContextStorage;

/**
 * Thread context stores additional thread-based context values for issued log entries. A stored context value is added
 * to all log entries issued by the same thread where the context value was set. In web applications for example, the IP
 * address or the user name are typical context values to store. The output of context values can be configured via
 * format patterns and writers.
 *
 * <p>
 *     Depending on the actual logging back-end, child threads can inherit the thread-based context value of its
 *     parent thread.
 * </p>
 *
 * <p>
 *     Stored context values should be cleared before returning a thread to a thread pool. This can be either done by
 *     calling {@link #clear()} after the actual code execution or by wrapping the actual code itself with the methods
 *     {@link #withIndependentContext(Runnable)} or {@link #withEmptyContext(Runnable)}.
 * </p>
 */
public final class ThreadContext {

	private static final ContextStorage storage = Tinylog.getFramework().getLoggingBackend().getContextStorage();

	/** */
	private ThreadContext() {
	}

	/**
	 * Retrieves a copy of all stored context values.
	 *
	 * <p>
	 *     The returned map is not intended to be modified. Modification methods of the returned map can throw runtime
	 *     exceptions.
	 * </p>
	 *
	 * @return Copy of all stored context values
	 */
	public static Map<String, String> getMapping() {
		return storage.getMapping();
	}

	/**
	 * Retrieves the stored context value for a given key.
	 *
	 * @param key The key of the requested context value
	 * @return The stored value for the passed key, or {@code null} if no value is stored for the passed key
	 */
	public static String get(String key) {
		return storage.get(key);
	}

	/**
	 * Stores a new context value for a given key. If a value is already stored for the passed key, the existent value
	 * is overwritten.
	 *
	 * <p>
	 *     If the passed value is neither {@code null} nor a string, it will be converted to a string by calling its
	 *     {@code toString()} method.
	 * </p>
	 *
	 * @param key The key with which the context value is to be associated
	 * @param value The context value to store
	 */
	public static void put(String key, Object value) {
		storage.put(key, value == null ? null : value.toString());
	}

	/**
	 * Removes the stored context value for a given key.
	 *
	 * <p>
	 *     If no value is associated with the passed key, this is a no-op. No exception will be thrown.
	 * </p>
	 *
	 * @param key The key whose associated context value is to be removed
	 */
	public static void remove(String key) {
		storage.remove(key);
	}

	/**
	 * Removes all stored context values. Afterwards, the thread context will be empty.
	 */
	public static void clear() {
		storage.clear();
	}

	/**
	 * Executes the passed command with the actual thread context. After the command execution, all thread context
	 * modification will be undone and the origin thread context will be restored.
	 *
	 * @param command The code to execute
	 */
	public static void withIndependentContext(Runnable command) {
		Map<String, String> mapping = storage.getMapping();
		try {
			command.run();
		} finally {
			storage.replace(mapping);
		}
	}

	/**
	 * Executes the passed command with an empty thread context. After the command execution, all thread context
	 * modification will be undone and the origin thread context will be restored.
	 *
	 * @param command The code to execute
	 */
	public static void withEmptyContext(Runnable command) {
		withIndependentContext(() -> {
			storage.clear();
			command.run();
		});
	}

}
