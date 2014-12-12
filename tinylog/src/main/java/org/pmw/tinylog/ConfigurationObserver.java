/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.pmw.tinylog.writers.Writer;

/**
 * Thread to observe a configuration file and reload changes.
 */
abstract class ConfigurationObserver extends Thread {

	private static final String THREAD_NAME = "tinylog-ConfigurationObserver";
	private static final Configuration DEFAULT_CONFIGURATION = Configurator.defaultConfig().create();

	private static final Object mutex = new Object();
	private static ConfigurationObserver activeObserver;

	private final Configurator basisConfigurator;
	private final Properties basisProperties;
	private final String file;

	private volatile boolean shutdown;

	private ConfigurationObserver(final Configurator basisConfigurator, final Properties basisProperties, final String file) {
		this.basisConfigurator = basisConfigurator;
		this.basisProperties = basisProperties;
		this.file = file;
		this.shutdown = false;
		setName(THREAD_NAME);
		setPriority((NORM_PRIORITY + MIN_PRIORITY) / 2);
		setDaemon(true);
	}

	/**
	 * Create a thread to observe a file from file system.
	 *
	 * @param configurator
	 *            Basis configuration
	 * @param properties
	 *            Basis properties
	 * @param file
	 *            Configuration file to observe
	 * @return A new instance of {@link org.pmw.tinylog.ConfigurationObserver ConfigurationObserver}
	 */
	static ConfigurationObserver createFileConfigurationObserver(final Configurator configurator, final Properties properties, final String file) {
		return new ConfigurationObserver(configurator, properties, file) {

			@Override
			protected InputStream openInputStream() {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException ex) {
					return null;
				}
			}

		};
	}

	/**
	 * Create a thread to observe a file from classpath.
	 *
	 * @param configurator
	 *            Basis configuration
	 * @param properties
	 *            Basis properties
	 * @param file
	 *            Configuration file to observe
	 * @return A new instance of {@link org.pmw.tinylog.ConfigurationObserver ConfigurationObserver}
	 */
	static ConfigurationObserver createResourceConfigurationObserver(final Configurator configurator, final Properties properties, final String file) {
		return new ConfigurationObserver(configurator, properties, file) {

			@Override
			protected InputStream openInputStream() {
				return ConfigurationObserver.class.getClassLoader().getResourceAsStream(file);
			}

		};
	}

	/**
	 * Get the active configuration observer.
	 *
	 * @return Active configuration observer or <code>null</code> if there is no active configuration observer
	 */
	public static ConfigurationObserver getActiveObserver() {
		synchronized (mutex) {
			return activeObserver;
		}
	}

	@Override
	public void start() {
		synchronized (mutex) {
			Configurator.shutdownConfigurationObserver(true);
			super.start();
			activeObserver = this;
		}
	}

	@Override
	public final void run() {
		Properties oldProperties = basisProperties;
		Configurator oldConfigurator = basisConfigurator;
		while (!shutdown) {
			Properties properties = readProperties();

			if (properties != null) {
				Properties systemProperties = (Properties) System.getProperties().clone();
				for (Object key : systemProperties.keySet()) {
					String name = (String) key;
					if (name.startsWith("tinylog.")) {
						properties.put(key, systemProperties.getProperty(name));
					}
				}
			}

			if (changed(properties, oldProperties)) {
				Configurator configurator = oldConfigurator.copy();
				if (properties != null) {
					if (levelHasChanged(properties, oldProperties)) {
						configurator.level(DEFAULT_CONFIGURATION.getLevel()).resetCustomLevels();
						PropertiesLoader.readLevel(configurator, properties);
					}
					if (formatPaternHasChanged(properties, oldProperties)) {
						configurator.formatPattern(DEFAULT_CONFIGURATION.getFormatPattern());
						PropertiesLoader.readFormatPattern(configurator, properties);
					}
					if (localeHasChanged(properties, oldProperties)) {
						configurator.locale(DEFAULT_CONFIGURATION.getLocale());
						PropertiesLoader.readLocale(configurator, properties);
					}
					if (maxStackTraceElementsHasChanged(properties, oldProperties)) {
						configurator.maxStackTraceElements(DEFAULT_CONFIGURATION.getMaxStackTraceElements());
						PropertiesLoader.readMaxStackTraceElements(configurator, properties);
					}
					if (writerHasChanged(properties, oldProperties)) {
						Iterator<Writer> iterator = DEFAULT_CONFIGURATION.getWriters().iterator();
						configurator.writer(iterator.hasNext() ? iterator.next() : null);
						while (iterator.hasNext()) {
							configurator.addWriter(iterator.next());
						}
						PropertiesLoader.readWriters(configurator, properties);
					}
					if (writingThreadHasChanged(properties, oldProperties)) {
						WritingThread writingThread = DEFAULT_CONFIGURATION.getWritingThread();
						if (writingThread == null) {
							configurator.writingThread(false);
						} else {
							configurator.writingThread(writingThread.getNameOfThreadToObserve(), writingThread.getPriority());
						}
						PropertiesLoader.readWritingThread(configurator, properties);
					}
				}
				configurator.activate();
				oldConfigurator = configurator;
			}

			oldProperties = properties;

			try {
				sleep(1000L);
			} catch (InterruptedException ex) {
				// Ignore and continue
			}
		}
	}

	/**
	 * Shutdown thread.
	 */
	public void shutdown() {
		shutdown = true;
		interrupt();

		synchronized (mutex) {
			if (activeObserver == this) {
				activeObserver = null;
			}
		}
	}

	/**
	 * Open the configuration file.
	 *
	 * @return Stream of configuration file or <code>null</code> if not exists.
	 */
	protected abstract InputStream openInputStream();

	private static boolean changed(final Properties properties, final Properties oldProperties) {
		if (oldProperties == null) {
			return properties != null;
		} else if (properties == null) {
			return true;
		} else {
			Set<Object> keys = new HashSet<>();
			keys.addAll(properties.keySet());
			keys.addAll(oldProperties.keySet());

			for (Object key : keys) {
				Object newValue = properties.get(key);
				Object oldValue = oldProperties.get(key);
				if ((newValue == null && oldValue != null) || (newValue != null && !newValue.equals(oldValue))) {
					return true;
				}
			}

			return false;
		}
	}

	private Properties readProperties() {
		try (InputStream stream = openInputStream()) {
			if (stream == null) {
				InternalLogger.error("Failed to open \"{}\"", file);
				return null;
			} else {
				Properties properties = new Properties();
				properties.load(stream);
				return properties;
			}
		} catch (IOException ex) {
			InternalLogger.error(ex, "Failed to read properties file");
			return null;
		}
	}

	private static boolean levelHasChanged(final Properties properties, final Properties oldProperties) {
		return compare(properties, oldProperties, Collections.singletonList(PropertiesLoader.LEVEL_PROPERTY),
				Collections.singletonList(PropertiesLoader.CUSTOM_LEVEL_PREFIX));
	}

	private static boolean formatPaternHasChanged(final Properties properties, final Properties oldProperties) {
		return compare(properties, oldProperties, Collections.singletonList(PropertiesLoader.FORMAT_PROPERTY), Collections.<String> emptyList());
	}

	private static boolean localeHasChanged(final Properties properties, final Properties oldProperties) {
		return compare(properties, oldProperties, Collections.singletonList(PropertiesLoader.LOCALE_PROPERTY), Collections.<String> emptyList());
	}

	private static boolean maxStackTraceElementsHasChanged(final Properties properties, final Properties oldProperties) {
		return compare(properties, oldProperties, Collections.singletonList(PropertiesLoader.STACKTRACE_PROPERTY), Collections.<String> emptyList());
	}

	private static boolean writerHasChanged(final Properties properties, final Properties oldProperties) {
		return compare(properties, oldProperties, Collections.<String> emptyList(), Collections.singletonList(PropertiesLoader.WRITER_PROPERTY));
	}

	private static boolean writingThreadHasChanged(final Properties properties, final Properties oldProperties) {
		return compare(properties, oldProperties, Arrays.asList(PropertiesLoader.WRITING_THREAD_PROPERTY, PropertiesLoader.WRITING_THREAD_OBSERVE_PROPERTY,
				PropertiesLoader.WRITING_THREAD_PRIORITY_PROPERTY), Collections.<String> emptyList());
	}

	private static boolean compare(final Properties a, final Properties b, final List<String> fullKeys, final List<String> startPatterns) {
		Properties relevantA = extract(a, fullKeys, startPatterns);
		Properties relevantB = extract(b, fullKeys, startPatterns);
		return !relevantA.equals(relevantB);
	}

	private static Properties extract(final Properties properties, final List<String> fullKeys, final List<String> startPatterns) {
		Properties relevantProperties = new Properties();

		for (String key : fullKeys) {
			if (properties.containsKey(key)) {
				relevantProperties.put(key, properties.get(key));
			}
		}

		for (String startPattern : startPatterns) {
			for (Object key : properties.keySet()) {
				String name = (String) key;
				if (name.startsWith(startPattern)) {
					relevantProperties.put(name, properties.get(name));
				}
			}
		}

		return relevantProperties;
	}

}
