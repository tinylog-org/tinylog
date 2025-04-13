package org.tinylog.core.backend;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;
import org.tinylog.core.internal.InternalLogger;

/**
 * Internal logging backend that prints internal tinylog errors and warnings to {@link System#err}.
 */
public class InternalLoggingBackend implements LoggingBackend {

    private static final LevelVisibility INVISIBLE = new LevelVisibility(OutputDetails.DISABLED);

    private static final LevelVisibility VISIBLE = new LevelVisibility(
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.ENABLED_WITHOUT_LOCATION_INFO,
        OutputDetails.ENABLED_WITHOUT_LOCATION_INFO
    );

    private final ContextStorage contextStorage;
    private final Configuration configuration;

    /**
     * @param configuration The current tinylog configuration
     */
    public InternalLoggingBackend(Configuration configuration) {
        this.contextStorage = new NopContextStorage();
        this.configuration = configuration;
    }

    @Override
    public ContextStorage getContextStorage() {
        return contextStorage;
    }

    @Override
    public LevelVisibility getLevelVisibilityByClass(String className) {
        return VISIBLE;
    }

    @Override
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        if (InternalLogger.TAG.equals(tag)) {
            return VISIBLE;
        } else {
            return INVISIBLE;
        }
    }

    @Override
    public boolean isEnabled(Object location, String tag, Level level) {
        return InternalLogger.TAG.equals(tag) && level.isAtLeastAsSevereAs(Level.WARN);
    }

    @Override
    public void output(LogEntry entry, boolean last) {
        if (InternalLogger.TAG.equals(entry.getTag()) && entry.getSeverityLevel().isAtLeastAsSevereAs(Level.WARN)) {
            StringBuilder builder = new StringBuilder();

            builder.append("TINYLOG ");
            builder.append(entry.getSeverityLevel());
            builder.append(": ");

            String message = entry.getFormattedMessage(configuration);
            if (message != null) {
                builder.append(message);
            }

            Throwable throwable = entry.getThrowable();
            if (throwable != null) {
                if (message != null) {
                    builder.append(": ");
                }

                StringWriter writer = new StringWriter();
                throwable.printStackTrace(new PrintWriter(writer));
                builder.append(writer);
            } else {
                builder.append(System.lineSeparator());
            }

            System.err.print(builder);
        }
    }

    @Override
    public void close() {
        // Ignore
    }

}
