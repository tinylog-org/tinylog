/*
 * Copyright 2021 Martin Winandy
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

package org.tinylog.writers;

import java.util.Map;

public abstract class AbstractWriter implements Writer {

	private final Map<String, String> properties;

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public AbstractWriter(final Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * Gets the trimmed value for the passed key from the configuration properties.
	 *
	 * <p>
	 *     Leading and trailing spaces of the found value will be removed.
	 * </p>
	 *
	 * @param key Case-sensitive property key
	 * @return Found value or {@code null}
	 */
	public String getStringValue(final String key) {
		String value = properties.get(key);
		if (value == null) {
			return null;
		} else {
			return value.trim();
		}
	}

	/**
	 * Gets the boolean value for the passed key from the configuration properties.
	 *
	 * <p>
	 *     Under the hood, {@link Boolean#parseBoolean(String)} is used with the trimmed string value.
	 * </p>
	 *
	 * @param key Case-sensitive property key
	 * @return Found boolean value
	 */
	public boolean getBooleanValue(final String key) {
		return Boolean.parseBoolean(getStringValue(key));
	}

}
