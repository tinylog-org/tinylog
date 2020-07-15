/*
 * Copyright 2018 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.runtime;

import java.time.Instant;
import java.util.Date;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * Precise timestamp that based on an {@link Instant} with nanosecond precision.
 */
@IgnoreJRERequirement
public final class PreciseTimestamp implements Timestamp {

	private static final long SECOND_IN_MILLIS = 1000;
	private static final long MILLISECOND_IN_NANOS = 1000000;

	private final Instant instant;

	/** */
	public PreciseTimestamp() {
		instant = Instant.now();
	}

	/**
	 * @param milliseconds
	 *            Milliseconds since January 1, 1970, 00:00:00 GMT
	 * @param nanoseconds
	 *            Additional nanoseconds [0 .. 1,000,000]
	 */
	public PreciseTimestamp(final long milliseconds, final long nanoseconds) {
		long epochSecond = milliseconds / SECOND_IN_MILLIS;
		long nanoAdjustment = (milliseconds % SECOND_IN_MILLIS) * MILLISECOND_IN_NANOS + nanoseconds;
		instant = Instant.ofEpochSecond(epochSecond, nanoAdjustment);
	}

	@Override
	public Date toDate() {
		return Date.from(instant);
	}

	@Override
	public Instant toInstant() {
		return instant;
	}

	@Override
	public java.sql.Timestamp toSqlTimestamp() {
		return java.sql.Timestamp.from(instant);
	}

	@Override
	public long calcDifferenceInNanoseconds(final Timestamp other) {
		Instant otherInstant = other.toInstant();
		long result = this.instant.getEpochSecond() - otherInstant.getEpochSecond();
		result *= SECOND_IN_MILLIS * MILLISECOND_IN_NANOS;
		result -= otherInstant.getNano();
		result += this.instant.getNano();
		return result;
	}

}
