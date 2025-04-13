package org.tinylog.impl.writer;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.impl.format.pattern.FormatPatternBuilder;

/**
 * Builder for creating an instance of a {@link Writer} that supports any kind of {@link OutputFormat}.
 *
 * <p>
 *     The output format can be configured via the property "format" in the passed configuration. If no output format
 *     is configured, {@link FormatPatternBuilder} will be used by default.
 * </p>
 */
public abstract class AbstractFormattableWriterBuilder implements WriterBuilder {

    private static final String FORMAT_KEY = "format";

    @Override
    public final Writer create(TinylogContext context) throws Exception {
        Configuration configuration = context.getConfiguration();
        InternalLogger logger = context.getLogger();

        String name = configuration.getValue(FORMAT_KEY);
        OutputFormatBuilder builder = null;

        if (name != null) {
            builder = OutputFormatBuilder.load(context.getLoader()).get(name);

            if (builder == null) {
                logger.log(
                    Level.ERROR,
                    "Unknown output format \"{}\" in property \"{}\"",
                    name,
                    configuration.resolveFullKey(FORMAT_KEY)
                );
            }
        }

        if (builder == null) {
            builder = new FormatPatternBuilder();
        }

        OutputFormat format = builder.create(context);
        return create(context, format);
    }

    /**
     * Creates a new instance of the writer.
     *
     * @param context The tinylog context to use for creating a new writer
     * @param format The output format for log entries
     * @return New instance of the writer
     * @throws Exception If failed to create a new writer for the passed configuration
     */
    protected abstract Writer create(TinylogContext context, OutputFormat format) throws Exception;

}
