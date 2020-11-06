package org.tinylog.core.backend;

import org.tinylog.core.Level;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * A no operation implementation of {@link LoggingBackend}. All issued log entries are silently ignored.
 */
public class NopLoggingBackend implements LoggingBackend {

	/** */
	public NopLoggingBackend() {
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		// Ignore
	}

}
