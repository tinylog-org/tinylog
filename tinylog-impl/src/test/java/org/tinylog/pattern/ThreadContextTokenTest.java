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

import java.util.Map;

import org.junit.Test;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ThreadContextToken}.
 */
public final class ThreadContextTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#CONTEXT} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		ThreadContextToken token = new ThreadContextToken("test");
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.CONTEXT);
	}

	/**
	 * Verifies that nothing will be output if a property doesn't exist in thread context.
	 */
	@Test
	public void defaultEmptyValue() {
		ThreadContextToken token = new ThreadContextToken("test");
		assertThat(render(token, emptyMap())).isEmpty();
	}

	/**
	 * Verifies that a defined default value will be output if a property doesn't exist in thread context.
	 */
	@Test
	public void definedEmptyValue() {
		ThreadContextToken token = new ThreadContextToken("test", "-");
		assertThat(render(token, emptyMap())).isEqualTo("-");
	}

	/**
	 * Verifies that an existing property will be output correctly.
	 */
	@Test
	public void existingProperty() {
		ThreadContextToken token = new ThreadContextToken("test");
		assertThat(render(token, singletonMap("test", "42"))).isEqualTo("42");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param context
	 *            Thread context for log entry
	 * @return Result text
	 */
	private String render(final Token token, final Map<String, String> context) {
		LogEntryBuilder logEntryBuilder = LogEntryBuilder.empty();
		context.forEach((key, value) -> logEntryBuilder.context(key, value));

		StringBuilder stringBuilder = new StringBuilder();
		token.render(logEntryBuilder.create(), stringBuilder);
		return stringBuilder.toString();
	}

}
