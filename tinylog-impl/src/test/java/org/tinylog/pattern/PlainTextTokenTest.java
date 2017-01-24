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
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PlainTextToken}.
 */
public final class PlainTextTokenTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Verifies that a plain text token has no required log entry values.
	 */
	@Test
	public void requiredLogEntryValues() {
		PlainTextToken token = new PlainTextToken("Hello World!");
		assertThat(token.getRequiredLogEntryValues()).isEmpty();
	}

	/**
	 * Verifies that a simple text without tabulators and new lines will be rendered correctly.
	 */
	@Test
	public void simpleText() {
		PlainTextToken token = new PlainTextToken("Hello World!");
		assertThat(render(token)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that tabulators will be rendered correctly.
	 */
	@Test
	public void tabulators() {
		PlainTextToken token = new PlainTextToken("\t");
		assertThat(render(token)).isEqualTo("\t");

		token = new PlainTextToken("\\t");
		assertThat(render(token)).isEqualTo("\t");
	}

	/**
	 * Verifies that Windows line breaks will be rendered correctly.
	 */
	@Test
	public void windowsNewLines() {
		PlainTextToken token = new PlainTextToken("\r\n");
		assertThat(render(token)).isEqualTo(NEW_LINE);

		token = new PlainTextToken("\\r\\n");
		assertThat(render(token)).isEqualTo(NEW_LINE);
	}


	/**
	 * Verifies that Unix line breaks will be rendered correctly.
	 */
	@Test
	public void unixNewLines() {
		PlainTextToken token = new PlainTextToken("\n");
		assertThat(render(token)).isEqualTo(NEW_LINE);

		token = new PlainTextToken("\\n");
		assertThat(render(token)).isEqualTo(NEW_LINE);
	}


	/**
	 * Verifies that classic Mac OS line breaks will be rendered correctly.
	 */
	@Test
	public void macNewLines() {
		PlainTextToken token = new PlainTextToken("\r");
		assertThat(render(token)).isEqualTo(NEW_LINE);

		token = new PlainTextToken("\\r");
		assertThat(render(token)).isEqualTo(NEW_LINE);
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
