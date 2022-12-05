package org.tinylog.core.backend;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import org.tinylog.core.Level;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Internal logging backend that prints internal tinylog errors and warnings to {@link System#err}.
 */
public class InternalLoggingBackend implements LoggingBackend {

    private static final ContextStorage STORAGE = new NopContextStorage();

    private static final LevelVisibility VISIBLE = new LevelVisibility(
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
        OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION
    );

    private static final LevelVisibility INVISIBLE = new LevelVisibility(
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.DISABLED
    );

    private final LoggingContext context;

    /**
     * @param context The current logging context
     */
    public InternalLoggingBackend(LoggingContext context) {
        this.context = context;
    }

    @Override
    public ContextStorage getContextStorage() {
        return STORAGE;
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
        return Objects.equals(tag, InternalLogger.TAG) && level.isAtLeastAsSevereAs(Level.WARN);
    }

    @Override
    public void log(Object location, String tag, Level level, Throwable throwable, Object message, Object[] arguments,
            MessageFormatter formatter) {
        if (Objects.equals(tag, InternalLogger.TAG) && level.isAtLeastAsSevereAs(Level.WARN)) {
            StringBuilder builder = new StringBuilder();

            builder.append("TINYLOG ");
            builder.append(level);
            builder.append(": ");

            if (message != null) {
                if (formatter == null || arguments == null) {
                    builder.append(message);
                } else {
                    builder.append(formatter.format(context, message.toString(), arguments));
                }
            }

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

}
