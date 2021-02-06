package org.tinylog.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.tinylog.core.Level;

/**
 * Immutable log entry record.
 */
public class LogEntry {

	private final Instant timestamp;
	private final Duration uptime;
	private final Thread thread;
	private final Map<String, String> context;
	private final String className;
	private final String methodName;
	private final String fileName;
	private final int lineNumber;
	private final String tag;
	private final Level severityLevel;
	private final String message;
	private final Throwable exception;

	/**
	 * @param timestamp Date and time of issue
	 * @param uptime Passed time since application start
	 * @param thread Source thread of issue
	 * @param context Present thread context values
	 * @param className Fully-qualified source class name
	 * @param methodName Source method name
	 * @param fileName Source file name
	 * @param lineNumber Line number in source file
	 * @param tag Assigned tag
	 * @param severityLevel Severity level
	 * @param message Human-readable logged text message
	 * @param exception Logged exception or any other kind of throwable
	 */
	public LogEntry(Instant timestamp, Duration uptime, Thread thread, Map<String, String> context, String className,
			String methodName, String fileName, int lineNumber, String tag, Level severityLevel, String message,
			Throwable exception) {
		this.timestamp = timestamp;
		this.uptime = uptime;
		this.thread = thread;
		this.context = context;
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.tag = tag;
		this.severityLevel = severityLevel;
		this.message = message;
		this.exception = exception;
	}

	/**
	 * Gets the date and time when this log entry was issued.
	 *
	 * <p>
	 *     This method can return {@code null}, if none of the active writers have defined
	 *     {@link LogEntryValue#TIMESTAMP} as required log entry value.
	 * </p>
	 *
	 * @return The date and time of issue
	 */
	public Instant getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the passed time since application start when this log entry was issued.
	 *
	 * <p>
	 *     This method can return {@code null}, if none of the active writers have defined {@link LogEntryValue#UPTIME}
	 *     as required log entry value.
	 * </p>
	 *
	 * @return The passed time since application start
	 */
	public Duration getUptime() {
		return uptime;
	}

	/**
	 * Gets the thread, in which this log entry has been issued.
	 *
	 * <p>
	 *     This method can return {@code null}, if none of the active writers have defined {@link LogEntryValue#THREAD}
	 *     as required log entry value.
	 * </p>
	 *
	 * @return The source thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * Gets the context values, which were set for the source thread, when issuing this log entry.
	 *
	 * <p>
	 *     This method can return an empty map, if there are no set context values or none of the active writers have
	 *     defined {@link LogEntryValue#CONTEXT} as required log entry value.
	 * </p>
	 *
	 * @return The present thread context values
	 */
	public Map<String, String> getContext() {
		return context;
	}

	/**
	 * Gets the fully-qualified name of the class, in which this log entry has been issued.
	 *
	 * <p>
	 *     This method can return {@code null}, if the class name is unavailable or none of the active writers have
	 *     defined {@link LogEntryValue#CLASS} as required log entry value.
	 * </p>
	 *
	 * @return The fully-qualified source class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Gets the name of the method, in which this log entry has been issued.
	 *
	 * <p>
	 *     This method can return {@code null}, if the method name is unavailable or none of the active writers have
	 *     defined {@link LogEntryValue#METHOD} as required log entry value.
	 * </p>
	 *
	 * @return The source method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Gets the name of the file, in which this log entry has been issued.
	 *
	 * <p>
	 *     This method can return {@code null}, if the source file is unavailable or none of the active writers have
	 *     defined {@link LogEntryValue#FILE} as required log entry value.
	 * </p>
	 *
	 * @return The source file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Gets the line number in the source file, in which this log entry has been issued.
	 *
	 * <p>
	 *     This method can return {@code -1}, if the line number is unavailable or none of the active writers have
	 *     defined {@link LogEntryValue#LINE} as required log entry value.
	 * </p>
	 *
	 * @return The line number in the source file
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Gets the assigned tag.
	 *
	 * <p>
	 *     This method can return {@code null} for untagged log entries and if none of the active writers have defined
	 *     {@link LogEntryValue#TAG} as required log entry value.
	 * </p>
	 *
	 * @return The assigned tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Gets the {@link Level severity level} of this log entry.
	 *
	 * <p>
	 *     This method can return {@code null}, if none of the active writers have defined {@link LogEntryValue#LEVEL}
	 *     as required log entry value.
	 * </p>
	 *
	 * @return The severity level
	 */
	public Level getSeverityLevel() {
		return severityLevel;
	}

	/**
	 * Gets the human-readable logged text message.
	 *
	 * <p>
	 *     This method can return {@code null}, if no message has been logged or none of the active writers have defined
	 *     {@link LogEntryValue#MESSAGE} as required log entry value.
	 * </p>
	 *
	 * @return The human-readable logged text message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the logged throwable.
	 *
	 * <p>
	 *     This method can return {@code null}, if no throwable has been logged or none of the active writers have
	 *     defined {@link LogEntryValue#EXCEPTION} as required log entry value.
	 * </p>
	 *
	 * @return The logged throwable
	 */
	public Throwable getException() {
		return exception;
	}

}
