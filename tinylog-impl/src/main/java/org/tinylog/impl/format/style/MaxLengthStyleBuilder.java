package org.tinylog.impl.format.style;

import org.tinylog.core.Framework;
import org.tinylog.impl.format.placeholder.ClassPlaceholder;
import org.tinylog.impl.format.placeholder.PackagePlaceholder;
import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Builder for creating {@link MaxTextLengthStyle MaxTextLengthStyles},
 * {@link MaxClassLengthStyle MaxClassLengthStyles}, and {@link MaxPackageLengthStyle MaxPackageLengthStyles},
 * depending on the passed placeholder.
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
			return create(placeholder, parseMaxLength(value.trim()));
		}
	}

	/**
	 * Applies a {@link AbstractMaxLengthStyle maximum length style wrapper} to a placeholder.
	 *
	 * @param placeholder The actual placeholder to style
	 * @param maxLength The maximum length for the placeholder's output
	 * @return The styled placeholder
	 */
	public static Placeholder create(Placeholder placeholder, int maxLength) {
		if (placeholder instanceof ClassPlaceholder) {
			return new MaxPackageLengthStyle(placeholder, maxLength);
		} else if (placeholder instanceof PackagePlaceholder) {
			return new MaxPackageLengthStyle(placeholder, maxLength);
		} else {
			return new MaxTextLengthStyle(placeholder, maxLength);
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
