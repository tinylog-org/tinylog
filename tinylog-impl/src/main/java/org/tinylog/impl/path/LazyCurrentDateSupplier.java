package org.tinylog.impl.path;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

/**
 * Lazy provider for getting the same current zoned date-time on all calls.
 */
class LazyCurrentDateSupplier implements Supplier<ZonedDateTime> {

    private final Clock clock;
    private final ZoneId zone;

    private ZonedDateTime date;

    /**
     * @param clock The clock for receiving the current date-time
     * @param zone The time zone to use for the zoned date-time
     */
    LazyCurrentDateSupplier(Clock clock, ZoneId zone) {
        this.clock = clock;
        this.zone = zone;
    }

    @Override
    public ZonedDateTime get() {
        if (date == null) {
            date = ZonedDateTime.ofInstant(clock.instant(), zone);
        }

        return date;
    }

}
