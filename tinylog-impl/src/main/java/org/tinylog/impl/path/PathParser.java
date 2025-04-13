package org.tinylog.impl.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.internal.AbstractPatternParser;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.path.segment.PathSegment;
import org.tinylog.impl.path.segment.PathSegmentBuilder;
import org.tinylog.impl.path.segment.StaticPathSegment;

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

    private final TinylogContext context;
    private final InternalLogger logger;
    private final Map<String, PathSegmentBuilder> builders;

    /**
     * @param context The tinylog context to use for creating path segment builders
     */
    public PathParser(TinylogContext context) {
        this.context = context;
        this.logger = context.getLogger();
        this.builders = PathSegmentBuilder.load(context.getLoader());
    }

    /**
     * Parses the dynamic path to the log file.
     *
     * @param path The dynamic path with placeholders
     * @return Resolvable path segments
     */
    public List<PathSegment> parse(String path) {
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

        return segments;
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
            logger.log(Level.ERROR, "Invalid path segment \"{}\"", placeholder);
            return null;
        } else {
            try {
                return builder.create(context, value);
            } catch (Exception ex) {
                logger.log(Level.ERROR, ex, "Failed to create path segment for \"{}\"", placeholder);
                return null;
            }
        }
    }

}
