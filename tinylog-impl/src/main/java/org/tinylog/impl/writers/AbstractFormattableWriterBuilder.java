package org.tinylog.impl.writers;

import java.util.List;
import java.util.Objects;

import org.tinylog.core.Configuration;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.internal.SafeServiceLoader;
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
    public final Writer create(LoggingContext context, Configuration configuration) throws Exception {
        String name = configuration.getValue(FORMAT_KEY);
        OutputFormatBuilder builder = null;

        if (name != null) {
            List<OutputFormatBuilder> builders = SafeServiceLoader.asList(
                context.getFramework().getClassLoader(),
                OutputFormatBuilder.class,
                "output format builders"
            );

            builder = builders.stream()
                .filter(service -> Objects.equals(service.getName(), name))
                .findAny()
                .orElse(null);

            if (builder == null) {
                InternalLogger.error(
                    null,
                    "Unknown output format \"{}\" in property \"{}\"",
                    name,
                    configuration.resolveFullKey(FORMAT_KEY)
                );
            }
        }

        if (builder == null) {
            builder = new FormatPatternBuilder();
        }

        OutputFormat format = builder.create(context, configuration);
        return create(context, configuration, format);
    }

    /**
     * Creates a new instance of the writer.
     *
     * <p>
     *     Synchronous writers can implement the plain {@link Writer} interface and asynchronous writers the
     *     {@link AsyncWriter} interface. Writers with blocking output operations (e.g. outputting log entries to files,
     *     databases, or remote servers) should use the {@link AsyncWriter} interface for performance reasons.
     * </p>
     *
     * @param context The current logging context
     * @param configuration The configuration properties for the new writer instance
     * @param format The output format for log entries
     * @return New instance of the writer
     * @throws Exception Failed to create a new writer for the passed configuration
     */
    public abstract Writer create(
        LoggingContext context,
        Configuration configuration,
        OutputFormat format
    ) throws Exception;

}
