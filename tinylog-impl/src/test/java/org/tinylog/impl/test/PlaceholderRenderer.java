package org.tinylog.impl.test;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Renderer for {@link Placeholder} implementations.
 */
public class PlaceholderRenderer {

	private final Placeholder placeholder;

	/**
	 * @param placeholder The placeholder to render
	 */
	public PlaceholderRenderer(Placeholder placeholder) {
		this.placeholder = placeholder;
	}

	/**
	 * Renders the stored placeholder with the passed log entry as input.
	 * 
	 * @param logEntry The log entry as input for {@link Placeholder#render(StringBuilder, LogEntry)}
	 * @return The render result
	 */
	public String render(LogEntry logEntry) {
		StringBuilder builder = new StringBuilder();
		placeholder.render(builder, logEntry);
		return builder.toString();
	}

}
