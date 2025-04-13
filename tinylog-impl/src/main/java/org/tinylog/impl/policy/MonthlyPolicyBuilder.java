package org.tinylog.impl.policy;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link MonthlyPolicy}.
 */
public class MonthlyPolicyBuilder extends AbstractDatePolicyBuilder {

    /** */
    public MonthlyPolicyBuilder() {
    }

    @Override
    public String getName() {
        return "monthly";
    }

    @Override
    public Policy create(TinylogContext context, String value) {
        Clock clock = context.getClock();
        ZoneId zone = context.getConfiguration().getZone();

        TemporalAccessor accessor = parse("H:mm", value);
        LocalTime time = getOrDefault(accessor, TemporalQueries.localTime(), LocalTime.MIDNIGHT);

        return new MonthlyPolicy(clock, zone, time);
    }

}
