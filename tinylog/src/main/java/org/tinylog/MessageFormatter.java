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

package org.tinylog;

/**
 * Format logging messages.
 */
final class MessageFormatter {

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
			String pattern = message;
			int index = 0;
			for (int i = 0; i < arguments.length && index < pattern.length(); ++i) {
				int found = pattern.indexOf("{}", index);
				if (found >= 0) {
					builder.append(pattern, index, found);
					builder.append(arguments[i]);
					index = found + 2;
				} else {
					break;
				}
			}
			builder.append(pattern, index, pattern.length());
			return builder.toString();
		}
	}

}
