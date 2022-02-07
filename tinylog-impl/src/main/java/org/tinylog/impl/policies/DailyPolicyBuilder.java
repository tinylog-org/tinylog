package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;

import org.tinylog.core.Framework;

/**
 * Builder for creating an instance of {@link DailyPolicy}.
 */
public class DailyPolicyBuilder implements PolicyBuilder {

	/** */
	public DailyPolicyBuilder() {
	}

	@Override
	public String getName() {
		return "daily";
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
				throw new IllegalArgumentException("Invalid configuration \"" + value + "\" for daily policy", ex);
			}
		}

		return new DailyPolicy(clock, time);
	}

}
