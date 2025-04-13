package org.tinylog.impl.backend;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.core.context.ContextStorage;

/**
 * tinylog's native context storage implementation based on an {@link InheritableThreadLocal} to store thread context
 * values.
 */
public class TinylogContextStorage implements ContextStorage {

    private final ThreadLocal<Map<String, String>> data;

    /** */
    public TinylogContextStorage() {
        data = new InheritableMapThreadLocal<>();
    }

    @Override
    public Map<String, String> getMapping() {
        return data.get();
    }

    @Override
    public String get(String key) {
        return data.get().get(key);
    }

    @Override
    public void put(String key, String value) {
        Map<String, String> map = new HashMap<>(data.get());
        if (value == null) {
            map.remove(key);
        } else {
            map.put(key, value);
        }
        data.set(Collections.unmodifiableMap(map));
    }

    @Override
    public void replace(Map<String, String> mapping) {
        data.set(mapping.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(mapping));
    }

    @Override
    public void remove(final String key) {
        Map<String, String> map = new HashMap<>(data.get());
        map.remove(key);
        data.set(map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map));
    }

    @Override
    public void clear() {
        data.set(Collections.emptyMap());
    }

}
