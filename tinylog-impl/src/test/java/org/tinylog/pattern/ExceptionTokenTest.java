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

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.throwable.ThrowableFilter;
import org.tinylog.throwable.ThrowableStore;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ExceptionToken}.
 */
public final class ExceptionTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#EXCEPTION} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		ExceptionToken token = new ExceptionToken(Collections.emptyList());
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that nothing will be output to a {@link StringBuilder}, if there is no caught exception in a log entry.
	 */
	@Test
	public void renderLogEntryWithoutException() {
		ExceptionToken token = new ExceptionToken(Collections.emptyList());
		assertThat(render(token, null)).isEmpty();
	}

	/**
	 * Verifies that {@code null} will be added to a {@link PreparedStatement}, if there is no caught exception in a log
	 * entry.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyLogEntryWithoutException() throws SQLException {
		ExceptionToken token = new ExceptionToken(Collections.emptyList());

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(LogEntryBuilder.empty().create(), statement, 1);
		verify(statement).setString(1, null);
	}

	/**
	 * Verifies that an exception without description will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderExceptionWithoutDescription() {
		Exception exception = new UnsupportedOperationException();
		ExceptionToken token = new ExceptionToken(Collections.emptyList());

		assertThat(render(token, exception))
			.startsWith(UnsupportedOperationException.class.getName())
			.contains(ExceptionTokenTest.class.getName(), "renderExceptionWithoutDescription")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that an exception without description will be added correctly rendered to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyExceptionWithoutDescription() throws SQLException {
		Exception exception = new UnsupportedOperationException();

		PreparedStatement statement = mock(PreparedStatement.class);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		new ExceptionToken(Collections.emptyList()).apply(createLogEntry(exception), statement, 1);

		verify(statement).setString(eq(1), captor.capture());
		assertThat(captor.getValue())
			.startsWith(UnsupportedOperationException.class.getName())
			.contains(ExceptionTokenTest.class.getName(), "applyExceptionWithoutDescription")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that an exception with description will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderExceptionWithDescription() {
		Exception exception = new NullPointerException("my message");
		ExceptionToken token = new ExceptionToken(Collections.emptyList());

		assertThat(render(token, exception))
			.startsWith(NullPointerException.class.getName() + ": my message")
			.contains(ExceptionTokenTest.class.getName(), "renderExceptionWithDescription")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that an exception with description will be added correctly rendered to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyExceptionWithDescription() throws SQLException {
		Exception exception = new NullPointerException("my message");

		PreparedStatement statement = mock(PreparedStatement.class);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		new ExceptionToken(Collections.emptyList()).apply(createLogEntry(exception), statement, 1);

		verify(statement).setString(eq(1), captor.capture());
		assertThat(captor.getValue())
			.startsWith(NullPointerException.class.getName() + ": my message")
			.contains(ExceptionTokenTest.class.getName(), "applyExceptionWithDescription")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that an exception including it's cause exception will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderExceptionWithCause() {
		Exception cause = new IOException("File not found");
		Exception exception = new RuntimeException(cause);

		ExceptionToken token = new ExceptionToken(Collections.emptyList());

		assertThat(render(token, exception))
			.startsWith(RuntimeException.class.getName())
			.contains(IOException.class.getName() + ": File not found")
			.contains(ExceptionTokenTest.class.getName(), "renderExceptionWithCause")
			.contains("... " + (cause.getStackTrace().length - 1) + " more")
			.hasLineCount(exception.getStackTrace().length + 4);
	}

	/**
	 * Verifies that an exception including its cause exception will be added correctly rendered to a
	 * {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyExceptionWithCause() throws SQLException {
		Exception cause = new IOException("File not found");
		Exception exception = new RuntimeException(cause);

		PreparedStatement statement = mock(PreparedStatement.class);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		new ExceptionToken(Collections.emptyList()).apply(createLogEntry(exception), statement, 1);

		verify(statement).setString(eq(1), captor.capture());
		assertThat(captor.getValue())
				.startsWith(RuntimeException.class.getName())
				.contains(IOException.class.getName() + ": File not found")
				.contains(ExceptionTokenTest.class.getName(), "applyExceptionWithCause")
				.contains("... " + (cause.getStackTrace().length - 1) + " more")
				.hasLineCount(exception.getStackTrace().length + 4);
	}

	/**
	 * Verifies that {@link ThrowableFilter throwable filters} can be applied to a passed exception when rendering a
	 * string.
	 */
	@Test
	public void renderyExceptionUsingFilters() {
		Exception exception = new RuntimeException("Test");
		List<ThrowableFilter> filters = Arrays.asList(
				origin -> new ThrowableStore(origin.getClassName(), origin.getMessage() + "1", origin.getStackTrace(), origin.getCause()),
				origin -> new ThrowableStore(origin.getClassName(), origin.getMessage() + "2", origin.getStackTrace(), origin.getCause())
		);

		ExceptionToken token = new ExceptionToken(filters);

		assertThat(render(token, exception)).startsWith(RuntimeException.class.getName() + ": Test12");
	}

	/**
	 * Verifies that {@link ThrowableFilter throwable filters} can be applied to a passed exception when rendering for a
	 * {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyyExceptionUsingFilters() throws SQLException {
		Exception exception = new RuntimeException("Test");
		List<ThrowableFilter> filters = Arrays.asList(
				origin -> new ThrowableStore(origin.getClassName(), origin.getMessage() + "1", origin.getStackTrace(), origin.getCause()),
				origin -> new ThrowableStore(origin.getClassName(), origin.getMessage() + "2", origin.getStackTrace(), origin.getCause())
		);

		PreparedStatement statement = mock(PreparedStatement.class);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		new ExceptionToken(filters).apply(createLogEntry(exception), statement, 1);

		verify(statement).setString(eq(1), captor.capture());
		assertThat(captor.getValue()).startsWith(RuntimeException.class.getName() + ": Test12");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param exception
	 *            Caught exception or throwable for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final Throwable exception) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(exception), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains an exception or throwable.
	 *
	 * @param exception
	 *            Caught exception or throwable for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final Throwable exception) {
		return LogEntryBuilder.empty().exception(exception).create();
	}

}
