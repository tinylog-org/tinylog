package org.tinylog.core.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for bundling multiple {@link ContextStorage} instances.
 */
public class BundleContextStorage implements ContextStorage {

    private final List<ContextStorage> storages;

    /**
     * @param storages Context storages to combine
     */
    public BundleContextStorage(List<ContextStorage> storages) {
        this.storages = new ArrayList<>(storages);
    }

    @Override
    public Map<String, String> getMapping() {
        return storages.stream()
            .flatMap(storage -> storage.getMapping().entrySet().stream())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (first, second) -> first
            ));
    }

    @Override
    public String get(String key) {
        for (ContextStorage storage : storages) {
            String value = storage.get(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    @Override
    public void put(String key, String value) {
        storages.forEach(storage -> storage.put(key, value));
    }

    @Override
    public void replace(Map<String, String> mapping) {
        storages.forEach(storage -> storage.replace(mapping));
    }

    @Override
    public void remove(String key) {
        storages.forEach(storage -> storage.remove(key));
    }

    @Override
    public void clear() {
        storages.forEach(ContextStorage::clear);
    }

}
