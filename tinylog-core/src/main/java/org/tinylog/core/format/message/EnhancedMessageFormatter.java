package org.tinylog.core.format.message;

import java.text.ChoiceFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.format.value.ValueFormat;
import org.tinylog.core.internal.AbstractPatternParser;
import org.tinylog.core.internal.InternalLogger;

/**
 * Enhanced message formatter that replaces '{}' placeholders with passed arguments and optionally accepts format
 * patterns in placeholders.
 *
 * <p>
 *     All registered {@link ValueFormat} instances can be used to format arguments via patterns. Additionally the
 *     {@link ChoiceFormat} syntax is supported for conditional formatting.
 * </p>
 *
 * <p>
 *     Curly brackets and other characters can be escaped by wrapping them in single quotes ('). Two directly
 *     consecutive single quotes ('') are output as one single quote.
 * </p>
 */
public class EnhancedMessageFormatter extends AbstractPatternParser implements MessageFormatter {

    private final List<ValueFormat> formats;
    private final InternalLogger logger;

    /**
     * @param loader The class loader to use for loading service implementations
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public EnhancedMessageFormatter(ClassLoader loader, InternalLogger logger) {
        this.formats = ValueFormat.load(loader);
        this.logger = logger;
    }

    @Override
    public String format(Configuration configuration, String message, Object... arguments) {
        return format(configuration, message, Arrays.stream(arguments).iterator());
    }

    /**
     * Replaces all placeholders with real values.
     *
     * @param configuration The current tinylog configuration
     * @param message A text message with placeholders
     * @param arguments The actual replacement values for placeholders
     * @return Formatted text message
     */
    private String format(Configuration configuration, String message, Iterator<Object> arguments) {
        BiConsumer<StringBuilder, String> groupConsumer = (builder, group) -> {
            if (arguments.hasNext()) {
                builder.append(render(configuration, group, arguments.next()));
            } else {
                builder.append('{').append(group).append('}');
            }
        };

        return parse(message, groupConsumer).toString();
    }

    /**
     * Renders a value as string.
     *
     * @param configuration The current tinylog configuration
     * @param pattern The format pattern for rendering the passed value
     * @param value The object to render
     * @return The formatted representation of the passed value
     */
    private String render(Configuration configuration, String pattern, Object value) {
        if (!pattern.isEmpty()) {
            if (isConditional(pattern)) {
                try {
                    Iterator<Object> iterator = Stream.generate(() -> value).iterator();
                    return new ChoiceFormat(format(configuration, pattern, iterator)).format(value);
                } catch (RuntimeException ex) {
                    logger.log(
                        Level.ERROR,
                        ex,
                        "Invalid choice format pattern \"{}\" for value \"{}\"",
                        pattern,
                        value
                    );
                }
            } else {
                for (ValueFormat format : formats) {
                    if (format.isSupported(value)) {
                        try {
                            return format.format(configuration, pattern, value);
                        } catch (RuntimeException ex) {
                            logger.log(
                                Level.ERROR,
                                ex,
                                "Failed to apply pattern \"{}\" for value \"{}\"",
                                pattern,
                                value
                            );
                        }
                    }
                }
            }
        }

        return String.valueOf(value);
    }

    /**
     * Checks if a pattern is conditional according to the syntax of {@link ChoiceFormat}.
     *
     * @param pattern The pattern to check
     * @return {@code true} if the passed pattern is conditional, {@code false} if not
     */
    private boolean isConditional(String pattern) {
        int length = pattern.length();
        for (int index = 0; index < length; ++index) {
            char character = pattern.charAt(index);
            if (character == '|') {
                return true;
            } else if (character == '\'') {
                int closingQuotePosition = findClosingQuote(pattern, index + 1);
                if (closingQuotePosition > 0) {
                    index = closingQuotePosition;
                }
            }
        }

        return false;
    }

}
