package org.tinylog.core.backend;

/**
 *  Visibility of all severity levels.
 */
public final class LevelVisibility {

	private final boolean traceEnabled;
	private final boolean debugEnabled;
	private final boolean infoEnabled;
	private final boolean warnEnabled;
	private final boolean errorEnabled;

	/**
	 * @param traceEnabled {@code true} if trace log entries can be output, {@code false} if completely deactivated
	 * @param debugEnabled {@code true} if debug log entries can be output, {@code false} if completely deactivated
	 * @param infoEnabled {@code true} if info log entries can be output, {@code false} if completely deactivated
	 * @param warnEnabled {@code true} if warn log entries can be output, {@code false} if completely deactivated
	 * @param errorEnabled {@code true} if error log entries can be output, {@code false} if completely deactivated
	 */
	public LevelVisibility(boolean traceEnabled, boolean debugEnabled, boolean infoEnabled, boolean warnEnabled,
			boolean errorEnabled) {
		this.traceEnabled = traceEnabled;
		this.debugEnabled = debugEnabled;
		this.infoEnabled = infoEnabled;
		this.warnEnabled = warnEnabled;
		this.errorEnabled = errorEnabled;
	}

	/**
	 * Retrieves whether trace log entries can be output.
	 *
	 * @return {@code true} if trace log entries can be output, {@code false} if this severity level is completely
	 *         deactivated
	 */
	public boolean isTraceEnabled() {
		return traceEnabled;
	}

	/**
	 * Retrieves whether debug log entries can be output.
	 *
	 * @return {@code true} if debug log entries can be output, {@code false} if this severity level is completely
	 *         deactivated
	 */
	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	/**
	 * Retrieves whether info log entries can be output.
	 *
	 * @return {@code true} if info log entries can be output, {@code false} if this severity level is completely
	 *         deactivated
	 */
	public boolean isInfoEnabled() {
		return infoEnabled;
	}

	/**
	 * Retrieves whether warn log entries can be output.
	 *
	 * @return {@code true} if warn log entries can be output, {@code false} if this severity level is completely
	 *         deactivated
	 */
	public boolean isWarnEnabled() {
		return warnEnabled;
	}

	/**
	 * Retrieves whether error log entries can be output.
	 *
	 * @return {@code true} if error log entries can be output, {@code false} if this severity level is completely
	 *         deactivated
	 */
	public boolean isErrorEnabled() {
		return errorEnabled;
	}

}
