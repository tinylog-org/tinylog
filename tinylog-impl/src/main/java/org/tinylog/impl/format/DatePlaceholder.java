package org.tinylog.impl.format;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.tinylog.impl.LogEntry;

/**
 * Placeholder implementation for resolving the date and time of issue for a log entry.
 */
public class DatePlaceholder implements Placeholder {

	private final DateTimeFormatter formatter;

	/**
	 * @param formatter The formatter to use for formatting the date and time of issue
	 */
	public DatePlaceholder(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Instant instant = entry.getTimestamp();
		if (instant == null) {
			builder.append("<unknown>");
		} else {
			formatter.formatTo(instant, builder);
		}
	}

}
