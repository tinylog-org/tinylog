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
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link PlainTextToken}.
 */
public final class PlainTextTokenTest {

	private static final String NEW_LINE = System.lineSeparator();

	/**
	 * Verifies that a plain text token has no required log entry values.
	 */
	@Test
	public void requiredLogEntryValues() {
		PlainTextToken token = new PlainTextToken("Hello World!");
		assertThat(token.getRequiredLogEntryValues()).isEmpty();
	}

	/**
	 * Verifies that a simple text without tabulators and new lines will be appended unmodified to a
	 * {@link StringBuilder}.
	 */
	@Test
	public void renderSimpleText() {
		PlainTextToken token = new PlainTextToken("Hello World!");
		assertThat(render(token)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that a simple text without tabulators and new lines will be added unmodified to a
	 * {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applySimpleText() throws SQLException {
		PlainTextToken token = new PlainTextToken("Hello World!");

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(LogEntryBuilder.empty().create(), statement, 1);
		verify(statement).setString(1, "Hello World!");
	}

	/**
	 * Verifies that Windows line breaks will be rendered as system-dependent line breaks for a {@link StringBuilder}.
	 */
	@Test
	public void renderWindowsNewLines() {
		PlainTextToken token = new PlainTextToken("\r\n");
		assertThat(render(token)).isEqualTo(NEW_LINE);
	}

	/**
	 * Verifies that Windows line breaks will be added as system-dependent line breaks to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyWindowsNewLines() throws SQLException {
		LogEntry logEntry = LogEntryBuilder.empty().create();

		PreparedStatement statement = mock(PreparedStatement.class);
		new PlainTextToken("\r\n").apply(logEntry, statement, 1);
		verify(statement).setString(1, NEW_LINE);
	}

	/**
	 * Verifies that Unix line breaks will be rendered as system-dependent line breaks for a {@link StringBuilder}.
	 */
	@Test
	public void renderUnixNewLines() {
		PlainTextToken token = new PlainTextToken("\n");
		assertThat(render(token)).isEqualTo(NEW_LINE);
	}

	/**
	 * Verifies that Unix line breaks will be added as system-dependent line breaks to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyUnixNewLines() throws SQLException {
		LogEntry logEntry = LogEntryBuilder.empty().create();

		PreparedStatement statement = mock(PreparedStatement.class);
		new PlainTextToken("\n").apply(logEntry, statement, 1);
		verify(statement).setString(1, NEW_LINE);
	}

	/**
	 * Verifies that classic Mac OS line breaks will be rendered as system-dependent line breaks for a {@link StringBuilder}.
	 */
	@Test
	public void renderMacNewLines() {
		PlainTextToken token = new PlainTextToken("\r");
		assertThat(render(token)).isEqualTo(NEW_LINE);
	}

	/**
	 * Verifies that classic Mac OS line breaks will be added as system-dependent line breaks to a
	 * {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyMacNewLines() throws SQLException {
		LogEntry logEntry = LogEntryBuilder.empty().create();

		PreparedStatement statement = mock(PreparedStatement.class);
		new PlainTextToken("\r").apply(logEntry, statement, 1);
		verify(statement).setString(1, NEW_LINE);
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @return Result text
	 */
	private static String render(final Token token) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().create(), builder);
		return builder.toString();
	}

}
