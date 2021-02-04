package org.tinylog.impl.format;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link MessageOnlyPlaceholder MessageOnlyPlaceholders}.
 */
public class MessageOnlyPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public MessageOnlyPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "message-only";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for message only placeholder: \"{}\"",
				value
			);
		}

		return new MessageOnlyPlaceholder();
	}

}
