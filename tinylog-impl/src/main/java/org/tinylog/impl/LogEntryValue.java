package org.tinylog.impl;

/**
 * All available log entry properties as enum.
 *
 * @see LogEntry
 */
public enum LogEntryValue {

	/**
	 * The date and time of issue.
	 *
	 * @see LogEntry#getTimestamp()
	 */
	TIMESTAMP,

	/**
	 * The source thread of issue.
	 *
	 * @see LogEntry#getThread()
	 */
	THREAD,

	/**
	 * The present thread context values.
	 *
	 * @see LogEntry#getContext()
	 */
	CONTEXT,

	/**
	 * The fully-qualified source class name.
	 *
	 * @see LogEntry#getClassName()
	 */
	CLASS,

	/**
	 * The source method name.
	 *
	 * @see LogEntry#getMethodName()
	 */
	METHOD,

	/**
	 * The source file name.
	 *
	 * @see LogEntry#getFileName()
	 */
	FILE,

	/**
	 * The line number in the source file.
	 *
	 * @see LogEntry#getLineNumber()
	 */
	LINE,

	/**
	 * The assigned tag.
	 *
	 * @see LogEntry#getTag()
	 */
	TAG,

	/**
	 * The severity level.
	 *
	 * @see LogEntry#getSeverityLevel()
	 */
	LEVEL,

	/**
	 * The human-readable logged text message.
	 *
	 * @see LogEntry#getMessage()
	 */
	MESSAGE,

	/**
	 * The logged throwable.
	 *
	 * @see LogEntry#getException()
	 */
	EXCEPTION

}
