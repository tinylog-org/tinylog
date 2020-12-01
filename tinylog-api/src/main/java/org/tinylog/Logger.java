package org.tinylog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.tinylog.core.Framework;
import org.tinylog.core.Tinylog;

/**
 * Static logger for issuing log entries.
 */
public final class Logger {

	private static final int CALLER_STACK_TRACE_DEPTH = 2;

	private static final Framework framework;
	private static final ConcurrentMap<String, TaggedLogger> taggedLoggers;
	private static final TaggedLogger logger;
	
	static {
		framework = Tinylog.getFramework();
		taggedLoggers = new ConcurrentHashMap<>();
		logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH + 1, null, framework);
	}

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
			return taggedLoggers.computeIfAbsent(
				"",
				key -> new TaggedLogger(CALLER_STACK_TRACE_DEPTH, null, framework)
			);
		} else {
			return taggedLoggers.computeIfAbsent(
				tag,
				key -> new TaggedLogger(CALLER_STACK_TRACE_DEPTH, key, framework)
			);
		}
	}

	/**
	 * Checks if the trace severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued trace log entry will be output. If this method returns
	 *     {@code false}, issued trace log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	/**
	 * Checks if the debug severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued debug log entry will be output. If this method returns
	 *     {@code false}, issued debug log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
	 * Checks if the info severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued info log entry will be output. If this method returns
	 *     {@code false}, issued info log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	/**
	 * Checks if the warn severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued warning log entry will be output. If this method returns
	 *     {@code false}, issued warning log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	/**
	 * Checks if the error severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued error log entry will be output. If this method returns
	 *     {@code false}, issued error log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

}
