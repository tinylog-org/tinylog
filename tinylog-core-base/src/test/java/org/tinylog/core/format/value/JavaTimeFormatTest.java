package org.tinylog.core.format.value;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

class JavaTimeFormatTest {

    /**
     * Tests for all known supported {@code java.time} types.
     */
    @CaptureLogEntries(configuration = {"locale=en_US", "zone=UTC"})
    @Nested
    class JavaTimeTypes {

        @Inject
        private LoggingContext context;

        /**
         * Verifies that a {@link LocalTime} can be formatted.
         */
        @Test
        void localTimeValue() {
            JavaTimeFormat format = new JavaTimeFormat();
            LocalTime time = LocalTime.of(12, 30);
            assertThat(format.isSupported(time)).isTrue();
            assertThat(format.format(context, "HH:mm", time)).isEqualTo("12:30");
        }

        /**
         * Verifies that a {@link LocalDate} can be formatted.
         */
        @Test
        void localDateValue() {
            JavaTimeFormat format = new JavaTimeFormat();
            LocalDate date = LocalDate.of(2020, 12, 31);
            assertThat(format.isSupported(date)).isTrue();
            assertThat(format.format(context, "dd.MM.yyyy", date)).isEqualTo("31.12.2020");
        }

        /**
         * Verifies that a {@link LocalDateTime} can be formatted.
         */
        @Test
        void localDateTimeValue() {
            JavaTimeFormat format = new JavaTimeFormat();
            LocalDateTime dateTime = LocalDateTime.of(2020, 12, 31, 12, 30);
            assertThat(format.isSupported(dateTime)).isTrue();
            assertThat(format.format(context, "dd.MM.yyyy, HH:mm", dateTime)).isEqualTo("31.12.2020, 12:30");
        }

        /**
         * Verifies that a {@link ZonedDateTime} can be formatted.
         */
        @Test
        void zonedDateTimeValue() {
            JavaTimeFormat format = new JavaTimeFormat();

            LocalDate date = LocalDate.of(2020, 12, 31);
            LocalTime time = LocalTime.of(12, 30);
            ZonedDateTime dateTime = ZonedDateTime.of(date, time, ZoneOffset.ofHours(2));

            assertThat(format.isSupported(dateTime)).isTrue();
            assertThat(format.format(context, "yyyy-MM-dd HH:mm Z", dateTime)).isEqualTo("2020-12-31 12:30 +0200");
        }

        /**
         * Verifies that an {@link Instant} can be formatted.
         */
        @Test
        void instantValue() {
            JavaTimeFormat format = new JavaTimeFormat();
            Instant instant = Instant.ofEpochMilli(0);
            assertThat(format.isSupported(instant)).isTrue();
            assertThat(format.format(context, "yyyy-MM-dd HH:mm", instant)).isEqualTo("1970-01-01 00:00");
        }

        /**
         * Verifies that strings are not supported.
         */
        @Test
        void unsupportedStringValue() {
            JavaTimeFormat format = new JavaTimeFormat();
            assertThat(format.isSupported("foo")).isFalse();
        }

    }

    /**
     * Tests for different languages.
     */
    @Nested
    class Languages {

        @Inject
        private LoggingContext context;

        /**
         * Verifies that a date can be formatted in the British style.
         */
        @CaptureLogEntries(configuration = {"locale=en_GB", "zone=UTC"})
        @Test
        void britishFormat() {
            LocalDate date = LocalDate.of(2020, 12, 31);
            String output = new JavaTimeFormat().format(context, "MMMM d, yyyy", date);
            assertThat(output).isEqualTo("December 31, 2020");
        }

        /**
         * Verifies that a date can be formatted in the German style.
         */
        @CaptureLogEntries(configuration = {"locale=de_DE", "zone=UTC"})
        @Test
        void germanFormat() {
            LocalDate date = LocalDate.of(2020, 12, 31);
            String output = new JavaTimeFormat().format(context, "dd. MMMM yyyy", date);
            assertThat(output).isEqualTo("31. Dezember 2020");
        }

    }

    /**
     * Tests for different time zones.
     */
    @Nested
    class TimeZones {

        @Inject
        private LoggingContext context;

        /**
         * Verifies that instants are correctly output for the time zone {@code GMT-1}.
         */
        @CaptureLogEntries(configuration = {"locale=en_US", "zone=GMT-1"})
        @Test
        void gmtMinus1() {
            LocalDateTime date = LocalDateTime.of(2020, 1, 1, 0, 0);
            Instant instant = date.atZone(ZoneOffset.UTC).toInstant();
            String output = new JavaTimeFormat().format(context, "yyyy-MM-dd HH:mm", instant);
            assertThat(output).isEqualTo("2019-12-31 23:00");
        }

        /**
         * Verifies that instants are correctly output for the time zone {@code GMT}.
         */
        @CaptureLogEntries(configuration = {"locale=en_US", "zone=GMT"})
        @Test
        void gmt() {
            LocalDateTime date = LocalDateTime.of(2020, 1, 1, 0, 0);
            Instant instant = date.atZone(ZoneOffset.UTC).toInstant();
            String output = new JavaTimeFormat().format(context, "yyyy-MM-dd HH:mm", instant);
            assertThat(output).isEqualTo("2020-01-01 00:00");
        }

        /**
         * Verifies that instants are correctly output for the time zone {@code GMT+1}.
         */
        @CaptureLogEntries(configuration = {"locale=en_US", "zone=GMT+1"})
        @Test
        void gmtPlus1() {
            LocalDateTime date = LocalDateTime.of(2020, 1, 1, 0, 0);
            Instant instant = date.atZone(ZoneOffset.UTC).toInstant();
            String output = new JavaTimeFormat().format(context, "yyyy-MM-dd HH:mm", instant);
            assertThat(output).isEqualTo("2020-01-01 01:00");
        }

    }

}
