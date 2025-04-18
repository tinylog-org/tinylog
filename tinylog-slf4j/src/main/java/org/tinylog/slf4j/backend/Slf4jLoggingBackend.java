package org.tinylog.slf4j.backend;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.slf4j.spi.NOPLoggingEventBuilder;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.internal.InternalLogger;

/**
 * Logging backend adapter for forwarding log entries to SLF4J.
 */
public class Slf4jLoggingBackend implements LoggingBackend {

    private final Slf4jContextStorage contextStorage;
    private final LevelVisibility levelVisibility;
    private final IMarkerFactory markerFactory;
    private final ILoggerFactory loggerFactory;
    private final Configuration configuration;
    private final InternalLogger internalLogger;

    /**
     * @param context The tinylog context to use for the backend
     */
    public Slf4jLoggingBackend(TinylogContext context) {
        this.contextStorage = new Slf4jContextStorage();
        this.levelVisibility = new LevelVisibility(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        this.markerFactory = MarkerFactory.getIMarkerFactory();
        this.loggerFactory = LoggerFactory.getILoggerFactory();
        this.configuration = context.getConfiguration();
        this.internalLogger = context.getLogger();
    }

    @Override
    public ContextStorage getContextStorage() {
        return contextStorage;
    }

    @Override
    public LevelVisibility getLevelVisibilityByClass(String className) {
        return levelVisibility;
    }

    @Override
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        return levelVisibility;
    }

    @Override
    public boolean isEnabled(Object location, String tag, Level level) {
        Logger logger = getLogger(location);
        Marker marker = getMarker(tag);

        switch (level) {
            case TRACE:
                return logger.isTraceEnabled(marker);
            case DEBUG:
                return logger.isDebugEnabled(marker);
            case INFO:
                return logger.isInfoEnabled(marker);
            case WARN:
                return logger.isWarnEnabled(marker);
            case ERROR:
                return logger.isErrorEnabled(marker);
            default:
                internalLogger.log(Level.ERROR, "Illegal severity level \"{}\"", level);
                return false;
        }
    }

    @Override
    public void output(LogEntry entry, boolean last) {
        LoggingEventBuilder builder = getLoggingEventBuilder(entry.getClassName(), entry.getSeverityLevel());

        Marker marker = getMarker(entry.getTag());
        if (marker != null) {
            builder = builder.addMarker(marker);
        }

        builder.setCause(entry.getThrowable()).setMessage(() -> entry.getFormattedMessage(configuration)).log();
    }

    @Override
    public void close() {
        // Nothing to do
    }

    /**
     * Gets the SLF4J marker for a given tag.
     *
     * @param tag The tinylog tag (can be {@code null})
     * @return The matching SLF4J marker if the tag is not {@code null}, otherwise {@code null}
     */
    private Marker getMarker(String tag) {
        return tag == null ? null : markerFactory.getMarker(tag);
    }

    /**
     * Gets the SLF4J logger for the class name of a given location object.
     *
     * @param location The location information
     * @return The assigned SLF4J logger
     */
    private Logger getLogger(Object location) {
        if (location instanceof StackTraceElement) {
            return getLogger(((StackTraceElement) location).getClassName());
        } else if (location instanceof Class<?>) {
            return getLogger(((Class<?>) location).getName());
        } else if (location instanceof String) {
            return getLogger((String) location);
        } else {
            internalLogger.log(Level.ERROR, "Illegal location information \"{}\"", location);
            return getLogger(Logger.ROOT_LOGGER_NAME);
        }
    }

    /**
     * Gets the SLF4J logger for a given name.
     *
     * @param name The name of the SLF4J logger
     * @return The SLF4J logger for the passed name
     */
    private Logger getLogger(String name) {
        if (name == null) {
            name = Logger.ROOT_LOGGER_NAME;
        }

        return loggerFactory.getLogger(name);
    }

    /**
     * Creates a logging event builder for a given logger name and severity level.
     *
     * @param name The name of the SLF4J logger
     * @param level The severity level of the log entry
     * @return A pre-initialized logging event builder
     */
    private LoggingEventBuilder getLoggingEventBuilder(String name, Level level) {
        Logger logger = getLogger(name);

        switch (level) {
            case TRACE:
                return logger.atTrace();
            case DEBUG:
                return logger.atDebug();
            case INFO:
                return logger.atInfo();
            case WARN:
                return logger.atWarn();
            case ERROR:
                return logger.atError();
            default:
                internalLogger.log(Level.ERROR, "Illegal severity level \"{}\"", level);
                return NOPLoggingEventBuilder.singleton();
        }
    }

}
