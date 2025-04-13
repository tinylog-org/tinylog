package org.tinylog.impl.backend;

import java.util.Comparator;
import java.util.Map;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.writer.Writer;

/**
 * Native configurable tinylog logging backend implementation.
 */
public class TinylogLoggingBackend implements LoggingBackend {

    private final ContextStorage contextStorage;
    private final BackendConfiguration configuration;
    private final InternalLogger logger;

    /**
     * @param context The tinylog context to use for the backend
     */
    public TinylogLoggingBackend(TinylogContext context) {
        this.contextStorage = new TinylogContextStorage();
        this.configuration = new BackendConfigurationParser(context).parse();
        this.logger = context.getLogger();
    }

    @Override
    public ContextStorage getContextStorage() {
        return contextStorage;
    }

    @Override
    public LevelVisibility getLevelVisibilityByClass(String className) {
        LevelConfiguration levelConfiguration = getLevelConfiguration(className);
        Level effectiveLevel = levelConfiguration.getLeastSevereLevel();

        return new LevelVisibility(
            getOutputDetails(Level.TRACE, effectiveLevel),
            getOutputDetails(Level.DEBUG, effectiveLevel),
            getOutputDetails(Level.INFO, effectiveLevel),
            getOutputDetails(Level.WARN, effectiveLevel),
            getOutputDetails(Level.ERROR, effectiveLevel)
        );
    }

    @Override
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        Level effectiveLevel = getEffectiveSeverityLevel(normalizeTag(tag));

        return new LevelVisibility(
            getOutputDetails(Level.TRACE, effectiveLevel),
            getOutputDetails(Level.DEBUG, effectiveLevel),
            getOutputDetails(Level.INFO, effectiveLevel),
            getOutputDetails(Level.WARN, effectiveLevel),
            getOutputDetails(Level.ERROR, effectiveLevel)
        );
    }

    @Override
    public boolean isEnabled(Object location, String tag, Level level) {
        if (location instanceof StackTraceElement) {
            return isEnabled(((StackTraceElement) location).getClassName(), normalizeTag(tag), level);
        } else if (location instanceof Class<?>) {
            return isEnabled(((Class<?>) location).getName(), normalizeTag(tag), level);
        } else if (location instanceof String) {
            return isEnabled((String) location, normalizeTag(tag), level);
        } else {
            logger.log(Level.ERROR, "Location information must not be null");
            return isEnabled(null, normalizeTag(tag), level);
        }
    }

    @Override
    public void output(LogEntry entry, boolean last) {
        String className = entry.getClassName();
        String tag = normalizeTag(entry.getTag());
        Level level = entry.getSeverityLevel();

        if (!isEnabled(className, tag, level)) {
            return;
        }

        for (Writer writer : configuration.getWriters(tag, level)) {
            try {
                writer.log(entry);
            } catch (Exception ex) {
                if (!InternalLogger.TAG.equals(tag)) {
                    logger.log(Level.ERROR, ex, "Failed to write log entry");
                }
            }

            if (last) {
                try {
                    writer.flush();
                } catch (Exception ex) {
                    logger.log(Level.ERROR, ex, "Failed to flush writer {}", writer.getClass().getName());
                }
            }
        }
    }

    @Override
    public void close() {
        for (Writer writer : configuration.getAllWriters()) {
            try {
                writer.close();
            } catch (Exception ex) {
                logger.log(Level.ERROR, ex, "Failed to close writer {}", writer.getClass().getName());
            }
        }
    }

    /**
     * Checks if a severity level is enabled for outputting log entries.
     *
     * @param className The class name of the caller
     * @param tag The category tag
     * @param level The severity level to check
     * @return {@code true} if log entries of the passed severity level will be output, {@code false} if not
     */
    private boolean isEnabled(String className, String tag, Level level) {
        LevelConfiguration levelConfiguration = getLevelConfiguration(className);
        Level enabledLevel = levelConfiguration.getLevel(tag);
        return level.isAtLeastAsSevereAs(enabledLevel);
    }

    /**
     * Gets the level configuration for a class.
     *
     * <p>
     *     If there is level configuration for the passed class, it will be returned. Otherwise the level configuration
     *     for the most concrete package or the global root will be returned.
     * </p>
     *
     * @param className The class name
     * @return The found level configuration
     */
    private LevelConfiguration getLevelConfiguration(String className) {
        String key = className == null ? "" : className;
        Map<String, LevelConfiguration> severityLevels = configuration.getSeverityLevels();

        while (true) {
            LevelConfiguration levelConfiguration = severityLevels.get(key);

            if (levelConfiguration != null) {
                return levelConfiguration;
            }

            int index = key.lastIndexOf('.');
            key = index >= 0 ? key.substring(0, index) : "";
        }
    }

    /**
     * Gets the least severe enabled level for a category tag.
     *
     * @param tag The category tag
     * @return The least severe enabled level
     */
    private Level getEffectiveSeverityLevel(String tag) {
        return configuration.getSeverityLevels().values().stream()
            .map(levelConfiguration -> levelConfiguration.getLevel(tag))
            .min(Comparator.naturalOrder())
            .orElse(Level.OFF);
    }

    /**
     * Gets the output details for a severity level.
     *
     * @param severityLevel The severity level of the output details
     * @param enabledLevel The least severe enabled level
     * @return The output details of the passed severity level
     */
    private OutputDetails getOutputDetails(Level severityLevel, Level enabledLevel) {
        if (!severityLevel.isAtLeastAsSevereAs(enabledLevel)) {
            return OutputDetails.DISABLED;
        }

        return configuration.getWriters(severityLevel).stream()
            .map(Writer::getOutputDetails)
            .max(Comparator.naturalOrder())
            .orElse(OutputDetails.DISABLED);
    }

    /**
     * Converts nullable tags into non-null tags.
     *
     * @param tag Nullable tag
     * @return Non-null tags
     */
    private static String normalizeTag(String tag) {
        return tag == null || tag.isEmpty() ? "-" : tag;
    }

}
