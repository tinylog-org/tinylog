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
import java.util.ServiceLoader;

import org.tinylog.core.providers.LoggingProvider;
import org.tinylog.core.providers.NopLoggingProvider;

/**
 * Storage for {@link Configuration}, {@link Hook Hooks}, and {@link LoggingProvider}.
 */
public final class Framework {

	private final Configuration configuration;
	private final Collection<Hook> hooks;
	private final Object loggingProviderMutex;

	private LoggingProvider loggingProvider;
	private boolean running;

	/**
	 * Loads the configuration from default properties file and hooks from service files.
	 */
	public Framework() {
		this(loadConfiguration(), loadHooks());
	}

	/**
	 * Initializes the framework with a custom configuration and custom hooks.
	 *
	 * @param configuration Configuration to store
	 * @param hooks Hooks to store
	 */
	public Framework(Configuration configuration, Collection<Hook> hooks) {
		this.configuration = configuration;
		this.hooks = hooks;
		this.loggingProviderMutex = new Object();
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
			synchronized (loggingProviderMutex) {
				loadLoggingProvider();
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
		hooks.add(hook);
	}

	/**
	 * Removes a registered {@link Hook}.
	 *
	 * @param hook Hook to unregister
	 */
	public void removeHook(Hook hook) {
		hooks.remove(hook);
	}

	/**
	 * Starts the framework and calls the start up method on all registered hooks, if the framework is not yet started.
	 */
	public void startUp() {
		synchronized (hooks) {
			running = true;

			for (Hook hook : hooks) {
				hook.startUp();
			}
		}

	}

	/**
	 * Stops the framework and calls the shut down method on all registered hooks, if the framework is not yet shut
	 * down.
	 */
	public void shutDown() {
		synchronized (hooks) {
			if (running) {
				running = false;

				for (Hook hook : hooks) {
					hook.shutDown();
				}
			}
		}
	}

	/**
	 * Creates a new {@link Configuration} and loads the settings from default properties file if available.
	 *
	 * @return The created and pre-filled configuration
	 */
	private static Configuration loadConfiguration() {
		Configuration configuration = new Configuration();
		configuration.loadPropertiesFile();
		return configuration;
	}

	/**
	 * Loads all hooks that are registered as {@link java.util.ServiceLoader service} in {@code META-INF/services}.
	 *
	 * @return All found hooks
	 */
	private static Collection<Hook> loadHooks() {
		Collection<Hook> hooks = new ArrayList<Hook>();
		for (Hook hook : ServiceLoader.load(Hook.class)) {
			hooks.add(hook);
		}
		return hooks;
	}

	/**
	 * Freezes the stored configuration and loads the logging provider.
	 */
	private void loadLoggingProvider() {
		configuration.freeze();
		loggingProvider = new NopLoggingProvider();
	}

}
