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

package org.tinylog.path;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.tinylog.core.ConfigurationParser;

/**
 * Path segment that represents a timestamp.
 */
final class DateSegment implements Segment {

	private static final Locale locale = ConfigurationParser.getLocale();

	private final DateFormat formatter;

	/**
	 * @param format
	 *            Pattern for formatting timestamp
	 */
	DateSegment(final String format) {
		formatter = new SimpleDateFormat(format, locale);
	}

	@Override
	public String getStaticText() {
		return null;
	}

	@Override
	public boolean validateToken(final String token) {
		try {
			formatter.parse(token);
			return true;
		} catch (ParseException ex) {
			return false;
		}
	}

	@Override
	public String createToken(final String prefix, final Date date) {
		return formatter.format(date);
	}

}
