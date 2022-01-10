package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating an instance of {@link ThreadPlaceholder}.
 */
public class ThreadPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public ThreadPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "thread";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for thread placeholder: \"{}\"",
				value
			);
		}

		return new ThreadPlaceholder();
	}

}
