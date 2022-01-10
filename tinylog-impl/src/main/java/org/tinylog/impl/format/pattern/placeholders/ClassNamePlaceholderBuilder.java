package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating an instance of {@link ClassNamePlaceholder}.
 */
public class ClassNamePlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public ClassNamePlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "class-name";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for class name placeholder: \"{}\"",
				value
			);
		}

		return new ClassNamePlaceholder();
	}

}
