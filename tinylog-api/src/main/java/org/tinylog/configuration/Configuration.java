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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Immutable global configuration for tinylog API and backend.
 *
 * <p>
 * By default, the configuration will be loaded from <tt>tinylog.properties</tt> in the default package. Another
 * configuration file can be loaded by setting the system property <tt>tinylog.configuration</tt>. The configuration
 * file can be either a resource in the class path or a file from file system. tinylog will find both.
 * </p>
 *
 * <p>
 * Alternately configuration properties can be set via system properties. These properties must be prefixed by
 * "<tt>tinylog.</tt>". For example: "<tt>level = debug</tt>" becomes "<tt>tinylog.level=debug</tt>". If a configuration
 * property exists as system property and in configuration file, the system property will win.
 * </p>
 */
public final class Configuration {

	private static final String DEFAULT_CONFIGURATION_FILE = "tinylog.properties";

	private static final String PROPERTIES_PREFIX = "tinylog.";
	private static final String CONFIGURATION_PROPERTY = PROPERTIES_PREFIX + "configuration";

	private static final Properties properties = load();

	/** */
	private Configuration() {
	}

	/**
	 * Gets a configuration property. Keys a case-sensitive.
	 *
	 * @param key
	 *            Case-sensitive key of property
	 * @return Found value or {@code null}
	 */
	public static String get(final String key) {
		return (String) properties.get(key);
	}

	/**
	 * Gets all siblings with a defined prefix. Child properties will be not returned.
	 *
	 * <p>
	 * For example: <code>getSiblings("writer")</code> will return properties with the keys <tt>writer</tt> as well as
	 * <tt>writerTest</tt> but not with the key <tt>writer.test</tt>.
	 * </p>
	 *
	 * @param prefix
	 *            Case-sensitive prefix for keys
	 * @return All found properties (map will be empty if there are no matching properties)
	 */
	public static Map<String, String> getSiblings(final String prefix) {
		Map<String, String> map = new HashMap<String, String>();
		for (Enumeration<Object> enumeration = properties.keys(); enumeration.hasMoreElements();) {
			String key = (String) enumeration.nextElement();
			if (key.startsWith(prefix) && key.indexOf('.', prefix.length()) == -1) {
				map.put(key, (String) properties.get(key));
			}
		}
		return map;
	}

	/**
	 * Gets all child properties for a parent property. The parent property itself will be not returned. Children keys
	 * will be returned without parent key prefix.
	 *
	 * <p>
	 * For example: <code>getChildren("writer")</code> will return the property <tt>writer.level</tt> as <tt>level</tt>.
	 * </p>
	 *
	 * @param key
	 *            Case-sensitive key of parent property
	 * @return All found children properties (map will be empty if there are no children properties)
	 */
	public static Map<String, String> getChildren(final String key) {
		String prefix = key + ".";

		Map<String, String> map = new HashMap<String, String>();
		for (Enumeration<Object> enumeration = properties.keys(); enumeration.hasMoreElements();) {
			String property = (String) enumeration.nextElement();
			if (property.startsWith(prefix)) {
				map.put(property.substring(prefix.length()), (String) properties.get(property));
			}
		}
		return map;
	}

	/**
	 * Loads all configuration properties.
	 *
	 * @return Found properties
	 */
	private static Properties load() {
		Properties properties = new Properties();

		String file = System.getProperty(CONFIGURATION_PROPERTY);
		try {
			if (file != null) {
				InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
				if (stream == null) {
					stream = new FileInputStream(file);
				}
				load(properties, stream);
			} else {
				InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CONFIGURATION_FILE);
				if (stream != null) {
					load(properties, stream);
				}
			}
		} catch (IOException ex) {
			InternalLogger.log(Level.ERROR, "Failed loading configuration from '" + file + "'");
		}

		for (Enumeration<Object> enumeration = System.getProperties().keys(); enumeration.hasMoreElements();) {
			String property = (String) enumeration.nextElement();
			if (property.startsWith(PROPERTIES_PREFIX)) {
				properties.put(property.substring(PROPERTIES_PREFIX.length()), System.getProperty(property));
			}
		}

		return properties;
	}

	/**
	 * Puts all properties from a stream to an existing properties object. Already existing properties will be
	 * overridden.
	 *
	 * @param properties
	 *            Read properties will be put to this properties object
	 * @param stream
	 *            Input stream with a properties file
	 * @throws IOException
	 *             Failed reading properties from input stream
	 */
	private static void load(final Properties properties, final InputStream stream) throws IOException {
		try {
			properties.load(stream);
		} finally {
			stream.close();
		}
	}

}
