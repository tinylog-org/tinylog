package org.tinylog.impl.format.pattern.styles;

import java.util.Arrays;

import org.tinylog.core.internal.LoggingContext;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Builder for creating an instance of {@link IndentStyle}.
 */
public class IndentStyleBuilder implements StyleBuilder {

    /** */
    public IndentStyleBuilder() {
    }

    @Override
    public String getName() {
        return "indent";
    }

    @Override
    public Placeholder create(LoggingContext context, Placeholder placeholder, String value) {
        if (value == null) {
            return new IndentStyle(placeholder, "\t");
        } else {
            char[] spaces = new char[parseIndentationDepth(value)];
            Arrays.fill(spaces, ' ');
            return new IndentStyle(placeholder, new String(spaces));
        }
    }

    /**
     * Parses the passed string as unsigned integer.
     *
     * @param value The indentation depth as string
     * @return The indentation depth as integer
     * @throws IllegalArgumentException The passed string does not contain a valid unsigned integer
     */
    private static int parseIndentationDepth(String value) {
        try {
            return Integer.parseUnsignedInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal indentation depth \"" + value + "\" for indent style", ex);
        }
    }

}
