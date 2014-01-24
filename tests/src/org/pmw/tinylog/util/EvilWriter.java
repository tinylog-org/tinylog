/*
 * Copyright 2014 Martin Winandy
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
import java.util.Set;

import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * This writer throws exceptions instead of writing log entries.
 */
public final class EvilWriter implements LoggingWriter {

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.emptySet();
	}

	@Override
	public void init() throws Exception {
		// Do nothing
	}

	@Override
	public void write(final LogEntry logEntry) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		// Do nothing
	}

}
