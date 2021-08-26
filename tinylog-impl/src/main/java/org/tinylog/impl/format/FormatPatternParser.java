package org.tinylog.impl.format;

import java.util.AbstractMap;
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
import org.tinylog.impl.format.placeholders.BundlePlaceholder;
import org.tinylog.impl.format.placeholders.Placeholder;
import org.tinylog.impl.format.placeholders.PlaceholderBuilder;
import org.tinylog.impl.format.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.format.style.StyleBuilder;

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
	private final Map<String, PlaceholderBuilder> placeholderBuilders;
	private final Map<String, StyleBuilder> styleBuilders;

	/**
	 * @param framework The actual logging framework instance
	 */
	public FormatPatternParser(Framework framework) {
		this.framework = framework;
		this.placeholderBuilders = new HashMap<>();
		this.styleBuilders = new HashMap<>();

		SafeServiceLoader
			.asList(framework, PlaceholderBuilder.class, "placeholder builder")
			.forEach(builder -> placeholderBuilders.put(builder.getName(), builder));

		SafeServiceLoader
			.asList(framework, StyleBuilder.class, "style builder")
			.forEach(builder -> styleBuilders.put(builder.getName(), builder));
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
		List<String> parts = splitPipes(originalPattern);
		String strippedPattern = parts.get(0);
		Map.Entry<String, String> configuration = parsePlaceholder(strippedPattern);
		PlaceholderBuilder builder = placeholderBuilders.get(configuration.getKey());

		if (builder != null) {
			try {
				Placeholder placeholder = builder.create(framework, configuration.getValue());
				List<String> styles = parts.subList(1, parts.size());
				return applyStyles(placeholder, styles);
			} catch (RuntimeException ex) {
				InternalLogger.error(ex, "Failed to create placeholder for \"{}\"", originalPattern);
			}
		}

		return new StaticTextPlaceholder(resolvedPattern);
	}

	/**
	 * Splits a format pattern by all unescaped pipes.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>"class | min-length:8 | max-length:8" -> ["class", "min-length:8", "max-length:8"]</code></pre>
	 * </p>
	 *
	 * @param pattern The format pattern to split
	 * @return All segments of the format pattern (list will never be empty)
	 */
	private List<String> splitPipes(String pattern) {
		List<String> parts = new ArrayList<>();
		int length = pattern.length();
		int start = 0;

		for (int index = 0; index < length; ++index) {
			char character = pattern.charAt(index);
			if (character == '\'') {
				index = Math.max(index, findClosingQuote(pattern, index + 1));
			} else if (character == '|') {
				parts.add(pattern.substring(start, index).trim());
				start = index + 1;
			}
		}

		if (parts.isEmpty() || start < length) {
			parts.add(pattern.substring(start).trim());
		}

		return parts;
	}

	/**
	 * Applies styles to a placeholder.
	 *
	 * @param placeholder The placeholder to style
	 * @param styles All styles to be apply (list can be empty)
	 * @return The styled placeholder
	 */
	private Placeholder applyStyles(Placeholder placeholder, List<String> styles) {
		Placeholder styledPlaceholder = placeholder;

		for (String style : styles) {
			styledPlaceholder = applyStyle(styledPlaceholder, style);
		}

		return styledPlaceholder;
	}

	/**
	 * Applies a style to a placeholder.
	 *
	 * @param placeholder The placeholder to style
	 * @param style The style to apply
	 * @return The styled placeholder
	 */
	private Placeholder applyStyle(Placeholder placeholder, String style) {
		Map.Entry<String, String> configuration = parsePlaceholder(style);
		StyleBuilder builder = styleBuilders.get(configuration.getKey());

		if (builder == null) {
			InternalLogger.error(null, "Invalid style \"{}\"", style);
		} else {
			try {
				return builder.create(framework, placeholder, configuration.getValue());
			} catch (RuntimeException ex) {
				InternalLogger.error(ex, "Failed to create style for \"{}\"", style);
			}
		}

		return placeholder;
	}

	/**
	 * Parses a placeholder by splitting the name and the configuration of the passed placeholder pattern.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>"context: foo" -> "context"="foo"</code></pre>
	 * </p>
	 *
	 * @param pattern The string representation of a placeholder
	 * @return The placeholder name as key (never null) and its configuration as value (can be {@code null})
	 */
	private Map.Entry<String, String> parsePlaceholder(String pattern) {
		int indexOfColon = pattern.indexOf(':');
		int indexOfQuote = pattern.indexOf('\'');

		if (indexOfColon >= 0 && (indexOfQuote < 0 || indexOfQuote > indexOfColon)) {
			return new AbstractMap.SimpleEntry<>(
				pattern.substring(0, indexOfColon).trim(),
				pattern.substring(indexOfColon + 1).trim()
			);
		} else {
			return new AbstractMap.SimpleEntry<>(pattern.trim(), null);
		}
	}

}
