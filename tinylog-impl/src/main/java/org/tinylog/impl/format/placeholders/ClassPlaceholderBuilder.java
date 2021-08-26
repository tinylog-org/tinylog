package org.tinylog.impl.format.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link ClassPlaceholder ClassPlaceholders}.
 */
public class ClassPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public ClassPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "class";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(null, "Unexpected configuration value for class placeholder: \"{}\"", value);
		}

		return new ClassPlaceholder();
	}

}
