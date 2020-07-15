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
 * Timestamp with the current date and time. The precision depends on the implementation but should be at least
 * millisecond precise.
 */
public interface Timestamp {

	/**
	 * Converts the timestamp to a {@link Date}.
	 *
	 * @return Timestamp as {@link Date}
	 */
	Date toDate();

	/**
	 * Converts the timestamp to an {@link Instant}.
	 *
	 * @return Timestamp as {@link Instant}
	 */
	@IgnoreJRERequirement
	Instant toInstant();

	/**
	 * Converts the timestamp to an {@link java.sql.Timestamp SQL Timestamp}.
	 *
	 * @return Timestamp as {@link java.sql.Timestamp SQL Timestamp}
	 */
	java.sql.Timestamp toSqlTimestamp();

	/**
	 * Gets the difference to the passed timestamps in nanoseconds.
	 *
	 * @param other
	 *            Timestamp to subtract
	 * @return Difference in nanoseconds
	 */
	long calcDifferenceInNanoseconds(Timestamp other);

}
