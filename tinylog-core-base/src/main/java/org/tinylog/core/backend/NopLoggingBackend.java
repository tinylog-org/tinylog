package org.tinylog.core.backend;

import org.tinylog.core.Level;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * A no operation implementation of {@link LoggingBackend}. All issued log entries are silently ignored.
 */
public class NopLoggingBackend implements LoggingBackend {

	private static final ContextStorage STORAGE = new NopContextStorage();

	private static final LevelVisibility VISIBILITY = new LevelVisibility(false, false, false, false, false);

	/** */
	public NopLoggingBackend() {
	}

	@Override
	public ContextStorage getContextStorage() {
		return STORAGE;
	}

	@Override
	public LevelVisibility getLevelVisibility(String tag) {
		return VISIBILITY;
	}

	@Override
	public boolean isEnabled(StackTraceLocation location, String tag, Level level) {
		return false;
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		// Ignore
	}

}