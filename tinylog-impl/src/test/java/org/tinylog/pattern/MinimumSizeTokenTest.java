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
 * Tests for {@link MinimumSizeToken}.
 */
public final class MinimumSizeTokenTest {

	/**
	 * Verifies that required log entry values from child token will be passed-through.
	 */
	@Test
	public void requiredLogEntryValues() {
		MinimumSizeToken token = new MinimumSizeToken(new SeverityLevelToken(), 0);
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that the text from a child token will be appended unmodified to a {@link StringBuilder}, if the text is
	 * longer than the defined minimum size.
	 */
	@Test
	public void renderLongerTextThanMinimumSize() {
		MinimumSizeToken token = new MinimumSizeToken(new PlainTextToken("Hello!"), 5);
		assertThat(render(token)).isEqualTo("Hello!");
	}

	/**
	 * Verifies that the text from a child token will be added unmodified to a {@link PreparedStatement}, if the text is
	 * longer than the defined minimum size.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyLongerTextThanMinimumSize() throws SQLException {
		MinimumSizeToken token = new MinimumSizeToken(new PlainTextToken("Hello!"), 5);

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(LogEntryBuilder.empty().create(), statement, 1);
		verify(statement).setString(1, "Hello!");
	}

	/**
	 * Verifies that the text from a child token will be appended unmodified to a {@link StringBuilder}, if the text
	 * length is equal to the defined minimum size.
	 */
	@Test
	public void renderTextOfEqualLengthToMinimumSize() {
		MinimumSizeToken token = new MinimumSizeToken(new PlainTextToken("Hello!"), 6);
		assertThat(render(token)).isEqualTo("Hello!");
	}

	/**
	 * Verifies that the text from a child token will be added unmodified to a {@link PreparedStatement}, if the text
	 * length is equal to the defined minimum size.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyTextOfEqualLengthToMinimumSize() throws SQLException {
		MinimumSizeToken token = new MinimumSizeToken(new PlainTextToken("Hello!"), 6);

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(LogEntryBuilder.empty().create(), statement, 1);
		verify(statement).setString(1, "Hello!");
	}

	/**
	 * Verifies that the text from a child token will be appended with padded spaces to a {@link StringBuilder}, if the
	 * text is shorter than the defined minimum size.
	 */
	@Test
	public void renderShorterTextThanMinimumSize() {
		MinimumSizeToken token = new MinimumSizeToken(new PlainTextToken("Hello!"), 7);
		assertThat(render(token)).isEqualTo("Hello! ");

		token = new MinimumSizeToken(new PlainTextToken("Hello!"), 8);
		assertThat(render(token)).isEqualTo("Hello!  ");
	}

	/**
	 * Verifies that the text from a child token will be added with padded spaces to a {@link PreparedStatement}, if the
	 * text is shorter than the defined minimum size.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyShorterTextThanMinimumSize() throws SQLException {
		PlainTextToken token = new PlainTextToken("Hello!");
		LogEntry logEntry = LogEntryBuilder.empty().create();

		PreparedStatement statement = mock(PreparedStatement.class);
		new MinimumSizeToken(token, 7).apply(logEntry, statement, 1);
		verify(statement).setString(1, "Hello! ");

		statement = mock(PreparedStatement.class);
		new MinimumSizeToken(token, 8).apply(logEntry, statement, 1);
		verify(statement).setString(1, "Hello!  ");
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
