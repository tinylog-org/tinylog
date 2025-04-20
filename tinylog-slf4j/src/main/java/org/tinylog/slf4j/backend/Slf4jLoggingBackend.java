package org.tinylog.slf4j.backend;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;
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

    private static final int SEVERITY_LEVEL_FACTOR = 10;

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
        return isEnabled(logger, marker, level);
    }

    @Override
    public void output(LogEntry entry, boolean last) {
        Logger logger = getLogger(entry.getClassName());
        Marker marker = getMarker(entry.getTag());
        Level level = entry.getSeverityLevel();

        if (!isEnabled(logger, marker, level)) {
            return;
        }

        Throwable throwable = entry.getThrowable();
        String message = entry.getFormattedMessage(configuration);

        if (message == null && throwable != null) {
            message = throwable.getMessage();
        }

        if (logger instanceof LocationAwareLogger) {
            LocationAwareLogger locationAwareLogger = (LocationAwareLogger) logger;
            locationAwareLogger.log(
                marker,
                Slf4jLoggingBackend.class.getName(),
                (Level.TRACE.ordinal() - level.ordinal()) * SEVERITY_LEVEL_FACTOR,
                message,
                null,
                throwable
            );
        } else {
            switch (level) {
                case TRACE:
                    logger.trace(marker, message, throwable);
                    return;
                case DEBUG:
                    logger.debug(marker, message, throwable);
                    return;
                case INFO:
                    logger.info(marker, message, throwable);
                    return;
                case WARN:
                    logger.warn(marker, message, throwable);
                    return;
                case ERROR:
                    logger.error(marker, message, throwable);
                    return;
                default:
                    internalLogger.log(Level.ERROR, "Illegal severity level \"{}\"", level);
            }
        }
    }

    @Override
    public void close() {
        // Nothing to do
    }

    /**
     * Checks if a given severity level is enabled for the given logger and marker.
     *
     * @param logger The SLF4J logger to check if the given severity level is enabled
     * @param marker The SLF4J marker to check for being enabled
     * @param level The severity level to check if it is enabled on the passed logger
     * @return {@code true} if log entries of the passed marker and severity level will be output, {@code false} if not
     */
    private boolean isEnabled(Logger logger, Marker marker, Level level) {
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

}
