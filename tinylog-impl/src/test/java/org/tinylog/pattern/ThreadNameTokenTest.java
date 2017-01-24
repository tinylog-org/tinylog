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
 * Tests for {@link ThreadNameToken}.
 */
public final class ThreadNameTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#THREAD} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		ThreadNameToken token = new ThreadNameToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.THREAD);
	}

	/**
	 * Verifies that the name of the thread will be output.
	 */
	@Test
	public void threadName() {
		ThreadNameToken token = new ThreadNameToken();
		Thread thread = new Thread("MyThread");
		assertThat(render(token, thread)).isEqualTo("MyThread");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param thread
	 *            Thread for log entry
	 * @return Result text
	 */
	private String render(final Token token, final Thread thread) {
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().thread(thread).create(), builder);
		return builder.toString();
	}

}
