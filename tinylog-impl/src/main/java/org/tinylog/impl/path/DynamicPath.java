package org.tinylog.impl.path;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.ZonedDateTime;

import org.tinylog.core.Framework;
import org.tinylog.impl.path.segments.PathSegment;

/**
 * Dynamic file path with static and dynamic path segments.
 */
public class DynamicPath {

	private final Clock clock;
	private final PathSegment[] pathSegments;

	/**
	 * @param framework The actual logging framework instance
	 * @param path The file path with placeholders
	 */
	public DynamicPath(Framework framework, String path) {
		this.clock = framework.getClock();
		this.pathSegments = new PathParser(framework).parse(path).toArray(new PathSegment[0]);
	}

	/**
	 * Gets the latest existing path by resolving all static and dynamic path segments.
	 *
	 * @return The latest existing path or {@code null} if none exist
	 * @throws Exception Failed to resolve the latest path
	 */
	public Path getLatestPath() throws Exception {
		Path parentDirectory = Paths.get("");
		String prefix = "";

		for (int i = 0; i < pathSegments.length; ++i) {
			String result = pathSegments[i].findLatest(parentDirectory, prefix);
			if (result == null) {
				return null;
			}

			Path path = parentDirectory.resolve(prefix + result);
			if (i == pathSegments.length - 1) {
				return Files.isRegularFile(path) ? path : null;
			}

			String normalizedResult = path.normalize().toString();
			String separator = parentDirectory.getFileSystem().getSeparator();
			int separatorIndex = normalizedResult.lastIndexOf(separator);
			if (separatorIndex >= 0) {
				parentDirectory = Paths.get(normalizedResult.substring(0, separatorIndex));
				prefix = normalizedResult.substring(separatorIndex + 1);
			} else {
				parentDirectory = Paths.get(normalizedResult);
				prefix = "";
			}
		}

		return null;
	}

	/**
	 * Generates a new file path by resolving all static and dynamic path segments.
	 *
	 * @return New static path
	 * @throws Exception Failed to generate the file path
	 */
	public Path generateNewPath() throws Exception {
		ZonedDateTime date = ZonedDateTime.ofInstant(clock.instant(), clock.getZone());
		StringBuilder builder = new StringBuilder();

		for (PathSegment segment : pathSegments) {
			segment.resolve(builder, date);
		}

		Path path = Paths.get(builder.toString()).toAbsolutePath();
		Path parent = path.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}

		return path;
	}

}
