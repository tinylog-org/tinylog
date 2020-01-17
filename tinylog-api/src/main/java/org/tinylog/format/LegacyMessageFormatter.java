/*
 * Copyright 2020 Martin Winandy
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

import java.text.MessageFormat;

/**
 * Message formatter that replaces '{}' placeholders with given arguments.
 *
 * <p>
 * Unlike {@link MessageFormat}, there are no argument indices in placeholders. Instead, the order of arguments counts.
 * </p>
 */
public class LegacyMessageFormatter extends AbstractMessageFormatter {

	/** */
	public LegacyMessageFormatter() {
	}

	@Override
	public String format(final String message, final Object[] arguments) {
		int length = message.length();
		StringBuilder builder = new StringBuilder(length + ADDITIONAL_STRING_BUILDER_CAPACITY);
		int argumentIndex = 0;

		for (int index = 0; index < length; ++index) {
			char character = message.charAt(index);
			if (character == '{' && index + 1 < length && message.charAt(index + 1) == '}' && argumentIndex < arguments.length) {
				builder.append(resolve(arguments[argumentIndex++]));
				index += 1; // Skip over closing curly bracket
			} else {
				builder.append(character);
			}
		}

		return builder.toString();
	}

}
