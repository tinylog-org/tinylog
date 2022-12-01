package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.WeekFields;

import org.tinylog.core.Framework;

/**
 * Builder for creating an instance of {@link WeeklyPolicy}.
 */
public class WeeklyPolicyBuilder extends AbstractDatePolicyBuilder {

    /** */
    public WeeklyPolicyBuilder() {
    }

    @Override
    public String getName() {
        return "weekly";
    }

    @Override
    public Policy create(Framework framework, String value) {
        Clock clock = framework.getClock();
        ZoneId zone = framework.getConfiguration().getZone();
        WeekFields weekFields = WeekFields.of(framework.getConfiguration().getLocale());

        TemporalAccessor accessor;
        try {
            accessor = parse("EEE[ H:mm]", value);
        } catch (IllegalArgumentException ex) {
            accessor = parse("EEEE[ H:mm]", value);
        }

        LocalTime time = getOrDefault(accessor, TemporalQueries.localTime(), LocalTime.MIDNIGHT);
        DayOfWeek day = getOrDefault(accessor, this::getDayOfWeek, weekFields.getFirstDayOfWeek());

        return new WeeklyPolicy(clock.withZone(zone), day, time);
    }

    /**
     * Gets the day of week from a {@link TemporalAccessor} without throwing an {@link
     * UnsupportedTemporalTypeException} if not present.
     *
     * @param accessor The temporal accessor that contains a day of week or not
     * @return The stored day of week if it is present, otherwise {@code null}
     */
    private DayOfWeek getDayOfWeek(TemporalAccessor accessor) {
        if (accessor.isSupported(ChronoField.DAY_OF_WEEK)) {
            return DayOfWeek.of(accessor.get(ChronoField.DAY_OF_WEEK));
        } else {
            return null;
        }
    }

}
