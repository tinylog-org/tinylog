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

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Token for rendering a log entry as text.
 */
public interface Token {

	/**
	 * Gets all log entry values that are required for outputting a log entry.
	 *
	 * @return Required log entry values
	 */
	Collection<LogEntryValue> getRequiredLogEntryValues();

	/**
	 * Renders a log entry.
	 *
	 * @param logEntry
	 *            Log entry to render
	 * @param builder
	 *            Output will be appended to this string builder
	 */
	void render(LogEntry logEntry, StringBuilder builder);

	/**
	 * Adds the value of this token to a prepared SQL statement.
	 * 
	 * @param logEntry
	 *            Log entry that contains the source value(s)
	 * @param statement
	 *            Target prepared SQL statement
	 * @param index
	 *            Index for value
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	void apply(LogEntry logEntry, PreparedStatement statement, int index) throws SQLException;

}
