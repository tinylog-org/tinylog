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
	private final PathSegment pathSegment;

	/**
	 * @param framework The actual logging framework instance
	 * @param path The file path with placeholders
	 */
	public DynamicPath(Framework framework, String path) {
		this.clock = framework.getClock();
		this.pathSegment = new PathParser(framework).parse(path);
	}

	/**
	 * Generates a new file path by resolving all static and dynamic path segments.
	 *
	 * @return New static path
	 * @throws Exception Failed to generate the file path
	 */
	public Path generateNewPath() throws Exception {
		StringBuilder builder = new StringBuilder();
		pathSegment.resolve(builder, ZonedDateTime.ofInstant(clock.instant(), clock.getZone()));

		Path path = Paths.get(builder.toString()).toAbsolutePath();
		Path parent = path.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}

		return path;
	}

}
