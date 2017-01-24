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
 * Tests for {@link IndentationToken}.
 */
public final class IndentationTokenTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Verifies that required log entry values from child token will be passed-through.
	 */
	@Test
	public void requiredLogEntryValues() {
		IndentationToken token = new IndentationToken(new SeverityLevelToken(), 0);
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that a single line will be not indented.
	 */
	@Test
	public void singleLine() {
		IndentationToken token = new IndentationToken(new PlainTextToken("Hello World!"), 2);
		assertThat(render(token)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that the second line will be not indented but not the first.
	 */
	@Test
	public void twoLines() {
		IndentationToken token = new IndentationToken(new PlainTextToken("Hello" + NEW_LINE + "World!"), 2);
		assertThat(render(token)).isEqualTo("Hello" + NEW_LINE + "  World!");
	}

	/**
	 * Verifies that the second and third line will be not indented but not the first.
	 */
	@Test
	public void multipleLines() {
		IndentationToken token = new IndentationToken(new PlainTextToken("A" + NEW_LINE + "B" + NEW_LINE + "C"), 2);
		assertThat(render(token)).isEqualTo("A" + NEW_LINE + "  B" + NEW_LINE + "  C");
	}

	/**
	 * Verifies that tabulators at the beginning of a new line will be replaced by the specified number of spaces. All
	 * other tabulators should be kept untouched.
	 */
	@Test
	public void tabulators() {
		IndentationToken token = new IndentationToken(new PlainTextToken("Hello" + NEW_LINE + "\tWorld\t!"), 2);
		assertThat(render(token)).isEqualTo("Hello" + NEW_LINE + "    World\t!");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @return Result text
	 */
	private String render(final Token token) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().create(), builder);
		return builder.toString();
	}

}
