package org.tinylog.impl.path.segment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParsePosition;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;

/**
 * Path segment for the date-time.
 */
public class DateTimeSegment implements PathSegment {

    private static final List<TemporalQuery<TemporalAccessor>> queries = Arrays.asList(
        ZonedDateTime::from,
        OffsetDateTime::from,
        LocalDateTime::from,
        LocalDate::from,
        OffsetTime::from,
        LocalTime::from,
        YearMonth::from,
        Year::from,
        MonthDay::from,
        Month::from,
        DayOfWeek::from
    );

    private final DateTimeFormatter formatter;
    private final String pattern;
    private final InternalLogger logger;

    /**
     * @param pattern The pattern to use for formatting date-times
     * @param locale The locale to use for formatting date-times
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public DateTimeSegment(String pattern, Locale locale, InternalLogger logger) {
        this.formatter = DateTimeFormatter.ofPattern(pattern, locale);
        this.pattern = pattern;
        this.logger = logger;
    }

    @Override
    public String findLatest(Path parentDirectory, String prefix) throws IOException {
        try (Stream<Path> stream = Files.list(parentDirectory)) {
            Optional<DateTimeTuple> latest = stream
                .map(Path::getFileName)
                .filter(Objects::nonNull)
                .map(Path::toString)
                .filter(name -> name.startsWith(prefix))
                .map(name -> new DateTimeTuple(formatter, name.substring(prefix.length())))
                .filter(tuple -> tuple.value != null)
                .max(DateTimeTuple::compare);

            return latest.map(tuple -> tuple.text).orElse(null);
        }
    }

    @Override
    public void resolve(StringBuilder pathBuilder, Supplier<ZonedDateTime> dateTimeSupplier) {
        int start = pathBuilder.length();
        formatter.formatTo(dateTimeSupplier.get(), pathBuilder);

        boolean containsPosixFileSeparator = pathBuilder.indexOf("/", start) >= 0;
        boolean containsWindowsFileSeparator = pathBuilder.indexOf("\\", start) >= 0;
        if (containsPosixFileSeparator || containsWindowsFileSeparator) {
            logger.log(
                Level.WARN,
                "Date-time pattern \"{}\" must not contain a file separator, but resolves to \"{}\"",
                pattern,
                pathBuilder.substring(start)
            );
        }
    }

    /**
     * Tuple for storing the date-time text together with the parsed {@link TemporalAccessor}.
     */
    private static final class DateTimeTuple {

        private final String text;
        private final Comparable<?> value;

        /**
         * @param formatter The date-time formatter to use for parsing the passed text
         * @param text The date-time text to parse
         */
        private DateTimeTuple(DateTimeFormatter formatter, String text) {
            ParsePosition position = new ParsePosition(0);
            Comparable<?> value = parse(formatter, text, position);

            this.text = value == null ? text : text.substring(0, position.getIndex());
            this.value = value;
        }

        /**
         * Compares the stored comparable with the comparable from the passed tuple.
         *
         * @param other The tuple with the comparable value to compare with
         * @return A negative integer if the comparable value of this tuple is less than the comparable value of the
         *         passed tuple, zero if the comparable values of both tuples are equals, or a positive integer if the
         *         comparable value of this tuple is greater than the comparable value of the passed tuple
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        public int compare(DateTimeTuple other) {
            return ((Comparable) this.value).compareTo(other.value);
        }

        /**
         * Parses the passed text by the passed date-time formatter.
         *
         * @param formatter The date-time formatter to use for parsing the passed text
         * @param text The date-time text to parse
         * @param position The updatable position of parsing
         * @return The parsed comparable value or {@code null} if no comparable value could be found
         */
        private static Comparable<?> parse(DateTimeFormatter formatter, String text, ParsePosition position) {
            TemporalAccessor parsedValue;
            try {
                parsedValue = formatter.parse(text, position);
            } catch (DateTimeParseException ex) {
                return null;
            }

            for (TemporalQuery<TemporalAccessor> query : queries) {
                try {
                    return (Comparable<?>) parsedValue.query(query);
                } catch (RuntimeException ex) {
                    // Ignore and continue
                }
            }

            char firstChar = text.charAt(0);
            if (firstChar >= '0' && firstChar <= '9') {
                try {
                    return Long.valueOf(text.substring(0, position.getIndex()));
                } catch (NumberFormatException ex) {
                    // Ignore and continue
                }
            }

            return null;
        }

    }

}
