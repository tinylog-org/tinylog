package org.tinylog.impl.format.pattern;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Supported SQL types for SQL records of placeholders.
 *
 * @see SqlRecord
 * @see Placeholder#resolve(LogEntry)
 */
public enum SqlType {

	/**
	 * Value type is an {@code int} or {@link Integer}.
	 */
	INTEGER,

	/**
	 * Value type is a {@code long} or {@link Long}.
	 */
	LONG,

	/**
	 * Value type is a {@link BigDecimal}.
	 */
	DECIMAL,

	/**
	 * Value type is a {@link Timestamp}.
	 */
	TIMESTAMP,

	/**
	 * Value type is a {@link String}.
	 */
	STRING

}
