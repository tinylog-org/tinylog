package org.tinylog.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder with a fluent API for tinylog configurations.
 *
 * <p>
 *     A created configuration is applied only if the method {@link ConfigurationBuilder#activate()} is called.
 * </p>
 *
 * @see Tinylog#getConfigurationBuilder(boolean)
 */
public class ConfigurationBuilder {

    private final Framework framework;
    private final Map<String, String> properties;

    /**
     * @param framework The underlying framework instance
     * @param properties The initial configuration
     */
    ConfigurationBuilder(Framework framework, Map<String, String> properties) {
        this.framework = framework;
        this.properties = new LinkedHashMap<>(properties);
    }

    /**
     * Gets the value of a specific key.
     *
     * @param key The key to search for
     * @return The found value or {@code null} if the key does not exist
     */
    public String get(String key) {
        return properties.get(key);
    }

    /**
     * Removes the value of a specific key if the key is present.
     *
     * <p>
     *     If the key is not present, nothing will happen.
     * </p>
     *
     * @param key The key to search for
     * @return The same configuration builder instance
     */
    public ConfigurationBuilder remove(String key) {
        properties.remove(key);
        return this;
    }

    /**
     * Sets a value for a given key. If another value is already stored under the passed key, the old value will be
     * overwritten with the new value.
     *
     * @param key The key under which the value should to be stored
     * @param value The value to store
     * @return The same configuration builder instance
     */
    public ConfigurationBuilder set(String key, String value) {
        properties.put(key, value);
        return this;
    }

    /**
     * Applies the configuration. The current configuration will be replaced.
     *
     * <p>
     *     New configurations can be activated as needed before issuing any log entries. As soon as the first log entry
     *     is issued, the current configuration becomes frozen and can no longer be replaced by others.
     * </p>
     *
     * @throws UnsupportedOperationException The current configuration has already been applied and cannot be replaced
     */
    public void activate() {
        Configuration configuration = new Configuration(properties);
        framework.setConfiguration(configuration);
    }

}
