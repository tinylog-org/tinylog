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
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Configuration for tinylog.
 *
 * <p>
 *     The configuration can be set and modified as needed before issuing any log entries. As soon as the first log
 *     entry is issued, the configuration becomes frozen and can no longer be modified.
 * </p>
 */
public class Configuration {

	private static final String FROZEN_MESSAGE =
			"Configuration has already been applied and cannot be modified anymore";

	private final Properties properties;
	private boolean frozen;

	/** */
	public Configuration() {
		this.properties = new Properties();
		this.frozen = false;
	}

	/**
	 * Searches the value of a specific key.
	 *
	 * @param key Key to search
	 * @return The found value or {@code null} if the key does not exist
	 */
	public String getValue(String key) {
		synchronized (properties) {
			return properties.getProperty(key);
		}
	}

	/**
	 * Searches the values of a specific key.
	 *
	 * <p>
	 *     The found string is split by commas.
	 * </p>
	 *
	 * @param key Key to search
	 * @return The found values or an empty list if the key does not exist
	 */
	public List<String> getList(String key) {
		synchronized (properties) {
			String value = properties.getProperty(key);
			if (value == null) {
				return Collections.emptyList();
			} else {
				List<String> elements = new ArrayList<String>();
				for (String element : value.split(",")) {
					String normalized = element.trim();
					if (!normalized.isEmpty()) {
						elements.add(normalized);
					}
				}
				return elements;
			}
		}
	}

	/**
	 * Sets a value for a given key. If another value is already stored under the passed key, the old value will be
	 * overwritten with the new value.
	 *
	 * @param key Key under which the value should to be stored
	 * @param value Value to store
	 * @throws UnsupportedOperationException The Configuration has already been applied and cannot be modified anymore
	 */
	public void set(String key, String value) {
		synchronized (properties) {
			if (frozen) {
				throw new UnsupportedOperationException(FROZEN_MESSAGE);
			}

			properties.setProperty(key, value);
		}
	}

	/**
	 * Loads the configuration from default properties file if available.
	 *
	 * @throws UnsupportedOperationException The Configuration has already been applied and cannot be modified anymore
	 */
	void loadPropertiesFile() {
		synchronized (properties) {
			if (frozen) {
				throw new UnsupportedOperationException(FROZEN_MESSAGE);
			}
		}
	}

	/**
	 * Freezes the configuration.
	 *
	 * <p>
	 *     Afterwards, the configuration cannot be modified anymore.
	 * </p>
	 */
	void freeze() {
		synchronized (properties) {
			frozen = true;
		}
	}

}
