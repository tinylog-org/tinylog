package org.tinylog.core.context;

import java.util.Map;

/**
 * Storage for thread-based context values.
 */
public interface ContextStorage {

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
    Map<String, String> getMapping();

    /**
     * Retrieves the stored context value for a given key.
     *
     * @param key The key of the requested context value
     * @return The stored value for the passed key, or {@code null} if no value is stored for the passed key
     */
    String get(String key);

    /**
     * Stores a new context value for a given key. If a value is already stored for the given key, the existing value
     * is overwritten.
     *
     * @param key The key with which the context value is to be associated
     * @param value The context value to store
     */
    void put(String key, String value);

    /**
     * Replaces the current thread-based context values with the passed values.
     *
     * <p>
     *     All currently stored values are removed, even if a associated key is not present in the passed mapping.
     * </p>
     *
     * @param mapping New context values
     */
    void replace(Map<String, String> mapping);

    /**
     * Removes the stored context value for a given key.
     *
     * <p>
     *     If no value is associated with the passed key, this is a no-op. No exception will be thrown.
     * </p>
     *
     * @param key The key whose associated context value to remove
     */
    void remove(String key);

    /**
     * Removes all stored context values. This storage will be empty afterwards.
     */
    void clear();

}
