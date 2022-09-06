/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.spi.MDCAdapter;
import org.tinylog.ThreadContext;

/**
 * MDC adapter for tinylog's {@link ThreadContext}.
 */
public final class TinylogMdcAdapter implements MDCAdapter {
	
	/** */
	public TinylogMdcAdapter() {
	}

	@Override
	public void put(final String key, final String value) {
		ThreadContext.put(key, value);
	}

	@Override
	public void pushByKey(final String key, final String value) {
		// Ignore
	}

	@Override
	public String get(final String key) {
		return ThreadContext.get(key);
	}

	@Override
	public void remove(final String key) {
		ThreadContext.remove(key);
	}

	@Override
	public String popByKey(final String key) {
		return null;
	}

	@Override
	public void clear() {
		ThreadContext.clear();
	}

	@Override
	public void clearDequeByKey(final String key) {
		// Ignore
	}

	@Override
	public Map<String, String> getCopyOfContextMap() {
		return new HashMap<String, String>(ThreadContext.getMapping());
	}

	@Override
	public Deque<String> getCopyOfDequeByKey(final String key) {
		return new ArrayDeque<String>(0);
	}

	@Override
	public void setContextMap(final Map<String, String> contextMap) {
		ThreadContext.clear();
		for (Entry<String, String> entry : contextMap.entrySet()) {
			ThreadContext.put(entry.getKey(), entry.getValue());
		}
	}

}
