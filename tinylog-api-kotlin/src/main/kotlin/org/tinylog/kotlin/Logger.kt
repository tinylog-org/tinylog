package org.tinylog.kotlin

import org.tinylog.core.Tinylog
import org.tinylog.core.format.message.EnhancedMessageFormatter
import java.util.concurrent.ConcurrentHashMap

/**
 * Static logger for issuing log entries.
 */
object Logger : AbstractLogger(
    null,
    Tinylog.getFramework(),
    EnhancedMessageFormatter(Tinylog.getFramework().getClassLoader(), Tinylog.getFramework().internalLogger),
) {
    private val taggedLoggers = ConcurrentHashMap<String, TaggedLogger>()

    /**
     * Retrieves a tagged logger instance. Category tags are case-sensitive. If a tagged logger does not yet exist for
     * the passed tag, a new logger will be created. This method always returns the same logger instance for the same
     * tag.
     *
     * @param tag The case-sensitive category tag of the requested logger, or `null` for receiving an untagged
     *            logger
     * @return Logger instance
     */
    fun tag(tag: String?): TaggedLogger =
        if (tag.isNullOrEmpty()) {
            taggedLoggers.computeIfAbsent("") {
                TaggedLogger(null, framework, formatter)
            }
        } else {
            taggedLoggers.computeIfAbsent(tag) {
                TaggedLogger(it, framework, formatter)
            }
        }
}
