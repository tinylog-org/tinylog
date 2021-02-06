package org.tinylog.impl.format;

import org.tinylog.core.Framework;

/**
 * Builder for creating {@link TagPlaceholder TagPlaceholders}.
 */
public class TagPlaceholderBuilder implements PlaceholderBuilder {

	/** */
	public TagPlaceholderBuilder() {
	}

	@Override
	public String getName() {
		return "tag";
	}

	@Override
	public Placeholder create(Framework framework, String value) {
		if (value == null) {
			return new TagPlaceholder("<untagged>", null);
		} else {
			return new TagPlaceholder(value, value);
		}
	}

}
