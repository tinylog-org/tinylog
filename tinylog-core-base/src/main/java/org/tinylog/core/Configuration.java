package org.tinylog.core;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.tinylog.core.internal.InternalLogger;

/**
 * Configuration for tinylog.
 */
public class Configuration {

    private static final int MAX_LOCALE_ARGUMENTS = 3;

    private final Configuration parent;
    private final String prefix;
    private final Map<String, String> properties;

    /**
     * @param properties All keys and values to store
     */
    public Configuration(Map<String, String> properties) {
        this(null, null, properties);
    }

    /**
     * @param parent The parent configuration ({@code null} for root configurations)
     * @param prefix The prefix for resolving real keys for subset configurations ({@code null} for root configurations)
     * @param properties All keys and values to store
     */
    private Configuration(Configuration parent, String prefix, Map<String, String> properties) {
        this.parent = parent;
        this.prefix = prefix;
        this.properties = new LinkedHashMap<>(properties);
    }

    /**
     * Checks if a value is stored for the passed key.
     *
     * @param key The key to search for
     * @return {@code true} if a value is stored for the passed key, {@code false} otherwise
    */
    public boolean isPresent(String key) {
        return properties.containsKey(key);
    }

    /**
     * Gets the configured locale from property "locale". If the property is not set, {@link Locale#getDefault()} will
     * be returned instead.
     *
     * @return The configured locale or {@link Locale#getDefault()} if not set
     */
    public Locale getLocale() {
        String value = getValue("locale");
        if (value == null) {
            return parent == null ? Locale.getDefault() : parent.getLocale();
        } else {
            String[] tokens = value.split("_", MAX_LOCALE_ARGUMENTS);
            return new Locale(tokens[0], tokens.length > 1 ? tokens[1] : "", tokens.length > 2 ? tokens[2] : "");
        }
    }

    /**
     * Gets the configured zone ID from property "zone". If the property is not set, {@link ZoneOffset#systemDefault()}
     * will be returned instead.
     *
     * @return The configured zone ID or {@link ZoneOffset#systemDefault()} if not set
     */
    public ZoneId getZone() {
        String value = getValue("zone");

        if (value != null) {
            try {
                return ZoneId.of(value);
            } catch (DateTimeException ex) {
                InternalLogger.error(
                    ex,
                    "Invalid zone ID \"{}\" in property \"{}\"",
                    value,
                    resolveFullKey("zone")
                );
            }
        }

        return parent == null ? ZoneOffset.systemDefault() : parent.getZone();
    }

    /**
     * Searches for the value of a specific key.
     *
     * @param key The key to search for
     * @return The found value or {@code null} if the key does not exist
     */
    public String getValue(String key) {
        String value = properties.get(key);
        if (value == null) {
            return null;
        } else {
            return value.trim();
        }
    }

    /**
     * Searches for the value of a specific key.
     *
     * @param key The key to search for
     * @param defaultValue The default value to use if there is no value stored for the passed key
     * @return The found value or the passed default value if the key does not exist
     */
    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    /**
     * Searches for the values of a specific key.
     *
     * <p>
     *     The found string is split by commas.
     * </p>
     *
     * @param key Key to search
     * @return The found values or an empty list if the key does not exist
     */
    public List<String> getList(String key) {
        String value = properties.get(key);
        if (value == null) {
            return Collections.emptyList();
        } else {
            List<String> elements = new ArrayList<>();
            for (String element : value.split(",")) {
                String normalized = element.trim();
                if (!normalized.isEmpty()) {
                    elements.add(normalized);
                }
            }
            return elements;
        }
    }

    /**
     * Collects all root keys.
     *
     * <p>
     *     A root key is only the part of key until the first dot. For example, the root key for the property "foo.bar"
     *     is "foo". Properties that do not contain a dot are used unchanged as root key.
     * </p>
     *
     * @return Distinct collection of all root keys in insert order
     */
    public Collection<String> getRootKeys() {
        List<String> keys = new ArrayList<>();

        for (String key : properties.keySet()) {
            int index = key.indexOf('.');
            if (index >= 0) {
                key = key.substring(0, index);
            }

            if (!keys.contains(key)) {
                keys.add(key);
            }
        }

        return keys;
    }

    /**
     * Gets the keys of all properties that a present in this configuration.
     *
     * @return All keys in insert order
     */
    public Collection<String> getKeys() {
        return properties.keySet();
    }

    /**
     * Collects all properties that start with the passed prefix. The function adds implicitly a dot to the passed
     * prefix as separator character.
     *
     * <p>
     *     For example, the property "foo.bar=42" would be returned as "bar=42" for a prefix "foo".
     * </p>
     *
     * @param prefix The prefix of desired property keys
     * @return All found properties with keys shortened by the prefix
     */
    public Configuration getSubConfiguration(String prefix) {
        return getSubConfiguration(prefix, '.');
    }

    /**
     * Collects all properties that start with the passed prefix and passed separator character.
     *
     * <p>
     *     For example, the property "foo@bar=42" would be returned as "bar=42" for a prefix "foo" and separator
     *     character '@'.
     * </p>
     *
     * @param prefix The prefix of desired property keys
     * @param separator The character to use as separator
     * @return All found properties with keys shortened by the prefix and separator character
     */
    public Configuration getSubConfiguration(String prefix, char separator) {
        prefix = prefix + separator;

        Map<String, String> childProperties = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                childProperties.put(key.substring(prefix.length()), entry.getValue());
            }
        }

        return new Configuration(this, resolveFullKey(prefix), childProperties);
    }

    /**
     * Resolves the full key as used in the root configuration.
     *
     * <p>
     *     For root configurations, this function always returns the passed key unchanged. For sub configurations, the
     *     prefix is added in front of the passed key.
     * </p>
     *
     * @param key The key to resolve
     * @return The full key  as used in the root configuration
     */
    public String resolveFullKey(String key) {
        if (prefix == null) {
            return key;
        } else {
            return prefix + key;
        }
    }

    /**
     * Gets all stored keys and values.
     *
     * <p>
     *     The returned map must be not modified.
     * </p>
     *
     * @return All stored keys and values
     */
    Map<String, String> getAllValues() {
        return properties;
    }

}
