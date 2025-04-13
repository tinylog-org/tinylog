package org.tinylog.impl.path.segment;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import org.tinylog.impl.writer.file.FileWriter;

/**
 * Path segment interface for generating dynamic paths to the log file for {@link FileWriter}.
 */
public interface PathSegment {

    /**
     * Finds the latest existing path segment to append after the given parent directory and prefix.
     *
     * @param parentDirectory The directory in which to search
     * @param prefix The static prefix for sub folders or files
     * @return The latest existing path segment or {@code null} if none found
     * @throws Exception If failed to find the latest existing path segment
     */
    String findLatest(Path parentDirectory, String prefix) throws Exception;

    /**
     * Resolves this path segment by appending its path data to the passed string builder.
     *
     * <p>
     *     Date and time based path segments should use the passed date-time supplier instead of resolving the current
     *     date-time by themselves. This ensures that all path segments will use exactly the same date and time.
     * </p>
     *
     * @param pathBuilder This string builder is initialized with the already resolved path from previous path segments
     *                    and additional path data can simply be appended to it
     * @param currentDateSupplier The date-time of the current rollover event
     * @throws Exception If failed to resolve the path segment
     */
    void resolve(StringBuilder pathBuilder, Supplier<ZonedDateTime> currentDateSupplier) throws Exception;

}
