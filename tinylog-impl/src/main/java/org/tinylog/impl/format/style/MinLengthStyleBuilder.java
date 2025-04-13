package org.tinylog.impl.format.style;

import java.util.Locale;

import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Builder for creating an instance of {@link MinLengthStyle}.
 */
public class MinLengthStyleBuilder implements StyleBuilder {

    /** */
    public MinLengthStyleBuilder() {
    }

    @Override
    public String getName() {
        return "min-length";
    }

    @Override
    public Placeholder create(TinylogContext context, Placeholder placeholder, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Minimum length is not defined for min length style");
        } else {
            int index = value.indexOf(',');
            return new MinLengthStyle(
                placeholder,
                parseMinLength(index < 0 ? value.trim() : value.substring(0, index).trim()),
                index < 0 ? Position.LEFT : parsePosition(value.substring(index + 1).trim())
            );
        }
    }

    /**
     * Parses the passed string as unsigned integer.
     *
     * @param value The minimum length as string
     * @return The minimum length as integer
     * @throws IllegalArgumentException If the passed string does not contain a valid unsigned integer
     */
    private static int parseMinLength(String value) {
        try {
            return Integer.parseUnsignedInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal minimum length \"" + value + "\" for min length style", ex);
        }
    }

    /**
     * Creates a {@link Position} by reading its name from a string.
     *
     * @param value The case-insensitive position name
     * @return The position enum value
     * @throws IllegalArgumentException If the passed value contains an invalid position name
     */
    private static Position parsePosition(String value) {
        try {
            return Position.valueOf(value.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Illegal position \"" + value + "\" for min length style", ex);
        }
    }

}
