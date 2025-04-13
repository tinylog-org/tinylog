package org.tinylog.core.format.value;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class DateFormatTest {

    /**
     * Verifies that the value format is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(ValueFormat.class)).anyMatch(loader -> loader instanceof DateFormat);
    }

    /**
     * Tests for supported value types.
     */
    @Nested
    class ValueTypes {

        /**
         * Verifies that {@link Date} is supported.
         */
        @Test
        void supportedDateValue() {
            DateFormat format = new DateFormat();
            assertThat(format.isSupported(new Date())).isTrue();
        }

        /**
         * Verifies that strings are not supported.
         */
        @Test
        void unsupportedStringValue() {
            DateFormat format = new DateFormat();
            assertThat(format.isSupported("foo")).isFalse();
        }

    }

    /**
     * Tests for formatting dates for different languages.
     */
    @Nested
    class Formatting {

        @Inject
        private Configuration configuration;

        /**
         * Verifies that a {@link Date} can be formatted in the US style.
         */
        @Tinylog(configuration = {"locale=en_US", "zone=UTC"})
        @Test
        void formatWithUsLocale() {
            ZonedDateTime zonedDateTime = LocalDateTime.parse("2020-12-31T11:55").atZone(ZoneOffset.UTC);
            Date date = Date.from(zonedDateTime.toInstant());

            String output = new DateFormat().format(configuration, "MMMM d, yyyy, HH:mm", date);
            assertThat(output).isEqualTo("December 31, 2020, 11:55");
        }

        /**
         * Verifies that a {@link Date} can be formatted in the German style.
         */
        @Tinylog(configuration = {"locale=de_DE", "zone=UTC"})
        @Test
        void formatWithGermanLocale() {
            ZonedDateTime zonedDateTime = LocalDateTime.parse("2020-12-31T11:55").atZone(ZoneOffset.UTC);
            Date date = Date.from(zonedDateTime.toInstant());

            String output = new DateFormat().format(configuration, "dd. MMMM yyyy, HH:mm", date);
            assertThat(output).isEqualTo("31. Dezember 2020, 11:55");
        }

    }

    /**
     * Tests for different time zones.
     */
    @Nested
    class TimeZones {

        @Inject
        private Configuration configuration;

        /**
         * Verifies that dates are correctly output for the time zone {@code GMT-1}.
         */
        @Tinylog(configuration = {"locale=en_US", "zone=GMT-1"})
        @Test
        void gmtMinus1() {
            LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
            Date date = Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());

            String output = new DateFormat().format(configuration, "yyyy-MM-dd HH:mm", date);
            assertThat(output).isEqualTo("2019-12-31 23:00");
        }

        /**
         * Verifies that dates are correctly output for the time zone {@code GMT}.
         */
        @Tinylog(configuration = {"locale=en_US", "zone=GMT"})
        @Test
        void gmt() {
            LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
            Date date = Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());

            String output = new DateFormat().format(configuration, "yyyy-MM-dd HH:mm", date);
            assertThat(output).isEqualTo("2020-01-01 00:00");
        }

        /**
         * Verifies that dates are correctly output for the time zone {@code GMT+1}.
         */
        @Tinylog(configuration = {"locale=en_US", "zone=GMT+1"})
        @Test
        void gmtPlus1() {
            LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
            Date date = Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());

            String output = new DateFormat().format(configuration, "yyyy-MM-dd HH:mm", date);
            assertThat(output).isEqualTo("2020-01-01 01:00");
        }

    }

}
