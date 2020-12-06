package org.tinylog.core.context;

import java.util.Collections;
import java.util.Map;

/**
 * A no operation implementation of {@link ContextStorage} that is always empty. All modification operations are
 * silently ignored.
 */
public class NopContextStorage implements ContextStorage {

	/** */
	public NopContextStorage() {
	}

	@Override
	public Map<String, String> getMapping() {
		return Collections.emptyMap();
	}

	@Override
	public String get(String key) {
		return null;
	}

	@Override
	public void put(String key, String value) {
		// Ignore
	}

	@Override
	public void replace(Map<String, String> mapping) {
		// Ignore
	}

	@Override
	public void remove(String key) {
		// Ignore
	}

	@Override
	public void clear() {
		// Ignore
	}

}
