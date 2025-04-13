package org.tinylog.test.junit.log;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Clock implementation that uses {@link Clock#systemUTC()} by default, but can also be fixed to a specific instant.
 */
public class TestClock extends Clock {

    private Clock clock;

    /** */
    public TestClock() {
        this.clock = Clock.systemUTC();
    }

    @Override
    public ZoneId getZone() {
        return clock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return clock.withZone(zone);
    }

    @Override
    public Instant instant() {
        return clock.instant();
    }

    /**
     * Fixes the clock to a specific instant.
     *
     * @param fixedInstant The fixed instant to use as clock
     */
    public void fixTo(Instant fixedInstant) {
        clock = Clock.fixed(fixedInstant, clock.getZone());
    }

}
