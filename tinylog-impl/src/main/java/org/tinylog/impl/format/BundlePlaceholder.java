package org.tinylog.impl.format;

import java.util.List;

import org.tinylog.impl.LogEntry;

/**
 * Bundle of multiple child placeholders.
 *
 * <p>
 *     This bundle placeholder combines the render result of multiple child placeholders. All child placeholders are
 *     rendered in the order in which they have been passed.
 * </p>
 */
public class BundlePlaceholder implements Placeholder {

	private final List<Placeholder> placeholders;

	/**
	 * @param placeholders Child placeholders
	 */
	public BundlePlaceholder(List<Placeholder> placeholders) {
		this.placeholders = placeholders;
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		placeholders.forEach(placeholder -> placeholder.render(builder, entry));
	}

}
