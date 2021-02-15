package org.tinylog.impl.format.style;

import org.tinylog.core.Framework;
import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Builder for creating {@link MaxLengthStyle MaxLengthStyles}.
 */
public class MaxLengthStyleBuilder implements StyleBuilder {

	/** */
	public MaxLengthStyleBuilder() {
	}

	@Override
	public String getName() {
		return "max-length";
	}

	@Override
	public Placeholder create(Framework framework, Placeholder placeholder, String value) {
		if (value == null) {
			throw new IllegalArgumentException("Maximum length is not defined for max length style");
		} else {
			return new MaxLengthStyle(placeholder, parseMaxLength(value.trim()));
		}
	}

	/**
	 * Parses the passed string as unsigned integer.
	 *
	 * @param value The maximum length as string
	 * @return The maximum length as integer
	 * @throws IllegalArgumentException The passed string does not contain a valid unsigned integer
	 */
	private static int parseMaxLength(String value) {
		try {
			return Integer.parseUnsignedInt(value);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Illegal maximum length \"" + value + "\" for max length style", ex);
		}
	}

}
