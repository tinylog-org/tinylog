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
 * Tests for {@link FileNameToken}.
 */
public final class FileNameTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#FILE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		FileNameToken token = new FileNameToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.FILE);
	}

	/**
	 * Verifies that the name of a source file will be will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderFileName() {
		FileNameToken token = new FileNameToken();
		assertThat(render(token, "TestClass.java")).isEqualTo("TestClass.java");
	}

	/**
	 * Verifies that the name of a source file will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyFileName() throws SQLException {
		FileNameToken token = new FileNameToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("TestClass.java"), statement, 1);
		verify(statement).setString(1, "TestClass.java");
	}

	/**
	 * Verifies that {@code null} will be rendered as "&lt;unknown&gt;" for a {@link StringBuilder}.
	 */
	@Test
	public void renderNull() {
		FileNameToken token = new FileNameToken();
		assertThat(render(token, null)).isEqualTo("<unknown>");
	}

	/**
	 * Verifies that {@code null} will be added to a {@link PreparedStatement}, if there is no name of a source file in
	 * a log entry.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyNull() throws SQLException {
		FileNameToken token = new FileNameToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(null), statement, 1);
		verify(statement).setString(1, null);
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param fileName
	 *            Name of source file for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final String fileName) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(fileName), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a file name.
	 *
	 * @param fileName
	 *            File name for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final String fileName) {
		return LogEntryBuilder.empty().fileName(fileName).create();
	}

}
