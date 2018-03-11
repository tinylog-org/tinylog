/*
 * Copyright 2017 Martin Winandy
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

package org.tinylog.pattern;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.runtime.RuntimeProvider;

/**
 * Token for outputting the process ID of the JVM that has issued a log entry.
 */
final class ProcessIdToken implements Token {

	private final long pid;
	private final String rendered;

	/** */
	ProcessIdToken() {
		pid = RuntimeProvider.getProcessId();
		rendered = Long.toString(pid);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.emptySet();
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		builder.append(rendered);
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		statement.setLong(index, pid);
	}

}
