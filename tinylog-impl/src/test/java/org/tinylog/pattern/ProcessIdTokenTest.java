/*
 * Copyright 2017 Martin Winandy
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
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ProcessIdToken}.
 */
public final class ProcessIdTokenTest {

	/**
	 * Verifies that a process ID token has no required log entry values.
	 */
	@Test
	public void requiredLogEntryValues() {
		ProcessIdToken token = new ProcessIdToken();
		assertThat(token.getRequiredLogEntryValues()).isEmpty();
	}

	/**
	 * Verifies that the process ID be will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void render() {
		ProcessIdToken token = new ProcessIdToken();
		assertThat(render(token)).isEqualTo(Integer.toString(RuntimeProvider.getProcessId()));
	}

	/**
	 * Verifies that the process ID be will be added to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void apply() throws SQLException {
		ProcessIdToken token = new ProcessIdToken();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(LogEntryBuilder.empty().create(), statement, 1);
		verify(statement).setInt(1, RuntimeProvider.getProcessId());
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @return Result text
	 */
	private static String render(final Token token) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().create(), builder);
		return builder.toString();
	}

}
