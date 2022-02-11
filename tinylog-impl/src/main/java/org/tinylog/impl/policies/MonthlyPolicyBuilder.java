package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import org.tinylog.core.Framework;

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
	public Policy create(Framework framework, String value) {
		Clock clock = framework.getClock();

		TemporalAccessor accessor = parse("HH:mm[ z]", value);
		LocalTime time = getOrDefault(accessor, TemporalQueries.localTime(), LocalTime.MIDNIGHT);
		ZoneId zone = getOrDefault(accessor, TemporalQueries.zone(), clock.getZone());

		return new MonthlyPolicy(clock.withZone(zone), time);
	}

}
