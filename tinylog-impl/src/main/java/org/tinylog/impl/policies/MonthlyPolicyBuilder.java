package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.LocalTime;

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
	protected AbstractDatePolicy createPolicy(Clock clock, LocalTime time) {
		return new MonthlyPolicy(clock, time);
	}

}
