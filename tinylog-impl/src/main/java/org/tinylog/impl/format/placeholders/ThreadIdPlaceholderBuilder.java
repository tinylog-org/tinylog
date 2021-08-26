package org.tinylog.impl.format.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link ThreadIdPlaceholder ThreadIdPlaceholders}.
 */
public class ThreadIdPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public ThreadIdPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "thread-id";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for thread ID placeholder: \"{}\"",
				value
			);
		}

		return new ThreadIdPlaceholder();
	}

}
