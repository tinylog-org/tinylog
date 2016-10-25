/*
 * Copyright 2014 Martin Winandy
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Format logging messages.
 */
final class MessageFormatter {
	
	private static final DecimalFormatSymbols FORMATTER_SYMBOLS = new DecimalFormatSymbols(); 

	private MessageFormatter() {
	}

	/**
	 * Replace "{}" placeholders with given arguments.
	 *
	 * @param message
	 *            Logging message with or without placeholders
	 * @param arguments
	 *            Replacements
	 * @return Formatted logging message
	 */
	static String format(final String message, final Object... arguments) {
		if (arguments == null || arguments.length == 0) {
			return message;
		} else {
			StringBuilder builder = new StringBuilder(256);
			
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
	}
	
	private static String format(String pattern, Object argument) {
		DecimalFormat formatter = new DecimalFormat(pattern, FORMATTER_SYMBOLS);
		try {
			return formatter.format(argument);
		} catch (IllegalArgumentException ex) {
			return String.valueOf(argument);
		}
	}

}
