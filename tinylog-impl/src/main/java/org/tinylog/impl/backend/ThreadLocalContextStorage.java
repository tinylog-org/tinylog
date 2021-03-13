package org.tinylog.impl.backend;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.core.context.ContextStorage;

/**
 * The context storage for tinylog's native logging backend implementation, based on an {@link InheritableThreadLocal}.
 */
public class ThreadLocalContextStorage implements ContextStorage {

	private final ThreadLocal<Map<String, String>> threadLocal;

	/** */
	public ThreadLocalContextStorage() {
		threadLocal = new InheritableThreadLocal<>() {

			@Override
			protected Map<String, String> initialValue() {
				return Collections.emptyMap();
			}

		};
	}

	@Override
	public Map<String, String> getMapping() {
		return threadLocal.get();
	}

	@Override
	public String get(String key) {
		return getMapping().get(key);
	}

	@Override
	public void put(String key, String value) {
		Map<String, String> map = new HashMap<>(getMapping());
		if (value == null) {
			map.remove(key);
		} else {
			map.put(key, value);
		}

		set(map);
	}

	@Override
	public void replace(Map<String, String> mapping) {
		set(mapping);
	}

	@Override
	public void remove(String key) {
		Map<String, String> map = new HashMap<>(getMapping());
		map.remove(key);
		set(map);
	}

	@Override
	public void clear() {
		threadLocal.set(Collections.emptyMap());
	}

	/**
	 * Replaces the map stored in the thread local.
	 *
	 * @param map The new map to store
	 */
	private void set(Map<String, String> map) {
		threadLocal.set(map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map));
	}

}
