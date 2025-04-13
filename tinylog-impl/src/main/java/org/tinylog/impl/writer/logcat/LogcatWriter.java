package org.tinylog.impl.writer.logcat;

import java.util.Comparator;
import java.util.stream.Stream;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.writer.Writer;

import android.util.Log;

/**
 * Writer that passes log entries to Logcat via {@link Log} on Android devices.
 */
public class LogcatWriter implements Writer {

    private static final int BUILDER_CAPACITY = 1024;

    private final Placeholder tagPlaceholder;
    private final Placeholder messagePlaceholder;
    private final InternalLogger logger;

    /**
     * @param tagPlaceholder The placeholder for rendering the tag (can be {@code null})
     * @param messagePlaceholder The placeholder for rendering the message (must not be {@code null})
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public LogcatWriter(Placeholder tagPlaceholder, Placeholder messagePlaceholder, InternalLogger logger) {
        this.tagPlaceholder = tagPlaceholder;
        this.messagePlaceholder = messagePlaceholder;
        this.logger = logger;
    }

    @Override
    public OutputDetails getOutputDetails() {
        if (tagPlaceholder == null) {
            return messagePlaceholder.getOutputDetails();
        } else {
            return Stream.of(tagPlaceholder.getOutputDetails(), messagePlaceholder.getOutputDetails())
                .max(Comparator.comparing(Enum::ordinal))
                .orElse(OutputDetails.DISABLED);
        }
    }

    @Override
    public void log(LogEntry entry) {
        StringBuilder builder = new StringBuilder(BUILDER_CAPACITY);
        String tag = renderTag(builder, entry);

        builder.setLength(0);
        String message = renderMessage(builder, entry);

        switch (entry.getSeverityLevel()) {
            case TRACE:
                Log.println(Log.VERBOSE, tag, message);
                break;
            case DEBUG:
                Log.println(Log.DEBUG, tag, message);
                break;
            case INFO:
                Log.println(Log.INFO, tag, message);
                break;
            case WARN:
                Log.println(Log.WARN, tag, message);
                break;
            case ERROR:
                Log.println(Log.ERROR, tag, message);
                break;
            default:
                logger.log(Level.ERROR, "Severity level \"{}\" is unsupported", entry.getSeverityLevel());
        }
    }

    @Override
    public void flush() {
        // Ignore
    }

    @Override
    public void close() {
        // Ignore
    }

    /**
     * Renders the tag for Logcat.
     *
     * @param builder The string builder to use for rendering
     * @param entry The log entry to render
     * @return The rendered tag or {@code null} if there is no tag placeholder
     */
    private String renderTag(StringBuilder builder, LogEntry entry) {
        if (tagPlaceholder == null) {
            return null;
        } else {
            tagPlaceholder.render(builder, entry);
            return builder.toString();
        }
    }

    /**
     * Renders the message for Logcat.
     *
     * @param builder The string builder to use for rendering
     * @param entry The log entry to render
     * @return The rendered message
     */
    private String renderMessage(StringBuilder builder, LogEntry entry) {
        messagePlaceholder.render(builder, entry);
        return builder.toString();
    }

}
