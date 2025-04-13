package org.tinylog.impl.format.placeholder;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ToLongFunction;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Placeholder implementation for resolving the uptime when a log entry was issued.
 */
public class UptimePlaceholder implements Placeholder {

    private static final int DECIMAL_BASE = 10;
    private static final int NANOS_SCALE = 9;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;

    private final RuntimeFlavor runtime;
    private final List<BiConsumer<StringBuilder, Duration>> segments;
    private final boolean forceFormatting;

    /**
     * @param runtime The runtime to use for extracting the current uptime
     * @param pattern The format pattern to use for formatting the uptime
     * @param forceFormatting The uptime will be returned as formatted string by the value getter if set to
     *                        {@code true}, otherwise it will be returned as a {@link BigDecimal}
     */
    public UptimePlaceholder(RuntimeFlavor runtime, String pattern, boolean forceFormatting) {
        this.runtime = runtime;
        this.segments = parse(pattern);
        this.forceFormatting = forceFormatting;
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITHOUT_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return forceFormatting ? ValueType.STRING : ValueType.DECIMAL;
    }

    @Override
    public Object getValue(LogEntry entry) {
        Duration uptime = entry.getUptime(runtime);

        if (forceFormatting) {
            StringBuilder builder = new StringBuilder();
            format(builder, uptime);
            return builder.toString();
        } else {
            return BigDecimal.valueOf(uptime.toNanos(), NANOS_SCALE);
        }
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        format(builder, entry.getUptime(runtime));
    }

    /**
     * Formats a {@link Duration} and appends the result into a {@link StringBuilder}.
     *
     * @param builder The target string builder for the formatted duration
     * @param duration The duration to format
     */
    private void format(StringBuilder builder, Duration duration) {
        for (BiConsumer<StringBuilder, Duration> segment : segments) {
            segment.accept(builder, duration);
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
    private static int count(String text, int start, char character) {
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
     * @param firstTimeUnit {@code true} if this is the first time unit in the format pattern and should not be
     *                      truncated, otherwise {@code false}
     * @return The created resolve function if the passed time unit is supported, otherwise {@code null}
     */
    private static ToLongFunction<Duration> createTimeUnitResolver(char timeUnit, int length, boolean firstTimeUnit) {
        switch (timeUnit) {
            case 'S':
                double multiplier = Math.pow(DECIMAL_BASE, length - NANOS_SCALE);
                return duration -> (long) (duration.getNano() * multiplier);
            case 's':
                return firstTimeUnit ? Duration::getSeconds : duration -> duration.getSeconds() % SECONDS_PER_MINUTE;
            case 'm':
                return firstTimeUnit ? Duration::toMinutes : duration -> duration.toMinutes() % MINUTES_PER_HOUR;
            case 'H':
                return firstTimeUnit ? Duration::toHours : duration -> duration.toHours() % HOURS_PER_DAY;
            case 'd':
                return Duration::toDays;
            default:
                return null;
        }
    }

    /**
     * Formats a long value into a {@link StringBuilder}.
     *
     * <p>
     *     If the passed long value has fewer digits than the passed number of minimum digits, additional zeros are
     *     inserted before the actual formatted number to satisfy the required minimum digits.
     * </p>
     *
     * @param builder The target string builder for the formatted long value
     * @param value The long value to format
     * @param minDigits The minimum number of digits
     */
    private static void formatLong(StringBuilder builder, long value, long minDigits) {
        int digits = value == 0 ? 1 : (int) (Math.log10(value) + 1);
        for (int i = 0; i < minDigits - digits; ++i) {
            builder.append('0');
        }
        builder.append(value);
    }

}
