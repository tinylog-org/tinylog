package org.tinylog.impl.backend;

import java.util.Locale;

import org.tinylog.core.Configuration;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.internal.SafeServiceLoader;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.WriterBuilder;

/**
 * Parser and creator for configured writers.
 */
class WriterConfiguration {

    /**
     * Property name for writer name.
     */
    static final String TYPE_KEY = "type";

    private final LoggingContext context;
    private final Configuration entireConfiguration;
    private final LevelConfiguration levelConfiguration;

    private Writer writer;
    private boolean created;

    /**
     * @param context The current logging context
     * @param configuration The writer configuration to parse
     */
    WriterConfiguration(LoggingContext context, Configuration configuration) {
        this.context = context;
        this.entireConfiguration = configuration;
        this.levelConfiguration = new LevelConfiguration(
            configuration.getList(LevelConfiguration.KEY),
            false
        );

        this.writer = null;
        this.created = false;
    }

    /**
     * Gets the level configuration of the parsed writer.
     *
     * @return The level configuration with activated severity levels and tags
     */
    public LevelConfiguration getLevelConfiguration() {
        return levelConfiguration;
    }

    /**
     * Get or create the writer.
     *
     * <p>
     *     The writer will be created only once to ensure that always the same writer instance is returned.
     * </p>
     *
     * @return The created writer or {@code null} if the creation failed
     */
    public Writer getOrCreateWriter() {
        if (!created) {
            created = true;

            String type = entireConfiguration.getValue(TYPE_KEY);
            if (type == null) {
                InternalLogger.error(
                    null,
                    "Missing writer name in property \"{}\"",
                    entireConfiguration.resolveFullKey(TYPE_KEY)
                );
            } else {
                String name = type.toLowerCase(Locale.ENGLISH);
                WriterBuilder builder = SafeServiceLoader
                    .asList(context.getFramework().getClassLoader(), WriterBuilder.class, "writer builders")
                    .stream()
                    .filter(writerBuilder -> name.equals(writerBuilder.getName()))
                    .findAny()
                    .orElse(null);

                if (builder == null) {
                    InternalLogger.error(
                        null,
                        "Could not find any writer builder with the name \"{}\" in the classpath",
                        name
                    );
                } else {
                    try {
                        writer = builder.create(context, entireConfiguration);
                    } catch (Exception ex) {
                        InternalLogger.error(ex, "Failed to create writer for \"{}\"", name);
                    }
                }
            }
        }

        return writer;
    }

}
