package org.tinylog.impl.format.styles;

import java.util.Locale;

import org.tinylog.core.Framework;
import org.tinylog.impl.format.placeholders.Placeholder;

/**
 * Builder for applying a {@link MinLengthStyle minimum length style wrapper} and a {@link AbstractMaxLengthStyle
 * maximum length style wrapper} to a placeholder for having a styled placeholder with an exactly defined length.
 */
public class LengthStyleBuilder implements StyleBuilder {

	/** */
	public LengthStyleBuilder() {
	}

	@Override
	public String getName() {
		return "length";
	}

	@Override
	public Placeholder create(Framework framework, Placeholder placeholder, String value) {
		if (value == null) {
			throw new IllegalArgumentException("Length is not defined for length style");
		} else {
			int commaIndex = value.indexOf(',');
			if (commaIndex < 0) {
				return create(placeholder, parseLength(value.trim()), Position.LEFT);
			} else {
				return create(
					placeholder,
					parseLength(value.substring(0, commaIndex).trim()),
					parsePosition(value.substring(commaIndex + 1).trim())
				);
			}
		}
	}

	/**
	 * Applies a {@link MinLengthStyle minimum length style wrapper} and a {@link AbstractMaxLengthStyle maximum length
	 * style wrapper} to a placeholder.
	 *
	 * @param placeholder The actual placeholder to style
	 * @param length The length for the placeholder's output
	 * @param position The position for the placeholder's output
	 * @return The created styled placeholder
	 */
	private static Placeholder create(Placeholder placeholder, int length, Position position) {
		return new MinLengthStyle(MaxLengthStyleBuilder.create(placeholder, length), length, position);
	}

	/**
	 * Parses the passed string as unsigned integer.
	 *
	 * @param value The length as string
	 * @return The length as integer
	 * @throws IllegalArgumentException The passed string does not contain a valid unsigned integer
	 */
	private static int parseLength(String value) {
		try {
			return Integer.parseUnsignedInt(value);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Illegal length \"" + value + "\" for length style", ex);
		}
	}

	/**
	 * Creates a {@link Position} by reading its name from a string.
	 *
	 * @param value The case insensitive position name
	 * @return The position enum value
	 * @throws IllegalArgumentException The passed value contains an invalid position name
	 */
	private static Position parsePosition(String value) {
		try {
			return Position.valueOf(value.toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Illegal position \"" + value + "\" for length style", ex);
		}
	}

}
