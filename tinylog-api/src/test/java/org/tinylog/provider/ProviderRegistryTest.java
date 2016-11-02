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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.tinylog.Level;
import org.tinylog.util.SystemStreamCollector;

import static java.util.Collections.singletonMap;
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

	private SystemStreamCollector systemStream;

	/**
	 * Redirects {@link System#out} and {@link System#err} as well as initializes {@link ProviderRegistry} properly.
	 */
	@Before
	public void init() {
		systemStream = new SystemStreamCollector();
		ProviderRegistry.getLoggingProvider();
		systemStream.clear();
	}

	/**
	 * Stops redirecting {@link System#out} and {@link System#err}.
	 */
	@After
	public void reset() {
		systemStream.close();

		assertThat(systemStream.consumeStandardOutput()).isEmpty();
		assertThat(systemStream.consumeErrorOutput()).isEmpty();
	}

	/**
	 * Verifies that the expected logging provider will be returned.
	 */
	@Test
	public void defaultLoggingProvider() {
		assertThat(ProviderRegistry.getLoggingProvider()).isInstanceOf(NopLoggingProvider.class);
	}

	/**
	 * Verifies that the expected context provider will be returned.
	 */
	@Test
	public void defaultContextProvider() {
		assertThat(ProviderRegistry.getContextProvider()).isInstanceOf(NopContextProvider.class);
	}

	/**
	 * Verifies that a {@link NopLoggingProvider} will be created if there are no registered logging providers.
	 *
	 * @throws Exception
	 *             Required for invoking private method via {@link Whitebox#invokeMethod()}
	 */
	@Test
	public void noLoggingProviders() throws Exception {
		registerProviders(LoggingProvider.class, 0);

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
	 *             Required for invoking private method via {@link Whitebox#invokeMethod()}
	 */
	@Test
	public void singleLoggingProvider() throws Exception {
		List<LoggingProvider> registerProviders = registerProviders(LoggingProvider.class, 1);

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isSameAs(registerProviders.get(0));
	}

	/**
	 * Verifies that multiple registered logging providers will be combined to one.
	 *
	 * @throws Exception
	 *             Required for invoking private method via {@link Whitebox#invokeMethod()}
	 */
	@Test
	public void multipleLoggingProviders() throws Exception {
		List<LoggingProvider> registerProviders = registerProviders(LoggingProvider.class, 2);
		for (LoggingProvider provider : registerProviders) {
			when(provider.getMinimumLevel(null)).thenReturn(Level.DEBUG);
		}

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(WrapperLoggingProvider.class);
		assertThat(createdProvider.getMinimumLevel(null)).isEqualTo(Level.DEBUG);
	}

	/**
	 * Verifies that a {@link NopContextProvider} will be created if there are no registered context providers.
	 *
	 * @throws Exception
	 *             Required for invoking private method via {@link Whitebox#invokeMethod()}
	 */
	@Test
	public void noContextProviders() throws Exception {
		registerProviders(ContextProvider.class, 0);

		ContextProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadContextProvider");
		assertThat(createdProvider).isInstanceOf(NopContextProvider.class);
	}

	/**
	 * Verifies that the registered context provider will be used if there is only one.
	 *
	 * @throws Exception
	 *             Required for invoking private method via {@link Whitebox#invokeMethod()}
	 */
	@Test
	public void singleContextProvider() throws Exception {
		List<ContextProvider> registerProviders = registerProviders(ContextProvider.class, 1);

		ContextProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadContextProvider");
		assertThat(createdProvider).isSameAs(registerProviders.get(0));
	}

	/**
	 * Verifies that multiple registered context providers will be combined to one.
	 *
	 * @throws Exception
	 *             Required for invoking private method via {@link Whitebox#invokeMethod()}
	 */
	@Test
	public void multipleContextProviders() throws Exception {
		List<ContextProvider> registerProviders = registerProviders(ContextProvider.class, 2);
		for (ContextProvider provider : registerProviders) {
			when(provider.getMapping()).thenReturn(singletonMap("test", "42"));
		}

		ContextProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadContextProvider");
		assertThat(createdProvider).isInstanceOf(WrapperContextProvider.class);
		assertThat(createdProvider.getMapping()).isEqualTo(singletonMap("test", "42"));
	}

	/**
	 * Registers a defined number of providers as service.
	 *
	 * @param providerType
	 *            Provider interface class
	 * @param times
	 *            Number of mocks that should be registered
	 * @param <T>
	 *            Provider interface type
	 * @return All registered mocks
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> List<T> registerProviders(final Class<T> providerType, final int times) {
		PowerMockito.mockStatic(ServiceLoader.class);
		ServiceLoader serviceLoaderMock = mock(ServiceLoader.class);

		List<T> providers = new ArrayList<>();
		for (int i = 0; i < times; ++i) {
			providers.add(mock(providerType));
		}

		when(serviceLoaderMock.iterator()).thenReturn(providers.iterator());
		when(ServiceLoader.load(any())).thenReturn(serviceLoaderMock);

		return providers;
	}

}
