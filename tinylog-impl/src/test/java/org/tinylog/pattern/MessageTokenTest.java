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
 * Tests for {@link MessageToken}.
 */
public final class MessageTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#MESSAGE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		MessageToken token = new MessageToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.MESSAGE);
	}

	/**
	 * Verifies that nothing will be output if there is no text message in a log entry.
	 */
	@Test
	public void missingMessage() {
		MessageToken token = new MessageToken();
		assertThat(render(token, null)).isEmpty();
	}

	/**
	 * Verifies that a text message will be output correctly.
	 */
	@Test
	public void textMessage() {
		MessageToken token = new MessageToken();
		assertThat(render(token, "Hello World!")).isEqualTo("Hello World!");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param message
	 *            Text message for log entry
	 * @return Result text
	 */
	private String render(final Token token, final String message) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().message(message).create(), builder);
		return builder.toString();
	}

}
