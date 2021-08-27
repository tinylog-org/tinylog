package org.tinylog.impl.backend;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;

/**
 * Parser and storage for configured levels and tags.
 */
class LevelConfiguration {

	/**
	 * The property name for severity level configurations.
	 */
	static final String KEY = "level";

	private static final String ANY_PLACEHOLDER = "*";
	private static final String UNTAGGED_PLACEHOLDER = "-";
	private static final String TAGGED_PLACEHOLDER = "+";

	private static final Predicate<String> placeholderFilter = key ->
		ANY_PLACEHOLDER.equals(key) || UNTAGGED_PLACEHOLDER.equals(key) || TAGGED_PLACEHOLDER.equals(key);

	private final Map<String, Level> levels;

	/**
	 * @param elements Configured severity levels (can be tagged or untagged)
	 * @param addInternalTagImplicitly Flag for activating the severity level {@link Level#WARN WARN} implicitly for the
	 *                                 {@link InternalLogger#TAG tinylog} tag if not defined in the passed elements
	 */
	LevelConfiguration(List<String> elements, boolean addInternalTagImplicitly) {
		levels = new HashMap<>();

		for (String element : elements) {
			int splitIndex = element.indexOf('@');
			String tag = splitIndex >= 0 ? element.substring(0, splitIndex).trim() : ANY_PLACEHOLDER;
			String levelName = splitIndex >= 0 ? element.substring(splitIndex + 1).trim() : element;

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
			&& !getTags().contains(InternalLogger.TAG)
			&& levels.getOrDefault(TAGGED_PLACEHOLDER, Level.TRACE).ordinal() > Level.WARN.ordinal()) {
			levels.put(InternalLogger.TAG, Level.WARN);
		}
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
	 * Gets the configured severity level for untagged log entries.
	 *
	 * @return The severity level for untagged log entries
	 */
	public Level getUntaggedLevel() {
		return levels.getOrDefault(UNTAGGED_PLACEHOLDER, Level.OFF);
	}

	/**
	 * Gets the configured default severity level for tags without custom severity level.
	 *
	 * @return The default severity level for tagged log entries
	 */
	public Level getDefaultTaggedLevel() {
		return levels.getOrDefault(TAGGED_PLACEHOLDER, Level.OFF);
	}

	/**
	 * Gets the severity level for a specific tag.
	 *
	 * @param tag The tag for which the configured severity level is requested
	 * @return The severity level for the passed tag
	 */
	public Level getTaggedLevel(String tag) {
		return levels.getOrDefault(tag, getDefaultTaggedLevel());
	}

}
