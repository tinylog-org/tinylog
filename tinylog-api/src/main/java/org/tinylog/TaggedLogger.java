package org.tinylog;

import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Logger for issuing tagged log entries.
 */
public final class TaggedLogger {

	private static final int CALLER_STACK_TRACE_DEPTH = 2;

	private final String tag;
	private final RuntimeFlavor runtime;
	private final LoggingBackend backend;
	private final LevelVisibility visibility;

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

	/**
	 * Checks if the trace severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued trace log entry will be output. If this method returns
	 *     {@code false}, issued trace log entries will be discarded. 
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isTraceEnabled() {
		return visibility.isTraceEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.TRACE);
	}

	/**
	 * Checks if the debug severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued debug log entry will be output. If this method returns
	 *     {@code false}, issued debug log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isDebugEnabled() {
		return visibility.isDebugEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.DEBUG);
	}

	/**
	 * Checks if the info severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued info log entry will be output. If this method returns
	 *     {@code false}, issued info log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isInfoEnabled() {
		return visibility.isInfoEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.INFO);
	}

	/**
	 * Checks if the warn severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued warn log entry will be output. If this method returns
	 *     {@code false}, issued warn log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isWarnEnabled() {
		return visibility.isWarnEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.WARN);
	}

	/**
	 * Checks if the error severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued error log entry will be output. If this method returns
	 *     {@code false}, issued error log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isErrorEnabled() {
		return visibility.isErrorEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.ERROR);
	}

}
