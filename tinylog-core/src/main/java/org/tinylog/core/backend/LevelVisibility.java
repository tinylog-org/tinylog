package org.tinylog.core.backend;

import org.tinylog.core.Level;

/**
 * Output details for all severity levels.
 */
public final class LevelVisibility {

    private final OutputDetails trace;
    private final OutputDetails debug;
    private final OutputDetails info;
    private final OutputDetails warn;
    private final OutputDetails error;

    /**
     * @param details The required output details for all log entries
     */
    public LevelVisibility(OutputDetails details) {
        this(details, details, details, details, details);
    }

    /**
     * @param trace The required output details for trace log entries
     * @param debug The required output details for debug log entries
     * @param info The required output details for info log entries
     * @param warn The required output details for warn log entries
     * @param error The required output details for error log entries
     */
    public LevelVisibility(OutputDetails trace, OutputDetails debug, OutputDetails info, OutputDetails warn,
            OutputDetails error) {
        this.trace = trace;
        this.debug = debug;
        this.info = info;
        this.warn = warn;
        this.error = error;
    }

    /**
     * Gets the required output details for trace log entries.
     *
     * @return The required output details for trace log entries
     */
    public OutputDetails getTrace() {
        return trace;
    }

    /**
     * Gets the required output details for debug log entries.
     *
     * @return The required output details for debug log entries
     */
    public OutputDetails getDebug() {
        return debug;
    }

    /**
     * Gets the required output details for info log entries.
     *
     * @return The required output details for info log entries
     */
    public OutputDetails getInfo() {
        return info;
    }

    /**
     * Gets the required output details for warn log entries.
     *
     * @return The required output details for warn log entries
     */
    public OutputDetails getWarn() {
        return warn;
    }

    /**
     * Gets the required output details for error log entries.
     *
     * @return The required output details for error log entries
     */
    public OutputDetails getError() {
        return error;
    }

    /**
     * Gets the required output details for log entries with the passed severity level.
     *
     * @param level The severity level
     * @return The required output details for log entries with the passed severity level
     * @throws IllegalArgumentException If the passed severity level is invalid (e.g. {@link Level#OFF})
     */
    public OutputDetails get(Level level) {
        switch (level) {
            case ERROR:
                return getError();
            case WARN:
                return getWarn();
            case INFO:
                return getInfo();
            case DEBUG:
                return getDebug();
            case TRACE:
                return getTrace();
            default:
                throw new IllegalArgumentException("Illegal severity level: " + level);
        }
    }

}
