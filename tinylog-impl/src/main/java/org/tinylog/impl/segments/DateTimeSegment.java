package org.tinylog.impl.segments;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Path segment for the date-time.
 */
public class DateTimeSegment implements PathSegment {

	private final DateTimeFormatter formatter;

	/**
	 * @param formatter The formatter to use for formatting the date-time of the rollover event
	 */
	public DateTimeSegment(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public void resolve(StringBuilder pathBuilder, ZonedDateTime date) {
		formatter.formatTo(date, pathBuilder);
	}

}
