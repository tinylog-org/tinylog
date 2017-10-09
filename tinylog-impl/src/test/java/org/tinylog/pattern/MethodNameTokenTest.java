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
 * Tests for {@link MethodNameToken}.
 */
public final class MethodNameTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#METHOD} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		MethodNameToken token = new MethodNameToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.METHOD);
	}

	/**
	 * Verifies that the name of a method will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderMethodName() {
		MethodNameToken token = new MethodNameToken();
		assertThat(render(token, "testMethod")).isEqualTo("testMethod");
	}

	/**
	 * Verifies that the name of a method will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyMethodName() throws SQLException {
		MethodNameToken token = new MethodNameToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("testMethod"), statement, 1);
		verify(statement).setString(1, "testMethod");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param method
	 *            Name of method for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final String method) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(method), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a method name.
	 *
	 * @param method
	 *            Method name for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final String method) {
		return LogEntryBuilder.empty().methodName(method).create();
	}

}
