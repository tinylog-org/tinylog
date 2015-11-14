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

package org.slf4j.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.pmw.tinylog.LoggingContext;
import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;

/**
 * Adapter for using {@link LoggingContext} as {@link MDC}.
 */
public final class TinylogMDCAdapter implements MDCAdapter {

	/** */
	public TinylogMDCAdapter() {
	}

	@Override
	public Map<String, String> getCopyOfContextMap() {
		return new HashMap<>(LoggingContext.getMapping());
	}

	@Override
	public void setContextMap(final Map<String, String> map) {
		LoggingContext.clear();
		for (Entry<String, String> entry : map.entrySet()) {
			LoggingContext.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public String get(final String key) {
		return LoggingContext.get(key);
	}

	@Override
	public void put(final String key, final String value) {
		LoggingContext.put(key, value);
	}

	@Override
	public void remove(final String key) {
		LoggingContext.remove(key);
	}

	@Override
	public void clear() {
		LoggingContext.clear();
	}

}
