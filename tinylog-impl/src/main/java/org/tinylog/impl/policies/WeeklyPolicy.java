package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Policy that triggers a rollover event once per week.
 */
public class WeeklyPolicy extends AbstractDatePolicy {

    private static final int DAYS_PER_WEEK = 7;

    private final DayOfWeek day;
    private final LocalTime time;

    /**
     * @param clock The clock for receiving the current date, time, and zone
     * @param zone The time zone to sue for date and time
     * @param day The day on which a rollover event should be triggered every week
     * @param time The time on which a rollover event should be triggered every week
     */
    public WeeklyPolicy(Clock clock, ZoneId zone, DayOfWeek day, LocalTime time) {
        super(clock, zone);
        this.day = day;
        this.time = time;
    }

    @Override
    protected ZonedDateTime getMinDate(ZonedDateTime now) {
        DayOfWeek today = now.getDayOfWeek();
        if (today.getValue() == day.getValue() && now.toLocalTime().isBefore(time)) {
            return now.minusWeeks(1).with(time);
        } else {
            int days = (day.getValue() - today.getValue()) % DAYS_PER_WEEK;
            return now.minusDays(days).with(time);
        }
    }

    @Override
    protected ZonedDateTime getMaxDate(ZonedDateTime now) {
        DayOfWeek today = now.getDayOfWeek();
        if (today.getValue() == day.getValue() && now.toLocalTime().isBefore(time)) {
            return now.with(time);
        } else {
            int days = (day.getValue() - today.getValue()) % DAYS_PER_WEEK;
            if (days <= 0) {
                days += DAYS_PER_WEEK;
            }
            return now.plusDays(days).with(time);
        }
    }

}
