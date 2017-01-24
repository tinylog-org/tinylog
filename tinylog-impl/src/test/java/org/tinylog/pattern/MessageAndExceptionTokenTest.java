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
 * Tests for {@link MessageAndExceptionToken}.
 */
public final class MessageAndExceptionTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#MESSAGE} and {@link LogEntryValue#EXCEPTION} are the only required log entry
	 * values.
	 */
	@Test
	public void requiredLogEntryValues() {
		MessageAndExceptionToken token = new MessageAndExceptionToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that nothing will be output if there is neither a text message nor an exception or throwable in a log
	 * entry.
	 */
	@Test
	public void neitherMessageNorException() {
		MessageAndExceptionToken token = new MessageAndExceptionToken();
		assertThat(render(token, null, null)).isEmpty();
	}

	/**
	 * Verifies that a text message will be output correctly.
	 */
	@Test
	public void messageOnly() {
		MessageAndExceptionToken token = new MessageAndExceptionToken();
		assertThat(render(token, "Hello World!", null)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that an exception will be output correctly.
	 */
	@Test
	public void exceptionOnly() {
		Exception exception = new UnsupportedOperationException();
		MessageAndExceptionToken token = new MessageAndExceptionToken();
		assertThat(render(token, null, exception))
				.startsWith(UnsupportedOperationException.class.getName())
				.contains(MessageAndExceptionTokenTest.class.getName(), "exceptionOnly")
				.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that a text message and an exception be output correctly in combination.
	 */
	@Test
	public void messageAndException() {
		Exception exception = new UnsupportedOperationException();
		MessageAndExceptionToken token = new MessageAndExceptionToken();
		assertThat(render(token, "Hello World!", exception))
				.startsWith("Hello World!")
				.contains(UnsupportedOperationException.class.getName())
				.contains(MessageAndExceptionTokenTest.class.getName(), "messageAndException")
				.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param message
	 *            Text message for log entry
	 * @param exception
	 *            Catched exception or throwable for log entry
	 * @return Result text
	 */
	private String render(final Token token, final String message, final Throwable exception) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().message(message).exception(exception).create(), builder);
		return builder.toString();
	}

}
