/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.format;

import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Advances message formatter that replaces '{}' placeholders with given arguments.
 *
 * <p>
 * Unlike {@link LegacyMessageFormatter}, choice format and decimal format compatible patterns can be used in
 * placeholders, and curly brackets can be escaped by a backslash.
 * </p>
 */
public class AdvancedMessageFormatter extends AbstractMessageFormatter {

	private final DecimalFormatSymbols symbols;
	private final boolean escape;

	/**
	 * @param locale
	 *            Locale for formatting numbers
	 * @param escape
	 *            {@code true} to enable escaping by ticks, {@code false} to disable
	 */
	public AdvancedMessageFormatter(final Locale locale, final boolean escape) {
		this.symbols = new DecimalFormatSymbols(locale);
		this.escape = escape;
	}

	@Override
	public String format(final String message, final Object[] arguments) {
		return format(message, Arrays.asList(arguments).iterator());
	}

	/**
	 * Formats a text message. All placeholders will be replaced with the given arguments.
	 *
	 * @param message
	 *            Text message with placeholders
	 * @param arguments
	 *            Replacements for placeholders
	 * @return Formatted text message
	 */
	private String format(final String message, final Iterator<Object> arguments) {
		int length = message.length();

		StringBuilder builder = new StringBuilder(length + ADDITIONAL_STRING_BUILDER_CAPACITY);

		int openingTickIndex = -1;
		int openingCurlyBracketIndex = -1;
		int openingCurlyBracketsCount = 0;

		for (int index = 0; index < length; ++index) {
			char character = message.charAt(index);
			if (escape && character == '\'' && index + 1 < length && openingCurlyBracketsCount == 0) {
				if (message.charAt(index + 1) == '\'') {
					builder.append('\'');
					index += 1;
				} else {
					openingTickIndex = openingTickIndex < 0 ? builder.length() : -1;
				}
			} else if (character == '{' && index + 1 < length && arguments.hasNext() && openingTickIndex < 0) {
				if (openingCurlyBracketsCount++ == 0) {
					openingCurlyBracketIndex = builder.length();
				} else {
					builder.append(character);
				}
			} else if (character == '}' && openingCurlyBracketsCount > 0 && openingTickIndex < 0) {
				if (--openingCurlyBracketsCount == 0) {
					Object argument = resolve(arguments.next());
					if (openingCurlyBracketIndex == builder.length()) {
						builder.append(argument);
					} else {
						String pattern = builder.substring(openingCurlyBracketIndex);
						builder.setLength(openingCurlyBracketIndex);
						builder.append(format(pattern, argument));
					}
				} else {
					builder.append(character);
				}
			} else {
				builder.append(character);
			}
		}

		if (openingCurlyBracketsCount > 0) {
			builder.insert(openingCurlyBracketIndex, '{');
		}

		if (openingTickIndex >= 0) {
			builder.insert(openingTickIndex, '\'');
		}

		return builder.toString();
	}

	/**
	 * Formats a pattern of a placeholder.
	 *
	 * @param pattern
	 *            Pattern of placeholder
	 * @param argument
	 *            Replacement for placeholder
	 * @return Formatted pattern
	 */
	private String format(final String pattern, final Object argument) {
		try {
			return getFormatter(pattern, argument).format(argument);
		} catch (IllegalArgumentException ex) {
			InternalLogger.log(Level.WARN, "Illegal argument '" + argument + "' for pattern '" + pattern + "'");
			return String.valueOf(argument);
		}
	}

	/**
	 * Gets the format object for a pattern of a placeholder. {@link ChoiceFormat} and {@link DecimalFormat} are
	 * supported.
	 *
	 * @param pattern
	 *            Pattern of placeholder
	 * @param argument
	 *            Replacement for placeholder
	 * @return Format object
	 */
	private Format getFormatter(final String pattern, final Object argument) {
		if (pattern.indexOf('|') != -1) {
			int start = pattern.indexOf('{');
			if (start >= 0 && start < pattern.lastIndexOf('}')) {
				return new ChoiceFormat(format(pattern, new EndlessIterator<Object>(argument)));
			} else {
				return new ChoiceFormat(pattern);
			}
		} else {
			return new DecimalFormat(pattern, symbols);
		}
	}

}
