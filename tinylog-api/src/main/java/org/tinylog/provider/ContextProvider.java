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

package org.tinylog.provider;

import java.util.Map;

/**
 * API for accessing thread-based mapped diagnostic context of concrete logging framework implementation.
 */
public interface ContextProvider {

	/**
	 * Gets a read-only copy with all values from thread context.
	 *
	 * @return Read-only copy
	 */
	Map<String, String> getMapping();

	/**
	 * Gets a value by key from thread context.
	 *
	 * @param key
	 *            Key of mapping
	 * @return Found value or {@code null}
	 */
	String get(String key);

	/**
	 * Stores a value in thread context. If the key already exists, the original value will be overridden.
	 *
	 * @param key
	 *            Key of mapping
	 * @param value
	 *            Value of mapping
	 */
	void put(String key, Object value);

	/**
	 * Removes a value from thread context. If there is no mapping with the given key, this method will quit silently.
	 *
	 * @param key
	 *            Key of mapping
	 */
	void remove(String key);

	/**
	 * Removes all existing values from thread context.
	 */
	void clear();

}
