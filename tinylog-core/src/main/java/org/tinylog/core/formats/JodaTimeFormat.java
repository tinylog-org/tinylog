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

package org.tinylog.core.formats;

import java.util.Locale;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Format for date and time classes from {@link org.joda.time}.
 */
public final class JodaTimeFormat implements ValueFormat {

	private final Locale locale;
	private final DateTimeZone defaultZone;

	/**
	 * @param locale Locale for language or country depending format outputs
	 * @param defaultZone Default zone for {@link Instant Instants}
	 */
	public JodaTimeFormat(Locale locale, DateTimeZone defaultZone) {
		this.locale = locale;
		this.defaultZone = defaultZone;
	}

	@Override
	public boolean isSupported(Object value) {
		return value instanceof ReadableInstant || value instanceof ReadablePartial;
	}

	@Override
	public String format(final String pattern, final Object value) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern).withLocale(locale);

		if (value instanceof Instant) {
			return formatter.print(((Instant) value).toDateTime().withZone(defaultZone));
		} else if (value instanceof ReadableInstant) {
			return formatter.print((ReadableInstant) value);
		} else {
			return formatter.print((ReadablePartial) value);
		}
	}

}
