/*
 * Copyright 2015 Martin Winandy
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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource bundle with {@link Map} source.
 */
public final class MappedResourceBundle extends ResourceBundle {

	private final Map<String, String> mapping;

	/** */
	public MappedResourceBundle() {
		mapping = new HashMap<>();
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(mapping.keySet());
	}

	/**
	 * Set a new mapping. Existing mappings will be overridden.
	 *
	 * @param key
	 *            Key of mapping
	 * @param value
	 *            Value of mapping
	 */
	public void put(final String key, final String value) {
		mapping.put(key, value);
	}

	@Override
	protected Object handleGetObject(final String key) {
		return mapping.get(key);
	}

}
