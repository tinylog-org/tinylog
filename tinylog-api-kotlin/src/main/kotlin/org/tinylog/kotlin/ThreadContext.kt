package org.tinylog.kotlin

import org.tinylog.core.Tinylog

/**
 * Thread context stores additional thread-based context values for issued log entries. A stored context value is added
 * to all log entries issued by the same thread where the context value was set. In web applications for example, the IP
 * address or the username are typical context values to store. The output of context values can be configured via
 * format patterns and writers.
 *
 * Depending on the actual logging back-end, child threads can inherit the thread-based context value of its
 * parent thread.
 *
 * Stored context values should be cleared before returning a thread to a thread pool. This can be either done by
 * calling [clear] after the actual code execution or by wrapping the actual code itself with the methods
 * [withIndependentContext] or [withEmptyContext].
 */
object ThreadContext {
    private val storage = Tinylog.getFramework().loggingBackend.contextStorage

    /**
     * Retrieves a copy of all stored context values.
     *
     * The returned map is not intended to be modified. Modification methods of the returned map can throw runtime
     * exceptions.
     */
    val mapping: Map<String, String>
        get() = storage.mapping

    /**
     * Retrieves the stored context value for a given key.
     *
     * @param key The key of the requested context value
     * @return The stored value for the passed key, or `null` if no value is stored for the passed key
     */
    operator fun get(key: String): String? {
        return storage[key]
    }

    /**
     * Stores a new context value for a given key. If a value is already stored for the passed key, the existent value
     * is overwritten.
     *
     * If the passed value is neither `null` nor a string, it will be converted to a string by calling its
     * `toString()` method.
     *
     * @param key The key with which the context value is to be associated
     * @param value The context value to store
     */
    fun put(
        key: String,
        value: Any?,
    ) {
        storage.put(key, value?.toString())
    }

    /**
     * Removes the stored context value for a given key.
     *
     * If no value is associated with the passed key, this is a no-op. No exception will be thrown.
     *
     * @param key The key whose associated context value is to be removed
     */
    fun remove(key: String) {
        storage.remove(key)
    }

    /**
     * Removes all stored context values. Afterwards, the thread context will be empty.
     */
    fun clear() {
        storage.clear()
    }

    /**
     * Executes the passed command with the actual thread context. After the command execution, all thread context
     * modification will be undone and the origin thread context will be restored.
     *
     * @param command The code to execute
     */
    fun withIndependentContext(command: () -> Unit) {
        val mapping = storage.mapping
        try {
            command()
        } finally {
            storage.replace(mapping)
        }
    }

    /**
     * Executes the passed command with an empty thread context. After the command execution, all thread context
     * modification will be undone and the origin thread context will be restored.
     *
     * @param command The code to execute
     */
    fun withEmptyContext(command: () -> Unit) {
        withIndependentContext {
            storage.clear()
            command()
        }
    }
}
