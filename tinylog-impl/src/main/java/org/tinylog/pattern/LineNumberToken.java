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

package org.tinylog.pattern;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Token for outputting the line number in source file where a log entry has been issued.
 */
final class LineNumberToken implements Token {

	/** */
	LineNumberToken() {
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singleton(LogEntryValue.LINE);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		int line = logEntry.getLineNumber();
		if (line >= 0) {
			builder.append(line);
		} else {
			builder.append("?");
		}
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		int line = logEntry.getLineNumber();
		if (line >= 0) {
			statement.setInt(index, line);
		} else {
			statement.setNull(index, Types.INTEGER);
		}
	}

}
