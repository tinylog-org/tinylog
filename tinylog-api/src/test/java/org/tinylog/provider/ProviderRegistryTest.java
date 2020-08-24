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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.format.MessageFormatter;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link ProviderRegistry}.
 */
@RunWith(PowerMockRunner.class)
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
	 * Deletes created service file from class path.
	 *
	 * @throws Exception
	 *             Failed deleting service file
	 */
	@After
	public void clear() throws Exception {
		FileSystem.deleteServiceFile(LoggingProvider.class);
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
		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(NopLoggingProvider.class);
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").containsOnlyOnce("logging");
	}

	/**
	 * Verifies that the registered logging provider will be used if there is only one.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	public void singleProvider() throws Exception {
		FileSystem.createServiceFile(LoggingProvider.class, LoggingProviderOne.class.getName());

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(LoggingProviderOne.class);
	}

	/**
	 * Verifies that multiple registered logging providers will be combined to one.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	public void multipleProviders() throws Exception {
		FileSystem.createServiceFile(LoggingProvider.class, LoggingProviderOne.class.getName(), LoggingProviderTwo.class.getName());

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(BundleLoggingProvider.class);
		assertThat(Whitebox.getInternalState(createdProvider, LoggingProvider[].class))
			.hasSize(2)
			.hasAtLeastOneElementOfType(LoggingProviderOne.class)
			.hasAtLeastOneElementOfType(LoggingProviderTwo.class);
	}
	
	/**
	 * Verifies that one or more (combined) providers can be obtained again.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	public void multipleProvidersGetter() throws Exception {
		FileSystem.createServiceFile(LoggingProvider.class, LoggingProviderOne.class.getName(), LoggingProviderTwo.class.getName());
		
		Object saveProvider = Whitebox.getInternalState(ProviderRegistry.class, "loggingProvider");
		assertThat(ProviderRegistry.getLoggingProvider()).isInstanceOf(NopLoggingProvider.class);
		
		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		Whitebox.setInternalState(ProviderRegistry.class, "loggingProvider", createdProvider);
		
		assertThat(ProviderRegistry.getLoggingProviders())
			.hasSize(2)
			.hasAtLeastOneElementOfType(LoggingProviderOne.class)
			.hasAtLeastOneElementOfType(LoggingProviderTwo.class);	
		
		Whitebox.setInternalState(ProviderRegistry.class, "loggingProvider", saveProvider);
		
		assertThat(ProviderRegistry.getLoggingProviders())
			.hasSize(1)
			.hasAtLeastOneElementOfType(NopLoggingProvider.class);	
		
		assertThat(ProviderRegistry.getLoggingProvider()).isInstanceOf(NopLoggingProvider.class);
	}

	/**
	 * Verifies that a defined logging provider can be loaded if multiple are available.
	 *
	 * @throws Exception
	 *             Failed creating service or invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	@PrepareForTest(Configuration.class)
	public void specificProvider() throws Exception {
		FileSystem.createServiceFile(LoggingProvider.class, LoggingProviderOne.class.getName(), LoggingProviderTwo.class.getName());

		spy(Configuration.class);
		when(Configuration.get("provider")).thenReturn(LoggingProviderOne.class.getName());

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(LoggingProviderOne.class);
	}

	/**
	 * Verifies that a defined logging provider list can be loaded. Also includes testing correct whitespace trim.
	 *
	 * @throws Exception
	 *             Failed creating service or invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	@PrepareForTest(Configuration.class)
	public void specificProviderList() throws Exception {
		FileSystem.createServiceFile(LoggingProvider.class, LoggingProviderOne.class.getName(), LoggingProviderTwo.class.getName());

		spy(Configuration.class);
		when(Configuration.get("provider"))
			.thenReturn(" " + LoggingProviderOne.class.getName() + " , " + LoggingProviderTwo.class.getName() + " ");

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(BundleLoggingProvider.class);
		assertThat(Whitebox.getInternalState(createdProvider, LoggingProvider[].class))
			.hasSize(2)
			.hasAtLeastOneElementOfType(LoggingProviderOne.class)
			.hasAtLeastOneElementOfType(LoggingProviderTwo.class);
	}

	/**
	 * Verifies that a defined logging provider list of several identical items can be loaded.
	 *
	 * @throws Exception
	 *             Failed creating service or invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	@PrepareForTest(Configuration.class)
	public void specificProviderListRepeated() throws Exception {
		FileSystem.createServiceFile(LoggingProvider.class, LoggingProviderOne.class.getName(), LoggingProviderTwo.class.getName());

		spy(Configuration.class);
		when(Configuration.get("provider"))
			.thenReturn(LoggingProviderTwo.class.getName() + " , " + LoggingProviderTwo.class.getName());

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(BundleLoggingProvider.class);
		assertThat(Whitebox.getInternalState(createdProvider, LoggingProvider[].class))
			.hasSize(2)
			.hasAtLeastOneElementOfType(LoggingProviderTwo.class)
			.hasAtLeastOneElementOfType(LoggingProviderTwo.class);
	}
	
	/**
	 * Verifies that an empty provider list generates the correct warnings and errors.
	 *
	 * @throws Exception
	 *             Failed creating service or invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	@PrepareForTest(Configuration.class)
	public void specificProviderEmptyList() throws Exception {
		FileSystem.createServiceFile(LoggingProvider.class, LoggingProviderOne.class.getName(), LoggingProviderTwo.class.getName());

		spy(Configuration.class);
		when(Configuration.get("provider")).thenReturn(" ");

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(NopLoggingProvider.class);
		
		assertThat(systemStream.consumeErrorOutput())
			.contains("WARN")
			.contains("'empty string' will be ignored.")
			.contains("ERROR")
			.contains("Logging will be disabled.");
	}
	
	/**
	 * Verifies that an accurate error message will be output, if a requested logging provider is not available.
	 *
	 * @throws Exception
	 *             Failed creating service or invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	@PrepareForTest(Configuration.class)
	public void specificProviderIsMissing() throws Exception {
		spy(Configuration.class);
		when(Configuration.get("provider")).thenReturn(ProviderRegistryTest.class.getName() + "$AnotherLoggingProvider");

		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(NopLoggingProvider.class);

		assertThat(systemStream.consumeErrorOutput())
			.contains("ERROR")
			.contains(ProviderRegistryTest.class.getName() + "$AnotherLoggingProvider");
	}

	/**
	 * Verifies that {@link NopLoggingProvider} can be set explicitly if multiple  logging providers are available.
	 *
	 * @throws Exception
	 *             Failed creating service or invoking private method {@link ProviderRegistry#loadLoggingProvider()}
	 */
	@Test
	@PrepareForTest(Configuration.class)
	public void nopProvider() throws Exception {
		FileSystem.createServiceFile(LoggingProvider.class, LoggingProviderOne.class.getName(), LoggingProviderTwo.class.getName());
	
		spy(Configuration.class);
		when(Configuration.get("provider")).thenReturn("nop");
	
		LoggingProvider createdProvider = Whitebox.invokeMethod(ProviderRegistry.class, "loadLoggingProvider");
		assertThat(createdProvider).isInstanceOf(NopLoggingProvider.class);
	}

	/**
	 * Dummy logging provider class for service loader.
	 */
	public static final class LoggingProviderOne extends AbstractLoggingProvider {

	}

	/**
	 * Another dummy logging provider class for service loader.
	 */
	public static final class LoggingProviderTwo extends AbstractLoggingProvider {

	}

	/**
	 * Base dummy logging provider class that does nothing.
	 */
	public abstract static class AbstractLoggingProvider implements LoggingProvider {

		@Override
		public final ContextProvider getContextProvider() {
			return new NopContextProvider();
		}

		@Override
		public final Level getMinimumLevel() {
			return Level.OFF;
		}

		@Override
		public final Level getMinimumLevel(final String tag) {
			return Level.OFF;
		}

		@Override
		public final boolean isEnabled(final int depth, final String tag, final Level level) {
			return false;
		}

		@Override
		public final void log(final int depth, final String tag, final Level level, final Throwable exception,
			final MessageFormatter formatter, final Object obj, final Object... arguments) {
		}

		@Override
		public void log(final String loggerClassName, final String tag, final Level level, final Throwable exception,
			final MessageFormatter formatter, final Object obj, final Object... arguments) {
		}

		@Override
		public void shutdown() {
		}

	}

}
