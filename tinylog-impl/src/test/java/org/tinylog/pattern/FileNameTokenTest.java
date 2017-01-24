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
 * Tests for {@link FileNameToken}.
 */
public final class FileNameTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#FILE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		FileNameToken token = new FileNameToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.FILE);
	}

	/**
	 * Verifies that the name of a source file will be output correctly.
	 */
	@Test
	public void fileName() {
		FileNameToken token = new FileNameToken();
		assertThat(render(token, "TestClass.java")).isEqualTo("TestClass.java");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param fileName
	 *            Name of source file for log entry
	 * @return Result text
	 */
	private String render(final Token token, final String fileName) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().fileName(fileName).create(), builder);
		return builder.toString();
	}

}
