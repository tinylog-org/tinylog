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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Locale;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * Thread-safe formatter that based on {@link DateTimeFormatter} with nanosecond precision. The last formatted timestamp
 * will be cached, if the formatter does neither output nanoseconds nor microseconds.
 */
@IgnoreJRERequirement
public final class PreciseTimestampFormatter implements TimestampFormatter {

	private final DateTimeFormatter formatter;
	private final TemporalUnit truncationUnit;

	private Instant minInstant;
	private Instant maxInstant;
	private String lastFormat;

	/**
	 * @param pattern
	 *            Format pattern that is compatible with {@link DateTimeFormatter}
	 * @param locale
	 *            Locale for formatting
	 */
	public PreciseTimestampFormatter(final String pattern, final Locale locale) {
		formatter = DateTimeFormatter.ofPattern(pattern, locale).withZone(ZoneId.systemDefault());

		if (pattern.contains("n") || pattern.contains("N") || pattern.contains("SSSS")) {
			truncationUnit = null;
		} else if (pattern.contains("S")) {
			truncationUnit = ChronoUnit.MILLIS;
		} else if (pattern.contains("s")) {
			truncationUnit = ChronoUnit.SECONDS;
		} else {
			truncationUnit = ChronoUnit.MINUTES;
		}

		minInstant = Instant.MAX;
		maxInstant = Instant.MIN;
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
		Instant instant = timestamp.toInstant();
		return truncationUnit == null ? formatter.format(instant) : format(instant);
	}

	/**
	 * Formats an {@link Instant}.
	 *
	 * @param instant
	 *            Instant to format
	 * @return Formatted instant
	 */
	private String format(final Instant instant) {
		synchronized (formatter) {
			if (!instant.isBefore(maxInstant) || instant.isBefore(minInstant)) {
				minInstant = instant.truncatedTo(truncationUnit);
				maxInstant = minInstant.plus(1, truncationUnit);
				lastFormat = formatter.format(instant);
			}
			return lastFormat;
		}
	}

}
