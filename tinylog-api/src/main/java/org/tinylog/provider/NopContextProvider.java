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

import java.util.Collections;
import java.util.Map;

/**
 * Context provider implementation that does nothing. All put values will be ignored.
 */
public final class NopContextProvider implements ContextProvider {

	/** */
	public NopContextProvider() {
	}

	@Override
	public Map<String, String> getMapping() {
		return Collections.emptyMap();
	}

	@Override
	public String get(final String key) {
		return null;
	}

	@Override
	public void put(final String key, final Object value) {
		// Ignore
	}

	@Override
	public void remove(final String key) {
		// Ignore
	}

	@Override
	public void clear() {
		// Ignore
	}

}
