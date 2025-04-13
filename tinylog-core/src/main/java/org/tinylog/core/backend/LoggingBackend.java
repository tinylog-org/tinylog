package org.tinylog.core.backend;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.context.ContextStorage;

/**
 * Provider for issuing log entries.
 *
 * <p>
 *     This interface has to be implemented by all logging backends.
 * </p>
 */
public interface LoggingBackend {

    /**
     * Retrieves the thread-based context value storage.
     *
     * @return The storage for thread-based context values
     */
    ContextStorage getContextStorage();

    /**
     * Retrieves the visibility of all severity levels for a fully-qualified class name. Log entries whose severity
     * levels are set to {@link OutputDetails#DISABLED} do not need to be created since they are never output.
     *
     * <p>
     *     This method must always return the same value for the same arguments as logger classes may cache the results
     *     for performance reasons.
     * </p>
     *
     * <p>
     *     This method must be implemented in a thread-safe way as it can be called by multiple threads in parallel.
     * </p>
     *
     * @param className The fully-qualified class name for which the visibility of severity levels is requested
     * @return The visibilities of all severity levels
     */
    LevelVisibility getLevelVisibilityByClass(String className);

    /**
     * Retrieves the visibility of all severity levels for a category tag. Log entries whose severity levels are set to
     * {@link OutputDetails#DISABLED} do not need to be created since they are never output.
     *
     * <p>
     *     This method must always return the same value for the same arguments as logger classes may cache the results
     *     for performance reasons.
     * </p>
     *
     * <p>
     *     This method must be implemented in a thread-safe way as it can be called by multiple threads in parallel.
     * </p>
     *
     * @param tag The category tag for which the visibility of severity levels is requested
     * @return The visibilities of all severity levels
     */
    LevelVisibility getLevelVisibilityByTag(String tag);

    /**
     * Checks if a severity level is enabled for outputting log entries.
     *
     * <p>
     *     This method should be quick and avoid blocking the current thread for providing the best performance
     *     experience. If the enable state of a severity level cannot be determined efficiently, {@code true} should
     *     simply be returned.
     * </p>
     *
     * <p>
     *     This method must be implemented in a thread-safe way as it can be called by multiple threads in parallel.
     * </p>
     *
     * @param location The location information of the caller
     * @param tag The category tag
     * @param level The severity level to check
     * @return {@code true} if log entries of the passed severity level will be output, {@code false} if not
     */
    boolean isEnabled(Object location, String tag, Level level);

    /**
     * Outputs a log entry if the severity level is enabled.
     *
     * <p>
     *     This method does not need to be implemented thread-safe as tinylog guarantees that it will always be
     *     executed by the same execution thread.
     * </p>
     *
     * <p>
     *     This method must validate by itself if the severity level of the passed log entry is enabled.
     * </p>
     *
     * @param entry The log entry to output
     * @param last {@code true} if this is the last log entry to be currently processed, {@code false} if there
     *             are still other log entries to be processed
     */
    void output(LogEntry entry, boolean last);

    /**
     * Closes the backend by releasing all resources.
     *
     * <p>
     *     This method does not need to be implemented thread-safe as tinylog guarantees that it will always be
     *     executed by the same execution thread as {@link #output(LogEntry, boolean)}.
     * </p>
     */
    void close();

}
