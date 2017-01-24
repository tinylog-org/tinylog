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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link TinylogContextProvider}.
 */
public final class TinylogContextProviderTest {

	/**
	 * Verifies that a new context provider has an empty mapping.
	 */
	@Test
	public void empty() {
		TinylogContextProvider provider = new TinylogContextProvider();
		assertThat(provider.getMapping()).isEmpty();
	}

	/**
	 * Verifies that a new value can be added.
	 */
	@Test
	public void addNewValue() {
		TinylogContextProvider provider = new TinylogContextProvider();
		provider.put("pi", "3.14");

		assertThat(provider.get("pi")).isEqualTo("3.14");
		assertThat(provider.getMapping()).containsOnly(entry("pi", "3.14"));
	}

	/**
	 * Verifies that an existing value can be overridden.
	 */
	@Test
	public void overrideExistingValue() {
		TinylogContextProvider provider = new TinylogContextProvider();

		provider.put("test", "a");
		assertThat(provider.getMapping()).containsOnly(entry("test", "a"));

		provider.put("test", "b");
		assertThat(provider.getMapping()).containsOnly(entry("test", "b"));
	}

	/**
	 * Verifies that {@code null} values will be not stored.
	 */
	@Test
	public void addNullValue() {
		TinylogContextProvider provider = new TinylogContextProvider();
		provider.put("test", null);
		assertThat(provider.getMapping()).isEmpty();
	}

	/**
	 * Verifies that existing values can be removed.
	 */
	@Test
	public void remove() {
		TinylogContextProvider provider = new TinylogContextProvider();

		provider.put("a", 1);
		provider.put("b", 2);
		assertThat(provider.getMapping()).containsOnly(entry("a", "1"), entry("b", "2"));

		provider.remove("a");
		assertThat(provider.getMapping()).containsOnly(entry("b", "2"));

		provider.remove("b");
		assertThat(provider.getMapping()).isEmpty();
	}

	/**
	 * Verifies that a context provider can be cleared.
	 */
	@Test
	public void clear() {
		TinylogContextProvider provider = new TinylogContextProvider();

		provider.put("a", 1);
		provider.put("b", 2);
		assertThat(provider.getMapping()).containsOnly(entry("a", "1"), entry("b", "2"));

		provider.clear();
		assertThat(provider.getMapping()).isEmpty();
	}

	/**
	 * Verifies that a child thread inherits values from parent thread but not the way around.
	 *
	 * @throws InterruptedException
	 *             Failed waiting for child thread
	 */
	@Test
	public void inheritance() throws InterruptedException {
		TinylogContextProvider provider = new TinylogContextProvider();
		provider.put("a", 1);

		Thread thread = new Thread(() -> {
			provider.put("b", 2);
			assertThat(provider.getMapping()).containsOnly(entry("a", "1"), entry("b", "2"));
		});
		thread.start();
		thread.join();

		assertThat(provider.getMapping()).containsOnly(entry("a", "1"));
	}

}
