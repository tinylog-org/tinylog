package org.tinylog.impl.path.segments;

import java.time.ZonedDateTime;

import org.tinylog.impl.writers.file.FileWriter;

/**
 * Path segment interface for generating dynamic paths to the log file for {@link FileWriter}.
 */
public interface PathSegment {

	/**
	 * Resolves this path segment by appending its path data to the passed string builder.
	 *
	 * <p>
	 *     Date and time based path segments should used the passed date-time instead of resolving the current
	 *     date-time by themselves. This ensures that all path segments will use exactly the same date-time.
	 * </p>
	 *
	 * @param pathBuilder This string builder contains the already resolved path and the path data of this path segment
	 *                    should be appended
	 * @param date The date-time of the current rollover event
	 * @throws Exception Failed to resolve the path segment
	 */
	void resolve(StringBuilder pathBuilder, ZonedDateTime date) throws Exception;

}
