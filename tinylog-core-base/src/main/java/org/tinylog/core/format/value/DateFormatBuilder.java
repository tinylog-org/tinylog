package org.tinylog.core.format.value;

import java.util.Locale;

/**
 * Builder for creating {@link DateFormat DateFormats}.
 */
public class DateFormatBuilder implements ValueFormatBuilder {

	/** */
	public DateFormatBuilder() {
	}

	@Override
	public DateFormat create(Locale locale) {
		return new DateFormat(locale);
	}

}
