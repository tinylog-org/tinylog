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

import java.util.Collection;
import java.util.EnumSet;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Bundler for combining multiple tokens to one.
 */
final class BundleToken implements Token {

	private final Collection<Token> tokens;

	/**
	 * @param tokens
	 *            Base tokens
	 */
	BundleToken(final Collection<Token> tokens) {
		this.tokens = tokens;
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		Collection<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
		for (Token token : tokens) {
			values.addAll(token.getRequiredLogEntryValues());
		}
		return values;
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		for (Token token : tokens) {
			token.render(logEntry, builder);
		}
	}

}
