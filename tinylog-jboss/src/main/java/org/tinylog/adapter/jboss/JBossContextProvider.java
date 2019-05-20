/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.adapter.jboss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.MDC;
import org.tinylog.provider.ContextProvider;

/**
 * Adapter context provider based on JBoss Logging's {@link MDC}.
 */
public final class JBossContextProvider implements ContextProvider {

	/** */
	public JBossContextProvider() {
	}
	
	@Override
	public Map<String, String> getMapping() {
		Map<String, Object> objectMap = MDC.getMap();
		if (objectMap.isEmpty()) {
			return Collections.emptyMap();
		} else {
			Map<String, String> stringMap = new HashMap<String, String>(objectMap.size());
			for (Entry<String, Object> entry : objectMap.entrySet()) {
				stringMap.put(entry.getKey(), String.valueOf(entry.getValue()));
			}
			return stringMap;
		}
	}

	@Override
	public String get(final String key) {
		Object value = MDC.get(key);
		return value == null ? null : value.toString();
	}

	@Override
	public void put(final String key, final Object value) {
		MDC.put(key, value);
	}

	@Override
	public void remove(final String key) {
		MDC.remove(key);
	}

	@Override
	public void clear() {
		MDC.clear();
	}

}
