/*
 * Copyright 2018 Martin Winandy
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

package org.pmw.tinylog;

import java.util.Map;

import org.tinylog.ThreadContext;

/**
 * Thread-based mapped diagnostic context for enriching log entries with additional values. A stored value is only
 * visible for the thread, in which this value has been set, and its child threads.
 * 
 * <p>
 * Logging context is a wrapper for {@link ThreadContext}. Therefore, all operations also affect the
 * {@link ThreadContext} and vice versa.
 * </p>
 *
 * <p>
 * If values will be stored by a thread from a thread pool, {@link #clear()} should be called before putting the thread
 * back to the pool.
 * </p>
 */
public final class LoggingContext {

	/** */
	private LoggingContext() {
	}

	/**
	 * Gets all values that are visible for the current thread from logging context.
	 *
	 * <p>
	 * The returned map is either read-only or a mutable copy. It cannot used to modify the logging context itself.
	 * </p>
	 *
	 * @return All stored values
	 */
	public static Map<String, String> getMapping() {
		return ThreadContext.getMapping();
	}

	/**
	 * Gets a value by key from logging context.
	 *
	 * @param key
	 *            Key of mapping
	 * @return Found value or {@code null}
	 */
	public static String get(final String key) {
		return ThreadContext.get(key);
	}

	/**
	 * Stores a value in logging context. If the key already exists, the original value will be overridden.
	 *
	 * @param key
	 *            Key of mapping
	 * @param value
	 *            Value of mapping
	 */
	public static void put(final String key, final Object value) {
		ThreadContext.put(key, value);
	}

	/**
	 * Removes a value from logging context. If there is no mapping with the given key, this method will just quit
	 * silently.
	 *
	 * @param key
	 *            Key of mapping
	 */
	public static void remove(final String key) {
		ThreadContext.remove(key);
	}

	/**
	 * Removes all existing values from logging context.
	 */
	public static void clear() {
		ThreadContext.clear();
	}

}
