package org.tinylog.core.format.message;

import java.text.ChoiceFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.tinylog.core.Framework;
import org.tinylog.core.format.value.ValueFormat;
import org.tinylog.core.format.value.ValueFormatBuilder;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.SafeServiceLoader;

/**
 * Enhanced message formatter that replaces '{}' placeholders with passed arguments and optionally accepts format
 * patterns in placeholders.
 *
 * <p>
 *     All registered {@link ValueFormat ValueFormats} can be used to format arguments via patterns. Additionally the
 *     {@link ChoiceFormat} syntax is supported for conditional formatting.
 * </p>
 *
 * <p>
 *     Curly brackets and other characters can be escaped by wrapping them in singe quotes ('). Two directly consecutive
 *     singe quotes ('') are output as one singe quote.
 * </p>
 */
public class EnhancedMessageFormatter implements MessageFormatter {

	private static final int EXTRA_CAPACITY = 32;

	private final List<ValueFormat> formats;

	/**
	 * @param framework The actual logging framework instance
	 */
	public EnhancedMessageFormatter(Framework framework) {
		Locale locale = framework.getConfiguration().getLocale();
		formats = SafeServiceLoader.asList(
			framework, ValueFormatBuilder.class, "value format builder", builder -> builder.create(locale)
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
		int length = message.length();

		StringBuilder builder = new StringBuilder(length + EXTRA_CAPACITY);

		for (int index = 0; index < length; ++index) {
			char character = message.charAt(index);
			if (character == '\'') {
				int closingQuotePosition = findClosingQuote(message, index + 1);
				if (closingQuotePosition == index + 1) {
					continue;
				} else if (closingQuotePosition > 0) {
					builder.append(message, index + 1, closingQuotePosition);
					index = closingQuotePosition;
					continue;
				}
			} else if (character == '{' && arguments.hasNext()) {
				int closingCurlyBracketPosition = findClosingCurlyBracket(message, index + 1, length);
				if (closingCurlyBracketPosition > 0) {
					String pattern = message.substring(index + 1, closingCurlyBracketPosition);
					builder.append(render(pattern, arguments.next()));
					index = closingCurlyBracketPosition;
					continue;
				}
			}

			builder.append(character);
		}

		return builder.toString();
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
				Object singleton = value;
				Iterator<Object> iterator = Stream.generate(() -> singleton).iterator();
				return new ChoiceFormat(format(pattern, iterator)).format(value);
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

	/**
	 * Finds the next single quote (').
	 *
	 * @param message The text in which to search for a single quote
	 * @param start The position from which the search is to be started
	 * @return Position of the found single quote or -1 if none could be found
	 */
	private int findClosingQuote(String message, int start) {
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
	private int findClosingCurlyBracket(String message, int start, int end) {
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
