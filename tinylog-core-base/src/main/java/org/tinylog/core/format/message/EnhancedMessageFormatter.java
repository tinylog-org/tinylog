package org.tinylog.core.format.message;

import java.text.ChoiceFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.tinylog.core.Framework;
import org.tinylog.core.format.value.ValueFormat;
import org.tinylog.core.internal.AbstractPatternParser;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.SafeServiceLoader;

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

    /**
     * @param loader The class loader to use for loading service files and service implementation classes
     */
    public EnhancedMessageFormatter(ClassLoader loader) {
        formats = SafeServiceLoader.asList(loader, ValueFormat.class, "value formats");
    }

    @Override
    public String format(Framework framework, String message, Object... arguments) {
        return format(framework, message, Arrays.stream(arguments).iterator());
    }

    /**
     * Replaces all placeholders with real values.
     *
     * @param framework The actual framework instance
     * @param message A text message with placeholders
     * @param arguments The actual replacement values for placeholders
     * @return Formatted text message
     */
    private String format(Framework framework, String message, Iterator<Object> arguments) {
        BiConsumer<StringBuilder, String> groupConsumer = (builder, group) -> {
            if (arguments.hasNext()) {
                builder.append(render(framework, group, arguments.next()));
            } else {
                builder.append('{').append(group).append('}');
            }
        };

        return parse(message, groupConsumer).toString();
    }

    /**
     * Renders a value as string.
     *
     * @param framework The actual framework instance
     * @param pattern The format pattern for rendering the passed value
     * @param value The object to render
     * @return The formatted representation of the passed value
     */
    private String render(Framework framework, String pattern, Object value) {
        if (value instanceof Supplier<?>) {
            value = ((Supplier<?>) value).get();
        }

        if (!pattern.isEmpty()) {
            if (isConditional(pattern)) {
                try {
                    Object singleton = value;
                    Iterator<Object> iterator = Stream.generate(() -> singleton).iterator();
                    return new ChoiceFormat(format(framework, pattern, iterator)).format(value);
                } catch (RuntimeException ex) {
                    InternalLogger.error(ex, "Invalid choice format pattern \"{}\" for value \"{}\"", pattern, value);
                }
            } else {
                for (ValueFormat format : formats) {
                    if (format.isSupported(value)) {
                        try {
                            return format.format(framework, pattern, value);
                        } catch (RuntimeException ex) {
                            InternalLogger.error(ex, "Failed to apply pattern \"{}\" for value \"{}\"", pattern, value);
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
    private boolean isConditional(final String pattern) {
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
