package org.tinylog.core.test;

import java.util.Objects;

import org.tinylog.core.Level;

/**
 * Log Entry record.
 */
public final class LogEntry {

	private final String caller;
	private final String tag;
	private final Level level;
	private final Throwable throwable;
	private final String message;

	/**
	 * @param caller Caller that has issued this log entry
	 * @param tag Category tag
	 * @param level Severity level
	 * @param throwable Exception or any other kind of throwable
	 * @param message Human-readable text message
	 */
	public LogEntry(String caller, String tag, Level level, Throwable throwable, String message) {
		this.caller = caller;
		this.tag = tag;
		this.level = level;
		this.throwable = throwable;
		this.message = message;
	}

	/**
	 * Gets the stored caller that has issued this log entry.
	 *
	 * @return The stored caller
	 */
	public String getCaller() {
		return caller;
	}

	/**
	 * Gets the stored category tag.
	 *
	 * @return The stored category tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Gets the stored severity level.
	 *
	 * @return The stored severity level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Gets the stored throwable.
	 *
	 * @return The stored throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * Gets the stored human-readable text message.
	 *
	 * @return The stored text message
	 */
	public String getMessage() {
		return message;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof LogEntry) {
			LogEntry entry = (LogEntry) other;
			return Objects.equals(caller, entry.caller)
				&& Objects.equals(tag, entry.tag)
				&& level == entry.level
				&& Objects.equals(throwable, entry.throwable)
				&& Objects.equals(message, entry.message);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(tag, level, message);
	}

	@Override
	public String toString() {
		String throwableOutput;
		if (throwable == null) {
			throwableOutput = null;
		} else {
			throwableOutput = throwable.getClass().getName();
			if (throwable.getMessage() != null) {
				throwableOutput += "(" + throwable.getMessage() + ")";
			}
		}

		return "LogEntry("
			+ "caller=\"" + caller + "\", "
			+ "tag=\"" + tag + "\", "
			+ "level=" + level + ", "
			+ "throwable=" + throwableOutput + ", "
			+ "message=\"" + message + "\""
			+ ")";
	}

}
