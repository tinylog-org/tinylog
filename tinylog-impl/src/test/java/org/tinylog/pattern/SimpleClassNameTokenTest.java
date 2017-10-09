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
 * Tests for {@link SimpleClassNameToken}.
 */
public final class SimpleClassNameTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#CLASS} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		SimpleClassNameToken token = new SimpleClassNameToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.CLASS);
	}

	/**
	 * Verifies that the simple name of a fully-qualified class will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderClassWithPackage() {
		SimpleClassNameToken token = new SimpleClassNameToken();
		assertThat(render(token, "my.package.TestClass")).isEqualTo("TestClass");
	}

	/**
	 * Verifies that the simple name of a fully-qualified class will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyClassWithPackage() throws SQLException {
		SimpleClassNameToken token = new SimpleClassNameToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("my.package.TestClass"), statement, 1);
		verify(statement).setString(1, "TestClass");
	}

	/**
	 * Verifies that the simple name of a class will be appended to a {@link StringBuilder} for classes in the default
	 * package.
	 */
	@Test
	public void renderClassWithoutPackage() {
		SimpleClassNameToken token = new SimpleClassNameToken();
		assertThat(render(token, "AnotherClass")).isEqualTo("AnotherClass");
	}

	/**
	 * Verifies that the simple name of a class will be added to a {@link PreparedStatement} for classes in the default
	 * package.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyClassWithoutPackage() throws SQLException {
		SimpleClassNameToken token = new SimpleClassNameToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("AnotherClass"), statement, 1);
		verify(statement).setString(1, "AnotherClass");
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
