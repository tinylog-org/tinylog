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
import java.util.stream.Collectors;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.tinylog.util.Maps.doubletonMap;

/**
 * Tests for {@link BundleContextProvider}.
 */
public final class BundleContextProviderTest {

	private ContextProvider first;
	private ContextProvider second;
	private ContextProvider bundle;

	/**
	 * Creates mocks for underlying context providers.
	 */
	@Before
	public void init() {
		first = mock(ContextProvider.class);
		second = mock(ContextProvider.class);
		bundle = new BundleContextProvider(asList(first, second));
	}

	/**
	 * Verifies that {@code getMapping()} returns an empty map, if child context providers have empty mappings.
	 */
	@Test
	public void getEmptyMapping() {
		when(first.getMapping()).thenReturn(emptyMap());
		when(second.getMapping()).thenReturn(emptyMap());

		assertThat(bundle.getMapping()).isEmpty();
	}

	/**
	 * Verifies that {@code getMapping()} can handle mappings that exist with the same value in both underlying context
	 * providers.
	 */
	@Test
	public void getSameMapping() {
		when(first.getMapping()).thenReturn(doubletonMap("pi", "3.14", "e", "2.71"));
		when(second.getMapping()).thenReturn(doubletonMap("pi", "3.14", "e", "2.71"));

		assertThat(bundle.getMapping()).containsOnly(entry("pi", "3.14"), entry("e", "2.71"));
	}

	/**
	 * Verifies that {@code getMapping()} can handle mappings that exist only in one of the underlying context
	 * providers.
	 */
	@Test
	public void getDifferentMapping() {
		when(first.getMapping()).thenReturn(doubletonMap("pi", "3.14", "e", "2.71"));
		when(second.getMapping()).thenReturn(singletonMap("pi", "3.14"));

		assertThat(bundle.getMapping()).containsOnly(entry("pi", "3.14"), entry("e", "2.71"));
	}

	/**
	 * Verifies that {@code getMapping()} method can handle mappings that exist with different values in both underlying
	 * context providers.
	 */
	@Test
	public void getConflictiveMapping() {
		when(first.getMapping()).thenReturn(doubletonMap("pi", "3.14", "G", "0.91"));
		when(second.getMapping()).thenReturn(doubletonMap("pi", "3.14", "G", "0.92"));

		assertThat(bundle.getMapping()).containsEntry("pi", "3.14").has(anyOf("G", "0.91", "0.92")).hasSize(2);
	}

	/**
	 * Verifies that {@code get()} method returns {@code null} for non-existent entries.
	 */
	@Test
	public void getNonExistentValue() {
		when(first.get("pi")).thenReturn(null);
		when(second.get("pi")).thenReturn(null);

		assertThat(bundle.get("pi")).isNull();
	}

	/**
	 * Verifies that {@code get()} method returns the correct value for in both underlying context providers existent
	 * mappings.
	 */
	@Test
	public void getExistentValue() {
		when(first.get("pi")).thenReturn("3.14");
		when(second.get("pi")).thenReturn("3.14");

		assertThat(bundle.get("pi")).isEqualTo("3.14");
	}

	/**
	 * Verifies that {@code get()} method returns the correct value for only in first underlying context provider
	 * existent mappings.
	 */
	@Test
	public void getExistentValueFromFirst() {
		when(first.get("pi")).thenReturn("3.14");
		when(second.get("pi")).thenReturn(null);

		assertThat(bundle.get("pi")).isEqualTo("3.14");
	}

	/**
	 * Verifies that {@code get()} method returns the correct value for only in second underlying context provider
	 * existent mappings.
	 */
	@Test
	public void getExistentValueFromSecond() {
		when(first.get("pi")).thenReturn(null);
		when(second.get("pi")).thenReturn("3.14");

		assertThat(bundle.get("pi")).isEqualTo("3.14");
	}

	/**
	 * Verifies that {@code put()} method puts a mapping to all underlying context providers.
	 */
	@Test
	public void putValue() {
		bundle.put("pi", "3.14");

		verify(first).put("pi", "3.14");
		verify(second).put("pi", "3.14");
	}

	/**
	 * Verifies that {@code remove()} method removes a value from underlying child context providers.
	 */
	@Test
	public void removeValue() {
		bundle.remove("pi");

		verify(first).remove("pi");
		verify(second).remove("pi");
	}

	/**
	 * Verifies that {@code clear()} method clears all underlying context providers.
	 */
	@Test
	public void clear() {
		bundle.clear();

		verify(first).clear();
		verify(second).clear();
	}

	/**
	 * Creates an AssertJ condition for verifying that a map contains an entry with the given key and one of the given
	 * values.
	 *
	 * @param key
	 *            Key of entry
	 * @param values
	 *            Value of entry should be one of these values
	 * @return AssertJ condition
	 */
	private static Condition<Map<String, String>> anyOf(final String key, final String... values) {
		return new Condition<>(map -> map.containsKey(key) && asList(values).contains(map.get(key)),
				"\"" + key + "\" = " + stream(values).map(value -> "\"" + value + "\"").collect(Collectors.joining(" or ")));
	}

}
