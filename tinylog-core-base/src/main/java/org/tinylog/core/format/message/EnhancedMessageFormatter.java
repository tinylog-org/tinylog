package org.tinylog.core.format.message;

import java.text.ChoiceFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.tinylog.core.Framework;
import org.tinylog.core.format.value.ValueFormat;
import org.tinylog.core.format.value.ValueFormatBuilder;
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
     * @param framework The actual logging framework instance
     */
    public EnhancedMessageFormatter(Framework framework) {
        Locale locale = framework.getConfiguration().getLocale();
        formats = SafeServiceLoader.asList(
            framework, ValueFormatBuilder.class, "value format builders", builder -> builder.create(locale)
        );
    }

    @Override
    public String format(String message, Object... arguments) {
        return format(message, Arrays.stream(arguments).iterator());
    }

    /**
     * Replaces all placeholders with real values.
     *
     * @param message
     *            Text message with placeholders
     * @param arguments
     *            Replacements for placeholders
     * @return Formatted text message
     */
    private String format(String message, Iterator<Object> arguments) {
        BiConsumer<StringBuilder, String> groupConsumer = (builder, group) -> {
            if (arguments.hasNext()) {
                builder.append(render(group, arguments.next()));
            } else {
                builder.append('{').append(group).append('}');
            }
        };

        return parse(message, groupConsumer).toString();
    }

    /**
     * Renders a value as string.
     *
     * @param pattern The format pattern for rendering the passed value
     * @param value Object to render
     * @return The formatted representation of the passed value
     */
    private String render(String pattern, Object value) {
        if (value instanceof Supplier<?>) {
            value = ((Supplier<?>) value).get();
        }

        if (!pattern.isEmpty()) {
            if (isConditional(pattern)) {
                try {
                    Object singleton = value;
                    Iterator<Object> iterator = Stream.generate(() -> singleton).iterator();
                    return new ChoiceFormat(format(pattern, iterator)).format(value);
                } catch (RuntimeException ex) {
                    InternalLogger.error(ex, "Invalid choice format pattern \"{}\" for value \"{}\"", pattern, value);
                }
            } else {
                for (ValueFormat format : formats) {
                    if (format.isSupported(value)) {
                        try {
                            return format.format(pattern, value);
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
