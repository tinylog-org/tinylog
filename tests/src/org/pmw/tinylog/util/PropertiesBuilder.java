/*
 * Copyright 2013 Martin Winandy
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

package org.pmw.tinylog.util;

import java.util.Properties;

/**
 * Fluent API to create and fill {@link Properties}.
 */
public final class PropertiesBuilder {

	private final Properties properties;

	/** */
	public PropertiesBuilder() {
		this.properties = new Properties();
	}

	/**
	 * Set a property.
	 * 
	 * @param key
	 *            Key of the property
	 * @param value
	 *            Value of the property
	 * @return The current properties builder
	 */
	public PropertiesBuilder set(final String key, final String value) {
		properties.setProperty(key, value);
		return this;
	}

	/**
	 * Remove a property.
	 * 
	 * @param key
	 *            Key of the property
	 * @return The current properties builder
	 */
	public PropertiesBuilder remove(final String key) {
		properties.remove(key);
		return this;
	}

	/**
	 * Create a copy of the current properties builder.
	 * 
	 * @return Copy of the current properties builder
	 */
	public PropertiesBuilder copy() {
		PropertiesBuilder copy = new PropertiesBuilder();
		for (Object key : properties.keySet()) {
			String keyAsString = (String) key;
			copy.set(keyAsString, properties.getProperty(keyAsString));
		}
		return copy;
	}

	/**
	 * Get the created properties.
	 * 
	 * @return Created properties
	 */
	public Properties create() {
		return properties;
	}

}
