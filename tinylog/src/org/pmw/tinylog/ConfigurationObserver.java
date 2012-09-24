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
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Thread to observe a configuration file and reload changes.
 */
abstract class ConfigurationObserver extends Thread {

	private static final String THREAD_NAME = "tinylog-ConfigurationObserver";

	private final Configurator basisConfigurator;

	private ConfigurationObserver(final Configurator basisConfigurator) {
		this.basisConfigurator = basisConfigurator;
		setName(THREAD_NAME);
		setPriority((NORM_PRIORITY + MIN_PRIORITY) / 2);
		setDaemon(true);
	}

	/**
	 * Create a thread to observe a file from file system.
	 * 
	 * @param basisConfigurator
	 *            Plain basis configuration
	 * @param file
	 *            Configuration file to observe
	 * @return A new instance of {@link ConfigurationObserver}
	 */
	static ConfigurationObserver createFileConfigurationObserver(final Configurator basisConfigurator, final String file) {
		return new ConfigurationObserver(basisConfigurator) {

			@Override
			protected InputStream openInputStream() throws FileNotFoundException {
				return new FileInputStream(file);
			}

		};
	}

	/**
	 * Create a thread to observe a file from classpath.
	 * 
	 * @param basisConfigurator
	 *            Plain basis configuration
	 * @param file
	 *            Configuration file to observe
	 * @return A new instance of {@link ConfigurationObserver}
	 */
	static ConfigurationObserver createResourceConfigurationObserver(final Configurator basisConfigurator, final String file) {
		return new ConfigurationObserver(basisConfigurator) {

			@Override
			protected InputStream openInputStream() {
				return Configurator.class.getResourceAsStream(file);
			}

		};
	}

	@Override
	public void run() {
		Properties oldProperties = null;
		while (true) {
			Properties properties = readProperties();
			if (properties != null) {
				if (changed(properties, oldProperties)) {
					Configurator configurator = basisConfigurator.copy();
					PropertiesLoader.readProperties(configurator, properties);
					configurator.activate();
				}
				oldProperties = properties;
			}

			try {
				sleep(1000L);
			} catch (InterruptedException ex) {
				// Ignore
			}
		}
	}

	private boolean changed(final Properties properties, final Properties oldProperties) {
		if (oldProperties == null) {
			return true;
		} else {
			Set<Object> keys = new HashSet<Object>();
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
		InputStream stream = null;
		try {
			stream = openInputStream();
			Properties properties = new Properties();
			properties.load(stream);
			return properties;
		} catch (IOException ex) {
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ex) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Open the configuration file.
	 * 
	 * @return Stream of configuration file.
	 * @throws IOException
	 *             Failed to load and read file
	 */
	protected abstract InputStream openInputStream() throws IOException;

}
