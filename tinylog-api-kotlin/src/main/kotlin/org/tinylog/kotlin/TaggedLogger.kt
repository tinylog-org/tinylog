package org.tinylog.kotlin

import org.tinylog.core.Framework
import org.tinylog.core.format.message.MessageFormatter

/**
 * Logger for issuing tagged log entries.
 *
 * @param tag The case-sensitive category tag of this logger
 * @param framework The underlying framework instance
 */
class TaggedLogger(
    val tag: String?,
    framework: Framework,
    formatter: MessageFormatter,
) : AbstractLogger(tag, framework, formatter)
