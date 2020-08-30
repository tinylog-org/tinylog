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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.tinylog.core.providers.BundleLoggingProvider;
import org.tinylog.core.providers.InternalLoggingProvider;
import org.tinylog.core.providers.InternalLoggingProviderBuilder;
import org.tinylog.core.providers.LoggingProvider;
import org.tinylog.core.providers.LoggingProviderBuilder;
import org.tinylog.core.providers.NopLoggingProviderBuilder;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.RuntimeProvider;

/**
 * Storage for {@link Configuration}, {@link Hook Hooks}, and {@link LoggingProvider}.
 */
public class Framework {

	private final Object mutex = new Object();

	private final RuntimeFlavor runtime;
	private final Configuration configuration;
	private final Collection<Hook> hooks;

	private LoggingProvider loggingProvider;
	private boolean running;

	/**
	 * Loads the configuration from default properties file and hooks from service files.
	 */
	public Framework() {
		this.runtime = new RuntimeProvider().getRuntime();
		this.configuration = loadConfiguration();
		this.hooks = loadHooks();
	}

	/**
	 * Initializes the framework with a custom configuration and no hooks.
	 *
	 * @param configuration Configuration to store
	 */
	public Framework(Configuration configuration) {
		this.runtime = new RuntimeProvider().getRuntime();
		this.configuration = configuration;
		this.hooks = new ArrayList<>();
	}

	/**
	 * Gets the class loader for loading resources and services from the classpath.
	 *
	 * @return A valid and existing class loader instance
	 */
	public ClassLoader getClassLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader == null ? Framework.class.getClassLoader() : classLoader;
	}

	/**
	 * Provides the appropriate {@link RuntimeFlavor} for the actual virtual machine.
	 *
	 * @return The appropriate runtime instance
	 */
	public RuntimeFlavor getRuntime() {
		return runtime;
	}

	/**
	 * Gets the stored configuration.
	 *
	 * @return The stored configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Gets the logging provider from the stored configuration.
	 *
	 * @return The logging provider implementation
	 */
	public LoggingProvider getLoggingProvider() {
		if (loggingProvider == null) {
			synchronized (mutex) {
				if (loggingProvider == null) {
					loadLoggingProvider();
				}
			}
		}

		return loggingProvider;
	}

	/**
	 * Registers a new {@link Hook}.
	 *
	 * @param hook Hook to register
	 */
	public void registerHook(Hook hook) {
		synchronized (mutex) {
			hooks.add(hook);
		}
	}

	/**
	 * Removes a registered {@link Hook}.
	 *
	 * @param hook Hook to unregister
	 */
	public void removeHook(Hook hook) {
		synchronized (mutex) {
			hooks.remove(hook);
		}
	}

	/**
	 * Starts the framework and calls the start up method on all registered hooks, if the framework is not yet started.
	 */
	public void startUp() {
		synchronized (mutex) {
			if (!running) {
				running = true;

				for (Hook hook : hooks) {
					hook.startUp();
				}

				loadLoggingProvider();
			}
		}
	}

	/**
	 * Stops the framework and calls the shut down method on all registered hooks, if the framework is not yet shut
	 * down.
	 */
	public void shutDown() {
		synchronized (mutex) {
			if (running) {
				running = false;

				for (Hook hook : hooks) {
					hook.shutDown();
				}

				loggingProvider = null;
			}
		}
	}

	/**
	 * Creates a new {@link Configuration} and loads the settings from default properties file if available.
	 *
	 * @return The created and pre-filled configuration
	 */
	private Configuration loadConfiguration() {
		Configuration configuration = new Configuration();
		configuration.loadPropertiesFile(getClassLoader());
		return configuration;
	}

	/**
	 * Loads all hooks that are registered as a {@link ServiceLoader service} in {@code META-INF/services}.
	 *
	 * @return All found hooks
	 */
	private Collection<Hook> loadHooks() {
		Collection<Hook> hooks = new ArrayList<>();
		for (Hook hook : ServiceLoader.load(Hook.class, getClassLoader())) {
			hooks.add(hook);
		}
		return hooks;
	}

	/**
	 * Freezes the stored configuration and creates a new logging provider, if none is assigned yet.
	 */
	private void loadLoggingProvider() {
		configuration.freeze();
		startUp();

		if (loggingProvider == null) {
			createLoggingProvider();
		}
	}

	/**
	 * Creates a new {@link LoggingProvider}.
	 */
	private void createLoggingProvider() {
		List<String> names = configuration.getList("backend");
		Map<String, LoggingProviderBuilder> builders = new HashMap<>();
		List<LoggingProvider> providers = new ArrayList<>();

		for (LoggingProviderBuilder builder : ServiceLoader.load(LoggingProviderBuilder.class, getClassLoader())) {
			builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder);
		}

		for (String name : names) {
			LoggingProviderBuilder builder = builders.get(name.toLowerCase(Locale.ENGLISH));
			if (builder == null) {
				System.err.println(
					"Could not find any logging backend with the name \"" + name + "\" in the classpath"
				);
			} else {
				providers.add(builder.create(this));
			}
		}

		if (providers.isEmpty()) {
			for (Map.Entry<String, LoggingProviderBuilder> entry : builders.entrySet()) {
				if (!(entry.getValue() instanceof NopLoggingProviderBuilder)
						&& !(entry.getValue() instanceof InternalLoggingProviderBuilder)) {
					providers.add(entry.getValue().create(this));
				}
			}
		}

		if (providers.isEmpty()) {
			loggingProvider = new InternalLoggingProvider();
			System.err.println(
				"No logging backend could be found in the classpath. Therefore, no log entries will be output. "
				+ "Please add tinylog-impl.jar or any other logging backend for outputting log entries."
			);
		} else if (providers.size() == 1) {
			loggingProvider = providers.get(0);
		} else {
			loggingProvider = new BundleLoggingProvider(providers);
		}
	}

}
