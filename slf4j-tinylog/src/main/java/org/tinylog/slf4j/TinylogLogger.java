package org.tinylog.slf4j;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Marker;
import org.slf4j.event.LoggingEvent;
import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.spi.LoggingEventAware;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.format.message.SimpleMessageFormatter;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * SLF4J logger implementation for tinylog.
 */
public class TinylogLogger implements LocationAwareLogger, LoggingEventAware {

    private static final String LOGGER_CLASS_NAME = TinylogLogger.class.getName();

    private final String category;
    private final RuntimeFlavor runtime;
    private final LoggingBackend backend;
    private final MessageFormatter formatter;

    private final LevelVisibility visibility;
    private final OutputDetails visibilityTrace;
    private final OutputDetails visibilityDebug;
    private final OutputDetails visibilityInfo;
    private final OutputDetails visibilityWarn;
    private final OutputDetails visibilityError;

    /**
     * @param category The logger category (usually the fully-qualified name of the class that uses this logger)
     * @param framework The actual tinylog framework instance
     */
    TinylogLogger(String category, Framework framework) {
        this.category = category;
        this.runtime = framework.getRuntime();
        this.backend = framework.getLoggingBackend();
        this.formatter = new SimpleMessageFormatter();

        this.visibility = backend.getLevelVisibilityByClass(category);
        this.visibilityTrace = visibility.getTrace();
        this.visibilityDebug = visibility.getDebug();
        this.visibilityInfo = visibility.getInfo();
        this.visibilityWarn = visibility.getWarn();
        this.visibilityError = visibility.getError();
    }

    @Override
    public String getName() {
        return category;
    }

    @Override
    public boolean isTraceEnabled() {
        return visibilityTrace != OutputDetails.DISABLED && isEnabled(null, Level.TRACE);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return visibilityTrace != OutputDetails.DISABLED && isEnabled(marker, Level.TRACE);
    }

    @Override
    public void trace(String message) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, null, Level.TRACE, null, message, (Object[]) null);
        }
    }

    @Override
    public void trace(String message, Throwable throwable) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, null, Level.TRACE, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void trace(String message, Object argument) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, null, Level.TRACE, null, message, argument);
        }
    }

    @Override
    public void trace(String message, Object arg1, Object arg2) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, null, Level.TRACE, null, message, arg1, arg2);
        }
    }

    @Override
    public void trace(String message, Object... arguments) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, null, Level.TRACE, null, message, arguments);
        }
    }

    @Override
    public void trace(Marker marker, String message) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, marker, Level.TRACE, null, message, (Object[]) null);
        }
    }

    @Override
    public void trace(Marker marker, String message, Throwable throwable) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, marker, Level.TRACE, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void trace(Marker marker, String message, Object argument) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, marker, Level.TRACE, null, message, argument);
        }
    }

    @Override
    public void trace(Marker marker, String message, Object arg1, Object arg2) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, marker, Level.TRACE, null, message, arg1, arg2);
        }
    }

    @Override
    public void trace(Marker marker, String message, Object... arguments) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityTrace).get();
            issueLogEntry(location, marker, Level.TRACE, null, message, arguments);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return visibilityDebug != OutputDetails.DISABLED && isEnabled(null, Level.DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return visibilityDebug != OutputDetails.DISABLED && isEnabled(marker, Level.DEBUG);
    }

    @Override
    public void debug(String message) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, null, Level.DEBUG, null, message, (Object[]) null);
        }
    }

    @Override
    public void debug(String message, Throwable throwable) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, null, Level.DEBUG, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void debug(String message, Object argument) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, null, Level.DEBUG, null, message, argument);
        }
    }

    @Override
    public void debug(String message, Object arg1, Object arg2) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, null, Level.DEBUG, null, message, arg1, arg2);
        }
    }

    @Override
    public void debug(String message, Object... arguments) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, null, Level.DEBUG, null, message, arguments);
        }
    }

    @Override
    public void debug(Marker marker, String message) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, marker, Level.DEBUG, null, message, (Object[]) null);
        }
    }

    @Override
    public void debug(Marker marker, String message, Throwable throwable) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, marker, Level.DEBUG, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void debug(Marker marker, String message, Object argument) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, marker, Level.DEBUG, null, message, argument);
        }
    }

    @Override
    public void debug(Marker marker, String message, Object arg1, Object arg2) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, marker, Level.DEBUG, null, message, arg1, arg2);
        }
    }

    @Override
    public void debug(Marker marker, String message, Object... arguments) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityDebug).get();
            issueLogEntry(location, marker, Level.DEBUG, null, message, arguments);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return visibilityInfo != OutputDetails.DISABLED && isEnabled(null, Level.INFO);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return visibilityInfo != OutputDetails.DISABLED && isEnabled(marker, Level.INFO);
    }

    @Override
    public void info(String message) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, null, Level.INFO, null, message, (Object[]) null);
        }
    }

    @Override
    public void info(String message, Throwable throwable) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, null, Level.INFO, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void info(String message, Object argument) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, null, Level.INFO, null, message, argument);
        }
    }

    @Override
    public void info(String message, Object arg1, Object arg2) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, null, Level.INFO, null, message, arg1, arg2);
        }
    }

    @Override
    public void info(String message, Object... arguments) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, null, Level.INFO, null, message, arguments);
        }
    }

    @Override
    public void info(Marker marker, String message) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, marker, Level.INFO, null, message, (Object[]) null);
        }
    }

    @Override
    public void info(Marker marker, String message, Throwable throwable) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, marker, Level.INFO, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void info(Marker marker, String message, Object argument) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, marker, Level.INFO, null, message, argument);
        }
    }

    @Override
    public void info(Marker marker, String message, Object arg1, Object arg2) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, marker, Level.INFO, null, message, arg1, arg2);
        }
    }

    @Override
    public void info(Marker marker, String message, Object... arguments) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityInfo).get();
            issueLogEntry(location, marker, Level.INFO, null, message, arguments);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return visibilityWarn != OutputDetails.DISABLED && isEnabled(null, Level.WARN);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return visibilityWarn != OutputDetails.DISABLED && isEnabled(marker, Level.WARN);
    }

    @Override
    public void warn(String message) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, null, Level.WARN, null, message, (Object[]) null);
        }
    }

    @Override
    public void warn(String message, Throwable throwable) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, null, Level.WARN, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void warn(String message, Object argument) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, null, Level.WARN, null, message, argument);
        }
    }

    @Override
    public void warn(String message, Object arg1, Object arg2) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, null, Level.WARN, null, message, arg1, arg2);
        }
    }

    @Override
    public void warn(String message, Object... arguments) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, null, Level.WARN, null, message, arguments);
        }
    }

    @Override
    public void warn(Marker marker, String message) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, marker, Level.WARN, null, message, (Object[]) null);
        }
    }

    @Override
    public void warn(Marker marker, String message, Throwable throwable) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, marker, Level.WARN, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void warn(Marker marker, String message, Object argument) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, marker, Level.WARN, null, message, argument);
        }
    }

    @Override
    public void warn(Marker marker, String message, Object arg1, Object arg2) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, marker, Level.WARN, null, message, arg1, arg2);
        }
    }

    @Override
    public void warn(Marker marker, String message, Object... arguments) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityWarn).get();
            issueLogEntry(location, marker, Level.WARN, null, message, arguments);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return visibilityError != OutputDetails.DISABLED && isEnabled(null, Level.ERROR);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return visibilityError != OutputDetails.DISABLED && isEnabled(marker, Level.ERROR);
    }

    @Override
    public void error(String message) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, null, Level.ERROR, null, message, (Object[]) null);
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, null, Level.ERROR, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void error(String message, Object argument) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, null, Level.ERROR, null, message, argument);
        }
    }

    @Override
    public void error(String message, Object arg1, Object arg2) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, null, Level.ERROR, null, message, arg1, arg2);
        }
    }

    @Override
    public void error(String message, Object... arguments) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, null, Level.ERROR, null, message, arguments);
        }
    }

    @Override
    public void error(Marker marker, String message) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, marker, Level.ERROR, null, message, (Object[]) null);
        }
    }

    @Override
    public void error(Marker marker, String message, Throwable throwable) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, marker, Level.ERROR, throwable, message, (Object[]) null);
        }
    }

    @Override
    public void error(Marker marker, String message, Object argument) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, marker, Level.ERROR, null, message, argument);
        }
    }

    @Override
    public void error(Marker marker, String message, Object arg1, Object arg2) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, marker, Level.ERROR, null, message, arg1, arg2);
        }
    }

    @Override
    public void error(Marker marker, String message, Object... arguments) {
        if (visibilityError != OutputDetails.DISABLED) {
            Object location = getDirectCaller(visibilityError).get();
            issueLogEntry(location, marker, Level.ERROR, null, message, arguments);
        }
    }

    @Override
    public void log(Marker marker, String className, int levelCode, String message, Object[] arguments,
            Throwable throwable) {
        Level level = getLevel(levelCode);
        OutputDetails outputDetails = visibility.get(level);
        if (outputDetails != OutputDetails.DISABLED) {
            Object location = getRelativeCaller(outputDetails).apply(LOGGER_CLASS_NAME);
            issueLogEntry(location, marker, level, throwable, message, arguments);
        }
    }

    @Override
    public void log(LoggingEvent event) {
        Level level = getLevel(event.getLevel().toInt());
        OutputDetails outputDetails = visibility.get(level);
        if (outputDetails != OutputDetails.DISABLED) {
            Object location = getRelativeCaller(outputDetails).apply(event.getCallerBoundary());
            List<Marker> markers = event.getMarkers();
            Marker marker = markers == null || markers.isEmpty() ? null : markers.get(0);
            issueLogEntry(location, marker, level, event.getThrowable(), event.getMessage(), event.getArgumentArray());
        }
    }

    /**
     * Converts an SLF4J level code into a tinylog severity level.
     *
     * @param levelCode The SLF4J level code to convert
     * @return The corresponding tinylog severity level
     * @throws IllegalArgumentException Passed an invalid SLF4J level code
     */
    private Level getLevel(int levelCode) {
        switch (levelCode) {
            case TRACE_INT:
                return Level.TRACE;
            case DEBUG_INT:
                return Level.DEBUG;
            case INFO_INT:
                return Level.INFO;
            case WARN_INT:
                return Level.WARN;
            case ERROR_INT:
                return Level.ERROR;
            default:
                throw new IllegalArgumentException("Invalid level code " + levelCode);
        }
    }

    /**
     * Gets a location information supplier of the direct caller.
     *
     * @param outputDetails The required output details
     * @return A supplier for receiving the location information of the direct caller
     */
    private Supplier<?> getDirectCaller(OutputDetails outputDetails) {
        if (outputDetails == OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION) {
            return runtime.getDirectCaller(outputDetails);
        } else {
            return () -> category;
        }
    }

    /**
     * Gets a location information supplier of a relative caller.
     *
     * @param outputDetails The required output details
     * @return A supplier for receiving the location information of the caller of a passed class
     */
    private Function<String, ?> getRelativeCaller(OutputDetails outputDetails) {
        if (outputDetails == OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION) {
            return runtime.getRelativeCaller(outputDetails);
        } else {
            return (String loggerClassName) -> category;
        }
    }

    /**
     * Checks whether a severity level is enabled for a given marker.
     *
     * @param marker The marker to check (can be {@code null})
     * @param level The severity level to check
     * @return {@code true} if logging is enabled, {@code false} if logging is disabled
     */
    private boolean isEnabled(Marker marker, Level level) {
        String tag = marker == null ? null : marker.getName();
        return backend.isEnabled(category, tag, level);
    }

    /**
     * Issues a log entry.
     *
     * @param location The location information of the caller
     * @param marker The optional marker for getting the tag
     * @param level The severity level
     * @param throwable The optional throwable to log
     * @param message The message to log
     * @param arguments The optional arguments for resolving potential placeholders in the passed message
     */
    private void issueLogEntry(Object location, Marker marker, Level level, Throwable throwable, String message,
            Object... arguments) {
        String tag = marker == null ? null : marker.getName();
        if (throwable == null) {
            Object lastArgument = arguments == null || arguments.length == 0 ? null : arguments[arguments.length - 1];
            throwable = lastArgument instanceof Throwable ? (Throwable) lastArgument : null;
        }
        backend.log(location, tag, level, throwable, message, arguments, arguments == null ? null : formatter);
    }

}
