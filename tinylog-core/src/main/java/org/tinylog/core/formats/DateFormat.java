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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Format for {@link Date}.
 */
public final class DateFormat implements ValueFormat {

	private final Locale locale;

	/**
	 * @param locale Locale for language or country depending format outputs
	 */
	public DateFormat(Locale locale) {
		this.locale = locale;
	}

	@Override
	public boolean isSupported(final Object value) {
		return value instanceof Date;
	}

	@Override
	public String format(final String pattern, final Object value) {
		return new SimpleDateFormat(pattern, locale).format(value);
	}

}
