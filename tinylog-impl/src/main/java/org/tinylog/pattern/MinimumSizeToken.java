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
 * Decorator token for ensuring a minimum size. If the output of the underlying token is shorter than the defined
 * minimum size, spaces will be added at the end.
 */
class MinimumSizeToken implements Token {

	private final Token token;
	private final int minimumSize;

	/**
	 * @param token
	 *            Base token
	 * @param size
	 *            Minimum size for output
	 */
	MinimumSizeToken(final Token token, final int size) {
		this.token = token;
		this.minimumSize = size;
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return token.getRequiredLogEntryValues();
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		int offset = builder.length();
		token.render(logEntry, builder);
		int size = builder.length() - offset;

		for (int i = 0; i < minimumSize - size; ++i) {
			builder.append(' ');
		}
	}
	
	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		StringBuilder builder = new StringBuilder();
		render(logEntry, builder);
		statement.setString(index, builder.toString());
	}

}
