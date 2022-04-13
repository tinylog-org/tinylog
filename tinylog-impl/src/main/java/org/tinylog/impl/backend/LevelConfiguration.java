package org.tinylog.impl.backend;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;

/**
 * Parser and storage for configured levels and tags.
 */
class LevelConfiguration {

	/**
	 * The property name for the global severity level configuration.
	 */
	static final String KEY = "level";

	/**
	 * The separator character to use between {@link #KEY} and packages or classes for property names for custom
	 * severity levels.
	 */
	static final char SEPARATOR = '@';

	/**
	 * The placeholder for all log entries (tagged and untagged).
	 */
	static final String ANY_PLACEHOLDER = "*";

	/**
	 * The placeholder for untagged log entries.
	 */
	static final String UNTAGGED_PLACEHOLDER = "-";

	/**
	 * The placeholder for all tagged log entries.
	 */
	static final String TAGGED_PLACEHOLDER = "+";

	private static final Predicate<String> placeholderFilter = key ->
		ANY_PLACEHOLDER.equals(key) || UNTAGGED_PLACEHOLDER.equals(key) || TAGGED_PLACEHOLDER.equals(key);

	private final Map<String, Level> levels;

	/**
	 * @param elements Configured severity levels (can be tagged or untagged)
	 * @param addInternalTagImplicitly Flag for activating the severity level {@link Level#WARN WARN} implicitly for the
	 *                                 {@link InternalLogger#TAG tinylog} tag if not defined in the passed elements
	 */
	LevelConfiguration(List<String> elements, boolean addInternalTagImplicitly) {
		this(parseLevels(elements, addInternalTagImplicitly));
	}

	/**
	 * @param levels The mapping of tags or placeholders and the assigned severity levels
	 */
	LevelConfiguration(Map<String, Level> levels) {
		this.levels = new HashMap<>(levels);
	}

	/**
	 * Gets all custom tags.
	 *
	 * @return All tags with custom severity levels
	 */
	public Collection<String> getTags() {
		return levels.keySet().stream().filter(placeholderFilter.negate()).collect(Collectors.toList());
	}

	/**
	 * Gets the severity level for a specific tag.
	 *
	 * @param tag The tag for which the configured severity level is requested (minus "{@code -}" can be used for
	 *            getting the configured severity level for untagged log entries)
	 * @return The severity level for the passed tag
	 */
	public Level getLevel(String tag) {
		if (UNTAGGED_PLACEHOLDER.equals(tag) || TAGGED_PLACEHOLDER.equals(tag)) {
			return levels.getOrDefault(tag, Level.OFF);
		} else {
			return levels.computeIfAbsent(tag, ignore -> getLevel(LevelConfiguration.TAGGED_PLACEHOLDER));
		}
	}

	/**
	 * Gets a stream with all internally stored entries with the tag or placeholder as key and the assigned severity
	 * level as value.
	 *
	 * @return The stream with all internally stored entries
	 */
	public Stream<Map.Entry<String, Level>> stream() {
		return levels.entrySet().stream();
	}

	/**
	 * Parses configured severity levels.
	 *
	 * @param elements Configured severity levels (can be tagged or untagged)
	 * @param addInternalTagImplicitly Flag for activating the severity level {@link Level#WARN WARN} implicitly for the
	 *                                 {@link InternalLogger#TAG tinylog} tag if not defined in the passed elements
	 * @return The mapping of tags or placeholders and the assigned severity levels
	 */
	private static Map<String, Level> parseLevels(List<String> elements, boolean addInternalTagImplicitly) {
		Map<String, Level> levels = new HashMap<>();

		for (String element : elements) {
			int splitIndex = element.indexOf('@');
			String levelName = splitIndex >= 0 ? element.substring(0, splitIndex).trim() : element;
			String tag = splitIndex >= 0 ? element.substring(splitIndex + 1).trim() : ANY_PLACEHOLDER;

			Level level;
			try {
				level = Level.valueOf(levelName.toUpperCase(Locale.ENGLISH));
			} catch (IllegalArgumentException ex) {
				InternalLogger.error(ex, "Invalid severity level \"{}\"", levelName);
				continue;
			}

			if (ANY_PLACEHOLDER.equals(tag)) {
				levels.put(UNTAGGED_PLACEHOLDER, level);
				levels.put(TAGGED_PLACEHOLDER, level);
			} else {
				levels.put(tag, level);
			}
		}

		if (levels.isEmpty()) {
			levels.put(UNTAGGED_PLACEHOLDER, Level.TRACE);
			levels.put(TAGGED_PLACEHOLDER, Level.TRACE);
		}

		if (addInternalTagImplicitly
			&& !levels.containsKey(InternalLogger.TAG)
			&& !levels.getOrDefault(TAGGED_PLACEHOLDER, Level.TRACE).isAtLeastAsSevereAs(Level.WARN)) {
			levels.put(InternalLogger.TAG, Level.WARN);
		}

		return levels;
	}

}
