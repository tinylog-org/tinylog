package org.tinylog.impl.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.AbstractPatternParser;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.SafeServiceLoader;
import org.tinylog.impl.format.placeholder.BundlePlaceholder;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.PlaceholderBuilder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;

/**
 * Parser for format patterns with placeholders and plain static text. Placeholders and sub format patterns can be
 * put in curly brackets.
 *
 * <p>
 *     All registered {@link PlaceholderBuilder PlaceholderBuilders} are loaded when creating a new format pattern
 *     instance automatically.
 * </p>
 *
 * <p>
 *     Curly brackets and other characters can be escaped by wrapping them in single quotes ('). Two directly
 *     consecutive single quotes ('') are output as one single quote.
 * </p>
 */
public class FormatPatternParser extends AbstractPatternParser {

	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\r\n|\n|\r");

	private final Framework framework;
	private final Map<String, PlaceholderBuilder> builders;

	/**
	 * @param framework The actual logging framework instance
	 */
	public FormatPatternParser(Framework framework) {
		this.framework = framework;
		this.builders = new HashMap<>();

		SafeServiceLoader
			.asList(framework, PlaceholderBuilder.class, "placeholder builder")
			.forEach(builder -> builders.put(builder.getName(), builder));
	}

	/**
	 * Parses a format pattern.
	 *
	 * @param pattern The format pattern to parse
	 * @return Renderable placeholder
	 */
	public Placeholder parse(String pattern) {
		String normalizedPattern = NEW_LINE_PATTERN.matcher(pattern).replaceAll(System.lineSeparator());
		return rawParse(normalizedPattern);
	}

	/**
	 * Parses a format pattern without normalizing new lines.
	 *
	 * @param pattern The format pattern to parse
	 * @return Renderable placeholder
	 */
	private Placeholder rawParse(String pattern) {
		List<Placeholder> placeholders = new ArrayList<>();

		BiConsumer<StringBuilder, String> groupConsumer = (builder, group) -> {
			if (builder.length() > 0) {
				placeholders.add(new StaticTextPlaceholder(builder.toString()));
				builder.setLength(0);
			}

			placeholders.add(parse(group));
		};

		StringBuilder builder = parse(pattern, groupConsumer);

		if (placeholders.isEmpty()) {
			placeholders.add(createPlaceholder(pattern, builder.toString()));
		} else if (builder.length() > 0) {
			placeholders.add(new StaticTextPlaceholder(builder.toString()));
		}

		if (placeholders.size() == 1) {
			return placeholders.get(0);
		} else {
			return new BundlePlaceholder(placeholders);
		}
	}

	/**
	 * Creates a placeholder from a (sub) format pattern.
	 *
	 * @param originalPattern The original format pattern (sub format patterns and escaped quotes are unresolved)
	 * @param resolvedPattern The resolved format pattern (sub format patterns and escaped quotes are resolved)
	 * @return Renderable placeholder
	 */
	private Placeholder createPlaceholder(String originalPattern, String resolvedPattern) {
		String name;
		String value;

		int indexOfColon = originalPattern.indexOf(':');
		int indexOfQuote = originalPattern.indexOf('\'');

		if (indexOfColon >= 0 && (indexOfQuote < 0 || indexOfQuote > indexOfColon)) {
			name = originalPattern.substring(0, indexOfColon);
			value = originalPattern.substring(indexOfColon + 1).trim();
		} else {
			name = originalPattern;
			value = null;
		}

		PlaceholderBuilder builder = builders.get(name);
		if (builder != null) {
			try {
				return builder.create(framework, value);
			} catch (RuntimeException ex) {
				InternalLogger.error(ex, "Failed to create placeholder for \"{}\"", originalPattern);
			}
		}

		return new StaticTextPlaceholder(resolvedPattern);
	}

}
