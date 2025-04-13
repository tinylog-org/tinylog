package org.tinylog.impl.backend;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tinylog.core.Level;
import org.tinylog.impl.writer.Writer;

/**
 * Configured writers and severity levels for tinylog's native logging backend.
 *
 * <p>
 *     All stored writers are mapped to their active severity levels and tags.
 * </p>
 */
class BackendConfiguration {

    private final Map<String, LevelConfiguration> severityLevels;
    private final Map<String, Map<Level, Collection<Writer>>> taggedWriters;
    private final Map<Level, Collection<Writer>> untaggedWriters;
    private final Collection<Writer> allWriters;

    /**
     * @param severityLevels The configured severity levels for packages and classes ({@code ""} for the global root
     *                       severity level)
     * @param writers All writers mapped to the active tags and severity levels
     */
    BackendConfiguration(
        Map<String, LevelConfiguration> severityLevels,
        Map<String, Map<Level, Collection<Writer>>> writers
    ) {
        this.severityLevels = getEffectiveLevels(severityLevels, writers);
        this.taggedWriters = new HashMap<>(writers);
        this.untaggedWriters = writers.values().stream()
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(
                Entry::getKey,
                Entry::getValue,
                (existing, supplement) -> Stream.concat(existing.stream(), supplement.stream())
                    .collect(Collectors.toSet())
            ));
        this.allWriters = writers.values().stream()
            .flatMap(map -> map.values().stream())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    /**
     * Gets all configured severity levels.
     *
     * @return The configured severity levels for packages and classes ({@code ""} for the global root severity level)
     */
    Map<String, LevelConfiguration> getSeverityLevels() {
        return severityLevels;
    }

    /**
     * Gets all writers.
     *
     * @return All writers in any order
     */
    Collection<Writer> getAllWriters() {
        return allWriters;
    }

    /**
     * Gets all active writers for log entries with the passed severity level.
     *
     * @param level The severity level
     * @return The active writers in any order
     */
    Collection<Writer> getWriters(Level level) {
        return untaggedWriters.get(level);
    }

    /**
     * Gets all active writers for log entries with the passed tag and severity level.
     *
     * @param tag The category tag
     * @param level The severity level
     * @return The active writers in any order
     */
    Collection<Writer> getWriters(String tag, Level level) {
        return taggedWriters.computeIfAbsent(
            tag,
            ignore -> taggedWriters.get(LevelConfiguration.TAGGED_PLACEHOLDER)
        ).get(level);
    }

    /**
     * Gets the effective severity levels for all passed packages and classes.
     *
     * <p>
     *     the effective severity level is the least severe level with at least one active writer.
     * </p>
     *
     * @param severityLevels The configured severity levels for packages and classes ({@code ""} for the global root
     *                       severity level)
     * @param writers A map providing all active writers for each tag and severity level
     * @return The effective severity levels for all passed packages and classes
     */
    private static Map<String, LevelConfiguration> getEffectiveLevels(
        Map<String, LevelConfiguration> severityLevels,
        Map<String, Map<Level, Collection<Writer>>> writers
    ) {
        Map<String, Level> effectiveLevels = writers.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> getEffectiveLevel(entry.getValue())
        ));

        Map<String, LevelConfiguration> adjustedConfigurations = new HashMap<>();

        for (Map.Entry<String, LevelConfiguration> configurationEntry : severityLevels.entrySet()) {
            Set<String> tags = new HashSet<>();
            tags.addAll(configurationEntry.getValue().getTags());
            tags.addAll(effectiveLevels.keySet());

            Map<String, Level> adjustedLevels = tags.stream().collect(Collectors.toMap(
                tag -> tag,
                tag -> Level.mostSevereLevel(configurationEntry.getValue().getLevel(tag), effectiveLevels.get(tag))
            ));

            adjustedConfigurations.put(configurationEntry.getKey(), new LevelConfiguration(adjustedLevels));
        }

        return adjustedConfigurations;
    }

    /**
     * Gets the effective severity level for a writer map.
     *
     * <p>
     *     the effective severity level is the least severe level with at least one active writer.
     * </p>
     *
     * @param writers A map providing all active writers for each severity level
     * @return The effective severity level
     */
    private static Level getEffectiveLevel(Map<Level, Collection<Writer>> writers) {
        return Stream.of(Level.values())
            .filter(level -> level != Level.OFF)
            .filter(level -> !writers.get(level).isEmpty())
            .reduce(Level::leastSevereLevel)
            .orElse(Level.OFF);
    }

}
