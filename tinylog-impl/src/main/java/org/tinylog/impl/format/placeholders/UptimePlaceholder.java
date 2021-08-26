package org.tinylog.impl.format.placeholders;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.ToLongFunction;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

/**
 * Placeholder implementation for resolving the passed time since application start when a log entry was issued.
 */
public class UptimePlaceholder implements Placeholder {

	private static final int DECIMAL_BASE = 10;
	private static final int NANOS_SCALE = 9;

	private static final int MAX_SECOND = 60;
	private static final int MAX_MINUTE = 60;
	private static final int MAX_HOUR = 24;

	private static final int MINUTE_IN_SECONDS = MAX_SECOND;
	private static final int HOUR_IN_SECONDS = MAX_MINUTE * MINUTE_IN_SECONDS;
	private static final int DAY_IN_SECONDS = MAX_HOUR * HOUR_IN_SECONDS;

	private final List<BiConsumer<StringBuilder, Duration>> segments;
	private final boolean formatForSql;

	/**
	 * @param pattern The format pattern to use for formatting the uptime
	 * @param formatForSql The uptime will be applied as formatted string to prepared SQL statements if set to
	 *                     {@code true}, otherwise it will be applied as {@link BigDecimal SQL NUMERIC}
	 */
	public UptimePlaceholder(String pattern, boolean formatForSql) {
		this.segments = parse(pattern);
		this.formatForSql = formatForSql;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.UPTIME);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Duration duration = entry.getUptime();

		if (duration == null) {
			builder.append("<uptime unknown>");
		} else {
			format(builder, duration);
		}
	}

	@Override
	public SqlRecord<?> resolve(LogEntry entry) {
		Duration duration = entry.getUptime();

		if (formatForSql) {
			if (duration == null) {
				return new SqlRecord<>(Types.VARCHAR, null);
			} else {
				StringBuilder builder = new StringBuilder();
				format(builder, duration);
				return new SqlRecord<>(Types.VARCHAR, builder);
			}
		} else {
			if (duration == null) {
				return new SqlRecord<>(Types.NUMERIC, null);
			} else {
				BigDecimal seconds = BigDecimal.valueOf(duration.getSeconds());
				BigDecimal nanos = BigDecimal.valueOf(duration.getNano(), NANOS_SCALE);
				return new SqlRecord<>(Types.NUMERIC, seconds.add(nanos));
			}
		}
	}

	/**
	 * Parses format patterns.
	 *
	 * <p>
	 *     The returned consumers can be used for formatting a {@link Duration} into a {@link StringBuilder}.
	 * </p>
	 *
	 * @param pattern The format pattern to parse
	 * @return List of formattable segments
	 */
	private static List<BiConsumer<StringBuilder, Duration>> parse(String pattern) {
		List<BiConsumer<StringBuilder, Duration>> segments = new ArrayList<>();
		boolean firstTimeUnit = true;

		for (int i = 0; i < pattern.length(); ++i) {
			char character = pattern.charAt(i);

			if (character == '\'') {
				int end = pattern.indexOf('\'', i + 1);
				if (end == -1) { // Unescaped single quote
					segments.add((builder, duration) -> builder.append(character));
				} else if (end == i + 1) { // Escaped single quote
					segments.add((builder, duration) -> builder.append(character));
					i += 1;
				} else { // Escaped phrase
					String text = pattern.substring(i + 1, end);
					segments.add((builder, duration) -> builder.append(text));
					i = end;
				}
			} else {
				int length = count(pattern, i, character);
				ToLongFunction<Duration> timeUnitResolver = createTimeUnitResolver(character, length, firstTimeUnit);

				if (timeUnitResolver == null) { // Plain character
					segments.add((builder, duration) -> builder.append(character));
				} else { // Time unit placeholder
					segments.add(
						(builder, duration) -> formatLong(builder, timeUnitResolver.applyAsLong(duration), length)
					);
					firstTimeUnit = false;
					i += length - 1;
				}
			}
		}

		return segments;
	}

	/**
	 * Counts the sequence length of a character at the given position in the passed text.
	 *
	 * @param text The source text that contains the character
	 * @param start The position in the passed text, where the sequence length count of the passed character starts
	 * @param character The character to count
	 * @return The sequence length of the passed character
	 */
	private static int count(final String text, final int start, final char character) {
		int index = start;
		while (index < text.length() && text.charAt(index) == character) {
			++index;
		}
		return index - start;
	}

	/**
	 * Creates a function that can resolve the time for the given time unit from a {@link Duration}.
	 *
	 * <p>
	 *     Supported time units:
	 *     <ul>
	 *     <li>'S': Fraction of second</li>
	 *     <li>'s': Seconds</li>
	 *     <li>'m': Minutes</li>
	 *     <li>'H': Hours</li>
	 *     <li>'d': Days</li>
	 *     </ul>
	 * </p>
	 *
	 * @param timeUnit The time unit as character
	 * @param length The sequence length of the passed time unit character
	 * @param firstTimeUnit {@code true} if this is the first time unit in the format pattern, otherwise {@code false}
	 * @return The created resolve function if the passed time unit is supported, otherwise {@code null}
	 */
	private static ToLongFunction<Duration> createTimeUnitResolver(char timeUnit, int length, boolean firstTimeUnit) {
		switch (timeUnit) {
			case 'S':
				if (NANOS_SCALE > length) {
					long divisor = (long) Math.pow(DECIMAL_BASE, NANOS_SCALE - length);
					return duration -> duration.getNano() / divisor;
				} else {
					long multiplier = (long) Math.pow(DECIMAL_BASE, length - NANOS_SCALE);
					return duration -> duration.getNano() * multiplier;
				}
			case 's':
				if (firstTimeUnit) {
					return Duration::getSeconds;
				} else {
					return duration -> duration.getSeconds() % MAX_SECOND;
				}
			case 'm':
				if (firstTimeUnit) {
					return duration -> duration.getSeconds() / MINUTE_IN_SECONDS;
				} else {
					return duration -> duration.getSeconds() / MINUTE_IN_SECONDS % MAX_MINUTE;
				}
			case 'H':
				if (firstTimeUnit) {
					return duration -> duration.getSeconds() / HOUR_IN_SECONDS;
				} else {
					return duration -> duration.getSeconds() / HOUR_IN_SECONDS % MAX_HOUR;
				}
			case 'd':
				return duration -> duration.getSeconds() / DAY_IN_SECONDS;
			default:
				return null;
		}
	}

	/**
	 * Formats a long value into a {@link StringBuilder}.
	 *
	 * <p>
	 *     If the passed long value has fewer digits than the passed number of minimum digits, additional zeros are
	 *     inserted before the actual formatted number to satisfy the number of minimum digits.
	 * </p>
	 *
	 * @param builder The target string builder for the formatted long value
	 * @param value The long value to format
	 * @param minDigits The minimum number of digits
	 */
	private static void formatLong(StringBuilder builder, long value, long minDigits) {
		String formatted = Long.toString(value);
		for (int i = 0; i < minDigits - formatted.length(); ++i) {
			builder.append('0');
		}
		builder.append(formatted);
	}

	/**
	 * Formats a {@link Duration} into a {@link StringBuilder}.
	 *
	 * @param builder The target string builder for the formatted duration
	 * @param duration The duration to format
	 */
	private void format(StringBuilder builder, Duration duration) {
		for (BiConsumer<StringBuilder, Duration> segment : segments) {
			segment.accept(builder, duration);
		}
	}

}
