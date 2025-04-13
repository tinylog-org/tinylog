package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Configuration;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for printing the log message of a log entry. In opposite to {@link MessagePlaceholder},
 * the log message is output without the logged throwable.
 */
public class MessageOnlyPlaceholder implements Placeholder {

    private final Configuration configuration;

    /**
     * @param configuration The current tinylog configuration
     */
    public MessageOnlyPlaceholder(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITHOUT_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        return entry.getFormattedMessage(configuration);
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String message = entry.getFormattedMessage(configuration);

        if (message != null) {
            builder.append(message);
        }
    }

}
