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

import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Message formatter for printf format syntax.
 * 
 * @see Formatter
 */
public class PrintfStyleFormatter extends AbstractMessageFormatter {
	
	private final Locale locale;
	
	/**
	 * @param locale
	 *            Locale for formatting numbers and dates
	 */
	public PrintfStyleFormatter(final Locale locale) {
		this.locale = locale;
	}

	@Override
	public String format(final String message, final Object[] arguments) {
		Object[] resolvedArguments = new Object[arguments.length];
		
		for (int i = 0; i < arguments.length; ++i) {
			resolvedArguments[i] = resolve(arguments[i]);
		}
		
		try {
			return String.format(locale, message, resolvedArguments);
		} catch (IllegalFormatException ex) {
			InternalLogger.log(Level.WARN, ex, "Illegal printf format message '" + message + "'");
			return message;
		}
	}

}
