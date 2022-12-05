package org.tinylog.core.format.message;

import org.tinylog.core.Framework;

/**
 * Message formatter can replace placeholders with real values in strings.
 */
public interface MessageFormatter {

    /**
     * Replaces all placeholders with real values.
     *
     * @param framework The actual framework instance
     * @param message A text message with placeholders
     * @param arguments The actual replacement values for placeholders
     * @return Formatted text message
     */
    String format(Framework framework, String message, Object... arguments);

}
