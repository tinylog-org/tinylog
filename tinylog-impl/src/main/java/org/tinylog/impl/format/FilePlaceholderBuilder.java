package org.tinylog.impl.format;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link FilePlaceholder FilePlaceholders}.
 */
public class FilePlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public FilePlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "file";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(null, "Unexpected configuration value for file placeholder: \"{}\"", value);
		}

		return new FilePlaceholder();
	}

}
