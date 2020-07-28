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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Locale;

/**
 * Thread-safe timestamp formatter implementation for {@link ModernTimestamp ModernTimestamps}.
 */
public final class ModernTimestampFormatter implements TimestampFormatter<Instant> {

	private final DateTimeFormatter formatter;
	private final TemporalUnit truncationUnit;

	private Instant lastInstant;
	private String lastFormat;

	/**
	 * @param pattern Date and time pattern, compatible with {@link DateTimeFormatter}
	 * @param locale Locale for language or country depending format outputs
	 */
	ModernTimestampFormatter(String pattern, Locale locale) {
		formatter = DateTimeFormatter.ofPattern(pattern, locale).withZone(ZoneId.systemDefault());

		if (pattern.contains("n") || pattern.contains("N") || pattern.contains("SSSSSSS")) {
			truncationUnit = ChronoUnit.NANOS;
		} else if (pattern.contains("SSSS")) {
			truncationUnit = ChronoUnit.MICROS;
		} else if (pattern.contains("S")) {
			truncationUnit = ChronoUnit.MILLIS;
		} else if (pattern.contains("s")) {
			truncationUnit = ChronoUnit.SECONDS;
		} else {
			truncationUnit = ChronoUnit.MINUTES;
		}
	}

	@Override
	public String format(Timestamp<Instant> timestamp) {
		Instant instant = timestamp.resole().truncatedTo(truncationUnit);

		synchronized (formatter) {
			if (!instant.equals(lastInstant)) {
				lastInstant = instant;
				lastFormat = formatter.format(instant);
			}
			return lastFormat;
		}
	}

}
