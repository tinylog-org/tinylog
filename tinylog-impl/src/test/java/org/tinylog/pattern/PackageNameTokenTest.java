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
 * Tests for {@link PackageNameToken}.
 */
public final class PackageNameTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#CLASS} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		PackageNameToken token = new PackageNameToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.CLASS);
	}

	/**
	 * Verifies that the package name of a fully-qualified class will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderClassWithPackage() {
		PackageNameToken token = new PackageNameToken();
		assertThat(render(token, "my.package.TestClass")).isEqualTo("my.package");
	}

	/**
	 * Verifies that the package name of a fully-qualified class will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyClassWithPackage() throws SQLException {
		PackageNameToken token = new PackageNameToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("my.package.TestClass"), statement, 1);
		verify(statement).setString(1, "my.package");
	}

	/**
	 * Verifies that nothing will be appended to a {@link StringBuilder} for classes in the default package.
	 */
	@Test
	public void renderClassWithoutPackage() {
		PackageNameToken token = new PackageNameToken();
		assertThat(render(token, "AnotherClass")).isEmpty();
	}

	/**
	 * Verifies that nothing will be appended to a {@link StringBuilder} for a null class name.
	 */
	@Test
	public void renderClassWithoutPackageIfClassnameIsNull() {
		PackageNameToken token = new PackageNameToken();
		assertThat(render(token, null)).isEmpty();
	}
	
	/**
	 * Verifies that {@code null} will be added to a {@link PreparedStatement} for classes in the default package.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyClassWithoutPackage() throws SQLException {
		PackageNameToken token = new PackageNameToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry("AnotherClass"), statement, 1);
		verify(statement).setString(1, null);
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
