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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BundleToken}.
 */
public final class BundleTokenTest {

	/**
	 * Verifies that all required log entry values from child tokens will be returned.
	 */
	@Test
	public void requiredLogEntryValues() {
		BundleToken token = new BundleToken(asList(new PackageNameToken(), new SeverityLevelToken()));
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.CLASS, LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that all child tokens will be rendered in the given order.
	 */
	@Test
	public void combineOutput() {
		BundleToken token = new BundleToken(asList(new PlainTextToken("Abc"), new PlainTextToken("123")));
		assertThat(render(token)).isEqualTo("Abc123");
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
