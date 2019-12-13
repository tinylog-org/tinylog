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

package org.tinylog.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.writers.Writer;

/**
 * Dummy writer that just stores the passed configuration.
 */
public final class ConfigurationStoreWriter implements Writer {

	private final Map<String, String> properties;

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public ConfigurationStoreWriter(final Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * Gets the parsed configuration to this writer.
	 * 
	 * @return Writer's configuration
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.emptyList();
	}

	@Override
	public void write(final LogEntry logEntry) {
		// Ignore
	}

	@Override
	public void flush() throws Exception {
		// Ignore
	}

	@Override
	public void close() throws Exception {
		// Ignore
	}

}
