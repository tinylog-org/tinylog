package org.tinylog.impl.format;

import org.tinylog.impl.LogEntry;

/**
 * Placeholder implementation for resolving the fully-qualified class name for a log entry.
 */
public class ClassPlaceholder implements Placeholder {

	/** */
	public ClassPlaceholder() {
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String className = entry.getClassName();
		builder.append(className == null ? "<unknown>" : className);
	}

}
