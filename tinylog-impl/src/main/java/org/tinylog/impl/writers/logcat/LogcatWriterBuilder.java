package org.tinylog.impl.writers.logcat;

import org.tinylog.core.Configuration;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.impl.format.pattern.FormatPatternParser;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.format.pattern.styles.MaxLengthStyleBuilder;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.WriterBuilder;

/**
 * Builder for creating an instance of {@link LogcatWriter}.
 */
public class LogcatWriterBuilder implements WriterBuilder {

    private static final String TAG_PATTERN_KEY = "tag-pattern";
    private static final String DEFAULT_TAG_PATTERN = null;
    private static final int MAX_TAG_LENGTH = 23;

    private static final String MESSAGE_PATTERN_KEY = "message-pattern";
    private static final String DEFAULT_MESSAGE_PATTERN = "{message}";

    /** */
    public LogcatWriterBuilder() {
    }

    @Override
    public String getName() {
        return "logcat";
    }

    @Override
    public Writer create(LoggingContext context, Configuration configuration) {
        FormatPatternParser formatPatternParser = new FormatPatternParser(context);

        String tagPattern = configuration.getValue(TAG_PATTERN_KEY, DEFAULT_TAG_PATTERN);
        Placeholder tagPlaceholder = null;
        if (tagPattern != null) {
            Placeholder placeholder = formatPatternParser.parse(tagPattern);
            MaxLengthStyleBuilder maxLengthStyleBuilder = new MaxLengthStyleBuilder();
            tagPlaceholder = maxLengthStyleBuilder.create(context, placeholder, Integer.toString(MAX_TAG_LENGTH));
        }

        String messagePattern = configuration.getValue(MESSAGE_PATTERN_KEY, DEFAULT_MESSAGE_PATTERN);
        Placeholder messagePlaceholder = formatPatternParser.parse(messagePattern);

        return new LogcatWriter(tagPlaceholder, messagePlaceholder);
    }

}
