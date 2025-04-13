package org.tinylog.impl.format.pattern;

import org.tinylog.core.Configuration;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.FormatPatternParser;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Builder for creating an instance of format pattern {@link Placeholder}.
 */
public class FormatPatternBuilder implements OutputFormatBuilder {

    private static final String PATTERN_KEY = "pattern";
    private static final String DEFAULT_PATTERN =
        "{date} [{thread}] {level|min-length:5} {class}.{method}(): {message}";

    /** */
    public FormatPatternBuilder() {
    }

    @Override
    public String getName() {
        return "pattern";
    }

    @Override
    public OutputFormat create(TinylogContext context) {
        Configuration configuration = context.getConfiguration();
        String pattern = configuration.getValue(PATTERN_KEY, DEFAULT_PATTERN) + System.lineSeparator();
        return new FormatPatternParser(context).parse(pattern);
    }

}
