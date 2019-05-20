/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.adapter.jboss;

import java.util.Map;

import org.jboss.logging.MDC;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link JBossContextProvider}.
 */
public final class JBossContextProviderTest {
	
	/**
	 * Clears {@link MDC} before and after each test.
	 */
	@Before
	@After
	public void clearMdc() {
		MDC.clear();
	}

	/**
	 * Verifies that an empty MDC can be output.
	 */
	@Test
	public void emptyMapping() {
		Map<String, String> mapping = new JBossContextProvider().getMapping();
		assertThat(mapping).isEmpty();
	}

	/**
	 * Verifies that a pre-filled MDC can be output.
	 */
	@Test
	public void filledMapping() {
		MDC.put("i", 42);
		MDC.put("s", "Hello World");
		
		Map<String, String> mapping = new JBossContextProvider().getMapping();
		assertThat(mapping).containsOnly(entry("i", "42"), entry("s", "Hello World"));
	}

	/**
	 * Verifies that an existing value can be fetched.
	 */
	@Test
	public void getExistingValue() {
		MDC.put("test", 42);

		String value = new JBossContextProvider().get("test");
		assertThat(value).isEqualTo("42");
	}

	/**
	 * Verifies that {@code null} will be returned for a missing value.
	 */
	@Test
	public void getMissingValue() {
		String value = new JBossContextProvider().get("test");
		assertThat(value).isNull();
	}

	/**
	 * Verifies that a new value can be added.
	 */
	@Test
	public void addValue() {
		new JBossContextProvider().put("pi", "3.14");

		assertThat(MDC.getMap()).containsOnly(entry("pi", "3.14"));
	}

	/**
	 * Verifies that an existing value can be overridden.
	 */
	@Test
	public void overrideValue() {
		MDC.put("test", "a");

		new JBossContextProvider().put("test", "b");
	
		assertThat(MDC.getMap()).containsOnly(entry("test", "b"));
	}

	/**
	 * Verifies that existing values can be removed.
	 */
	@Test
	public void remove() {
		MDC.put("a", 1);
		MDC.put("b", 2);
		
		JBossContextProvider provider = new JBossContextProvider();

		provider.remove("a");
		assertThat(MDC.getMap()).containsOnly(entry("b", 2));

		provider.remove("b");
		assertThat(MDC.getMap()).isEmpty();
	}

	/**
	 * Verifies that a context provider can be cleared.
	 */
	@Test
	public void clear() {
		MDC.put("a", 1);
		MDC.put("b", 2);

		new JBossContextProvider().clear();
		
		assertThat(MDC.getMap()).isEmpty();
	}

}
