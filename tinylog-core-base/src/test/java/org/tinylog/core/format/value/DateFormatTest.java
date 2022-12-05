package org.tinylog.core.format.value;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class DateFormatTest {

    @Inject
    private Framework framework;

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

        /**
         * Verifies that a {@link Date} can be formatted in the US style.
         */
        @CaptureLogEntries(configuration = {"locale=en_US", "zone=UTC"})
        @Test
        void formatWithUsLocale() {
            ZonedDateTime zonedDateTime = LocalDateTime.parse("2020-12-31T11:55").atZone(ZoneOffset.UTC);
            Date date = Date.from(zonedDateTime.toInstant());

            String output = new DateFormat().format(framework, "MMMM d, yyyy, HH:mm", date);
            assertThat(output).isEqualTo("December 31, 2020, 11:55");
        }

        /**
         * Verifies that a {@link Date} can be formatted in the German style.
         */
        @CaptureLogEntries(configuration = {"locale=de_DE", "zone=UTC"})
        @Test
        void formatWithGermanLocale() {
            ZonedDateTime zonedDateTime = LocalDateTime.parse("2020-12-31T11:55").atZone(ZoneOffset.UTC);
            Date date = Date.from(zonedDateTime.toInstant());

            String output = new DateFormat().format(framework, "dd. MMMM yyyy, HH:mm", date);
            assertThat(output).isEqualTo("31. Dezember 2020, 11:55");
        }

    }

    /**
     * Tests for different time zones.
     */
    @Nested
    class TimeZones {

        @Inject
        private Framework framework;

        /**
         * Verifies that dates are correctly output for the time zone {@code GMT-1}.
         */
        @CaptureLogEntries(configuration = {"locale=en_US", "zone=GMT-1"})
        @Test
        void gmtMinus1() {
            LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
            Date date = Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());
            String output = new DateFormat().format(framework, "yyyy-MM-dd HH:mm", date);
            assertThat(output).isEqualTo("2019-12-31 23:00");
        }

        /**
         * Verifies that dates are correctly output for the time zone {@code GMT}.
         */
        @CaptureLogEntries(configuration = {"locale=en_US", "zone=GMT"})
        @Test
        void gmt() {
            LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
            Date date = Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());
            String output = new DateFormat().format(framework, "yyyy-MM-dd HH:mm", date);
            assertThat(output).isEqualTo("2020-01-01 00:00");
        }

        /**
         * Verifies that dates are correctly output for the time zone {@code GMT+1}.
         */
        @CaptureLogEntries(configuration = {"locale=en_US", "zone=GMT+1"})
        @Test
        void gmtPlus1() {
            LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
            Date date = Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());
            String output = new DateFormat().format(framework, "yyyy-MM-dd HH:mm", date);
            assertThat(output).isEqualTo("2020-01-01 01:00");
        }

    }

}
