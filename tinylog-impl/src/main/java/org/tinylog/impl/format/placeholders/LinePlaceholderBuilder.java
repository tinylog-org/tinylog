package org.tinylog.impl.format.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating an instance of {@link LinePlaceholder}.
 */
public class LinePlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public LinePlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "line";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(null, "Unexpected configuration value for line placeholder: \"{}\"", value);
		}

		return new LinePlaceholder();
	}

}
