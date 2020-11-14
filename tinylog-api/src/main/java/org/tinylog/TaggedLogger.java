package org.tinylog;

import org.tinylog.core.Framework;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Logger for issuing tagged log entries.
 */
public final class TaggedLogger {

	private final String tag;
	private final RuntimeFlavor runtime;
	private final LoggingBackend backend;
	private final LevelVisibility visibility;

	/**
	 * @param tag The case-sensitive category tag for the logger (can be {@code null})
	 */
	TaggedLogger(String tag) {
		this(tag, Tinylog.getFramework());
	}

	/**
	 * @param tag The case-sensitive category tag for the logger (can be {@code null})
	 * @param framework The actual framework instance
	 */
	TaggedLogger(String tag, Framework framework) {
		this.tag = tag;
		this.runtime = framework.getRuntime();
		this.backend = framework.getLoggingBackend();
		this.visibility = backend.getLevelVisibility(tag);
	}

	/**
	 * Gets the assigned case-sensitive category tag.
	 *
	 * @return The assigned category tag, or {@code null} if the logger is untagged
	 */
	public String getTag() {
		return tag;
	}

}
