package org.tinylog;

import org.tinylog.core.Configuration;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LoggingBackend;

/**
 * Logger for issuing tagged log entries.
 */
public final class TaggedLogger {

	private final String tag;
	private final Configuration configuration;
	private final LoggingBackend backend;

	/**
	 * @param tag The case-sensitive category tag for the logger (can be {@code null})
	 */
	TaggedLogger(String tag) {
		this(tag, Tinylog.getConfiguration(), Tinylog.getLoggingBackend());
	}

	/**
	 * @param tag The case-sensitive category tag for the logger (can be {@code null})
	 * @param configuration The configuration for the logger
	 * @param backend The logging backend for the logger
	 */
	TaggedLogger(String tag, Configuration configuration, LoggingBackend backend) {
		this.tag = tag;
		this.configuration = configuration;
		this.backend = backend;
	}

	/**
	 * Gets the assigned case-sensitive tag.
	 *
	 * @return The assigned tag or {@code null} if the logger is untagged
	 */
	public String getTag() {
		return tag;
	}

}
