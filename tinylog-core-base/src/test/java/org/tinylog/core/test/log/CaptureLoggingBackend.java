package org.tinylog.core.test.log;

import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Logging backend for storing all issued log entries in a passed {@link Log}.
 */
class CaptureLoggingBackend implements LoggingBackend {

	private final Log log;
	private final Level visibleLevel;

	/**
	 * @param log All issued log entries will be stored in this {@link Log}
	 * @param visibleLevel The minimum visible severity level for {@link #getLevelVisibility(String)}
	 */
	CaptureLoggingBackend(Log log, Level visibleLevel) {
		this.log = log;
		this.visibleLevel = visibleLevel;
	}

	@Override
	public LevelVisibility getLevelVisibility(String tag) {
		return new LevelVisibility(
			Level.TRACE.ordinal() <= visibleLevel.ordinal(),
			Level.DEBUG.ordinal() <= visibleLevel.ordinal(),
			Level.INFO.ordinal() <= visibleLevel.ordinal(),
			Level.WARN.ordinal() <= visibleLevel.ordinal(),
			Level.ERROR.ordinal() <= visibleLevel.ordinal()
		);
	}

	@Override
	public boolean isEnabled(StackTraceLocation location, String tag, Level level) {
		return level.ordinal() <= log.getMinLevel().ordinal();
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		String output = arguments == null ? String.valueOf(message) : formatter.format(message.toString(), arguments);
		log.add(new LogEntry(location.getCallerClassName(), tag, level, throwable, output));
	}

}
