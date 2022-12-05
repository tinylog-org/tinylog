package org.tinylog.core.format.value;

import org.tinylog.core.Framework;

/**
 * Format interface for different value types.
 */
public interface ValueFormat {

    /**
     * Checks if the passed value is supported.
     *
     * @param value The value to test
     * @return {@code true} if the passed value is supported, {@code false} if not
     */
    boolean isSupported(Object value);

    /**
     * Formats the passed value.
     *
     * @param framework The actual framework instance
     * @param pattern The format pattern for the value
     * @param value The value to format
     * @return The formatted value
     */
    String format(Framework framework, String pattern, Object value);

}
