package org.tinylog.core.test.log;

import java.util.function.Supplier;

import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.context.ContextStorage;
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
	 * @param visibleLevel The least severe visible severity level for {@link #getLevelVisibility(String)}
	 */
	CaptureLoggingBackend(Log log, Level visibleLevel) {
		this.log = log;
		this.visibleLevel = visibleLevel;
	}

	@Override
	public ContextStorage getContextStorage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public LevelVisibility getLevelVisibility(String tag) {
		return new LevelVisibility(
			Level.TRACE.isAtLeastAsSevereAs(visibleLevel),
			Level.DEBUG.isAtLeastAsSevereAs(visibleLevel),
			Level.INFO.isAtLeastAsSevereAs(visibleLevel),
			Level.WARN.isAtLeastAsSevereAs(visibleLevel),
			Level.ERROR.isAtLeastAsSevereAs(visibleLevel)
		);
	}

	@Override
	public boolean isEnabled(StackTraceLocation location, String tag, Level level) {
		return level.isAtLeastAsSevereAs(log.getLevel());
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		if (message instanceof Supplier<?>) {
			message = ((Supplier<?>) message).get();
		}

		String output;

		if (message == null) {
			output = null;
		} else if (arguments == null) {
			output = String.valueOf(message);
		} else {
			output = formatter.format(message.toString(), arguments);
		}

		log.add(new LogEntry(location.getCallerClassName(), tag, level, throwable, output));
	}

}
