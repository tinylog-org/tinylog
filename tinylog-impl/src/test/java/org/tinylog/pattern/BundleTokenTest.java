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
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link BundleToken}.
 */
public final class BundleTokenTest {

	/**
	 * Verifies that all required log entry values from child tokens will be returned.
	 */
	@Test
	public void requiredLogEntryValues() {
		BundleToken token = new BundleToken(asList(new PackageNameToken(), new SeverityLevelToken()));
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.CLASS, LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that all child tokens will be rendered for a {@link StringBuilder} in the given order.
	 */
	@Test
	public void render() {
		BundleToken token = new BundleToken(asList(new PlainTextToken("Abc"), new PlainTextToken("123")));

		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().create(), builder);
		assertThat(builder).hasToString("Abc123");
	}

	/**
	 * Verifies that all child tokens will be rendered for a {@link PreparedStatement} in the given order.
	 * 
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void apply() throws SQLException {
		BundleToken token = new BundleToken(asList(new PlainTextToken("Abc"), new PlainTextToken("123")));

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(LogEntryBuilder.empty().create(), statement, 1);
		verify(statement).setString(1, "Abc123");
	}

}
