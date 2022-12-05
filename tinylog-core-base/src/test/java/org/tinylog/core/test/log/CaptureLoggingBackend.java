package org.tinylog.core.test.log;

import java.util.function.Supplier;

import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.internal.LoggingContext;

/**
 * Logging backend for storing all issued log entries in a passed {@link Log}.
 */
class CaptureLoggingBackend implements LoggingBackend {

    private final LoggingContext context;
    private final Log log;
    private final Level visibleLevel;

    /**
     * @param context The current logging context
     * @param log All issued log entries will be stored in this {@link Log}
     * @param visibleLevel The least severe visible severity level for {@link #getLevelVisibilityByClass(String)} and
     *                     {@link #getLevelVisibilityByTag(String)}
     */
    CaptureLoggingBackend(LoggingContext context, Log log, Level visibleLevel) {
        this.context = context;
        this.log = log;
        this.visibleLevel = visibleLevel;
    }

    @Override
    public ContextStorage getContextStorage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LevelVisibility getLevelVisibilityByClass(String className) {
        return getLevelVisibility();
    }

    @Override
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        return getLevelVisibility();
    }

    @Override
    public boolean isEnabled(Object location, String tag, Level level) {
        return level.isAtLeastAsSevereAs(log.getLevel());
    }

    @Override
    public void log(Object location, String tag, Level level, Throwable throwable, Object message, Object[] arguments,
            MessageFormatter formatter) {
        String caller;
        if (location instanceof Class) {
            caller = ((Class<?>) location).getName();
        } else if (location instanceof String) {
            caller = (String) location;
        } else if (location instanceof StackTraceElement) {
            caller = ((StackTraceElement) location).getClassName();
        } else {
            caller = null;
        }

        if (message instanceof Supplier<?>) {
            message = ((Supplier<?>) message).get();
        }

        String output;
        if (message == null) {
            output = null;
        } else if (arguments == null) {
            output = String.valueOf(message);
        } else {
            output = formatter.format(context, message.toString(), arguments);
        }

        log.add(new LogEntry(caller, tag, level, throwable, output));
    }

    /**
     * Retrieves the visibility of all severity levels.
     *
     * @return The visibilities of all severity levels
     */
    private LevelVisibility getLevelVisibility() {
        return new LevelVisibility(
            Level.TRACE.isAtLeastAsSevereAs(visibleLevel)
                ? OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME : OutputDetails.DISABLED,
            Level.DEBUG.isAtLeastAsSevereAs(visibleLevel)
                ? OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME : OutputDetails.DISABLED,
            Level.INFO.isAtLeastAsSevereAs(visibleLevel)
                ? OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME : OutputDetails.DISABLED,
            Level.WARN.isAtLeastAsSevereAs(visibleLevel)
                ? OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME : OutputDetails.DISABLED,
            Level.ERROR.isAtLeastAsSevereAs(visibleLevel)
                ? OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME : OutputDetails.DISABLED
        );
    }

}
