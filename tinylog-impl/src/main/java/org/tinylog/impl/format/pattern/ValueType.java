package org.tinylog.impl.format.pattern;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Supported value types for placeholders.
 *
 * @see Placeholder#getType()
 */
public enum ValueType {

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
