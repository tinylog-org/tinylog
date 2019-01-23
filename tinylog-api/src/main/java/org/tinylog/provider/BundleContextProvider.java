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

package org.tinylog.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Context provider that combines multiple context providers into one.
 */
final class BundleContextProvider implements ContextProvider {

	private final ContextProvider[] providers;

	/**
	 * @param providers
	 *            Base context providers
	 */
	BundleContextProvider(final Collection<ContextProvider> providers) {
		this.providers = providers.toArray(new ContextProvider[0]);
	}

	@Override
	public Map<String, String> getMapping() {
		Map<String, String> mapping = new HashMap<String, String>();
		for (int i = 0; i < providers.length; ++i) {
			mapping.putAll(providers[i].getMapping());
		}
		return mapping;
	}

	@Override
	public String get(final String key) {
		for (int i = 0; i < providers.length; ++i) {
			String value = providers[i].get(key);
			if (value != null) {
				return value;
			}
		}

		return null;
	}

	@Override
	public void put(final String key, final Object value) {
		for (int i = 0; i < providers.length; ++i) {
			providers[i].put(key, value);
		}
	}

	@Override
	public void remove(final String key) {
		for (int i = 0; i < providers.length; ++i) {
			providers[i].remove(key);
		}
	}

	@Override
	public void clear() {
		for (int i = 0; i < providers.length; ++i) {
			providers[i].clear();
		}
	}

}
