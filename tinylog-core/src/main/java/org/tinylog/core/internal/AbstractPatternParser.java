package org.tinylog.core.internal;

import java.util.function.BiConsumer;

/**
 * Abstract parser for parsing format patterns with placeholders in curly brackets. Phrases including curly brackets
 * can be escaped by wrapping them in single quotes ('). Two directly consecutive single quotes ('') are output as one
 * single quote.
 *
 * <p>Example format pattern with placeholders:</p>
 * <pre><code>Hello {foo}!</code></pre>
 *
 * <p>Example format pattern with quoting:</p>
 * <pre><code>'{"bar": "'{foo}'"}'</code></pre>
 *
 * <p>Example format pattern with single quotes:</p>
 * <pre><code>It''s {foo} o''clock</code></pre>
 */
public abstract class AbstractPatternParser {

    private static final int EXTRA_CAPACITY = 32;

    /**
     * Parses a format pattern. For each found placeholder that is wrapped in curly brackets, the passed placeholder
     * consumer is called with the actual used string builder and the found placeholder (without the wrapped curly
     * brackets) as arguments.
     *
     * @param pattern The format pattern to parse
     * @param placeholderConsumer The callback for found placeholders
     * @return The used string builder containing the resolved text
     */
    protected StringBuilder parse(String pattern, BiConsumer<StringBuilder, String> placeholderConsumer) {
        int length = pattern.length();
        StringBuilder builder = new StringBuilder(length + EXTRA_CAPACITY);

        for (int index = 0; index < length; ++index) {
            char character = pattern.charAt(index);
            if (character == '\'') {
                int closingQuotePosition = findClosingQuote(pattern, index + 1);
                if (closingQuotePosition == index + 1) {
                    continue;
                } else if (closingQuotePosition > 0) {
                    builder.append(pattern, index + 1, closingQuotePosition);
                    index = closingQuotePosition;
                    continue;
                }
            } else if (character == '{') {
                int closingCurlyBracketPosition = findClosingCurlyBracket(pattern, index + 1, length);
                if (closingCurlyBracketPosition > 0) {
                    placeholderConsumer.accept(builder, pattern.substring(index + 1, closingCurlyBracketPosition));
                    index = closingCurlyBracketPosition;
                    continue;
                }
            }

            builder.append(character);
        }

        return builder;
    }

    /**
     * Finds the next single quote (').
     *
     * @param message The text in which to search for a single quote
     * @param start The position from which the search is to be started
     * @return Position of the found single quote or -1 if none could be found
     */
    protected static int findClosingQuote(String message, int start) {
        return message.indexOf('\'', start);
    }

    /**
     * Finds the next closing curly bracket '{'.
     *
     * @param message The text in which to search for a closing curly bracket
     * @param start The included position from which the search is to be started
     * @param end The excluded position at which the search is to be stopped
     * @return Position of the found closing curly bracket or -1 if none could be found
     */
    protected static int findClosingCurlyBracket(String message, int start, int end) {
        int openCount = 1;

        for (int index = start; index < end; ++index) {
            char character = message.charAt(index);
            if (character == '\'') {
                int closingQuotePosition = findClosingQuote(message, index + 1);
                if (closingQuotePosition > 0) {
                    index = closingQuotePosition;
                }
            } else if (character == '{') {
                openCount += 1;
            } else if (character == '}') {
                openCount -= 1;
                if (openCount == 0) {
                    return index;
                }
            }
        }

        return -1;
    }

}
