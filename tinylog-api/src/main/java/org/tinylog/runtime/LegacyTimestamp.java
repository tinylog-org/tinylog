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
 * Legacy timestamp that based on a {@link Date} with millisecond precision.
 */
public final class LegacyTimestamp implements Timestamp {

	private static final long MILLISECOND_IN_NANOS = 1000000;

	private final Date date;

	/** */
	public LegacyTimestamp() {
		date = new Date();
	}

	/**
	 * @param milliseconds
	 *            Milliseconds since January 1, 1970, 00:00:00 GMT
	 */
	public LegacyTimestamp(final long milliseconds) {
		date = new Date(milliseconds);
	}

	@Override
	public Date toDate() {
		return date;
	}

	@IgnoreJRERequirement
	@Override
	public Instant toInstant() {
		return date.toInstant();
	}

	@Override
	public java.sql.Timestamp toSqlTimestamp() {
		return new java.sql.Timestamp(date.getTime());
	}

	@Override
	public long calcDifferenceInNanoseconds(final Timestamp other) {
		return (date.getTime() - other.toDate().getTime()) * MILLISECOND_IN_NANOS;
	}

}
