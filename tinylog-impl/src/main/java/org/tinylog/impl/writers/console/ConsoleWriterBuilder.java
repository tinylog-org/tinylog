package org.tinylog.impl.writers.console;

import java.util.Locale;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.writers.AbstractFormattableWriterBuilder;
import org.tinylog.impl.writers.Writer;

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
    public Writer create(LoggingContext context, Configuration configuration, OutputFormat format) {
        String threshold = configuration.getValue(THRESHOLD_KEY);
        Level level = DEFAULT_THRESHOLD;
        if (threshold != null) {
            try {
                level = Level.valueOf(threshold.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException ex) {
                InternalLogger.error(
                    null,
                    "Invalid severity level \"{}\" in property \"{}\"",
                    threshold,
                    configuration.resolveFullKey(THRESHOLD_KEY)
                );
            }
        }

        return new ConsoleWriter(format, level);
    }

}
