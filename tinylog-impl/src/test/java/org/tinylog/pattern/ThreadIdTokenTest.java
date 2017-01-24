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
 * Tests for {@link ThreadIdToken}.
 */
public final class ThreadIdTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#THREAD} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		ThreadIdToken token = new ThreadIdToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.THREAD);
	}

	/**
	 * Verifies that the ID of the thread will be output.
	 */
	@Test
	public void threadId() {
		ThreadIdToken token = new ThreadIdToken();
		Thread thread = new Thread();
		assertThat(render(token, thread)).isEqualTo(String.valueOf(thread.getId()));
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
