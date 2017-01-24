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
 * Tests for {@link MethodNameToken}.
 */
public final class MethodNameTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#METHOD} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		MethodNameToken token = new MethodNameToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.METHOD);
	}

	/**
	 * Verifies that the name of methods will be output correctly.
	 */
	@Test
	public void methodName() {
		MethodNameToken token = new MethodNameToken();
		assertThat(render(token, "testMethod")).isEqualTo("testMethod");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param methodName
	 *            Name of method for log entry
	 * @return Result text
	 */
	private String render(final Token token, final String methodName) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().methodName(methodName).create(), builder);
		return builder.toString();
	}

}
