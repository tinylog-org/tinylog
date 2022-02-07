package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;

import org.tinylog.core.Framework;

/**
 * Abstract builder for creating instances of {@link AbstractDatePolicy}.
 */
public abstract class AbstractDatePolicyBuilder implements PolicyBuilder {

	/** */
	public AbstractDatePolicyBuilder() {
	}

	@Override
	public Policy create(Framework framework, String value) {
		Clock clock = Clock.systemDefaultZone();
		LocalTime time = LocalTime.MIDNIGHT;

		if (value != null) {
			try {
				int splitIndex = value.indexOf('@');
				if (splitIndex >= 0) {
					ZoneId zone = ZoneId.of(value.substring(splitIndex + 1));
					clock = Clock.system(zone);
					time = LocalTime.parse(value.substring(0, splitIndex));
				} else {
					time = LocalTime.parse(value);
				}
			} catch (DateTimeException ex) {
				throw new IllegalArgumentException(
					"Invalid configuration \"" + value + "\" for " + getName() + " policy",
					ex
				);
			}
		}

		return createPolicy(clock, time);
	}

	/**
	 * Creates a new instance of the date policy.
	 *
	 * @param clock The clock for receiving the current date, time, and zone
	 * @param time The time on which a rollover event should be triggered
	 * @return New instance of the date policy
	 */
	protected abstract AbstractDatePolicy createPolicy(Clock clock, LocalTime time);

}
