package org.tinylog.impl.format;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link DatePlaceholder DatePlaceholders}.
 */
public class DatePlaceholderBuilder implements PlaceholderBuilder {

	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/** */
	public DatePlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "date";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		String pattern = value == null ? DEFAULT_PATTERN : value;
		Locale locale = framework.getConfiguration().getLocale();
		ZoneId zone = framework.getConfiguration().getZone();

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
			return new DatePlaceholder(formatter.withZone(zone), value != null);
		} catch (IllegalArgumentException ex) {
			InternalLogger.error(ex, "Invalid date time pattern: \"" + pattern + "\"");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN, locale);
			return new DatePlaceholder(formatter.withZone(zone), false);
		}
	}

}
