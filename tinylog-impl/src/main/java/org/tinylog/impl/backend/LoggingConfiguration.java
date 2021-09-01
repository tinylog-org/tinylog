package org.tinylog.impl.backend;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tinylog.core.Level;
import org.tinylog.impl.writers.Writer;

/**
 * Storage for configured writers and severity levels.
 *
 * <p>
 *     All stored writers are mapped to their active severity levels and tags.
 * </p>
 */
class LoggingConfiguration {

	private final Map<String, LevelConfiguration> severityLevels;
	private final Map<String, Map<Level, WriterRepository>> mappedWriters;
	private final Collection<Writer> unmappedWriters;
	private final Function<String, Map<Level, WriterRepository>> defaultTaggedWritersGetter;

	/**
	 * @param severityLevels The configured severity levels for packages and classes ({@code ""} for the global root
	 *                       severity level)
	 * @param writers All writers mapped to the active tags and severity levels
	 */
	LoggingConfiguration(
		Map<String, LevelConfiguration> severityLevels,
		Map<String, Map<Level, WriterRepository>> writers
	) {
		this.severityLevels = getEffectiveLevels(severityLevels, writers);
		this.mappedWriters = writers;
		this.unmappedWriters = writers.values().stream()
			.flatMap(map -> map.values().stream())
			.flatMap(repository -> repository.getAllWriters().stream())
			.collect(Collectors.toSet());
		this.defaultTaggedWritersGetter = tag -> mappedWriters.get(LevelConfiguration.TAGGED_PLACEHOLDER);
	}

	/**
	 * Gets all configured severity levels.
	 *
	 * @return The configured severity levels for packages and classes ({@code ""} for the global root severity level)
	 */
	public Map<String, LevelConfiguration> getSeverityLevels() {
		return severityLevels;
	}

	/**
	 * Gets all active writers for log entries with the passed tag and severity level.
	 *
	 * @param tag The tag
	 * @param level The severity level
	 * @return The active writers
	 */
	public WriterRepository getWriters(String tag, Level level) {
		return mappedWriters.computeIfAbsent(tag, defaultTaggedWritersGetter).get(level);
	}

	/**
	 * Gets all stored writers.
	 *
	 * @return All stored writers in any order
	 */
	public Collection<Writer> getAllWriters() {
		return unmappedWriters;
	}

	/**
	 * Gets the effective severity levels for all passed packages and classes.
	 *
	 * <p>
	 *     the effective severity level is the least severe level for which a writer repository provides at least
	 *     one active writer.
	 * </p>
	 *
	 * @param severityLevels The configured severity levels for packages and classes ({@code ""} for the global root
	 *                       severity level)
	 * @param writers A map providing all active writers for each tag and severity level
	 * @return The effective severity levels for all passed packages and classes
	 */
	private static Map<String, LevelConfiguration> getEffectiveLevels(
		Map<String, LevelConfiguration> severityLevels,
		Map<String, Map<Level, WriterRepository>> writers
	) {
		Map<String, Level> effectiveLevels = writers.entrySet().stream()
			.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), getEffectiveLevel(entry.getValue())))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		Map<String, LevelConfiguration> adjustedConfigurations = new HashMap<>();

		for (Map.Entry<String, LevelConfiguration> configurationEntry : severityLevels.entrySet()) {
			Map<String, Level> adjustedLevels = configurationEntry.getValue().stream()
				.map(entry -> new AbstractMap.SimpleEntry<>(
					entry.getKey(),
					Level.mostSevereLevel(entry.getValue(), effectiveLevels.get(entry.getKey()))
				))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			adjustedConfigurations.put(configurationEntry.getKey(), new LevelConfiguration(adjustedLevels));
		}

		return adjustedConfigurations;
	}

	/**
	 * Gets the effective severity level for a writer repository map.
	 *
	 * <p>
	 *     The effective severity level is the least severe level for which a writer repository provides at least
	 *     one active writer.
	 * </p>
	 *
	 * @param writers A map providing all active writers for each severity level
	 * @return The effective severity level
	 */
	private static Level getEffectiveLevel(Map<Level, WriterRepository> writers) {
		return Stream.of(Level.values())
			.filter(level -> level != Level.OFF)
			.filter(level -> !writers.get(level).getAllWriters().isEmpty())
			.reduce(Level::leastSevereLevel)
			.orElse(Level.OFF);
	}

}
