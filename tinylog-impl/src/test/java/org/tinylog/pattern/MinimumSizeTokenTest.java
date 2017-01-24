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
 * Tests for {@link MinimumSizeToken}.
 */
public final class MinimumSizeTokenTest {

	/**
	 * Verifies that required log entry values from child token will be passed-through.
	 */
	@Test
	public void requiredLogEntryValues() {
		MinimumSizeToken token = new MinimumSizeToken(new SeverityLevelToken(), 0);
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that text from child token will be output unmodified if text is longer than the defined minimum size.
	 */
	@Test
	public void longerThanMinimumSize() {
		MinimumSizeToken token = new MinimumSizeToken(new PlainTextToken("Hello!"), 5);
		assertThat(render(token)).isEqualTo("Hello!");
	}

	/**
	 * Verifies that text from child token will be output unmodified if text length is equal to the defined minimum
	 * size.
	 */
	@Test
	public void equalToMinimumSize() {
		MinimumSizeToken token = new MinimumSizeToken(new PlainTextToken("Hello!"), 6);
		assertThat(render(token)).isEqualTo("Hello!");
	}

	/**
	 * Verifies that additional spaces will be put after text from child token if text is shorter than the defined
	 * minimum size.
	 */
	@Test
	public void shorterThanMinimumSize() {
		MinimumSizeToken token = new MinimumSizeToken(new PlainTextToken("Hello!"), 7);
		assertThat(render(token)).isEqualTo("Hello! ");

		token = new MinimumSizeToken(new PlainTextToken("Hello!"), 8);
		assertThat(render(token)).isEqualTo("Hello!  ");
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
