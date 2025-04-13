package org.tinylog.core.format.message;

import org.tinylog.core.Configuration;

/**
 * Message formatters can replace placeholders in strings with real values.
 */
public interface MessageFormatter {

    /**
     * Replaces all placeholders with real values.
     *
     * @param configuration The current tinylog configuration
     * @param message A text message with placeholders
     * @param arguments The actual replacement values for placeholders
     * @return Formatted text message
     */
    String format(Configuration configuration, String message, Object... arguments);

}
