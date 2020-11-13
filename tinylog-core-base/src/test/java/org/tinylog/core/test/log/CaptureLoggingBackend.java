package org.tinylog.core.test.log;

import org.tinylog.core.Level;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Logging backend for storing all issued log entries in a passed {@link Log}.
 */
class CaptureLoggingBackend implements LoggingBackend {

	private final Log log;

	/**
	 * @param log All issued log entries will be stored in this {@link Log}
	 */
	CaptureLoggingBackend(Log log) {
		this.log = log;
	}

	@Override
	public boolean isEnabled(StackTraceLocation location, String tag, Level level) {
		return true;
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		String output = arguments == null ? String.valueOf(message) : formatter.format(message.toString(), arguments);
		log.add(new LogEntry(location.getCallerClassName(), tag, level, throwable, output));
	}

}
