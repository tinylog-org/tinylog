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
 * Tests for {@link MessageToken}.
 */
public final class MessageTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#MESSAGE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		MessageToken token = new MessageToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.MESSAGE);
	}

	/**
	 * Verifies that nothing will be rendered for a {@link StringBuilder}, if there is no text message in a log entry.
	 */
	@Test
	public void renderMissingMessage() {
		MessageToken token = new MessageToken();
		assertThat(render(token, null)).isEmpty();
	}

	/**
	 * Verifies that {@code null} will be added to a {@link PreparedStatement}, if there is no text message in a log entry.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyMissingMessage() throws SQLException {
		MessageToken token = new MessageToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(null), statement, 1);
		verify(statement).setString(1, null);
	}

	/**
	 * Verifies that a text message will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderTextMessage() {
		MessageToken token = new MessageToken();
		assertThat(render(token, "Hello World!")).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that a text message will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyTextMessage() throws SQLException {
		MessageToken token = new MessageToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("Hello World!"), statement, 1);
		verify(statement).setString(1, "Hello World!");
	}

	/**
	 * Verifies that an Unix line separator ("\n") in a text message will be converted to a system line separator.
	 */
	@Test
	public void convertUnixLineSeparator() {
		MessageToken token = new MessageToken();
		assertThat(render(token, "Hello\nWorld!")).isEqualTo("Hello" + System.lineSeparator() + "World!");
	}

	/**
	 * Verifies that a class Macintosh line separator ("\r") in a text message will be converted to a system line separator.
	 */
	@Test
	public void convertMacintoshLineSeparator() {
		MessageToken token = new MessageToken();
		assertThat(render(token, "Hello\rWorld!")).isEqualTo("Hello" + System.lineSeparator() + "World!");
	}

	/**
	 * Verifies that a Windows line separator ("\r") in a text message will be converted to a system line separator.
	 */
	@Test
	public void convertWindowsLineSeparator() {
		MessageToken token = new MessageToken();
		assertThat(render(token, "Hello\r\nWorld!")).isEqualTo("Hello" + System.lineSeparator() + "World!");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param message
	 *            Text message for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final String message) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(message), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a.
	 *
	 * @param message
	 *            Text message for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final String message) {
		return LogEntryBuilder.empty().message(message).create();
	}

}
