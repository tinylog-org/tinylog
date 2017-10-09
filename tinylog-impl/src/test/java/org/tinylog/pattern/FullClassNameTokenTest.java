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
 * Tests for {@link FullClassNameToken}.
 */
public final class FullClassNameTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#CLASS} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		FullClassNameToken token = new FullClassNameToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.CLASS);
	}

	/**
	 * Verifies that the fully-qualified name of a class will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderClassName() {
		FullClassNameToken token = new FullClassNameToken();
		assertThat(render(token, "my.package.TestClass")).isEqualTo("my.package.TestClass");
	}

	/**
	 * Verifies that the fully-qualified name of a class will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyClassName() throws SQLException {
		FullClassNameToken token = new FullClassNameToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("my.package.TestClass"), statement, 1);
		verify(statement).setString(1, "my.package.TestClass");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param className
	 *            Issuing class name for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final String className) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(className), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a class name.
	 *
	 * @param className
	 *            Class name for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final String className) {
		return LogEntryBuilder.empty().className(className).create();
	}

}
