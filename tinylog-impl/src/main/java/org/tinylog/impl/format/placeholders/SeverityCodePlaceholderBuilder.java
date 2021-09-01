package org.tinylog.impl.format.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating an instance of {@link SeverityCodePlaceholder}.
 */
public class SeverityCodePlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public SeverityCodePlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "severity-code";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for severity code placeholder: \"{}\"",
				value
			);
		}

		return new SeverityCodePlaceholder();
	}

}
