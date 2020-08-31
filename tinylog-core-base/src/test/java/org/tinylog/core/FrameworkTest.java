/*
 * Copyright 2020 Martin Winandy
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

import java.util.Collection;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.providers.BundleLoggingProvider;
import org.tinylog.core.providers.InternalLoggingProvider;
import org.tinylog.core.providers.LoggingProvider;
import org.tinylog.core.providers.LoggingProviderBuilder;
import org.tinylog.core.providers.NopLoggingProvider;
import org.tinylog.core.providers.NopLoggingProviderBuilder;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.test.RegisterService;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class FrameworkTest {

	/**
	 * Verifies that a {@link RuntimeFlavor} is provided.
	 */
	@Test
	void runtime() {
		assertThat(new Framework(false, false).getRuntime()).isNotNull();
	}

	/**
	 * Verifies that a {@link Configuration} is provided.
	 */
	@Test
	void configuration() {
		assertThat(new Framework(false, false).getConfiguration()).isNotNull();
	}

	/**
	 * Tests for {@link Framework#getClassLoader()}.
	 */
	@Nested
	class ClassLoaderGetter {

		/**
		 * Verifies that the context class loader from the current thread will be used if available.
		 */
		@Test
		void provideFromCurrentThread() {
			ClassLoader classLoader = new Framework(false, false).getClassLoader();
			assertThat(classLoader).isNotNull().isEqualTo(Thread.currentThread().getContextClassLoader());
		}

		/**
		 * Verifies that the class loader from {@link Framework} class will be used, if the context class loader from
		 * the current thread is unavailable.
		 */
		@Test
		void provideFromClass() {
			Thread thread = Thread.currentThread();
			ClassLoader threadClassLoader = thread.getContextClassLoader();
			try {
				thread.setContextClassLoader(null);
				ClassLoader providedClassLoader = new Framework(false, false).getClassLoader();
				assertThat(providedClassLoader).isNotNull().isEqualTo(Framework.class.getClassLoader());
			} finally {
				thread.setContextClassLoader(threadClassLoader);
			}
		}

	}

	/**
	 * Tests for {@link Framework#registerHook(Hook)}, {@link Framework#removeHook(Hook)}, {@link Framework#startUp()},
	 * and {@link Framework#shutDown()}.
	 */
	@Nested
	class LifeCycle {

		/**
		 * Verifies that as service registered hooks are loaded.
		 */
		@RegisterService(service = Hook.class, implementations = TestHook.class)
		@Test
		void loadServiceHooks() {
			Framework framework = new Framework(false, true);
			assertThat(TestHook.running).isFalse();

			try {
				framework.startUp();
				assertThat(TestHook.running).isTrue();
			} finally {
				framework.shutDown();
				assertThat(TestHook.running).isFalse();
			}
		}

		/**
		 * Verifies that a registered hook is called at startup.
		 */
		@Test
		void registerHookForStartupOnly() {
			Hook hook = mock(Hook.class);
			Framework framework = new Framework(false, false);
			framework.registerHook(hook);

			try {
				framework.startUp();
				framework.removeHook(hook);
			} finally {
				framework.shutDown();
			}

			verify(hook).startUp();
			verify(hook, never()).shutDown();
		}

		/**
		 * Verifies that a registered hook is called at shutdown.
		 */
		@Test
		void registerHookForShutdownOnly() {
			Hook hook = mock(Hook.class);
			Framework framework = new Framework(false, false);

			try {
				framework.startUp();
				framework.registerHook(hook);
			} finally {
				framework.shutDown();
			}

			verify(hook, never()).startUp();
			verify(hook).shutDown();
		}

		/**
		 * Verifies that a registered hook is called at startup.
		 */
		@Test
		void registerHookForStartupAndShutdown() {
			Hook hook = mock(Hook.class);
			Framework framework = new Framework(false, false);
			framework.registerHook(hook);

			try {
				framework.startUp();
			} finally {
				framework.shutDown();
			}

			verify(hook).startUp();
			verify(hook).shutDown();
		}

		/**
		 * Verifies that hooks are called only called during the first startup.
		 */
		@Test
		void ignoreSecondStartup() {
			Hook hook = mock(Hook.class);
			Framework framework = new Framework(false, false);
			framework.registerHook(hook);

			try {
				framework.startUp();
				verify(hook).startUp();
				clearInvocations(hook);

				framework.startUp();
				verify(hook, never()).startUp();
			} finally {
				framework.shutDown();
			}
		}

		/**
		 * Verifies that hooks are called only called during the first shutdown.
		 */
		@Test
		void ignoreSecondShutdown() {
			Hook hook = mock(Hook.class);
			Framework framework = new Framework(false, false);
			framework.registerHook(hook);

			try {
				framework.startUp();
			} finally {
				framework.shutDown();
				verify(hook).shutDown();
				clearInvocations(hook);

				framework.shutDown();
				verify(hook, never()).shutDown();
			}
		}

		/**
		 * Verifies that the configuration becomes frozen after startup.
		 */
		@Test
		void freezeConfigurationAfterStartup() {
			Framework framework = new Framework(false, false);
			try {
				assertThat(framework.getConfiguration().isFrozen()).isFalse();
				framework.startUp();
				assertThat(framework.getConfiguration().isFrozen()).isTrue();
			} finally {
				framework.shutDown();
			}
		}

	}

	/**
	 * Tests for {@link Framework#getLoggingProvider()}.
	 */
	@Nested
	class LoggingProviderGetter {

		/**
		 * Verifies that the internal logging provider is loaded if none other is available.
		 */
		@Test
		void loadInternalLoggingProvider() throws Exception {
			Framework framework = new Framework(false, false);

			String output = tapSystemErr(
				() -> assertThat(framework.getLoggingProvider()).isInstanceOf(InternalLoggingProvider.class)
			);

			assertThat(output).contains("tinylog-impl");
		}

		/**
		 * Verifies that a logging provider is loaded if it is the only available.
		 */
		@RegisterService(service = LoggingProviderBuilder.class, implementations = TestOneLoggingProviderBuilder.class)
		@Test
		void loadSingleAvailableProvider() {
			Framework framework = new Framework(false, false);
			assertThat(framework.getLoggingProvider()).isSameAs(TestOneLoggingProviderBuilder.provider);
		}

		/**
		 * Verifies that all available logging providers are loaded and bundled in a {@link BundleLoggingProvider}.
		 */
		@RegisterService(
			service = LoggingProviderBuilder.class,
			implementations = {TestOneLoggingProviderBuilder.class, TestTwoLoggingProviderBuilder.class}
		)
		@Test
		void loadAllAvailableProviders() {
			Framework framework = new Framework(false, false);

			LoggingProvider provider = framework.getLoggingProvider();
			assertThat(provider).isInstanceOf(BundleLoggingProvider.class);

			Collection<LoggingProvider> children = ((BundleLoggingProvider) provider).getProviders();
			assertThat(children).containsExactlyInAnyOrder(
				TestOneLoggingProviderBuilder.provider, TestTwoLoggingProviderBuilder.provider
			);
		}

		/**
		 * Verifies that one logging provider can be defined by name if multiple are available.
		 */
		@RegisterService(
			service = LoggingProviderBuilder.class,
			implementations = {TestOneLoggingProviderBuilder.class, TestTwoLoggingProviderBuilder.class}
		)
		@Test
		void loadSingleProviderByName() {
			Framework framework = new Framework(false, false);
			framework.getConfiguration().set("backend", "test2");
			assertThat(framework.getLoggingProvider()).isSameAs(TestTwoLoggingProviderBuilder.provider);
		}

		/**
		 * Verifies that several logging providers can be defined by name if multiple are available.
		 */
		@RegisterService(
			service = LoggingProviderBuilder.class,
			implementations = {TestOneLoggingProviderBuilder.class, TestTwoLoggingProviderBuilder.class}
		)
		@Test
		void loadMultipleProvidersByName() {
			Framework framework = new Framework(true, false);
			framework.getConfiguration().set("backend", "test1, nop");

			LoggingProvider provider = framework.getLoggingProvider();
			assertThat(provider).isInstanceOf(BundleLoggingProvider.class);

			Collection<LoggingProvider> children = ((BundleLoggingProvider) provider).getProviders();
			assertThat(children).containsExactlyInAnyOrder(
				TestOneLoggingProviderBuilder.provider, new NopLoggingProviderBuilder().create(null)
			);
		}

		/**
		 * Verifies that the available logging provider will be created, if the configured logging provider does not
		 * exist.
		 */
		@RegisterService(service = LoggingProviderBuilder.class, implementations = TestOneLoggingProviderBuilder.class)
		@Test
		void fallbackForEntireInvalidName() throws Exception {
			String output = tapSystemErr(() -> {
				Framework framework = new Framework(true, false);
				framework.getConfiguration().set("backend", "test3");
				LoggingProvider provider = framework.getLoggingProvider();
				assertThat(provider).isSameAs(TestOneLoggingProviderBuilder.provider);
			});

			assertThat(output).contains("test3");
		}

		/**
		 * Verifies that all other configured logging providers will be created, if one of them does not exist.
		 */
		@RegisterService(
			service = LoggingProviderBuilder.class,
			implementations = {TestOneLoggingProviderBuilder.class, TestTwoLoggingProviderBuilder.class}
		)
		@Test
		void fallbackForPartialInvalidName() throws Exception {
			String output = tapSystemErr(() -> {
				Framework framework = new Framework(true, false);
				framework.getConfiguration().set("backend", "test0, test1");
				LoggingProvider provider = framework.getLoggingProvider();
				assertThat(provider).isSameAs(TestOneLoggingProviderBuilder.provider);
			});

			assertThat(output).contains("test0");
		}

		/**
		 * Verifies that the configuration becomes frozen after providing a logging provider.
		 */
		@Test
		void freezeConfigurationAfterProvidingLoggingProvider() {
			Framework framework = new Framework(false, false);
			assertThat(framework.getConfiguration().isFrozen()).isFalse();
			assertThat(framework.getLoggingProvider()).isNotNull();
			assertThat(framework.getConfiguration().isFrozen()).isTrue();
		}

	}

	/**
	 * Additional hook for JUnit tests.
	 */
	public static final class TestHook implements Hook {

		private static boolean running;

		@Override
		public void startUp() {
			running = true;
		}

		@Override
		public void shutDown() {
			running = false;
		}

	}

	/**
	 * Additional logging provider builder for JUnit tests.
	 */
	public static final class TestOneLoggingProviderBuilder implements LoggingProviderBuilder {

		private static final LoggingProvider provider = new NopLoggingProvider();

		@Override
		public String getName() {
			return "test1";
		}

		@Override
		public LoggingProvider create(Framework framework) {
			return provider;
		}

	}

	/**
	 * Additional logging provider builder for JUnit tests.
	 */
	public static final class TestTwoLoggingProviderBuilder implements LoggingProviderBuilder {

		private static final LoggingProvider provider = new NopLoggingProvider();

		@Override
		public String getName() {
			return "test2";
		}

		@Override
		public LoggingProvider create(Framework framework) {
			return provider;
		}

	}

}
