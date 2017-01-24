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
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LineNumberToken}.
 */
public final class LineNumberTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#LINE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		LineNumberToken token = new LineNumberToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LINE);
	}

	/**
	 * Verifies that a source file line number will be output correctly.
	 */
	@Test
	public void lineNumber() {
		LineNumberToken token = new LineNumberToken();
		assertThat(render(token, 42)).isEqualTo("42");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param line
	 *            Line number in source file for log entry
	 * @return Result text
	 */
	private String render(final Token token, final int line) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().lineNumber(line).create(), builder);
		return builder.toString();
	}

}
