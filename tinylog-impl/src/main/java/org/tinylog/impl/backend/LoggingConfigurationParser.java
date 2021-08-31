package org.tinylog.impl.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.impl.writers.Writer;

/**
 * Parser for creating a {@link LoggingConfiguration} from the configuration of a provided {@link Framework}.
 */
class LoggingConfigurationParser {

	private static final String WRITER_PREFIX = "writer";

	private final Framework framework;

	/**
	 * @param framework The actual logging framework instance
	 */
	LoggingConfigurationParser(Framework framework) {
		this.framework = framework;
	}

	/**
	 * Parses the configuration of the stored {@link Framework} and creates a {@link LoggingConfiguration} based on it.
	 *
	 * @return The parsed logging configuration
	 */
	public LoggingConfiguration parse() {
		Set<String> tags = new TreeSet<>();
		LevelConfiguration globalLevelConfiguration = getGlobalLevelConfiguration(tags);
		Collection<WriterConfiguration> writerConfigurations = getWriterConfigurations(tags);

		Map<Level, WriterRepository> untaggedWriters = getWriterRepositories(
			globalLevelConfiguration,
			writerConfigurations,
			LevelConfiguration::getUntaggedLevel
		);

		Map<Level, WriterRepository> defaultTaggedWriters = getWriterRepositories(
			globalLevelConfiguration,
			writerConfigurations,
			LevelConfiguration::getDefaultTaggedLevel
		);

		Map<String, Map<Level, WriterRepository>> customTaggedWriters = new HashMap<>();
		for (String tag : tags) {
			Map<Level, WriterRepository> customTaggedWriter = getWriterRepositories(
				globalLevelConfiguration,
				writerConfigurations,
				levelConfiguration -> levelConfiguration.getTaggedLevel(tag)
			);
			customTaggedWriters.put(tag, customTaggedWriter);
		}

		return new LoggingConfiguration(untaggedWriters, defaultTaggedWriters, customTaggedWriters);
	}

	/**
	 * Provides the global severity level configuration while adding all found tags, which are found in the global
	 * severity configuration, to the passed set.
	 *
	 * @param tags All found tags will be added to this set
	 * @return The parsed global severity level configuration
	 */
	private LevelConfiguration getGlobalLevelConfiguration(Set<String> tags) {
		List<String> globalLevels = framework.getConfiguration().getList(LevelConfiguration.KEY);
		LevelConfiguration globalLevelConfiguration = new LevelConfiguration(globalLevels, true);
		tags.addAll(globalLevelConfiguration.getTags());
		return globalLevelConfiguration;
	}

	/**
	 * Provides all writer configurations while adding all found tags, which are found in these writer configurations,
	 * to the passed set.
	 *
	 * @param tags All found tags will be added to this set
	 * @return All found and parsed writer configurations
	 */
	private Collection<WriterConfiguration> getWriterConfigurations(Set<String> tags) {
		List<WriterConfiguration> writerConfigurations = new ArrayList<>();
		Configuration configuration = framework.getConfiguration();

		for (String key : configuration.getRootKeys()) {
			if (key.startsWith(WRITER_PREFIX)) {
				Configuration subConfiguration = configuration.getSubConfiguration(key);
				WriterConfiguration writerConfiguration = new WriterConfiguration(framework, subConfiguration);
				writerConfigurations.add(writerConfiguration);
				tags.addAll(writerConfiguration.getLevelConfiguration().getTags());
			}
		}

		return writerConfigurations;
	}

	/**
	 * Maps all writers from the passed writer configurations to their enabled severity levels.
	 *
	 * @param globalLevelConfiguration The global severity level configuration
	 * @param writerConfigurations All relevant writer configuration
	 * @param levelExtractor The function to get the relevant severity level from a writer configuration
	 * @return A map with the active writers for each severity level
	 */
	private Map<Level, WriterRepository> getWriterRepositories(
		LevelConfiguration globalLevelConfiguration,
		Collection<WriterConfiguration> writerConfigurations,
		Function<LevelConfiguration, Level> levelExtractor
	) {
		Map<Level, WriterRepository> writerRepositories = new EnumMap<>(Level.class);
		Level maxLevel = getEffectiveLevel(globalLevelConfiguration, writerConfigurations, levelExtractor);

		for (Level level : Level.values()) {
			if (level != Level.OFF) {
				List<Writer> writers = new ArrayList<>();

				if (level.ordinal() <= maxLevel.ordinal()) {
					for (WriterConfiguration writerConfiguration : writerConfigurations) {
						Level configuredLevel = levelExtractor.apply(writerConfiguration.getLevelConfiguration());
						if (configuredLevel.ordinal() >= level.ordinal()) {
							writers.add(writerConfiguration.getOrCreateWriter());
						}
					}
				}

				writerRepositories.put(level, new WriterRepository(writers));
			}
		}

		return writerRepositories;
	}

	/**
	 * Gets the effective severity level from which log entries can be really output.
	 *
	 * @param globalLevelConfiguration The global severity level configuration
	 * @param writerConfigurations All relevant writer configuration
	 * @param levelExtractor The function to get the relevant severity level from a writer configuration
	 * @return The effective severity level from which log entries can be really output
	 */
	private Level getEffectiveLevel(
		LevelConfiguration globalLevelConfiguration,
		Collection<WriterConfiguration> writerConfigurations,
		Function<LevelConfiguration, Level> levelExtractor
	) {
		Level maxGlobalLevel = levelExtractor.apply(globalLevelConfiguration);
		Level maxWriterLevel = writerConfigurations.stream()
			.map(WriterConfiguration::getLevelConfiguration)
			.map(levelExtractor)
			.max(Comparator.naturalOrder())
			.orElse(Level.OFF);

		return maxGlobalLevel.ordinal() < maxWriterLevel.ordinal() ? maxGlobalLevel : maxWriterLevel;
	}

}
