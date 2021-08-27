package org.tinylog.impl.backend;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.tinylog.core.Level;
import org.tinylog.impl.writers.Writer;

/**
 * Storage for writers that maps all stored writes to the active severity levels and tags.
 */
class WriterMatrix {

	private final Map<Level, WriterRepository> untaggedWriters;
	private final Map<Level, WriterRepository> defaultTaggedWriters;
	private final Map<String, Map<Level, WriterRepository>> customTaggedWriters;

	private final Collection<Writer> allWriters;

	/**
	 * @param untaggedWriters All writers that are active for untagged log entries
	 * @param defaultTaggedWriters All writers that are active for tagged log entries with unknown tags
	 * @param customTaggedWriters All writers that are active for tagged log entries with specific tags
	 */
	WriterMatrix(
		Map<Level, WriterRepository> untaggedWriters,
		Map<Level, WriterRepository> defaultTaggedWriters,
		Map<String, Map<Level, WriterRepository>> customTaggedWriters
	) {
		this.untaggedWriters = untaggedWriters;
		this.defaultTaggedWriters = defaultTaggedWriters;
		this.customTaggedWriters = customTaggedWriters;
		this.allWriters = new HashSet<>();

		untaggedWriters.values().stream()
			.map(WriterRepository::getAllWriters)
			.forEach(allWriters::addAll);

		defaultTaggedWriters.values().stream()
			.map(WriterRepository::getAllWriters)
			.forEach(allWriters::addAll);

		customTaggedWriters.values().stream()
			.flatMap(map -> map.values().stream())
			.map(WriterRepository::getAllWriters)
			.forEach(allWriters::addAll);
	}

	/**
	 * Gets all active writers for untagged log entries with the passed severity level.
	 *
	 * @param level The severity level
	 * @return The active writers
	 */
	public WriterRepository getUntaggedWriters(Level level) {
		return untaggedWriters.get(level);
	}

	/**
	 * Gets all active writers for log entries with the passed tag and severity level.
	 *
	 * @param tag The tag
	 * @param level The severity level
	 * @return The active writers
	 */
	public WriterRepository getTaggedWriters(String tag, Level level) {
		return customTaggedWriters.getOrDefault(tag, defaultTaggedWriters).get(level);
	}

	/**
	 * Gets all stored writers.
	 *
	 * @return All stored writers in any order
	 */
	public Collection<Writer> getAllWriters() {
		return allWriters;
	}

}
