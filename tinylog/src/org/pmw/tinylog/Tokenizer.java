/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Convert a format pattern to a list of tokens.
 * 
 * @see Logger#setLoggingFormat(String)
 */
final class Tokenizer {

	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final Pattern NEW_LINE_REPLACER = Pattern.compile("\r\n|\\\\r\\\\n|\n|\\\\n|\r|\\\\r");

	private Tokenizer() {
	}

	/**
	 * Parse a format pattern.
	 * 
	 * @param formatPattern
	 *            Format pattern for logging entries
	 * 
	 * @return List of tokens
	 */
	static List<Token> parse(final String formatPattern) {
		List<Token> tokens = new ArrayList<Token>();
		char[] chars = formatPattern.toCharArray();

		int start = 0;
		int openMarkers = 0;
		for (int i = 0; i < chars.length; ++i) {
			char c = chars[i];
			if (c == '{') {
				if (openMarkers == 0 && start < i) {
					tokens.add(getToken(formatPattern.substring(start, i)));
					start = i;
				}
				++openMarkers;
			} else if (openMarkers > 0 && c == '}') {
				--openMarkers;
				if (openMarkers == 0) {
					tokens.add(getToken(formatPattern.substring(start, i + 1)));
					start = i + 1;
				}
			}
		}

		if (start < chars.length - 1) {
			tokens.add(getToken(formatPattern.substring(start, chars.length)));
		}

		return tokens;
	}

	private static Token getToken(final String text) {
		if ("{thread}".equals(text)) {
			return new Token(EToken.THREAD);
		} else if ("{method}".equals(text)) {
			return new Token(EToken.METHOD);
		} else if ("{level}".equals(text)) {
			return new Token(EToken.LOGGING_LEVEL);
		} else if ("{message}".equals(text)) {
			return new Token(EToken.MESSAGE);
		} else if (text.startsWith("{date") && text.endsWith("}")) {
			String dateFormatPattern;
			if (text.length() > 6) {
				dateFormatPattern = text.substring(6, text.length() - 1);
			} else {
				dateFormatPattern = DEFAULT_DATE_FORMAT_PATTERN;
			}
			return new Token(EToken.DATE, new SimpleDateFormat(dateFormatPattern));
		} else {
			return new Token(EToken.PLAIN_TEXT, NEW_LINE_REPLACER.matcher(text).replaceAll(NEW_LINE));
		}
	}

}
