package org.tinylog.core.format.value;

import java.util.Locale;

/**
 * Builder for creating {@link NumberFormat NumberFormats}.
 */
public class NumberFormatBuilder implements ValueFormatBuilder {

	/** */
	public NumberFormatBuilder() {
	}

	@Override
	public NumberFormat create(Locale locale) {
		return new NumberFormat(locale);
	}

}
