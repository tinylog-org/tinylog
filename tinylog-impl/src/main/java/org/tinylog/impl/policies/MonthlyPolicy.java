package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Policy that triggers a rollover event once per month.
 */
public class MonthlyPolicy extends AbstractDatePolicy {

	private final LocalTime time;

	/**
	 * @param clock The clock for receiving the current date, time, and zone
	 * @param time The time on which a rollover event should be triggered on the first day of every month
	 */
	public MonthlyPolicy(Clock clock, LocalTime time) {
		super(clock);
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
