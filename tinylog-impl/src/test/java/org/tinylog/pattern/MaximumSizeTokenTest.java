/*
 * Copyright 2021 Victor Kropp, Martin Winandy
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
 * Tests for {@link MaximumSizeToken}.
 */
public final class MaximumSizeTokenTest {

	/**
	 * Verifies that required log entry values from child token will be passed-through.
	 */
	@Test
	public void requiredLogEntryValues() {
		MaximumSizeToken token = new MaximumSizeToken(new SeverityLevelToken(), 10);
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that the text from a child token will be appended unmodified to a {@link StringBuilder}, if the text is
	 * shorter than the defined maximum size.
	 */
	@Test
	public void renderShorterTextThanMaximumSize() {
		MaximumSizeToken token = new MaximumSizeToken(new PlainTextToken("Hello!"), 10);
		assertThat(render(token)).isEqualTo("Hello!");
	}

	/**
	 * Verifies that the text from a child token will be added unmodified to a {@link PreparedStatement}, if the text is
	 * shorter than the defined maximum size.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyShorterTextThanMaximumSize() throws SQLException {
		MaximumSizeToken token = new MaximumSizeToken(new PlainTextToken("Hello!"), 10);

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(LogEntryBuilder.empty().create(), statement, 1);
		verify(statement).setString(1, "Hello!");
	}

	/**
	 * Verifies that the text from a child token will be appended unmodified to a {@link StringBuilder}, if the text
	 * length is equal to the defined maximum size.
	 */
	@Test
	public void renderTextOfEqualLengthToMaximumSize() {
		MaximumSizeToken token = new MaximumSizeToken(new PlainTextToken("Hello!"), 6);
		assertThat(render(token)).isEqualTo("Hello!");
	}

	/**
	 * Verifies that the text from a child token will be added unmodified to a {@link PreparedStatement}, if the text
	 * length is equal to the defined maximum size.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyTextOfEqualLengthToMaximumSize() throws SQLException {
		MaximumSizeToken token = new MaximumSizeToken(new PlainTextToken("Hello!"), 6);

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(LogEntryBuilder.empty().create(), statement, 1);
		verify(statement).setString(1, "Hello!");
	}

	/**
	 * Verifies that the text from a child token will be appended trimmed to a {@link StringBuilder}, if the
	 * text is shorter than the defined minimum size.
	 */
	@Test
	public void renderLongerTextThanMaximumSize() {
		MaximumSizeToken token = new MaximumSizeToken(new PlainTextToken("Hello!"), 4);
		assertThat(render(token)).isEqualTo("llo!");

		token = new MaximumSizeToken(new PlainTextToken("Hello!"), 3);
		assertThat(render(token)).isEqualTo("lo!");
	}

	/**
	 * Verifies that the text from a child token will be appended trimmed to a {@link PreparedStatement}, if the
	 * text is longer than the defined maximum size.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyLongerTextThanMaximumSize() throws SQLException {
		PlainTextToken token = new PlainTextToken("Hello!");
		LogEntry logEntry = LogEntryBuilder.empty().create();

		PreparedStatement statement = mock(PreparedStatement.class);
		new MaximumSizeToken(token, 4).apply(logEntry, statement, 1);
		verify(statement).setString(1, "llo!");

		statement = mock(PreparedStatement.class);
		new MaximumSizeToken(token, 3).apply(logEntry, statement, 1);
		verify(statement).setString(1, "lo!");
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
