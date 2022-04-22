package org.tinylog.impl.format.pattern;

/**
 * SQL record represents a typed value to insert or update into an SQL table.
 *
 * @param <T> The Java type of the value to store
 */
public class SqlRecord<T> {

	private final SqlType type;
	private final T value;

	/**
	 * @param type The SQL type of the value
	 * @param value The value to store
	 */
	public SqlRecord(SqlType type, T value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Gets the SQL type for the stored value.
	 *
	 * @return The SQL type of the stored value
	 */
	public SqlType getType() {
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
