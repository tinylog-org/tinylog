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
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link LoggerTagToken}.
 */
public final class LoggerTagTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#TAG} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		LoggerTagToken token = new LoggerTagToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.TAG);
	}

	/**
	 * Verifies that a tag from a logger will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderExistingTag() {
		LoggerTagToken token = new LoggerTagToken();
		assertThat(render(token, "test")).isEqualTo("test");
	}

	/**
	 * Verifies that a tag from a logger will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyExistingTag() throws SQLException {
		LoggerTagToken token = new LoggerTagToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("test"), statement, 1);
		verify(statement).setString(1, "test");
	}

	/**
	 * Verifies that nothing will be appended to a {@link StringBuilder}, if there is neither a tag in the log entry nor
	 * a specified replacement for empty tags.
	 */
	@Test
	public void renderDefaultEmptyTag() {
		LoggerTagToken token = new LoggerTagToken();
		assertThat(render(token, null)).isEmpty();
	}

	/**
	 * Verifies that {@code null} will be added to a {@link PreparedStatement}, if there is neither a tag in the log
	 * entry nor a specified replacement for empty tags.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyDefaultEmptyTag() throws SQLException {
		LoggerTagToken token = new LoggerTagToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(null), statement, 1);
		verify(statement).setString(1, null);
	}

	/**
	 * Verifies that a specified replacement for empty tags will be rendered correctly for a {@link StringBuilder}, if
	 * there is no tag in the log entry.
	 */
	@Test
	public void renderDefinedEmptyTag() {
		LoggerTagToken token = new LoggerTagToken("-");
		assertThat(render(token, null)).isEqualTo("-");
	}

	/**
	 * Verifies that a specified replacement for empty tags will be added to a {@link PreparedStatement}, if there is no
	 * tag in the log entry.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyDefinedEmptyTag() throws SQLException {
		LoggerTagToken token = new LoggerTagToken("-");

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(null), statement, 1);
		verify(statement).setString(1, "-");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param tag
	 *            Logger tag for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final String tag) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(tag), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a logger tag.
	 *
	 * @param tag
	 *            Logger tag for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final String tag) {
		return LogEntryBuilder.empty().tag(tag).create();
	}

}
