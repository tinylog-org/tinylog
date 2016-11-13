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

package org.tinylog;

import java.util.Map;

import org.tinylog.provider.ContextProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * Thread-based mapped diagnostic context for enriching log entries with additional values. A stored value is only
 * visible for the thread, in which this value has been set, and its child threads.
 *
 * <p>
 * If values will be stored by a thread from a thread pool, {@link #clear()} should be called before putting the thread
 * back to the pool.
 * </p>
 */
public final class ThreadContext {

	private static final ContextProvider provider = ProviderRegistry.getLoggingProvider().getContextProvider();

	/** */
	private ThreadContext() {
	}

	/**
	 * Gets all values that are visible for the current thread from thread context.
	 *
	 * <p>
	 * The returned map is either read-only or a mutable copy. It cannot used to modify the thread context itself.
	 * </p>
	 *
	 * @return All stored values
	 */
	public static Map<String, String> getMapping() {
		return provider.getMapping();
	}

	/**
	 * Gets a value by key from thread context.
	 *
	 * @param key
	 *            Key of mapping
	 * @return Found value or {@code null}
	 */
	public static String get(final String key) {
		return provider.get(key);
	}

	/**
	 * Stores a value in thread context. If the key already exists, the original value will be overridden.
	 *
	 * @param key
	 *            Key of mapping
	 * @param value
	 *            Value of mapping
	 */
	public static void put(final String key, final Object value) {
		provider.put(key, value);
	}

	/**
	 * Removes a value from thread context. If there is no mapping with the given key, this method will just quit
	 * silently.
	 *
	 * @param key
	 *            Key of mapping
	 */
	public static void remove(final String key) {
		provider.remove(key);
	}

	/**
	 * Removes all existing values from thread context.
	 */
	public static void clear() {
		provider.clear();
	}

}
