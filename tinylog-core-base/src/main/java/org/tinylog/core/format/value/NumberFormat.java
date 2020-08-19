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

package org.tinylog.core.format.value;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Format for numbers.
 */
public final class NumberFormat implements ValueFormat {

	private final DecimalFormatSymbols symbols;

	/**
	 * @param locale Locale for language or country depending decimal format symbols
	 */
	public NumberFormat(Locale locale) {
		this.symbols = new DecimalFormatSymbols(locale);
	}

	@Override
	public boolean isSupported(final Object value) {
		return value instanceof Number;
	}

	@Override
	public String format(final String pattern, final Object value) {
		return new DecimalFormat(pattern, symbols).format(value);
	}

}
