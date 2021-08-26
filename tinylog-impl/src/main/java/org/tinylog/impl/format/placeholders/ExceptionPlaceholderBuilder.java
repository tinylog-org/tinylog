package org.tinylog.impl.format.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link ExceptionPlaceholder ExceptionPlaceholders}.
 */
public class ExceptionPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public ExceptionPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "exception";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for exception placeholder: \"{}\"",
				value
			);
		}

		return new ExceptionPlaceholder();
	}

}
