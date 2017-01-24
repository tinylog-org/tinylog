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

import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SeverityLevelToken}.
 */
public final class SeverityLevelTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#LEVEL} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that {@link Level#TRACE} will be output correctly.
	 */
	@Test
	public void trace() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.TRACE)).isEqualTo("TRACE");
	}

	/**
	 * Verifies that {@link Level#DEBUG} will be output correctly.
	 */
	@Test
	public void debug() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.DEBUG)).isEqualTo("DEBUG");
	}

	/**
	 * Verifies that {@link Level#INFO} will be output correctly.
	 */
	@Test
	public void info() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.INFO)).isEqualTo("INFO");
	}

	/**
	 * Verifies that {@link Level#WARNING} will be output correctly.
	 */
	@Test
	public void warning() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.WARNING)).isEqualTo("WARNING");
	}

	/**
	 * Verifies that {@link Level#ERROR} will be output correctly.
	 */
	@Test
	public void error() {
		SeverityLevelToken token = new SeverityLevelToken();
		assertThat(render(token, Level.ERROR)).isEqualTo("ERROR");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param level
	 *            Severity level for log entry
	 * @return Result text
	 */
	private String render(final Token token, final Level level) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().level(level).create(), builder);
		return builder.toString();
	}

}
