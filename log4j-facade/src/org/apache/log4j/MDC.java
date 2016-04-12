/*
 * Copyright 2015 Martin Winandy
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

package org.apache.log4j;

import java.util.Hashtable;

import org.pmw.tinylog.LoggingContext;

/**
 * Thread-based mapped diagnostic context.
 */
public final class MDC {

	/** */
	private MDC() {
	}

	/**
	 * Get a copy of all mapped context values.
	 *
	 * @return Copy current mapped diagnostic context
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Hashtable getContext() {
		return new Hashtable(LoggingContext.getMapping());
	}

	/**
	 * Get the context value by a key.
	 *
	 * @param key
	 *            Key of mapping
	 * @return Context value or {@code null}
	 */
	public static Object get(final String key) {
		return LoggingContext.get(key);
	}

	/**
	 * Put a new mapped context value.
	 *
	 * @param key
	 *            Key of mapping
	 * @param value
	 *            New value
	 */
	public static void put(final String key, final Object value) {
		LoggingContext.put(key, value);
	}

	/**
	 * Remove a mapped context value if existing.
	 *
	 * @param key
	 *            Key of mapping
	 */
	public static void remove(final String key) {
		LoggingContext.remove(key);
	}

	/**
	 * Remove all existing mapped context values.
	 */
	public static void clear() {
		LoggingContext.clear();
	}

}
