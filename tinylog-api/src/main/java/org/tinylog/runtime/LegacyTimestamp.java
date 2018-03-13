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
final class LegacyTimestamp implements Timestamp {

	private final Date date;

	/** */
	LegacyTimestamp() {
		date = new Date();
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

}
