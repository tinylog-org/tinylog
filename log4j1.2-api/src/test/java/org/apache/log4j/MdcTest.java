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

package org.apache.log4j;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.ThreadContext;
import org.tinylog.provider.ContextProvider;
import org.tinylog.provider.ProviderRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.tinylog.util.Maps.doubletonMap;

/**
 * Tests for {@link MDC}.
 */
public final class MdcTest {

	private ContextProvider provider;

	/**
	 * Mocks the underlying context provider.
	 */
	@Before
	public void init() {
		provider = mock(ContextProvider.class);
		Whitebox.setInternalState(ThreadContext.class, provider);
	}

	/**
	 * Resets the underlying context provider.
	 */
	@After
	public void reset() {
		Whitebox.setInternalState(ThreadContext.class, ProviderRegistry.getLoggingProvider().getContextProvider());
	}

	/**
	 * Verifies that stored context values will be used from underlying context provider.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getContext() {
		when(provider.getMapping()).thenReturn(doubletonMap("e", "2.71", "pi", "3.14"));
		assertThat(MDC.getContext()).contains(entry("e", "2.71"), entry("pi", "3.14"));
	}

	/**
	 * Verifies that a context value will be returned from underlying context provider, if the requested key parameter
	 * exists there.
	 */
	@Test
	public void getExistingValue() {
		when(provider.get("pi")).thenReturn("3.14");
		assertThat(MDC.get("pi")).isEqualTo("3.14");
	}

	/**
	 * Verifies that {@code null} will be returned, if a requested context value doesn't exist in underlying context
	 * provider.
	 */
	@Test
	public void getMissingValue() {
		assertThat(MDC.get("pi")).isNull();
	}

	/**
	 * Verifies that a new context value will be passed-through to underlying context provider.
	 */
	@Test
	public void putValue() {
		MDC.put("pi", "3.14");
		verify(provider).put("pi", "3.14");
	}

	/**
	 * Verifies that a context value can be removed from underlying context provider.
	 */
	@Test
	public void removeValue() {
		MDC.remove("pi");
		verify(provider).remove("pi");
	}

	/**
	 * Verifies that all context values can be cleared in underlying context provider.
	 */
	@Test
	public void clearValues() {
		MDC.clear();
		verify(provider).clear();
	}

}
