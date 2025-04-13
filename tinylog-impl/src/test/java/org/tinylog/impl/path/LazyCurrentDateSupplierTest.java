package org.tinylog.impl.path;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class LazyCurrentDateSupplierTest {

    /**
     * Verifies that there are no interactions with the clock if the current date-time is never requested.
     */
    @Test
    void noCreationIfNotCalled() {
        Clock clock = mock(Clock.class);
        ZoneId zoneId = ZoneOffset.UTC;

        new LazyCurrentDateSupplier(clock, zoneId);

        verifyNoInteractions(clock);
    }

    /**
     * Verifies that the same date-time is always returned.
     */
    @Test
    void sameDateTime() {
        Clock clock = Clock.systemUTC();
        ZoneId zoneId = ZoneOffset.UTC;

        LazyCurrentDateSupplier supplier = new LazyCurrentDateSupplier(clock, zoneId);

        ZonedDateTime firstDateTime = supplier.get();
        assertThat(firstDateTime).isNotNull();

        ZonedDateTime secondDateTime = supplier.get();
        assertThat(secondDateTime).isSameAs(firstDateTime);
    }

    /**
     * Verifies that the passed time zone is used for the creation of zoned date-times.
     */
    @Test
    void respectTimeZone() {
        Clock clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        ZoneId zoneId = ZoneId.of("Europe/Berlin");

        LazyCurrentDateSupplier supplier = new LazyCurrentDateSupplier(clock, zoneId);
        assertThat(supplier.get()).isEqualTo(ZonedDateTime.ofInstant(Instant.EPOCH, zoneId));
    }

}
