package org.tinylog.core.format.value;

import java.time.ZoneId;
import java.util.Locale;

/**
 * Builder for creating an instance of {@link JavaTimeFormat}.
 */
public class JavaTimeFormatBuilder implements ValueFormatBuilder {

	/** */
	public JavaTimeFormatBuilder() {
	}

	@Override
	public JavaTimeFormat create(Locale locale) {
		return new JavaTimeFormat(locale, ZoneId.systemDefault());
	}

}
