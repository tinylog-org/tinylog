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
	JavaTimeFormat(Locale locale, ZoneId defaultZone) {
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
