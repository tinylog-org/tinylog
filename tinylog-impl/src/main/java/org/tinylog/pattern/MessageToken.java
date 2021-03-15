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
import java.util.Collection;
import java.util.Collections;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Token for outputting the text message of a log entry.
 */
final class MessageToken implements Token {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/** */
	MessageToken() {
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singleton(LogEntryValue.MESSAGE);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		String message = logEntry.getMessage();
		if (message != null) {
			builder.ensureCapacity(builder.length() + message.length());
			for (int i = 0; i < message.length(); ++i) {
				char character = message.charAt(i);
				if (character == '\r') {
					builder.append(NEW_LINE);
					if (i + 1 < message.length() && message.charAt(i + 1) == '\n') {
						i += 1;
					}
				} else if (character == '\n') {
					builder.append(NEW_LINE);
				} else {
					builder.append(character);
				}
			}
		}
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		statement.setString(index, logEntry.getMessage());
	}

}
