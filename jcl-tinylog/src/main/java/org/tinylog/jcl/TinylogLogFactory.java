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

package org.tinylog.jcl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Apache Commons Logging (JCL) compatible log factory implementation.
 */
public final class TinylogLogFactory extends LogFactory {

	private final TinylogLog log;
	private final Map<String, Object> attributes;

	/** */
	public TinylogLogFactory() {
		log = new TinylogLog();
		attributes = new ConcurrentHashMap<String, Object>();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Log getInstance(final Class clazz) {
		return log;
	}

	@Override
	public Log getInstance(final String name) {
		return log;
	}

	@Override
	public String[] getAttributeNames() {
		Set<String> names = attributes.keySet();
		return names.toArray(new String[names.size()]);
	}

	@Override
	public Object getAttribute(final String name) {
		return attributes.get(name);
	}

	@Override
	public void setAttribute(final String name, final Object value) {
		if (value == null) {
			attributes.remove(name);
		} else {
			attributes.put(name, value);
		}
	}

	@Override
	public void removeAttribute(final String name) {
		attributes.remove(name);
	}

	@Override
	public void release() {
		// Nothing to do
	}

}
