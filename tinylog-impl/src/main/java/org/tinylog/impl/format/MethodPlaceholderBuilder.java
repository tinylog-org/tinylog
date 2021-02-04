package org.tinylog.impl.format;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link MethodPlaceholder MethodPlaceholders}.
 */
public class MethodPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public MethodPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "method";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for method placeholder: \"{}\"",
				value
			);
		}

		return new MethodPlaceholder();
	}

}
