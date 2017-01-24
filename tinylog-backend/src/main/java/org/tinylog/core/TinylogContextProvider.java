/*
 * Copyright 2016 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.provider.ContextProvider;

/**
 * tinylog's native context provider implementation uses a {@link InheritableThreadLocal} to store thread context
 * values.
 */
public class TinylogContextProvider implements ContextProvider {

	private final ThreadLocal<Map<String, String>> data;

	/** */
	public TinylogContextProvider() {
		data = new InheritableEmptyMapThreadLocal<String, String>();
	}

	@Override
	public Map<String, String> getMapping() {
		return data.get();
	}

	@Override
	public String get(final String key) {
		return data.get().get(key);
	}

	@Override
	public void put(final String key, final Object value) {
		Map<String, String> map = new HashMap<String, String>(data.get());
		if (value == null) {
			map.remove(key);
		} else {
			map.put(key, value.toString());
		}
		data.set(Collections.unmodifiableMap(map));
	}

	@Override
	public void remove(final String key) {
		Map<String, String> map = new HashMap<String, String>(data.get());
		map.remove(key);
		data.set(map.isEmpty() ? Collections.<String, String>emptyMap() : Collections.unmodifiableMap(map));

	}

	@Override
	public void clear() {
		data.set(Collections.<String, String>emptyMap());
	}

	/**
	 * Inheritable thread local with an empty map as initial value.
	 *
	 * @param <K>
	 *            Type of map keys
	 * @param <V>
	 *            Type of map values
	 */
	private static final class InheritableEmptyMapThreadLocal<K, V> extends InheritableThreadLocal<Map<K, V>> {

		/** */
		private InheritableEmptyMapThreadLocal() {
		}

		@Override
		protected Map<K, V> initialValue() {
			return Collections.emptyMap();
		}

	}

}
