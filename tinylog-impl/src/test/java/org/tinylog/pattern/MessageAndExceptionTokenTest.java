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
import org.mockito.ArgumentCaptor;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link MessageAndExceptionToken}.
 */
public final class MessageAndExceptionTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#MESSAGE} and {@link LogEntryValue#EXCEPTION} are the only required log entry
	 * values.
	 */
	@Test
	public void requiredLogEntryValues() {
		MessageAndExceptionToken token = new MessageAndExceptionToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that nothing will be rendered for a {@link StringBuilder}, if there is neither a text message nor an
	 * exception or throwable in a log entry.
	 */
	@Test
	public void renderNeitherMessageNorException() {
		MessageAndExceptionToken token = new MessageAndExceptionToken();
		assertThat(render(token, null, null)).isEmpty();
	}

	/**
	 * Verifies that {@code null} will be added to a {@link PreparedStatement}, if there is neither a text message nor
	 * an exception or throwable in a log entry.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyNeitherMessageNorException() throws SQLException {
		MessageAndExceptionToken token = new MessageAndExceptionToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(null, null), statement, 1);
		verify(statement).setString(1, null);
	}

	/**
	 * Verifies that a text message will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderMessageOnly() {
		MessageAndExceptionToken token = new MessageAndExceptionToken();
		assertThat(render(token, "Hello World!", null)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that a text message will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyMessageOnly() throws SQLException {
		MessageAndExceptionToken token = new MessageAndExceptionToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("Hello World!", null), statement, 1);
		verify(statement).setString(1, "Hello World!");
	}

	/**
	 * Verifies that an exception will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderExceptionOnly() {
		Exception exception = new UnsupportedOperationException();
		MessageAndExceptionToken token = new MessageAndExceptionToken();

		assertThat(render(token, null, exception))
			.startsWith(UnsupportedOperationException.class.getName())
			.contains(MessageAndExceptionTokenTest.class.getName(), "renderExceptionOnly")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that an exception will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyExceptionOnly() throws SQLException {
		Exception exception = new UnsupportedOperationException();

		PreparedStatement statement = mock(PreparedStatement.class);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		new MessageAndExceptionToken().apply(createLogEntry(null, exception), statement, 1);

		verify(statement).setString(eq(1), captor.capture());
		assertThat(captor.getValue())
			.startsWith(UnsupportedOperationException.class.getName())
			.contains(MessageAndExceptionTokenTest.class.getName(), "applyExceptionOnly")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that a text message and an exception will be added combined to a {@link PreparedStatement}.
	 */
	@Test
	public void renderMessageAndException() {
		Exception exception = new UnsupportedOperationException();
		MessageAndExceptionToken token = new MessageAndExceptionToken();

		assertThat(render(token, "Hello World!", exception))
			.startsWith("Hello World!")
			.contains(UnsupportedOperationException.class.getName())
			.contains(MessageAndExceptionTokenTest.class.getName(), "renderMessageAndException")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that a text message and an exception will be rendered correctly in combination for a {@link StringBuilder}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyMessageAndException() throws SQLException {
		Exception exception = new UnsupportedOperationException();

		PreparedStatement statement = mock(PreparedStatement.class);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		new MessageAndExceptionToken().apply(createLogEntry("Hello World!", exception), statement, 1);

		verify(statement).setString(eq(1), captor.capture());
		assertThat(captor.getValue())
			.startsWith("Hello World!")
			.contains(UnsupportedOperationException.class.getName())
			.contains(MessageAndExceptionTokenTest.class.getName(), "applyMessageAndException")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param message
	 *            Text message for log entry
	 * @param exception
	 *            Caught exception or throwable for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final String message, final Throwable exception) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(message, exception), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a message and an exception or throwable.
	 *
	 * @param message
	 *            Text message for log entry
	 * @param exception
	 *            Caught exception or throwable for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final String message, final Throwable exception) {
		return LogEntryBuilder.empty().message(message).exception(exception).create();
	}

}
