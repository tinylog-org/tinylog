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

import java.io.IOException;

import org.junit.Test;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ExceptionToken}.
 */
public final class ExceptionTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#EXCEPTION} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		ExceptionToken token = new ExceptionToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that nothing will be output if there is no catched exception in a log entry.
	 */
	@Test
	public void withoutException() {
		ExceptionToken token = new ExceptionToken();
		assertThat(render(token, null)).isEmpty();
	}

	/**
	 * Verifies that an exception without description will be output correctly.
	 */
	@Test
	public void exceptionWithoutDescription() {
		Exception exception = new UnsupportedOperationException();
		ExceptionToken token = new ExceptionToken();
		assertThat(render(token, exception))
			.startsWith(UnsupportedOperationException.class.getName())
			.contains(ExceptionTokenTest.class.getName(), "exceptionWithoutDescription")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that an exception with description will be output correctly.
	 */
	@Test
	public void exceptionWithDescription() {
		Exception exception = new NullPointerException("my message");
		ExceptionToken token = new ExceptionToken();
		assertThat(render(token, exception))
			.startsWith(NullPointerException.class.getName() + ": my message")
			.contains(ExceptionTokenTest.class.getName(), "exceptionWithDescription")
			.hasLineCount(exception.getStackTrace().length + 1);
	}

	/**
	 * Verifies that an exception including it's cause exception will be output correctly.
	 */
	@Test
	public void exceptionWithCause() {
		Exception cause = new IOException("File not found");
		Exception exception = new RuntimeException(cause);

		ExceptionToken token = new ExceptionToken();
		assertThat(render(token, exception))
			.startsWith(RuntimeException.class.getName())
			.contains(IOException.class.getName() + ": File not found")
			.contains(ExceptionTokenTest.class.getName(), "exceptionWithCause")
			.hasLineCount(exception.getStackTrace().length + cause.getStackTrace().length + 2);
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param exception
	 *            Catched exception or throwable for log entry
	 * @return Result text
	 */
	private String render(final Token token, final Throwable exception) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().exception(exception).create(), builder);
		return builder.toString();
	}

}
