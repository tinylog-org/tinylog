package org.tinylog.core.internal;

import org.tinylog.core.Level;

/**
 * Immutable log entry record for tinylog's {@link InternalLogger internal logger}.
 */
final class LogEntry {

	private final Level level;
	private final Throwable throwable;
	private final String message;
	private final Object[] arguments;

	/**
	 * @param level Severity level
	 * @param throwable Exception or any other kind of throwable
	 * @param message Human-readable text message with or without placeholders
	 * @param arguments Argument values for all placeholders in the text message
	 */
	LogEntry(Level level, Throwable throwable, String message, Object[] arguments) {
		this.level = level;
		this.throwable = throwable;
		this.message = message;
		this.arguments = arguments;
	}

	/**
	 * Gets the stored severity level.
	 *
	 * @return The severity level
	 */
	Level getLevel() {
		return level;
	}

	/**
	 * Gets the stored throwable.
	 *
	 * @return The throwable or {@code null} if none has been stored
	 */
	Throwable getThrowable() {
		return throwable;
	}

	/**
	 * Gets the stored human-readable text message.
	 *
	 * @return The text message
	 */
	String getMessage() {
		return message;
	}

	/**
	 * Gets the stored argument values for all placeholders in the text message.
	 *
	 * @return The argument values or {@code null} if none has been stored
	 */
	Object[] getArguments() {
		return arguments;
	}

}
