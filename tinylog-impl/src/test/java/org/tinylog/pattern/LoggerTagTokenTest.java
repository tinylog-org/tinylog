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
 * Tests for {@link LoggerTagToken}.
 */
public final class LoggerTagTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#TAG} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		LoggerTagToken token = new LoggerTagToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.TAG);
	}

	/**
	 * Verifies that a tag from log entry will be output correctly.
	 */
	@Test
	public void existingTag() {
		LoggerTagToken token = new LoggerTagToken();
		assertThat(render(token, "test")).isEqualTo("test");
	}

	/**
	 * Verifies that nothing will be output, if there is neither a tag in the log entry nor a specified replacement for
	 * empty tags.
	 */
	@Test
	public void defaultEmptyTag() {
		LoggerTagToken token = new LoggerTagToken();
		assertThat(render(token, null)).isEmpty();
	}

	/**
	 * Verifies that a specified replacement for empty tags will be output, if there is no tag in the log entry.
	 */
	@Test
	public void definedEmptyTag() {
		LoggerTagToken token = new LoggerTagToken("-");
		assertThat(render(token, null)).isEqualTo("-");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param tag
	 *            Logger tag for log entry
	 * @return Result text
	 */
	private String render(final Token token, final String tag) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().tag(tag).create(), builder);
		return builder.toString();
	}

}
