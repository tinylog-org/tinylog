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

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NopContextProvider}.
 */
public final class NopContextProviderTest {

	private NopContextProvider provider;

	/**
	 * Initializes NOP context provider.
	 */
	@Before
	public void init() {
		provider = new NopContextProvider();
	}

	/**
	 * Verifies that returned mapping is empty.
	 */
	@Test
	public void getMapping() {
		assertThat(provider.getMapping()).isEmpty();
	}

	/**
	 * Verifies that returned values are always {@code null}.
	 */
	@Test
	public void get() {
		assertThat(provider.get("abc")).isNull();
		assertThat(provider.get(null)).isNull();
	}

	/**
	 * Verifies that put mappings will be not stored.
	 */
	@Test
	public void put() {
		provider.put("abc", "123");
		assertThat(provider.getMapping()).isEmpty();
	}

	/**
	 * Verifies that {@code remove()} method is invokable without throwing any exceptions.
	 */
	@Test
	public void remove() {
		provider.remove("abc");
		provider.remove(null);
	}

	/**
	 * Verifies that {@code clear()} method is invokable without throwing any exceptions.
	 */
	@Test
	public void clear() {
		provider.clear();
	}

}
