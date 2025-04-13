package org.tinylog.impl.backend;

import java.util.Locale;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.writer.Writer;
import org.tinylog.impl.writer.WriterBuilder;

/**
 * Parser and creator for configured writers.
 */
class WriterConfiguration {

    /**
     * Property name for writer name.
     */
    static final String TYPE_KEY = "type";

    private final TinylogContext context;
    private final LevelConfiguration levelConfiguration;

    private boolean created;
    private Writer writer;

    /**
     * @param context The tinylog context to use for the writer
     */
    WriterConfiguration(TinylogContext context) {
        this.context = context;
        this.levelConfiguration = new LevelConfiguration(
            context.getConfiguration().getList(LevelConfiguration.KEY),
            false,
            context.getLogger()
        );

        this.created = false;
        this.writer = null;
    }

    /**
     * Gets the level configuration of the parsed writer.
     *
     * @return The level configuration with activated severity levels and tags
     */
    LevelConfiguration getLevelConfiguration() {
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
    Writer getOrCreateWriter() {
        if (!created) {
            created = true;
            writer = createWriter();
        }

        return writer;
    }

    /**
     * Creates a new writer instance.
     *
     * @return The created writer or {@code null} if the creation failed
     */
    private Writer createWriter() {
        Configuration configuration = context.getConfiguration();
        InternalLogger logger = context.getLogger();

        String type = configuration.getValue(TYPE_KEY);

        if (type == null) {
            logger.log(
                Level.ERROR,
                "Missing writer name in property \"{}\"",
                configuration.resolveFullKey(TYPE_KEY)
            );
            return null;
        }

        ClassLoader loader = context.getLoader();
        String name = type.toLowerCase(Locale.ENGLISH);
        WriterBuilder builder = WriterBuilder.load(loader).get(name);

        if (builder == null) {
            logger.log(
                Level.ERROR,
                "Could not find any writer builder with the name \"{}\" in the classpath",
                name
            );
            return null;
        }

        try {
            return builder.create(context);
        } catch (Exception ex) {
            logger.log(Level.ERROR, ex, "Failed to create the writer for \"{}\"", name);
            return null;
        }
    }

}
