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

package org.tinylog.util;

import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class for creating small maps with less overhead to have readable JUnit tests.
 */
public final class Maps {

	/** */
	private Maps() {
	}

	/**
	 * Creates a map with two entries.
	 *
	 * @param firstKey
	 *            Key of first entry
	 * @param firstValue
	 *            Value of first entry
	 * @param secondKey
	 *            Key of second entry
	 * @param secondValue
	 *            Value of second entry
	 * @param <K>
	 *            Key type
	 * @param <V>
	 *            Value type
	 * @return Map with specified entries
	 */
	public static <K, V> Map<K, V> doubletonMap(final K firstKey, final V firstValue, final K secondKey, final V secondValue) {
		Map<K, V> map = new TreeMap<>();
		map.put(firstKey, firstValue);
		map.put(secondKey, secondValue);
		return map;
	}

	/**
	 * Creates a map with three entries.
	 *
	 * @param firstKey
	 *            Key of first entry
	 * @param firstValue
	 *            Value of first entry
	 * @param secondKey
	 *            Key of second entry
	 * @param secondValue
	 *            Value of second entry
	 * @param thirdKey
	 *            Key of third entry
	 * @param thirdValue
	 *            Value of third entry
	 * @param <K>
	 *            Key type
	 * @param <V>
	 *            Value type
	 * @return Map with specified entries
	 */
	public static <K, V> Map<K, V> tripletonMap(final K firstKey, final V firstValue, final K secondKey, final V secondValue,
		final K thirdKey, final V thirdValue) {
		Map<K, V> map = new TreeMap<>();
		map.put(firstKey, firstValue);
		map.put(secondKey, secondValue);
		map.put(thirdKey, thirdValue);
		return map;
	}

}
