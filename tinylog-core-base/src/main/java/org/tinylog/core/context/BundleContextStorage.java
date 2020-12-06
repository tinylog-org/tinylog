package org.tinylog.core.context;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for multiple {@link ContextStorage ContextStorages}.
 */
public class BundleContextStorage implements ContextStorage {

	private final List<ContextStorage> storages;

	/**
	 * @param storages Context storages to combine
	 */
	public BundleContextStorage(List<ContextStorage> storages) {
		this.storages = storages;
	}

	@Override
	public Map<String, String> getMapping() {
		return new CombinedHashMap(this);
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
		if (mapping instanceof CombinedHashMap) {
			CombinedHashMap combinedMapping = (CombinedHashMap) mapping;
			WeakReference<BundleContextStorage> source = combinedMapping.source;
			if (source != null && source.get() == this && combinedMapping.equals(combinedMapping.original)) {
				Iterator<ContextStorage> storageIterator = storages.iterator();
				Iterator<Map<String, String>> mapIterator = combinedMapping.maps.iterator();
				while (storageIterator.hasNext() && mapIterator.hasNext()) {
					storageIterator.next().replace(mapIterator.next());
				}
				return;
			}
		}

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

	/**
	 * A hash map that uses the mapping of the child storages of a bundle context storage as initial mapping and stores
	 * the sources for recovery.
	 */
	private static final class CombinedHashMap extends HashMap<String, String> {

		private static final long serialVersionUID = 1L;

		private final transient WeakReference<BundleContextStorage> source;
		private final Map<String, String> original;
		private final List<Map<String, String>> maps;

		/**
		 * @param source The bundle context storage to use as source for the mapping
		 */
		private CombinedHashMap(BundleContextStorage source) {
			source.storages.stream()
				.flatMap(storage -> storage.getMapping().entrySet().stream())
				.forEach(entry -> this.putIfAbsent(entry.getKey(), entry.getValue()));

			this.source = new WeakReference<>(source);
			this.original = new HashMap<>(this);
			this.maps = source.storages.stream().map(ContextStorage::getMapping).collect(Collectors.toList());
		}

	}

}
