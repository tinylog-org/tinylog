package org.tinylog.core.backend;

/**
 *  Output requirement details of all severity levels.
 */
public final class LevelVisibility {

	private final OutputDetails trace;
	private final OutputDetails debug;
	private final OutputDetails info;
	private final OutputDetails warn;
	private final OutputDetails error;

	/**
	 * @param trace Output requirement details for trace log entries
	 * @param debug Output requirement details for debug log entries
	 * @param info Output requirement details for info log entries
	 * @param warn Output requirement details for warn log entries
	 * @param error Output requirement details for error log entries
	 */
	public LevelVisibility(OutputDetails trace, OutputDetails debug, OutputDetails info, OutputDetails warn,
			OutputDetails error) {
		this.trace = trace;
		this.debug = debug;
		this.info = info;
		this.warn = warn;
		this.error = error;
	}

	/**
	 * Gets the output requirement details for trace log entries.
	 *
	 * @return Output requirement details for trace log entries
	 */
	public OutputDetails getTrace() {
		return trace;
	}

	/**
	 * Gets the output requirement details for debug log entries.
	 *
	 * @return Output requirement details for debug log entries
	 */
	public OutputDetails getDebug() {
		return debug;
	}

	/**
	 * Gets the output requirement details for info log entries.
	 *
	 * @return Output requirement details for info log entries
	 */
	public OutputDetails getInfo() {
		return info;
	}

	/**
	 * Gets the output requirement details for warn log entries.
	 *
	 * @return Output requirement details for warn log entries
	 */
	public OutputDetails getWarn() {
		return warn;
	}

	/**
	 * Gets the output requirement details for error log entries.
	 *
	 * @return Output requirement details for error log entries
	 */
	public OutputDetails getError() {
		return error;
	}

}
