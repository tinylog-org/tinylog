package org.tinylog.core.backend;

import java.util.Collections;
import java.util.List;

import org.tinylog.core.Level;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Wrapper for multiple {@link LoggingBackend LoggingBackends}.
 */
public class BundleLoggingBackend implements LoggingBackend {

	private static final LevelVisibility INVISIBLE = new LevelVisibility(false, false, false, false, false);

	private final List<LoggingBackend> backends;

	/**
	 * @param backends Logging backends to combine
	 */
	public BundleLoggingBackend(List<LoggingBackend> backends) {
		this.backends = backends;
	}

	/**
	 * Gets all wrapped child logging backends.
	 *
	 * @return The wrapped child logging backends
	 */
	public List<LoggingBackend> getChildren() {
		return Collections.unmodifiableList(backends);
	}

	@Override
	public LevelVisibility getLevelVisibility(String tag) {
		return backends.stream()
			.map(backend -> backend.getLevelVisibility(tag))
			.reduce((first, second) -> new LevelVisibility(
				first.isTraceEnabled() || second.isTraceEnabled(),
				first.isDebugEnabled() || second.isDebugEnabled(),
				first.isInfoEnabled() || second.isInfoEnabled(),
				first.isWarnEnabled() || second.isWarnEnabled(),
				first.isErrorEnabled() || second.isErrorEnabled()
			))
			.orElse(INVISIBLE);
	}

	@Override
	public boolean isEnabled(StackTraceLocation location, String tag, Level level) {
		StackTraceLocation childLocation = location.push();
		for (LoggingBackend backend : backends) {
			if (backend.isEnabled(childLocation, tag, level)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		StackTraceLocation childLocation = location.push();
		for (LoggingBackend backend : backends) {
			backend.log(childLocation, tag, level, throwable, message, arguments, formatter);
		}
	}

}
