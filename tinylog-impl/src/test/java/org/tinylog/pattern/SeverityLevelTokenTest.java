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

import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link SeverityLevelToken}.
 */
public final class SeverityLevelTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#LEVEL} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that {@link Level#TRACE} will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderTrace() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.TRACE)).isEqualTo("TRACE");
	}

	/**
	 * Verifies that {@link Level#TRACE} will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyTrace() throws SQLException {
		SeverityLevelToken token = new SeverityLevelToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(Level.TRACE), statement, 1);
		verify(statement).setString(1, "TRACE");
	}

	/**
	 * Verifies that {@link Level#DEBUG} will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderDebug() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.DEBUG)).isEqualTo("DEBUG");
	}

	/**
	 * Verifies that {@link Level#DEBUG} will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyDebug() throws SQLException {
		SeverityLevelToken token = new SeverityLevelToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(Level.DEBUG), statement, 1);
		verify(statement).setString(1, "DEBUG");
	}

	/**
	 * Verifies that {@link Level#INFO} will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderInfo() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.INFO)).isEqualTo("INFO");
	}

	/**
	 * Verifies that {@link Level#INFO} will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyInfo() throws SQLException {
		SeverityLevelToken token = new SeverityLevelToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(Level.INFO), statement, 1);
		verify(statement).setString(1, "INFO");
	}

	/**
	 * Verifies that {@link Level#WARNING} will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderWarning() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.WARNING)).isEqualTo("WARNING");
	}

	/**
	 * Verifies that {@link Level#WARNING} will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyWarning() throws SQLException {
		SeverityLevelToken token = new SeverityLevelToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(Level.WARNING), statement, 1);
		verify(statement).setString(1, "WARNING");
	}

	/**
	 * Verifies that {@link Level#ERROR} will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderError() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.ERROR)).isEqualTo("ERROR");
	}

	/**
	 * Verifies that {@link Level#ERROR} will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyError() throws SQLException {
		SeverityLevelToken token = new SeverityLevelToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(Level.ERROR), statement, 1);
		verify(statement).setString(1, "ERROR");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param level
	 *            Severity level for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final Level level) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(level), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a severity level.
	 *
	 * @param level
	 *            Severity level for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final Level level) {
		return LogEntryBuilder.empty().level(level).create();
	}

}
