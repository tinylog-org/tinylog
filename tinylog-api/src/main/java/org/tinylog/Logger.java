package org.tinylog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Static logger for issuing log entries.
 */
public final class Logger {

	private static final ConcurrentMap<String, TaggedLogger> loggers = new ConcurrentHashMap<>();
	private static final TaggedLogger logger = new TaggedLogger(null);

	/** */
	private Logger() {
	}

	/**
	 * Retrieves a tagged logger instance. Category tags are case-sensitive. If a tagged logger does not yet exists for
	 * the passed tag, a new logger will be created. This method always returns the same logger instance for the same
	 * tag.
	 *
	 * @param tag The case-sensitive category tag of the requested logger, or {@code null} for receiving an untagged
	 *            logger
	 * @return Logger instance
	 */
	public static TaggedLogger tag(String tag) {
		if (tag == null || tag.isEmpty()) {
			return logger;
		} else {
			TaggedLogger logger = loggers.get(tag);
			if (logger == null) {
				logger = new TaggedLogger(tag);
				TaggedLogger existing = loggers.putIfAbsent(tag, logger);
				return existing == null ? logger : existing;
			} else {
				return logger;
			}
		}
	}

}
