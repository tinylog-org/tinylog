package org.tinylog.core.format.message;

import org.tinylog.core.Configuration;

/**
 * Simple message formatter that just replaces '{}' placeholders with passed arguments.
 *
 * <p>
 *     Neither custom formats nor quoting are supported.
 * </p>
 */
public class SimpleMessageFormatter implements MessageFormatter {

    /** */
    public SimpleMessageFormatter() {
    }

    @Override
    public String format(Configuration configuration, String message, Object... arguments) {
        int length = message.length();
        StringBuilder builder = new StringBuilder(length);

        int messageIndex = 0;
        int argumentIndex = 0;

        while (messageIndex < length) {
            char character = message.charAt(messageIndex);
            if (character == '{'
                && messageIndex + 1 < length
                && message.charAt(messageIndex + 1) == '}'
                && argumentIndex < arguments.length
            ) {
                String value = String.valueOf(arguments[argumentIndex++]);
                builder.append(value);
                messageIndex += 2;
            } else {
                builder.append(character);
                messageIndex += 1;
            }
        }

        return builder.toString();
    }

}
