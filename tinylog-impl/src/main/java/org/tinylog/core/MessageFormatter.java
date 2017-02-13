/*
 * Copyright 2017 Martin Winandy
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

package org.tinylog.core;

import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Formatter for text messages. Placeholders '{}' will be replaced with given arguments.
 *
 * <p>
 * Unlike {@link MessageFormat}, there are no argument indices in placeholders. Instead, the order of arguments counts.
 * Choice format and decimal format compatible patterns can be used in placeholders.
 * </p>
 *
 * @see ChoiceFormat
 * @see DecimalFormat
 */
final class MessageFormatter {

	private static final int INITIAL_STRING_BUFFER_CAPACITY = 256;

	private final DecimalFormatSymbols symbols;

	/**
	 * @param locale
	 *            Locale for formatting numbers
	 */
	MessageFormatter(final Locale locale) {
		symbols = new DecimalFormatSymbols(locale);
	}

	/**
	 * Formats a text message. All placeholders '{}' will be replaced with the given arguments.
	 *
	 * @param message
	 *            Text message with placeholders
	 * @param arguments
	 *            Replacements for placeholders
	 * @return Formatted text message
	 */
	String format(final String message, final Object[] arguments) {
		StringBuilder builder = new StringBuilder(INITIAL_STRING_BUFFER_CAPACITY);

		int argumentIndex = 0;
		int start = 0;
		int openBraces = 0;

		for (int index = 0; index < message.length(); ++index) {
			char character = message.charAt(index);
			if (character == '{') {
				if (openBraces++ == 0 && start < index) {
					builder.append(message, start, index);
					start = index;
				}
			} else if (character == '}' && openBraces > 0) {
				if (--openBraces == 0) {
					if (argumentIndex < arguments.length) {
						Object argument = arguments[argumentIndex++];
						if (index == start + 1) {
							builder.append(argument);
						} else {
							builder.append(format(message.substring(start + 1, index), argument));
						}
					} else {
						builder.append(message, start, index + 1);
					}

					start = index + 1;
				}
			}
		}

		if (start < message.length()) {
			builder.append(message, start, message.length());
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
			InternalLogger.log(Level.WARNING, "Illegal argument '" + String.valueOf(argument) + "' for pattern '" + pattern + "'");
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
				return new ChoiceFormat(format(pattern, new Object[] { argument }));
			} else {
				return new ChoiceFormat(pattern);
			}
		} else {
			return new DecimalFormat(pattern, symbols);
		}
	}

}
