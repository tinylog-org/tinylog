package org.tinylog.core.format.message;

import org.tinylog.core.internal.LoggingContext;

/**
 * Message formatter can replace placeholders with real values in strings.
 */
public interface MessageFormatter {

    /**
     * Replaces all placeholders with real values.
     *
     * @param context The current logging context
     * @param message A text message with placeholders
     * @param arguments The actual replacement values for placeholders
     * @return Formatted text message
     */
    String format(LoggingContext context, String message, Object... arguments);

}
