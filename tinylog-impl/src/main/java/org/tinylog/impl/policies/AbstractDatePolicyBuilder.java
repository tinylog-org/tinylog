package org.tinylog.impl.policies;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.Locale;

/**
 * Abstract builder for creating instances of {@link AbstractDatePolicy}.
 */
public abstract class AbstractDatePolicyBuilder implements PolicyBuilder {

	/** */
	public AbstractDatePolicyBuilder() {
	}

	/**
	 * Parses a text by a given {@link DateTimeFormatter date-time pattern}.
	 *
	 * @param pattern The date-time pattern to use for parsing
	 * @param value The text value to parse (can be {@code null})
	 * @return The parsed date-time object or {@code null} if the passed value is {@code null}
	 * @throws IllegalArgumentException Failed to parse the text value by the given date-time pattern
	 */
	protected TemporalAccessor parse(String pattern, String value) throws IllegalArgumentException {
		if (value == null) {
			return null;
		} else {
			try {
				return new DateTimeFormatterBuilder()
					.parseCaseInsensitive()
					.appendPattern(pattern)
					.toFormatter(Locale.ENGLISH)
					.parse(value);
			} catch (DateTimeParseException ex) {
				throw new IllegalArgumentException(
					"Invalid configuration \"" + value + "\" for " + getName() + " policy",
					ex
				);
			}
		}
	}

	/**
	 * Extracts date-time information from a {@link TemporalAccessor}.
	 *
	 * @param accessor The parsed date-time object from {@link #parse(String, String)}
	 * @param query A query from {@link TemporalQueries} (must support {@code null} results)
	 * @param defaultValue The default value to use if the passed query returns {@code null}
	 * @param <T> The result type
	 * @return The extracted value or the default value if not present in the passed temporal accessor
	 */
	protected <T> T getOrDefault(TemporalAccessor accessor, TemporalQuery<T> query, T defaultValue) {
		T value = accessor == null ? null : accessor.query(query);
		return value == null ? defaultValue : value;
	}

}
