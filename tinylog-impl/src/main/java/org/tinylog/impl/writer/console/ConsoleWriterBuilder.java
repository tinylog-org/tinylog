package org.tinylog.impl.writer.console;

import java.util.Locale;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.writer.AbstractFormattableWriterBuilder;
import org.tinylog.impl.writer.Writer;

/**
 * Builder for creating an instance of {@link ConsoleWriter}.
 */
public class ConsoleWriterBuilder extends AbstractFormattableWriterBuilder {

    private static final String THRESHOLD_KEY = "threshold";
    private static final Level DEFAULT_THRESHOLD = Level.WARN;

    /** */
    public ConsoleWriterBuilder() {
    }

    @Override
    public String getName() {
        return "console";
    }

    @Override
    protected Writer create(TinylogContext context, OutputFormat format) {
        Configuration configuration = context.getConfiguration();
        InternalLogger logger = context.getLogger();

        String threshold = configuration.getValue(THRESHOLD_KEY);
        Level level = DEFAULT_THRESHOLD;

        if (threshold != null) {
            try {
                level = Level.valueOf(threshold.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException ex) {
                logger.log(
                    Level.ERROR,
                    "Invalid severity level \"{}\" in property \"{}\"",
                    threshold,
                    configuration.resolveFullKey(THRESHOLD_KEY)
                );
            }
        }

        return new ConsoleWriter(format, level);
    }

}
