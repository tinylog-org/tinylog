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

package org.tinylog.core.format.value;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Format for date and time classes from {@link java.time}.
 */
public class JavaTimeFormat implements ValueFormat {

	private final Locale locale;
	private final ZoneId defaultZone;

	/**
	 * @param locale Locale for language or country depending format outputs
	 * @param defaultZone Default zone for {@link Instant Instants}
	 */
	public JavaTimeFormat(Locale locale, ZoneId defaultZone) {
		this.locale = locale;
		this.defaultZone = defaultZone;
	}

	@Override
	public boolean isSupported(Object value) {
		return value instanceof TemporalAccessor;
	}

	@Override
	public String format(final String pattern, final Object value) {
		TemporalAccessor accessor = (TemporalAccessor) value;
		if (accessor instanceof Instant) {
			accessor = ZonedDateTime.ofInstant((Instant) accessor, defaultZone);
		}

		return DateTimeFormatter.ofPattern(pattern, locale).format(accessor);
	}

}
