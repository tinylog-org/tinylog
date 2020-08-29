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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tinylog.core.providers.BundleLoggingProvider;
import org.tinylog.core.providers.InternalLoggingProvider;
import org.tinylog.core.providers.LoggingProvider;
import org.tinylog.core.providers.LoggingProviderBuilder;
import org.tinylog.core.providers.NopLoggingProviderBuilder;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.RuntimeProvider;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static java.util.stream.Collectors.joining;
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
		assertThat(new Framework().getRuntime()).isNotNull();
	}

	/**
	 * Verifies that a {@link Configuration} is provided.
	 */
	@Test
	void configuration() {
		assertThat(new Framework().getConfiguration()).isNotNull();
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
			ClassLoader classLoader = new Framework().getClassLoader();
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
				ClassLoader providedClassLoader = new Framework().getClassLoader();
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
		 * Verifies that a registered hook is called at startup.
		 */
		@Test
		void registerHookForStartupOnly() {
			Hook hook = mock(Hook.class);
			Framework framework = new Framework();
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
			Framework framework = new Framework();

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
			Framework framework = new Framework();
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
			Framework framework = new Framework();
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
			Framework framework = new Framework();
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
			Framework framework = new Framework();
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
		 * Custom temporary folder for creating files.
		 */
		@TempDir
		Path folder;

		/**
		 * Verifies that the internal logging provider is loaded if none other is available.
		 */
		@Test
		void loadInternalLoggingProvider() throws Exception {
			Framework framework = new Framework();

			String output = tapSystemErr(
				() -> assertThat(framework.getLoggingProvider()).isInstanceOf(InternalLoggingProvider.class)
			);

			assertThat(output).contains("tinylog-impl");
		}

		/**
		 * Verifies that a logging provider is loaded if it is the only available.
		 */
		@Test
		void loadSingleAvailableProvider() throws IOException {
			Framework framework = createCustomFramework(
				new Configuration(),
				TestOneLoggingProviderBuilder.class
			);

			assertThat(framework.getLoggingProvider()).isSameAs(TestOneLoggingProviderBuilder.provider);
		}

		/**
		 * Verifies that all available logging providers are loaded and bundled in a {@link BundleLoggingProvider}.
		 */
		@Test
		void loadAllAvailableProviders() throws IOException {
			Framework framework = createCustomFramework(
				new Configuration(),
				TestOneLoggingProviderBuilder.class,
				TestTwoLoggingProviderBuilder.class
			);

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
		@Test
		void loadSingleProviderByName() throws IOException {
			Framework framework = createCustomFramework(
				new Configuration().set("backend", "test2"),
				TestOneLoggingProviderBuilder.class,
				TestTwoLoggingProviderBuilder.class
			);

			assertThat(framework.getLoggingProvider()).isSameAs(TestTwoLoggingProviderBuilder.provider);
		}

		/**
		 * Verifies that several logging providers can be defined by name if multiple are available.
		 */
		@Test
		void loadMultipleProvidersByName() throws IOException {
			Framework framework = createCustomFramework(
				new Configuration().set("backend", "test1, nop"),
				TestOneLoggingProviderBuilder.class,
				TestTwoLoggingProviderBuilder.class
			);

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
		@Test
		void fallbackForEntireInvalidName() throws Exception {
			Framework framework = createCustomFramework(
				new Configuration().set("backend", "test3"),
				TestOneLoggingProviderBuilder.class
			);

			String output = tapSystemErr(() -> {
				LoggingProvider provider = framework.getLoggingProvider();
				assertThat(provider).isSameAs(TestOneLoggingProviderBuilder.provider);
			});

			assertThat(output).contains("test3");
		}

		/**
		 * Verifies that all other configured logging providers will be created, if one of them does not exist.
		 */
		@Test
		void fallbackForPartialInvalidName() throws Exception {
			Framework framework = createCustomFramework(
				new Configuration().set("backend", "test0, test1"),
				TestOneLoggingProviderBuilder.class,
				TestTwoLoggingProviderBuilder.class
			);

			String output = tapSystemErr(() -> {
				LoggingProvider provider = framework.getLoggingProvider();
				assertThat(provider).isSameAs(TestOneLoggingProviderBuilder.provider);
			});

			assertThat(output).contains("test0");
		}

		/**
		 * Creates a {@link Framework} with a custom configuration and additional registered
		 * {@link LoggingProviderBuilder LoggingProviderBuilders}.
		 *
		 * @param configuration	Custom configuration to apply
		 * @param implementations Additional logging provider builders to register a service
		 * @return The created framework
		 */
		@SafeVarargs
		private Framework createCustomFramework(Configuration configuration,
				Class<? extends LoggingProviderBuilder>... implementations) throws IOException {
			String services = Arrays.stream(implementations).map(Class::getName).collect(joining("\n"));
			Path file = folder.resolve("META-INF").resolve("services").resolve(LoggingProviderBuilder.class.getName());
			Files.createDirectories(file.getParent());
			Files.write(file, services.getBytes(StandardCharsets.UTF_8));
			URL url = folder.toUri().toURL();

			return new Framework(new RuntimeProvider().getRuntime(), configuration, Collections.emptyList()) {
				@Override
				public ClassLoader getClassLoader() {
					return new URLClassLoader(new URL[] {url}, super.getClassLoader());
				}
			};
		}

		/**
		 * Verifies that the configuration becomes frozen after providing a logging provider.
		 */
		@Test
		void freezeConfigurationAfterProvidingLoggingProvider() {
			Framework framework = new Framework();
			assertThat(framework.getConfiguration().isFrozen()).isFalse();
			assertThat(framework.getLoggingProvider()).isNotNull();
			assertThat(framework.getConfiguration().isFrozen()).isTrue();
		}

	}

	/**
	 * Additional logging provider builder for JUnit test.
	 */
	public static final class TestOneLoggingProviderBuilder implements LoggingProviderBuilder {

		private static final LoggingProvider provider = mock(LoggingProvider.class);

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
	 * Additional logging provider builder for JUnit test.
	 */
	public static final class TestTwoLoggingProviderBuilder implements LoggingProviderBuilder {

		private static final LoggingProvider provider = mock(LoggingProvider.class);

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
