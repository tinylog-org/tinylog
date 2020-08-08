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

package org.tinylog.pattern;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.runtime.RuntimeProvider;

/**
 * Token for outputting the uptime.
 */
final class UptimeToken implements Token {

	private static final String DEFAULT_PATTERN = "HH:mm:ss";

	private static final long DECIMAL_BASE = 10;
	private static final long MAX_FRACTION_DIGITS = 9;

	private static final long MAX_SECOND = 60;
	private static final long MAX_MINUTE = 60;
	private static final long MAX_HOUR = 24;

	private static final long SECOND_IN_NANOS = 1000000000;
	private static final long MINUTE_IN_NANOS = MAX_SECOND * SECOND_IN_NANOS;
	private static final long HOUR_IN_NANOS = MAX_MINUTE * MINUTE_IN_NANOS;
	private static final long DAY_IN_NANOS = MAX_HOUR * HOUR_IN_NANOS;

	private final boolean formatted;
	private final List<Segment> segments;

	/**	*/
	UptimeToken() {
		this.formatted = false;
		this.segments = parse(DEFAULT_PATTERN);
	}

	/**
	 * @param pattern
	 *            Format pattern for formatting times
	 */
	UptimeToken(final String pattern) {
		this.formatted = true;
		this.segments = parse(pattern);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singletonList(LogEntryValue.DATE);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		long nanoseconds = logEntry.getTimestamp().calcDifferenceInNanoseconds(RuntimeProvider.getStartTime());
		format(builder, nanoseconds);
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		long nanoseconds = logEntry.getTimestamp().calcDifferenceInNanoseconds(RuntimeProvider.getStartTime());
		if (formatted) {
			StringBuilder builder = new StringBuilder();
			format(builder, nanoseconds);
			statement.setString(index, builder.toString());
		} else {
			statement.setLong(index, nanoseconds);
		}
	}

	/**
	 * Parses a format pattern.
	 *
	 * @param pattern
	 *            Pattern to parse
	 * @return Parsed segments that can be used for formatting
	 */
	private static List<Segment> parse(final String pattern) {
		List<Segment> segments = new ArrayList<Segment>();
		long maxDivisor = 1;

		for (int i = 0; i < pattern.length(); ++i) {
			char character = pattern.charAt(i);
			int length = count(pattern, i, character);

			switch (character) {
				case '\'':
					int end = pattern.indexOf('\'', i + 1);
					if (end == -1) {
						segments.add(new StringSegment("'"));
					} else if (end == i + 1) {
						segments.add(new StringSegment("'"));
						++i;
					} else {
						segments.add(new StringSegment(pattern.substring(i + 1, end)));
						i = end;
					}
					break;
				case 'S':
					long divisor = (long) Math.pow(DECIMAL_BASE, Math.max(0, MAX_FRACTION_DIGITS - length));
					long modulo = (long) Math.pow(DECIMAL_BASE, Math.min(MAX_FRACTION_DIGITS, length));
					segments.add(new TimeSegment(length, divisor, modulo));
					maxDivisor = Math.max(maxDivisor, divisor);
					i += length - 1;
					break;
				case 's':
					segments.add(new TimeSegment(length, SECOND_IN_NANOS, MAX_SECOND));
					maxDivisor = Math.max(maxDivisor, SECOND_IN_NANOS);
					i += length - 1;
					break;
				case 'm':
					segments.add(new TimeSegment(length, MINUTE_IN_NANOS, MAX_MINUTE));
					maxDivisor = Math.max(maxDivisor, MINUTE_IN_NANOS);
					i += length - 1;
					break;
				case 'H':
					segments.add(new TimeSegment(length, HOUR_IN_NANOS, MAX_HOUR));
					maxDivisor = Math.max(maxDivisor, HOUR_IN_NANOS);
					i += length - 1;
					break;
				case 'd':
					segments.add(new TimeSegment(length, DAY_IN_NANOS, 0));
					maxDivisor = Math.max(maxDivisor, DAY_IN_NANOS);
					i += length - 1;
					break;
				default:
					segments.add(new StringSegment(Character.toString(character)));
					break;
			}
		}

		for (int i = 0; i < segments.size(); ++i) {
			Segment segment = segments.get(i);
			if (segment instanceof TimeSegment) {
				TimeSegment timeSegment = (TimeSegment) segment;
				if (timeSegment.divisor == maxDivisor) {
					segments.set(i, new TimeSegment(timeSegment.digits, timeSegment.divisor, 0));
				}
			}
		}

		return segments;
	}

	/**
	 * Counts how many same characters appears in a row at a defined position in a format pattern.
	 *
	 * @param pattern
	 *            Format pattern
	 * @param start
	 *            Position in the formatting pattern from which to count consecutive characters
	 * @param character
	 *            Character to count
	 * @return Number of consecutive characters at the defined position
	 */
	private static int count(final String pattern, final int start, final char character) {
		int index = start;
		while (index < pattern.length() && pattern.charAt(index) == character) {
			++index;
		}
		return index - start;
	}

	/**
	 * Formats nanoseconds as duration using the segments generated by the defined format pattern.
	 *
	 * @param builder
	 *            Builder to append the formatted output to
	 * @param nanoseconds
	 *            Nanoseconds to format
	 */
	private void format(final StringBuilder builder, final long nanoseconds) {
		for (Segment segment : segments) {
			segment.render(builder, nanoseconds);
		}
	}

	/**
	 * Format segment.
	 */
	private interface Segment {

		/**
		 * Renders the segment.
		 *
		 * @param builder
		 *            String builder for appending the rendered output
		 * @param nanoseconds
		 *            Uptime in nanoseconds
		 */
		void render(StringBuilder builder, long nanoseconds);

	}

	/**
	 * Format segment with a static text.
	 */
	private static class StringSegment implements Segment {

		private final String text;

		/**
		 * @param text
		 *            Static text to store
		 */
		StringSegment(final String text) {
			this.text = text;
		}

		@Override
		public void render(final StringBuilder builder, final long nanoseconds) {
			builder.append(text);
		}

	}

	/**
	 * Time placeholder segment.
	 */
	private static class TimeSegment implements Segment {

		private final int digits;
		private final long divisor;
		private final long modulus;

		/**
		 * @param digits
		 *            Minimum number of digits to output
		 * @param divisor
		 *            Divisor to apply to the passed nanoseconds
		 * @param modulus
		 *            Modulus to apply to the division result (passed numbers <= 0 will disable the modulo operation)
		 */
		TimeSegment(final int digits, final long divisor, final long modulus) {
			this.digits = digits;
			this.divisor = divisor;
			this.modulus = modulus;
		}

		@Override
		public void render(final StringBuilder builder, final long nanoseconds) {
			long result = nanoseconds / divisor;
			if (modulus > 0) {
				result %= modulus;
			}

			String rendered = Long.toString(result);
			for (int i = 0; i < digits - rendered.length(); ++i) {
				builder.append('0');
			}
			builder.append(rendered);
		}

	}

}
