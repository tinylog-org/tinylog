package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Configuration;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for printing the log message and throwable of a log entry.
 */
public class MessagePlaceholder implements Placeholder {

    private final Configuration configuration;
    private final Placeholder messageOnlyPlaceholder;
    private final Placeholder exceptionPlaceholder;

    /**
     * @param configuration The current tinylog configuration
     */
    public MessagePlaceholder(Configuration configuration) {
        this.configuration = configuration;
        this.messageOnlyPlaceholder = new MessageOnlyPlaceholder(configuration);
        this.exceptionPlaceholder = new ExceptionPlaceholder();
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
        String message = entry.getFormattedMessage(configuration);
        Throwable throwable = entry.getThrowable();

        if (message == null && throwable == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            render(builder, entry);
            return builder.toString();
        }
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String message = entry.getFormattedMessage(configuration);
        Throwable throwable = entry.getThrowable();

        if (message != null) {
            messageOnlyPlaceholder.render(builder, entry);
        }

        if (message != null && throwable != null) {
            builder.append(": ");
        }

        if (throwable != null) {
            exceptionPlaceholder.render(builder, entry);
        }
    }

}
