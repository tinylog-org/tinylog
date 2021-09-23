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

package org.tinylog.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;
import org.tinylog.runtime.RuntimeProvider;

/**
 * Global configuration for tinylog.
 *
 * <p>
 * By default, the configuration will be loaded from {@code tinylog.properties} in the default package. Another
 * configuration file can be loaded by setting the system property {@code tinylog.configuration}. The configuration file
 * can be a resource in the classpath, a file from file system or an URL
 * </p>
 *
 * <p>
 * Alternately configuration properties can be set via system properties. These properties must be prefixed by
 * "{@code tinylog.}". For example: "{@code level = debug}" becomes "{@code tinylog.level=debug}". If a configuration
 * property exists as system property and in configuration file, the system property will win.
 * </p>
 * 
 * <p>
 * The default property loading behavior can be changed by supplying a property service loader which reads the properties
 * in any other way. The properties can have a priority. The service loader which loads the properties with the highest
 * priority is chosen. Alternatively, the loader can be directly chosen by setting a system property
 * "{@code tinylog.configurationloader}" to the simple class name of the desired loader.
 * </p>
 */
public final class Configuration {

	static final String PROPERTIES_PREFIX = "tinylog.";

	private static final int MAX_LOCALE_ARGUMENTS = 3;

	private static final String LOCALE_KEY = "locale";
	private static final String ESCAPING_ENABLED_KEY = "escaping.enabled";
	
	private static final String CONFIGURATION_LOADER_CLASS_PROPERTY = PROPERTIES_PREFIX + "configurationloader";

	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
	private static final Properties properties = load();
	private static boolean frozen;
	
	/** */
	private Configuration() {
	}
	
	/**
	 * Loads the configuration properties. Per default the properties are loaded from the internal
	 * Java property loader. If a custom service loader is found it is preferred over the internal one.
	 * If a system property {@code tinylog.configurationloader} is given matching the simple class name
	 * of a configuration service loader this used. It is possible to set this system property
	 * to the internal configuration service loader's class name to override all custom loader.
	 *
	 * @return Loaded properties
	 */
	private static Properties load() {
		if (RuntimeProvider.getProcessId() == Long.MIN_VALUE) {
			java.util.ServiceLoader.load(ConfigurationLoader.class); // Workaround for ProGuard (see issue #126)
		}
		
		ConfigurationLoader loader = null;
		ServiceLoader<ConfigurationLoader> serviceLoader = new ServiceLoader<ConfigurationLoader>(ConfigurationLoader.class);
		
		// Loader is given via system property? 
		String loaderName = System.getProperty(CONFIGURATION_LOADER_CLASS_PROPERTY);
		if (loaderName != null) {
			loader = serviceLoader.create(loaderName);
		} else {
			// Load all services and take the first. Log an error if there is more than one (not counting the standard loader).
			Collection<ConfigurationLoader> loaders = serviceLoader.createAll();
			ConfigurationLoader internalLoader = null;
			for (ConfigurationLoader currentLoader : loaders) {
				if (currentLoader.getClass().equals(PropertiesConfigurationLoader.class)) {
					internalLoader = currentLoader;
				} else if (loader == null) {
					loader = currentLoader;
				} else {
					InternalLogger.log(
						Level.WARN,
						"Multiple configuration loaders found. Configuration loader " + currentLoader.getClass() + " will be ignored."
					);
				}
			}

			// No user loader found. Use the internal loader.
			if (loader == null) {
				loader = internalLoader;
			}
		}
		
		Properties currentProps = load(loader);
		mergeSystemProperties(currentProps);
		resolveProperties(currentProps, EnvironmentVariableResolver.INSTANCE, SystemPropertyResolver.INSTANCE);
		return currentProps;
	}
	
	/**
	 * Loads the configuration properties from the given loader.
	 * 
	 * @param loader
	 *            The configuration loader to use
	 *            
	 * @return Loaded properties
	 */
	private static Properties load(final ConfigurationLoader loader) {
		if (loader == null) {
			return new Properties();
		}
		try {
			Properties currentProps = loader.load();
			if (currentProps != null) {
				return currentProps;
			} else {
				return new Properties();
			}
		} catch (Exception ex) {
			InternalLogger.log(Level.ERROR, "Configuration loader error: '" + ex + "'");
			return new Properties();
		}
	}

	/**
	 * Gets the global locale.
	 *
	 * @return Locale from property {@code locale} or {@link Locale#ROOT} if no locale is configured
	 */
	public static Locale getLocale() {
		String tag = get(LOCALE_KEY);
		if (tag == null) {
			return Locale.ROOT;
		} else {
			String[] splitTag = tag.trim().split("_", MAX_LOCALE_ARGUMENTS);
			if (splitTag.length == 1) {
				return new Locale(splitTag[0]);
			} else if (splitTag.length == 2) {
				return new Locale(splitTag[0], splitTag[1]);
			} else {
				return new Locale(splitTag[0], splitTag[1], splitTag[2]);
			}
		}
	}

	/**
	 * Checks whether escaping is enabled or disabled.
	 *
	 * @return {@code true} if escaping is enabled, otherwise {@code false}
	 */
	public static boolean isEscapingEnabled() {
		String enabled = get(ESCAPING_ENABLED_KEY);
		return enabled != null && Boolean.parseBoolean(enabled.trim());
	}

	/**
	 * Gets a configuration property. Keys a case-sensitive.
	 *
	 * @param key
	 *            Case-sensitive key of property
	 * @return Found value or {@code null}
	 */
	public static String get(final String key) {
		try {
			lock.readLock().lock();
			frozen = true;
			return (String) properties.get(key);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets all siblings with a defined prefix. Child properties will be not returned.
	 *
	 * <p>
	 * <strong>Example:</strong>
	 * </p>
	 *
	 * <p>
	 * {@code getSiblings("writer")} will return properties with the keys {@code writer} as well as {@code writerTest}
	 * but not with the key {@code writer.test}. Dots after a prefix ending with an at sign will be not handled as
	 * children. Therefore, {@code getSiblings("level@")} will return a property with the key {@code level@com.test}.
	 * </p>
	 *
	 * @param prefix
	 *            Case-sensitive prefix for keys
	 * @return All found properties (map will be empty if there are no matching properties)
	 */
	public static Map<String, String> getSiblings(final String prefix) {
		try {
			lock.readLock().lock();
			frozen = true;

			Map<String, String> map = new HashMap<String, String>();
			for (Enumeration<Object> enumeration = properties.keys(); enumeration.hasMoreElements();) {
				String key = (String) enumeration.nextElement();
				if (key.startsWith(prefix) && (prefix.endsWith("@") || key.indexOf('.', prefix.length()) == -1)) {
					map.put(key, (String) properties.get(key));
				}
			}
			return map;
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets all child properties for a parent property. The parent property itself will be not returned. Children keys
	 * will be returned without parent key prefix.
	 *
	 * <p>
	 * For example: {@code getChildren("writer")} will return the property {@code writer.level} as {@code level}.
	 * </p>
	 *
	 * @param key
	 *            Case-sensitive key of parent property
	 * @return All found children properties (map will be empty if there are no children properties)
	 */
	public static Map<String, String> getChildren(final String key) {
		try {
			lock.readLock().lock();
			frozen = true;

			String prefix = key + ".";

			Map<String, String> map = new HashMap<String, String>();
			for (Enumeration<Object> enumeration = properties.keys(); enumeration.hasMoreElements();) {
				String property = (String) enumeration.nextElement();
				if (property.startsWith(prefix)) {
					map.put(property.substring(prefix.length()), (String) properties.get(property));
				}
			}
			return map;
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Sets a property. If there is already a value for the given key, it will be overridden by the new value.
	 *
	 * <p>
	 * Configuration properties must be set before calling any logging methods. If the framework has been initialized
	 * once, the configuration is immutable and further configuration changes will throw an {@link UnsupportedOperationException}.
	 * </p>
	 *
	 * @param key
	 *            Name of the property
	 * @param value
	 *            Value of the property
	 * @throws UnsupportedOperationException
	 *             Configuration has already been applied and is frozen
	 */
	public static void set(final String key, final String value) throws UnsupportedOperationException {
		try {
			lock.writeLock().lock();

			if (frozen) {
				throw new UnsupportedOperationException("Configuration cannot be changed after applying to tinylog");
			} else {
				properties.put(key, value);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Replaces the current configuration by a new one. Already existing properties will be dropped.
	 *
	 * <p>
	 * Configuration properties must be set before calling any logging methods. If the framework has been initialized
	 * once, the configuration is immutable and further configuration changes will throw an {@link UnsupportedOperationException}.
	 * </p>
	 *
	 * @param configuration
	 *            New configuration
	 * @throws UnsupportedOperationException
	 *             Configuration has already been applied and is frozen
	 */
	public static void replace(final Map<String, String> configuration) throws UnsupportedOperationException {
		try {
			lock.writeLock().lock();

			if (frozen) {
				throw new UnsupportedOperationException("Configuration cannot be changed after applying to tinylog");
			} else {
				properties.clear();
				properties.putAll(configuration);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Resolves placeholders with passed resolver.
	 *
	 * @param value
	 *            String with placeholders
	 * @param resolver
	 *            Resolver for replacing placeholders
	 * @return Input value with resolved placeholders
	 */
	public static String resolve(final String value, final Resolver resolver) {
		StringBuilder builder = new StringBuilder();
		int position = 0;

		String prefix = resolver.getPrefix() + "{";
		String postfix = "}";

		for (int index = value.indexOf(prefix); index != -1; index = value.indexOf(prefix, position)) {
			builder.append(value, position, index);

			int start = index + 2;
			int end = value.indexOf(postfix, start);

			if (end == -1) {
				InternalLogger.log(Level.WARN, "Closing curly bracket is missing for '" + value + "'");
				return value;
			}

			String name = value.substring(start, end);
			if (name.length() == 0) {
				InternalLogger.log(Level.WARN, "Empty variable names cannot be resolved: " + value);
				return value;
			}

			String[] colonSplittedName = name.split(":", -1);
			if (colonSplittedName.length > 2) {
				InternalLogger.log(Level.WARN, "Multiple default values found: " + value);
				return value;
			}

			String key = colonSplittedName[0];
			String defaultValue = colonSplittedName.length == 2 ? colonSplittedName[1] : null;
			String data = resolver.resolve(key);
			if (data == null) {
				if (defaultValue == null) {
					InternalLogger.log(Level.WARN, "'" + key + "' could not be found in " + resolver.getName());
					return value;
				}
				data = defaultValue;
			}
			builder.append(data);

			position = end + 1;
		}

		builder.append(value, position, value.length());
		return builder.toString();
	}

	/**
	 * Merges system properties starting with the Tinylog property prefix into the given properties.
	 * 
	 * @param properties
	 *            The properties which the system values shall be merged into
	 */
	public static void mergeSystemProperties(final Properties properties) {
		for (Object key : new ArrayList<Object>(System.getProperties().keySet())) {
			String name = (String) key;
			if (name.startsWith(PROPERTIES_PREFIX)) {
				properties.put(name.substring(PROPERTIES_PREFIX.length()), System.getProperty(name));
			}
		}
	}
	
	/**
	 * Resolves the given property entries with the provided resolvers.
	 * 
	 * @param properties
	 *            The properties which shall be resolved
	 * @param resolvers
	 *            One or more resolvers to apply to each property entry
	 */
	public static void resolveProperties(final Properties properties, final Resolver... resolvers) {
		if (resolvers == null) {
			return;
		}
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String value = (String) entry.getValue();
			if (value.indexOf('{') != -1) {
				for (Resolver resolver : resolvers) {
					value = Configuration.resolve(value, resolver);
				}
				properties.put(entry.getKey(), value);
			}
		}
	}

	/**
	 * Checks whether the configuration is already frozen.
	 *
	 * @return {@code true} if the configuration is frozen, otherwise {@code false}
	 */
	public static boolean isFrozen() {
		return frozen;
	}

}
