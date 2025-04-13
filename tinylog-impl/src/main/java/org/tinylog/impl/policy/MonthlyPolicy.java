package org.tinylog.impl.policy;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Policy that triggers a rollover event once per month.
 */
public class MonthlyPolicy extends AbstractDatePolicy {

    private final LocalTime time;

    /**
     * @param clock The clock for receiving the current date-time
     * @param zone The time zone to use for date-time
     * @param time The time on which a rollover event should be triggered on the first day of every month
     */
    public MonthlyPolicy(Clock clock, ZoneId zone, LocalTime time) {
        super(clock, zone);
        this.time = time;
    }

    @Override
    protected ZonedDateTime getMinDate(ZonedDateTime now) {
        if (now.getDayOfMonth() == 1 && now.toLocalTime().isBefore(time)) {
            return now.minusMonths(1).with(time);
        } else {
            return now.withDayOfMonth(1).with(time);
        }
    }

    @Override
    protected ZonedDateTime getMaxDate(ZonedDateTime now) {
        if (now.getDayOfMonth() == 1 && now.toLocalTime().isBefore(time)) {
            return now.with(time);
        } else {
            return now.withDayOfMonth(1).plusMonths(1).with(time);
        }
    }

}
