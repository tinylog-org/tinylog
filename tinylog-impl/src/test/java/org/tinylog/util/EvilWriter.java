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

package org.tinylog.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.writers.Writer;

/**
 * Evil writer implementation that throws for any operation an {@link IOException}. This writer can be used for testing
 * error handling.
 */
public final class EvilWriter implements Writer {

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public EvilWriter(final Map<String, String> properties) {
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.emptyList();
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		throw new IOException();
	}

	@Override
	public void flush() throws IOException {
		throw new IOException();
	}

	@Override
	public void close() throws IOException {
		throw new IOException();
	}

}
