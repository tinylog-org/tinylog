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
import java.util.Collections;
import java.util.regex.Pattern;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Token for outputting static text.
 */
final class PlainTextToken implements Token {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final Pattern TABULATOR_PATTERN = Pattern.compile("\t|\\\\t");
	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\r\n|\\\\r\\\\n|\n|\\\\n|\r|\\\\r");
	private static final Pattern ESCAPE_PATTERN = Pattern.compile("\\\\(.?)");

	private final String text;

	/**
	 * @param text
	 *            Static text
	 */
	PlainTextToken(final String text) {
		String normalized = TABULATOR_PATTERN.matcher(text).replaceAll("\t");
		normalized = NEW_LINE_PATTERN.matcher(normalized).replaceAll(NEW_LINE);
		this.text = ESCAPE_PATTERN.matcher(normalized).replaceAll("$1");
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.emptyList();
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		builder.append(text);
	}

}
