package org.tinylog.impl.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.AbstractPatternParser;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.SafeServiceLoader;
import org.tinylog.impl.path.segments.BundleSegment;
import org.tinylog.impl.path.segments.PathSegment;
import org.tinylog.impl.path.segments.PathSegmentBuilder;
import org.tinylog.impl.path.segments.StaticPathSegment;

/**
 * Parser for log file paths with placeholders and plain static text. Dynamic placeholders can be put in curly brackets.
 *
 * <p>
 *     All registered {@link PathSegmentBuilder} implementations are loaded automatically when parsing a new log file
 *     path.
 * </p>
 *
 * <p>
 *     Curly brackets and other characters can be escaped by wrapping them in single quotes ('). Two directly
 *     consecutive single quotes ('') are output as one single quote. However, it is recommended to avoid using
 *     any kind of brackets in file names as they make trouble on the most operating systems.
 * </p>
 */
public class PathParser extends AbstractPatternParser {

	private final Framework framework;
	private final Map<String, PathSegmentBuilder> builders;

	/**
	 * @param framework The actual logging framework instance
	 */
	public PathParser(Framework framework) {
		this.framework = framework;
		this.builders = new HashMap<>();

		SafeServiceLoader
			.asList(framework, PathSegmentBuilder.class, "path segment builders")
			.forEach(builder -> builders.put(builder.getName(), builder));
	}

	/**
	 * Parses the dynamic path to the log file.
	 *
	 * @param path The dynamic path with placeholders
	 * @return Resolvable path
	 */
	public PathSegment parse(String path) {
		List<PathSegment> segments = new ArrayList<>();

		BiConsumer<StringBuilder, String> groupConsumer = (builder, group) -> {
			if (builder.length() > 0) {
				segments.add(new StaticPathSegment(builder.toString()));
				builder.setLength(0);
			}

			PathSegment segment = createSegment(group);
			if (segment == null) {
				segments.add(new StaticPathSegment("undefined"));
			} else {
				segments.add(segment);
			}
		};

		StringBuilder builder = parse(path, groupConsumer);

		if (builder.length() > 0) {
			segments.add(new StaticPathSegment(builder.toString()));
		}

		if (segments.size() == 1) {
			return segments.get(0);
		} else {
			return new BundleSegment(segments);
		}
	}

	/**
	 * Creates a path segment from a string placeholder.
	 *
	 * @param placeholder The placeholder with the name and optionally configuration value
	 * @return The corresponding path segment or {@code null}
	 */
	private PathSegment createSegment(String placeholder) {
		int index = placeholder.indexOf(':');
		String name = index >= 0 ? placeholder.substring(0, index).trim() : placeholder.trim();
		String value = index >= 0 ? placeholder.substring(index + 1).trim() : null;

		PathSegmentBuilder builder = builders.get(name);
		if (builder == null) {
			InternalLogger.error(null, "Invalid path segment \"{}\"", placeholder);
			return null;
		} else {
			try {
				return builder.create(framework, value);
			} catch (Exception ex) {
				InternalLogger.error(ex, "Failed to create path segment for \"{}\"", placeholder);
				return null;
			}
		}
	}

}
