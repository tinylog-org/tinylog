package org.tinylog.impl.path.segments;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.tinylog.core.Framework;

/**
 * Builder for creating an instance of {@link DateTimeSegment}.
 */
public class DateTimeSegmentBuilder implements PathSegmentBuilder {

	private static final String DEFAULT_PATTERN = "yyyy-MM-dd_HH-mm-ss";

	/** */
	public DateTimeSegmentBuilder() {
	}

	@Override
	public String getName() {
		return "date";
	}

	@Override
	public PathSegment create(Framework framework, String value) throws Exception {
		String pattern = value == null ? DEFAULT_PATTERN : value;
		Locale locale = framework.getConfiguration().getLocale();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
		return new DateTimeSegment(formatter);
	}

}
