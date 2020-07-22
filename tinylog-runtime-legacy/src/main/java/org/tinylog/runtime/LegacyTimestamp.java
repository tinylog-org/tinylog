/*
 * Copyright 2020 Martin Winandy
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

import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Legacy timestamp based on {@link Date} with millisecond precision.
 */
public final class LegacyTimestamp implements Timestamp<Date> {

	private final Date date;

	/**
	 * @param date Date to store as timestamp
	 */
	LegacyTimestamp(Date date) {
		this.date = date;
	}

	@SuppressFBWarnings("EI_EXPOSE_REP")
	@Override
	public Date resole() {
		return date;
	}

}
