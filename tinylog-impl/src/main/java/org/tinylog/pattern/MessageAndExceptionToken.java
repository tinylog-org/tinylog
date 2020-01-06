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
import java.util.EnumSet;
import java.util.List;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.throwable.ThrowableFilter;

/**
 * Token for outputting the text message and the exception or other throwable of a log entry.
 */
final class MessageAndExceptionToken implements Token {

	private final MessageToken messageToken;
	private final ExceptionToken exceptionToken;

	/**
	 * @param filters
	 *            Throwable filters for output of exceptions and other throwables
	 */
	MessageAndExceptionToken(final List<ThrowableFilter> filters) {
		messageToken = new MessageToken();
		exceptionToken = new ExceptionToken(filters);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		messageToken.render(logEntry, builder);

		if (logEntry.getException() != null) {
			if (logEntry.getMessage() != null) {
				builder.append(": ");
			}
			exceptionToken.render(logEntry, builder);
		}
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		if (logEntry.getException() == null) {
			statement.setString(index, logEntry.getMessage());
		} else {
			StringBuilder builder = new StringBuilder();
			render(logEntry, builder);
			statement.setString(index, builder.toString());
		}
	}

}
