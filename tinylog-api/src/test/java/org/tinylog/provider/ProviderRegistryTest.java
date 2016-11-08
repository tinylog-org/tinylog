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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.tinylog.Level;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link ProviderRegistry}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ProviderRegistry.class)
public final class ProviderRegistryTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Initializes {@link ProviderRegistry} properly.
	 */
	@Before
	public void init() {
		ProviderRegistry.getLoggingProvider();
		systemStream.clear();
	}

	/**
	 * Verifies that the expected logging provider will be returned.
	 */
	@Test
	public void defaultProvider() {
		assertThat(ProviderRegistry.getLoggingProvider()).isInstanceOf(NopLoggingProvider.class);
	}

	/**
	 * Verifies that a {@link NopLoggingProvider} will be created if there are no registered logging providers.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	public void noProviders() throws Exception {
		registerProviders(0);

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(NopLoggingProvider.class);

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("WARNING")
			.containsOnlyOnce("logging")
			.endsWith(System.lineSeparator())
			.hasLineCount(1);
	}

	/**
	 * Verifies that the registered logging provider will be used if there is only one.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	public void singleProvider() throws Exception {
		List<LoggingProvider> registerProviders = registerProviders(1);

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isSameAs(registerProviders.get(0));
	}

	/**
	 * Verifies that multiple registered logging providers will be combined to one.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	public void multipleProviders() throws Exception {
		List<LoggingProvider> registerProviders = registerProviders(2);
		for (LoggingProvider provider : registerProviders) {
			when(provider.getMinimumLevel(null)).thenReturn(Level.DEBUG);
		}

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(WrapperLoggingProvider.class);
		assertThat(createdProvider.getMinimumLevel(null)).isEqualTo(Level.DEBUG);
	}

	/**
	 * Registers a defined number of logging providers as service.
	 *
	 * @param times
	 *            Number of mocks that should be registered
	 * @return All registered mocks
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<LoggingProvider> registerProviders(final int times) {
		PowerMockito.mockStatic(ServiceLoader.class);
		ServiceLoader serviceLoaderMock = mock(ServiceLoader.class);

		List<LoggingProvider> providers = new ArrayList<>();
		for (int i = 0; i < times; ++i) {
			providers.add(mock(LoggingProvider.class));
		}

		when(serviceLoaderMock.iterator()).thenReturn(providers.iterator());
		when(ServiceLoader.load(any())).thenReturn(serviceLoaderMock);

		return providers;
	}

}
