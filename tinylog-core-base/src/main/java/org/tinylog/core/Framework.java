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

import org.tinylog.core.backend.BundleLoggingBackend;
import org.tinylog.core.backend.InternalLoggingBackend;
import org.tinylog.core.backend.InternalLoggingBackendBuilder;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.NopLoggingBackendBuilder;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.RuntimeProvider;

/**
 * Storage for {@link Configuration}, {@link Hook Hooks}, and {@link LoggingBackend}.
 */
public class Framework {

	private final Object mutex = new Object();

	private final InternalLogger logger;
	private final RuntimeFlavor runtime;
	private final Configuration configuration;
	private final Collection<Hook> hooks;

	private LoggingBackend loggingBackend;
	private boolean running;

	/**
	 * Loads the configuration from default properties file and hooks from service files.
	 *
	 * @param loadConfiguration {@code true} to load the configuration from found properties file, {@code false} to
	 *                          keep the configuration empty
	 * @param loadHooks {@code true} to load all hooks that are registered as services, {@code false} to do not load
	 *                  any hooks
	 */
	public Framework(boolean loadConfiguration, boolean loadHooks) {
		this.logger = new InternalLogger();
		this.runtime = new RuntimeProvider().getRuntime();
		this.configuration = new Configuration(this);
		this.hooks = loadHooks ? loadHooks() : new ArrayList<>();

		if (loadConfiguration) {
			configuration.loadPropertiesFile(getClassLoader());
		}
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
	 * Gets the internal logger.
	 *
	 * @return Internal logger instance
	 */
	public InternalLogger getLogger() {
		return logger;
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
	 * Gets the logging backend from the stored configuration.
	 *
	 * @return The logging backend implementation
	 */
	public LoggingBackend getLoggingBackend() {
		if (loggingBackend == null) {
			synchronized (mutex) {
				if (loggingBackend == null) {
					loadLoggingBackend();
				}
			}
		}

		return loggingBackend;
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

				loadLoggingBackend();
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

				loggingBackend = null;
			}
		}
	}

	/**
	 * Creates a new {@link LoggingBackend}.
	 *
	 * @return The newly created logging backend instance
	 */
	protected LoggingBackend createLoggingBackend() {
		List<String> names = configuration.getList("backend");
		Map<String, LoggingBackendBuilder> builders = new HashMap<>();
		List<LoggingBackend> backends = new ArrayList<>();

		for (LoggingBackendBuilder builder : ServiceLoader.load(LoggingBackendBuilder.class, getClassLoader())) {
			builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder);
		}

		for (String name : names) {
			LoggingBackendBuilder builder = builders.get(name.toLowerCase(Locale.ENGLISH));
			if (builder == null) {
				logger.error(
					null,
					"Could not find any logging backend with the name \"{}\" in the classpath",
					name
				);
			} else {
				backends.add(builder.create(this));
			}
		}

		if (backends.isEmpty()) {
			for (Map.Entry<String, LoggingBackendBuilder> entry : builders.entrySet()) {
				if (!(entry.getValue() instanceof NopLoggingBackendBuilder)
						&& !(entry.getValue() instanceof InternalLoggingBackendBuilder)) {
					backends.add(entry.getValue().create(this));
				}
			}
		}

		if (backends.isEmpty()) {
			logger.warn(null, "No logging backend could be found in the classpath. Therefore, no log "
				+ "entries will be output. Please add tinylog-impl.jar or any other logging backend for outputting log "
				+ "entries, or disable logging explicitly by setting \"backend = nop\" in the configuration.");
			return new InternalLoggingBackend();
		} else if (backends.size() == 1) {
			return backends.get(0);
		} else {
			return new BundleLoggingBackend(backends);
		}
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
	 * Freezes the stored configuration and creates a new logging backend, if none is assigned yet.
	 */
	private void loadLoggingBackend() {
		configuration.freeze();
		startUp();

		if (loggingBackend == null) {
			loggingBackend = createLoggingBackend();
			logger.init(this);
		}
	}

}
