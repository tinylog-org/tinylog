package org.tinylog.core.test.log;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Clock that can be fixed to any time and zone for tests.
 */
public class TestClock extends Clock {

    private final Clock parent;
    private ZoneId zone;
    private Instant instant;

    /**
     * @param parent The parent clock to use if the time or zone are not fixed
     */
    public TestClock(Clock parent) {
        this.parent = parent;
    }

    @Override
    public ZoneId getZone() {
        return zone == null ? parent.getZone() : zone;
    }

    /**
     * Sets the time zone to a fixed value.
     *
     * @param zone The new time zone or {@code null} for resetting to default
     */
    public void setZone(ZoneId zone) {
        this.zone = zone;
    }

    @Override
    public Instant instant() {
        return instant == null ? parent.instant() : instant;
    }

    /**
     * Sets the current instant to a fixed value.
     *
     * @param instant The new current date-time or {@code null} for resetting to default
     */
    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        TestClock clock = new TestClock(this);
        clock.setZone(zone);
        return clock;
    }

}
