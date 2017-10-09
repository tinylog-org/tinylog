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
import java.util.Map;

import org.junit.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ThreadContextToken}.
 */
public final class ThreadContextTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#CONTEXT} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		ThreadContextToken token = new ThreadContextToken("test");
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.CONTEXT);
	}

	/**
	 * Verifies that nothing will be rendered, if a property doesn't exist in thread context and no default value is
	 * defined.
	 */
	@Test
	public void renderDefaultEmptyValue() {
		ThreadContextToken token = new ThreadContextToken("test");
		assertThat(render(token, emptyMap())).isEmpty();
	}

	/**
	 * Verifies that {@code null} will be added to a {@link PreparedStatement}, if a property doesn't exist in thread
	 * context and no default value is defined.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyDefaultEmptyTag() throws SQLException {
		ThreadContextToken token = new ThreadContextToken("test");

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(emptyMap()), statement, 1);
		verify(statement).setString(1, null);
	}

	/**
	 * Verifies that a defined default value will be rendered correctly for a {@link StringBuilder}, if a property
	 * doesn't exist in thread context.
	 */
	@Test
	public void renderDefinedEmptyValue() {
		ThreadContextToken token = new ThreadContextToken("test", "-");
		assertThat(render(token, emptyMap())).isEqualTo("-");
	}

	/**
	 * Verifies that a defined default value will be added to a {@link PreparedStatement}, if a property doesn't exist
	 * in thread context.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyDefinedEmptyValue() throws SQLException {
		ThreadContextToken token = new ThreadContextToken("test", "-");

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(emptyMap()), statement, 1);
		verify(statement).setString(1, "-");
	}

	/**
	 * Verifies that an existing property will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderExistingProperty() {
		ThreadContextToken token = new ThreadContextToken("test");
		assertThat(render(token, singletonMap("test", "42"))).isEqualTo("42");
	}

	/**
	 * Verifies that an existing property will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyExistingProperty() throws SQLException {
		ThreadContextToken token = new ThreadContextToken("test");

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(singletonMap("test", "42")), statement, 1);
		verify(statement).setString(1, "42");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param context
	 *            Thread context for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final Map<String, String> context) {
		StringBuilder stringBuilder = new StringBuilder();
		token.render(createLogEntry(context), stringBuilder);
		return stringBuilder.toString();
	}

	/**
	 * Creates a log entry that contains thread context values.
	 *
	 * @param context
	 *            Threat context values for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final Map<String, String> context) {
		LogEntryBuilder logEntryBuilder = LogEntryBuilder.empty();
		context.forEach((key, value) -> logEntryBuilder.context(key, value));
		return logEntryBuilder.create();
	}

}
