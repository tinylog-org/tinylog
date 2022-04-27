package org.tinylog.impl.writers.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.writers.AsyncWriter;

/**
 * Asynchronous writer for inserting log entries into a relational database table.
 */
public class JdbcWriter implements AsyncWriter {

	private static final Pattern NO_CONTROLS_PATTERN = Pattern.compile("[^\\p{Cntrl}]+");
	private static final Pattern SAFE_IDENTIFIER_PATTERN = Pattern.compile("[A-Za-z0-9_@$#]+");

	private static final int MAX_COUNT = 1024;

	private final List<Placeholder> placeholders;
	private final Connection connection;
	private final String sql;
	private final PreparedStatement statement;

	private int count;

	/**
	 * @param url The JDBC URL or JNDI name of the relational database
	 * @param user The optional username if login is required
	 * @param password The optional password if login is required
	 * @param schema The optional database schema of the table
	 * @param table The name of the database table
	 * @param fields The fields for the database table (column names mapped to value placeholders)
	 * @throws NamingException Failed to look up the data source by JNDI name
	 * @throws SQLException Failed to connect to the database or to prepare the insert statement
	 */
	public JdbcWriter(String url, String user, String password, String schema, String table,
			Map<String, Placeholder> fields) throws NamingException, SQLException {
		this.placeholders = new ArrayList<>(fields.values());
		this.connection = connect(url, user, password);
		this.sql = renderSql(connection, schema, table, fields.keySet());
		this.statement = connection.prepareStatement(sql);
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		Set<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
		for (Placeholder placeholder : placeholders) {
			values.addAll(placeholder.getRequiredLogEntryValues());
		}
		return values;
	}

	@Override
	public void log(LogEntry entry) throws SQLException {
		for (int i = 0; i < placeholders.size(); ++i) {
			Placeholder placeholder = placeholders.get(i);
			ValueType type = placeholder.getType();
			Object value = placeholder.getValue(entry);

			if (value == null) {
				setNullValue(i, type);
			} else {
				setRealValue(i, value, type);
			}
		}

		statement.addBatch();
		count += 1;

		if (count >= MAX_COUNT) {
			flush();
		}
	}

	@Override
	public void flush() throws SQLException {
		if (count > 0) {
			try {
				statement.executeBatch();
			} finally {
				count = 0;
				statement.clearBatch();
			}
		}
	}

	@Override
	public void close() throws SQLException {
		if (connection != null) {
			try {
				flush();
			} finally {
				connection.close();
			}
		}
	}

	/**
	 * Establishes a connection to the database.
	 *
	 * @param url The JDBC URL or JNDI name of the relational database
	 * @param user The optional username if login is required
	 * @param password The optional password if login is required
	 * @return The established connection to the database.
	 * @throws NamingException Failed to look up the data source by JNDI name
	 * @throws SQLException Failed to connect to the database
	 */
	private static Connection connect(String url, String user, String password) throws NamingException, SQLException {
		if (url.toLowerCase(Locale.ENGLISH).startsWith("java:")) {
			DataSource source = InitialContext.doLookup(url);
			if (user == null) {
				return source.getConnection();
			} else {
				return source.getConnection(user, password);
			}
		} else {
			if (user == null) {
				return DriverManager.getConnection(url);
			} else {
				return DriverManager.getConnection(url, user, password);
			}
		}
	}

	/**
	 * Renders the SQL insert statement.
	 *
	 * @param connection The current database connection
	 * @param schema The optional database schema of the table
	 * @param table The name of the database table
	 * @param fields The column names of the table
	 * @return The SQL insert statement
	 * @throws SQLException Failed to get the metadata of the database or the table or any of the fields contain an
	 *                      unsupported character
	 */
	private static String renderSql(Connection connection, String schema, String table, Collection<String> fields)
			throws SQLException {
		String quote = connection.getMetaData().getIdentifierQuoteString().trim();
		StringBuilder builder = new StringBuilder();

		builder.append("INSERT INTO ");
		if (schema != null) {
			append(builder, schema, quote);
			builder.append(".");
		}
		append(builder, table, quote);
		builder.append(" (");

		boolean first = true;
		for (String fieldName : fields) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}

			append(builder, fieldName, quote);
		}

		builder.append(") VALUES (");

		for (int i = 0; i < fields.size(); ++i) {
			if (i == 0) {
				builder.append("?");
			} else {
				builder.append(", ?");
			}
		}

		builder.append(")");

		return builder.toString();
	}

	/**
	 * Appends a quoted identifier to a {@link StringBuilder}.
	 *
	 * @param builder The target string builder
	 * @param identifier The identifier to append
	 * @param quote The quote character (can be an empty string if the database does not support quotes)
	 * @throws SQLException The passed identifier contains unsupported characters
	 */
	private static void append(StringBuilder builder, String identifier, String quote) throws SQLException {
		if (quote.isEmpty()) {
			if (SAFE_IDENTIFIER_PATTERN.matcher(identifier).matches()) {
				builder.append(identifier);
			} else {
				throw new SQLException("Illegal identifier: " + identifier);
			}
		} else {
			if (NO_CONTROLS_PATTERN.matcher(identifier).matches()) {
				String escapedIdentifier = identifier.replace(quote, quote + quote);
				builder.append(quote).append(escapedIdentifier).append(quote);
			} else {
				throw new SQLException("Illegal identifier: " + identifier);
			}
		}
	}

	/**
	 * Sets {@code null} as parameter value at the passed parameter index in the current {@link PreparedStatement}.
	 *
	 * @param parameterIndex The parameter index (starts with 0)
	 * @param type The value type of the parameter
	 * @throws SQLException Failed to set the parameter
	 * @throws IllegalArgumentException The passed value type is not supported
	 */
	private void setNullValue(int parameterIndex, ValueType type) throws SQLException {
		switch (type) {
			case INTEGER:
				statement.setNull(parameterIndex + 1, Types.INTEGER);
				break;
			case LONG:
				statement.setNull(parameterIndex + 1, Types.BIGINT);
				break;
			case DECIMAL:
				statement.setNull(parameterIndex + 1, Types.DECIMAL);
				break;
			case TIMESTAMP:
				statement.setNull(parameterIndex + 1, Types.TIMESTAMP);
				break;
			case STRING:
				statement.setNull(parameterIndex + 1, Types.VARCHAR);
				break;
			default:
				throw new IllegalArgumentException("Invalid value type: " + type);
		}
	}

	/**
	 * Sets a non-null parameter value at the passed parameter index in the current {@link PreparedStatement}.
	 *
	 * @param parameterIndex The parameter index (starts with 0)
	 * @param value The parameter value (must be not {@code null})
	 * @param type The value type of the parameter
	 * @throws SQLException Failed to set the parameter
	 * @throws IllegalArgumentException The passed value type is not supported
	 */
	private void setRealValue(int parameterIndex, Object value, ValueType type) throws SQLException {
		switch (type) {
			case INTEGER:
				statement.setInt(parameterIndex + 1, (Integer) value);
				break;
			case LONG:
				statement.setLong(parameterIndex + 1, (Long) value);
				break;
			case DECIMAL:
				statement.setBigDecimal(parameterIndex + 1, (BigDecimal) value);
				break;
			case TIMESTAMP:
				statement.setTimestamp(parameterIndex + 1, (Timestamp) value);
				break;
			case STRING:
				statement.setString(parameterIndex + 1, (String) value);
				break;
			default:
				throw new IllegalArgumentException("Invalid value type: " + type);
		}
	}

}
