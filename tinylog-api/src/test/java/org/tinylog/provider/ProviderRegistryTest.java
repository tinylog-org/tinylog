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
import org.powermock.reflect.Whitebox;
import org.tinylog.Level;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProviderRegistry}.
 */
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

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("WARNING")
			.containsOnlyOnce("logging")
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
		assertThat(createdProvider).isInstanceOf(WrapperLoggingProvider.class);
		assertThat(Whitebox.getInternalState(createdProvider, LoggingProvider[].class))
			.hasSize(2)
			.hasAtLeastOneElementOfType(LoggingProviderOne.class)
			.hasAtLeastOneElementOfType(LoggingProviderTwo.class);
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
		public final Level getMinimumLevel(final String tag) {
			return Level.OFF;
		}

		@Override
		public final boolean isEnabled(final int depth, final String tag, final Level level) {
			return false;
		}

		@Override
		public final void log(final int depth, final String tag, final Level level, final Throwable exception, final Object obj,
			final Object... arguments) {
		}

		@Override
		public final void internal(final int depth, final Level level, final Throwable exception, final String message) {
		}

	}

}
