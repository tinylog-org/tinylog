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

import org.junit.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link LineNumberToken}.
 */
public final class LineNumberTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#LINE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		LineNumberToken token = new LineNumberToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LINE);
	}

	/**
	 * Verifies that a valid source file line number will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderValidLineNumber() {
		LineNumberToken token = new LineNumberToken();
		assertThat(render(token, 42)).isEqualTo("42");
	}

	/**
	 * Verifies that a valid source file line number will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyValidLineNumber() throws SQLException {
		LineNumberToken token = new LineNumberToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(42), statement, 1);
		verify(statement).setInt(1, 42);
	}

	/**
	 * Verifies that a invalid source file line number will be rendered as question mark ("?") for a
	 * {@link StringBuilder}.
	 */
	@Test
	public void renderInvalidLineNumber() {
		LineNumberToken token = new LineNumberToken();
		assertThat(render(token, -1)).isEqualTo("?");
	}

	/**
	 * Verifies that a invalid source file line number will be added as {@code null} to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyInvalidLineNumber() throws SQLException {
		LineNumberToken token = new LineNumberToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(-1), statement, 1);
		verify(statement).setNull(1, Types.INTEGER);
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param line
	 *            Line number for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final int line) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(line), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a source file line number.
	 *
	 * @param line
	 *            Line number for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final int line) {
		return LogEntryBuilder.empty().lineNumber(line).create();
	}

}
