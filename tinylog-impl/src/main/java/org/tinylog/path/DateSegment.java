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

import java.util.Locale;

import org.tinylog.configuration.Configuration;
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.runtime.Timestamp;
import org.tinylog.runtime.TimestampFormatter;

/**
 * Path segment that represents a timestamp.
 */
final class DateSegment implements Segment {

	private static final Locale locale = Configuration.getLocale();

	private final TimestampFormatter formatter;

	/**
	 * @param format
	 *            Pattern for formatting timestamp
	 */
	DateSegment(final String format) {
		formatter = RuntimeProvider.createTimestampFormatter(format, locale);
	}

	@Override
	public String getStaticText() {
		return null;
	}

	@Override
	public boolean validateToken(final String token) {
		return formatter.isValid(token);
	}

	@Override
	public String createToken(final String prefix, final Timestamp timestamp) {
		return formatter.format(timestamp);
	}

}
