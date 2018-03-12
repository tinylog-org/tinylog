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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * Thread-safe formatter that based on {@link DateTimeFormatter} with nanosecond precision.
 */
@IgnoreJRERequirement
final class PreciseTimestampFormatter implements TimestampFormatter {

	private final DateTimeFormatter formatter;

	/**
	 * @param pattern
	 *            Format pattern that is compatible with {@link DateTimeFormatter}
	 * @param locale
	 *            Locale for formatting
	 */
	PreciseTimestampFormatter(final String pattern, final Locale locale) {
		formatter = DateTimeFormatter.ofPattern(pattern, locale).withZone(ZoneId.systemDefault());
	}

	@Override
	public boolean isPrecise() {
		return true;
	}

	@Override
	public boolean isValid(final String timestamp) {
		try {
			formatter.parse(timestamp);
			return true;
		} catch (DateTimeParseException ex) {
			return false;
		}
	}

	@Override
	public String format(final Timestamp timestamp) {
		return formatter.format(timestamp.toInstant());
	}

}
