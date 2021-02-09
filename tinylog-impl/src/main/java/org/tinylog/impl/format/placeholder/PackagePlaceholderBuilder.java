package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating {@link PackagePlaceholder PackagePlaceholders}.
 */
public class PackagePlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public PackagePlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "package";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value != null) {
			InternalLogger.warn(
				null,
				"Unexpected configuration value for package placeholder: \"{}\"",
				value
			);
		}

		return new PackagePlaceholder();
	}

}
