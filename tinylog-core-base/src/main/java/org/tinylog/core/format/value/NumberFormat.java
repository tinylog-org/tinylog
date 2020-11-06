package org.tinylog.core.format.value;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Format for numbers.
 */
public class NumberFormat implements ValueFormat {

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
