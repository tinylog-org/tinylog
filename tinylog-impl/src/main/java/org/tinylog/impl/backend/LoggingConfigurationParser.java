package org.tinylog.impl.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.impl.writers.Writer;

/**
 * Parser for creating a {@link LoggingConfiguration} from the configuration of a provided {@link Framework}.
 */
class LoggingConfigurationParser {

    private static final String WRITER_PREFIX = "writer";

    private final LoggingContext context;

    /**
     * @param context The current logging context
     */
    LoggingConfigurationParser(LoggingContext context) {
        this.context = context;
    }

    /**
     * Parses the configuration of the stored {@link Framework} and creates a {@link LoggingConfiguration} based on it.
     *
     * @return The parsed logging configuration
     */
    public LoggingConfiguration parse() {
        Set<String> tags = new TreeSet<>(Arrays.asList(
            LevelConfiguration.UNTAGGED_PLACEHOLDER,
            LevelConfiguration.TAGGED_PLACEHOLDER
        ));

        Map<String, LevelConfiguration> levelConfigurations = getLevelConfigurations(tags);
        Collection<WriterConfiguration> writerConfigurations = getWriterConfigurations(tags);

        Map<String, Map<Level, WriterRepository>> writerRepositories = new HashMap<>();
        for (String tag : tags) {
            Map<Level, WriterRepository> taggedWriterRepositories = getWriterRepositories(
                tag,
                levelConfigurations,
                writerConfigurations
            );
            writerRepositories.put(tag, taggedWriterRepositories);
        }

        return new LoggingConfiguration(levelConfigurations, writerRepositories);
    }

    /**
     * Provides the severity level configurations for all packages and classes while adding all found tags, which are
     * found in these severity level configurations, to the passed set.
     *
     * @param tags All found tags will be added to this set
     * @return All found severity level configurations
     */
    private Map<String, LevelConfiguration> getLevelConfigurations(Set<String> tags) {
        Map<String, LevelConfiguration> levels = new HashMap<>();

        Configuration configuration = context.getConfiguration();
        List<String> globalLevels = configuration.getList(LevelConfiguration.KEY);
        LevelConfiguration globalLevelConfiguration = new LevelConfiguration(globalLevels, true);
        tags.addAll(globalLevelConfiguration.getTags());
        levels.put("", globalLevelConfiguration);

        Configuration subConfiguration = configuration.getSubConfiguration(
            LevelConfiguration.KEY,
            LevelConfiguration.SEPARATOR
        );

        for (String key : subConfiguration.getKeys()) {
            List<String> customLevels = subConfiguration.getList(key);
            LevelConfiguration customLevelConfiguration = new LevelConfiguration(customLevels, true);
            tags.addAll(customLevelConfiguration.getTags());
            levels.put(key, customLevelConfiguration);
        }

        return levels;
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
        Configuration configuration = context.getConfiguration();

        for (String key : configuration.getRootKeys()) {
            if (key.startsWith(WRITER_PREFIX)) {
                Configuration subConfiguration = configuration.getSubConfiguration(key);
                WriterConfiguration writerConfiguration = new WriterConfiguration(context, subConfiguration);
                writerConfigurations.add(writerConfiguration);
                tags.addAll(writerConfiguration.getLevelConfiguration().getTags());
            }
        }

        if (writerConfigurations.isEmpty()) {
            Configuration subConfiguration = new Configuration(Collections.singletonMap(
                WriterConfiguration.TYPE_KEY,
                context.getFramework().getRuntime().getDefaultWriter()
            ));
            writerConfigurations.add(new WriterConfiguration(context, subConfiguration));
        }

        return writerConfigurations;
    }

    /**
     * Maps all writers from the passed writer configurations to their enabled severity levels.
     *
     * @param tag The tag for which writers should map to their enabled severity levels
     * @param levelConfigurations All severity level configurations
     * @param writerConfigurations All relevant writer configuration
     * @return A map with the active writers for each severity level
     */
    private Map<Level, WriterRepository> getWriterRepositories(
        String tag,
        Map<String, LevelConfiguration> levelConfigurations,
        Collection<WriterConfiguration> writerConfigurations
    ) {
        Map<Level, WriterRepository> writerRepositories = new EnumMap<>(Level.class);
        Level effectiveLevel = getEffectiveLevel(tag, levelConfigurations, writerConfigurations);

        for (Level level : Level.values()) {
            if (level != Level.OFF) {
                List<Writer> writers = new ArrayList<>();

                if (level.isAtLeastAsSevereAs(effectiveLevel)) {
                    for (WriterConfiguration writerConfiguration : writerConfigurations) {
                        Level configuredLevel = writerConfiguration.getLevelConfiguration().getLevel(tag);
                        if (configuredLevel.ordinal() >= level.ordinal()) {
                            Writer writer = writerConfiguration.getOrCreateWriter();
                            if (writer != null) {
                                writers.add(writer);
                            }
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
     * @param tag The tag for which the effective severity level should be returned
     * @param levelConfigurations All severity level configurations
     * @param writerConfigurations All relevant writer configuration
     * @return The effective severity level from which log entries can be really output
     */
    private Level getEffectiveLevel(
        String tag,
        Map<String, LevelConfiguration> levelConfigurations,
        Collection<WriterConfiguration> writerConfigurations
    ) {
        Level globalLevel = levelConfigurations.values().stream()
            .map(configuration -> configuration.getLevel(tag))
            .reduce(Level::leastSevereLevel)
            .orElse(Level.TRACE);

        Level writerLevel = writerConfigurations.stream()
            .map(WriterConfiguration::getLevelConfiguration)
            .map(configuration -> configuration.getLevel(tag))
            .reduce(Level::leastSevereLevel)
            .orElse(Level.OFF);

        return Level.mostSevereLevel(globalLevel, writerLevel);
    }

}
