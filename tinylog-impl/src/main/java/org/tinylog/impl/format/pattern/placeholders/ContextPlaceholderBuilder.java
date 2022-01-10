package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.Framework;

/**
 * Builder for creating an instance of {@link ContextPlaceholder}.
 */
public class ContextPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public ContextPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "context";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value == null) {
			throw new IllegalArgumentException("Thread context key is not defined for context placeholder");
		} else {
			int commaIndex = value.indexOf(',');
			if (commaIndex < 0) {
				return new ContextPlaceholder(value, "<" + value + " not set>", null);
			} else {
				String key = value.substring(0, commaIndex);
				String defaultValue = value.substring(commaIndex + 1);
				return new ContextPlaceholder(key, defaultValue, defaultValue);
			}
		}
	}

}
