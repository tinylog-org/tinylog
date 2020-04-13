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

package org.tinylog.slf4j;

import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.ThreadContext;
import org.tinylog.provider.ContextProvider;
import org.tinylog.provider.ProviderRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link TinylogMdcAdapter}.
 */
public final class TinylogMdcAdapterTest {

	private ContextProvider provider;

	/**
	 * Mocks the underlying context provider.
	 */
	@Before
	public void init() {
		ThreadContext.clear();

		provider = mock(ContextProvider.class);
		Whitebox.setInternalState(ThreadContext.class, provider);
	}

	/**
	 * Resets the underlying context provider.
	 */
	@After
	public void reset() {
		Whitebox.setInternalState(ThreadContext.class, ProviderRegistry.getLoggingProvider().getContextProvider());
		ThreadContext.clear();
	}

	/**
	 * Verifies that a new mapping will be passed-through to the actual context provider.
	 */
	@Test
	public void put() {
		new TinylogMdcAdapter().put("test", "42");
		verify(provider).put("test", "42");
	}

	/**
	 * Verifies that an existing mapping from the actual context provider can be received.
	 */
	@Test
	public void get() {
		when(provider.get("test")).thenReturn("42");
		assertThat(new TinylogMdcAdapter().get("test")).isEqualTo("42");
	}

	/**
	 * Verifies that removing a mapping will be passed-through to the actual context provider.
	 */
	@Test
	public void remove() {
		new TinylogMdcAdapter().remove("test");
		verify(provider).remove("test");
	}

	/**
	 * Verifies that clearing all mappings will be passed-through to the actual context provider.
	 */
	@Test
	public void clear() {
		new TinylogMdcAdapter().clear();
		verify(provider).clear();
	}

	/**
	 * Verifies that the context map from the actual context provider will be returned as a copy.
	 */
	@Test
	public void getCopyOfContextMap() {
		Map<String, String> currentMap = Collections.singletonMap("test", "42");
		when(provider.getMapping()).thenReturn(currentMap);

		assertThat(new TinylogMdcAdapter().getCopyOfContextMap()).isEqualTo(currentMap).isNotSameAs(currentMap);
	}

	/**
	 * Verifies that setting a new context map will replace the existing mapping of the actual context provider.
	 */
	@Test
	public void setContextMap() {
		new TinylogMdcAdapter().setContextMap(Collections.singletonMap("test", "42"));

		verify(provider).clear();
		verify(provider).put("test", "42");
	}

}
