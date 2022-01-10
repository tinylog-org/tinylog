package org.tinylog.impl.format.pattern;

import java.sql.Types;

/**
 * SQL record represents a typed value to insert or update into an SQL table.
 *
 * @param <T> The Java type of the value to store
 */
public class SqlRecord<T> {

	private final int type;
	private final T value;

	/**
	 * @param type The SQL type from {@link Types}
	 * @param value The value to store
	 */
	public SqlRecord(int type, T value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Gets the SQL type for the stored value.
	 *
	 * @return SQL type from {@link Types}
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the nullable value to store.
	 *
	 * @return The value to store
	 */
	public T getValue() {
		return value;
	}

}
