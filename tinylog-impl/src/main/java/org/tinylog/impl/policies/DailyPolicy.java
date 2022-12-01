package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Policy that triggers a rollover event once per day.
 */
public class DailyPolicy extends AbstractDatePolicy {

    private final LocalTime time;

    /**
     * @param clock The clock for receiving the current date, time, and zone
     * @param zone The time zone to sue for date and time
     * @param time The time on which a rollover event should be triggered every day
     */
    public DailyPolicy(Clock clock, ZoneId zone, LocalTime time) {
        super(clock, zone);
        this.time = time;
    }

    @Override
    protected ZonedDateTime getMinDate(ZonedDateTime now) {
        return now.toLocalTime().isBefore(time) ? now.minusDays(1).with(time) : now.with(time);
    }

    @Override
    protected ZonedDateTime getMaxDate(ZonedDateTime now) {
        return now.toLocalTime().isBefore(time) ? now.with(time) : now.plusDays(1).with(time);
    }

}
