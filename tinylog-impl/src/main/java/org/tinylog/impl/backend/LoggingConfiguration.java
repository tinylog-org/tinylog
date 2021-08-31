package org.tinylog.impl.backend;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.tinylog.core.Level;
import org.tinylog.impl.writers.Writer;

/**
 * Storage for writers that maps all stored writes to the active severity levels and tags.
 */
class LoggingConfiguration {

	private final Map<String, Map<Level, WriterRepository>> mappedWriters;
	private final Collection<Writer> unmappedWriters;
	private final Function<String, Map<Level, WriterRepository>> defaultTaggedWritersGetter;

	/**
	 * @param writers All writers mapped to the active tags and severity levels
	 */
	LoggingConfiguration(Map<String, Map<Level, WriterRepository>> writers) {
		this.mappedWriters = writers;
		this.unmappedWriters = mappedWriters.values().stream()
			.flatMap(map -> map.values().stream())
			.flatMap(repository -> repository.getAllWriters().stream())
			.collect(Collectors.toSet());
		this.defaultTaggedWritersGetter = tag -> mappedWriters.get(LevelConfiguration.TAGGED_PLACEHOLDER);
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

}
