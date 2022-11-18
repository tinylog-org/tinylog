package org.tinylog.core.format.value;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaTimeFormatTest {

    /**
     * Verifies that a {@link LocalTime} can be formatted.
     */
    @Test
    void localTimeValue() {
        JavaTimeFormat format = new JavaTimeFormat(Locale.US, ZoneOffset.UTC);
        LocalTime time = LocalTime.of(12, 30);
        assertThat(format.isSupported(time)).isTrue();
        assertThat(format.format("HH:mm", time)).isEqualTo("12:30");
    }

    /**
     * Verifies that a {@link LocalDate} can be formatted.
     */
    @Test
    void localDateValue() {
        JavaTimeFormat format = new JavaTimeFormat(Locale.GERMANY, ZoneOffset.UTC);
        LocalDate date = LocalDate.of(2020, 12, 31);
        assertThat(format.isSupported(date)).isTrue();
        assertThat(format.format("dd.MM.yyyy", date)).isEqualTo("31.12.2020");
    }

    /**
     * Verifies that a {@link LocalDateTime} can be formatted.
     */
    @Test
    void localDateTimeValue() {
        JavaTimeFormat format = new JavaTimeFormat(Locale.GERMANY, ZoneOffset.UTC);
        LocalDateTime dateTime = LocalDateTime.of(2020, 12, 31, 12, 30);
        assertThat(format.isSupported(dateTime)).isTrue();
        assertThat(format.format("dd.MM.yyyy, HH:mm", dateTime)).isEqualTo("31.12.2020, 12:30");
    }

    /**
     * Verifies that a {@link ZonedDateTime} can be formatted.
     */
    @Test
    void zonedDateTimeValue() {
        JavaTimeFormat format = new JavaTimeFormat(Locale.FRANCE, ZoneOffset.UTC);

        LocalDate date = LocalDate.of(2020, 12, 31);
        LocalTime time = LocalTime.of(12, 30);
        ZonedDateTime dateTime = ZonedDateTime.of(date, time, ZoneOffset.ofHours(2));

        assertThat(format.isSupported(dateTime)).isTrue();
        assertThat(format.format("yyyy-MM-dd HH:mm Z", dateTime)).isEqualTo("2020-12-31 12:30 +0200");
    }

    /**
     * Verifies that an {@link Instant} can be formatted.
     */
    @Test
    void instantValue() {
        JavaTimeFormat format = new JavaTimeFormat(Locale.US, ZoneOffset.UTC);
        Instant instant = Instant.ofEpochMilli(0);
        assertThat(format.isSupported(instant)).isTrue();
        assertThat(format.format("yyyy-MM-dd HH:mm", instant)).isEqualTo("1970-01-01 00:00");
    }

    /**
     * Verifies that strings are not supported.
     */
    @Test
    void stringValue() {
        assertThat(new JavaTimeFormat(Locale.US, ZoneOffset.UTC).isSupported("foo")).isFalse();
    }

}
