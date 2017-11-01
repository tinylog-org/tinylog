/*
 * Copyright 2017 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.writers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.tinylog.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.pattern.FormatPatternParser;
import org.tinylog.pattern.Token;
import org.tinylog.provider.InternalLogger;

/**
 * Writer for inserting log entries into a SQL database table.
 */
public final class JdbcWriter implements Writer {

	private static final String FIELD_PREFIX = "field.";
	private static final long MAX_BATCH_SIZE = 100;
	private static final long MIN_RETRY_INTERVAL = 1000;

	private final String url;
	private final String user;
	private final String password;
	private final boolean reconnect;
	private final boolean batch;

	private final Object mutex;
	private final String sql;
	private final List<Token> tokens;

	private Connection connection;
	private PreparedStatement statement;
	private long batchCount;
	private long lostCount;
	private long reconnectTimestamp;

	/**
	 * @param properties
	 *            Configuration for writer
	 *
	 * @throws NamingException
	 *             Data source cannot be found
	 * @throws SQLException
	 *             Database connection cannot be established
	 */
	public JdbcWriter(final Map<String, String> properties) throws NamingException, SQLException {
		url = getUrl(properties);
		user = properties.get("user");
		password = properties.get("password");
		reconnect = Boolean.parseBoolean(properties.get("reconnect"));
		batch = Boolean.parseBoolean(properties.get("batch"));

		mutex = Boolean.parseBoolean(properties.get("writingthread")) ? null : new Object();

		connection = connect(url, user, password);
		sql = renderSql(properties, connection.getMetaData().getIdentifierQuoteString());
		statement = connection.prepareStatement(sql);
		tokens = createTokens(properties);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		Collection<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
		for (Token token : tokens) {
			values.addAll(token.getRequiredLogEntryValues());
		}
		return values;
	}

	@Override
	public void write(final LogEntry logEntry) throws SQLException {
		if (mutex == null) {
			doWrite(logEntry);
		} else {
			synchronized (mutex) {
				doWrite(logEntry);
			}
		}
	}

	@Override
	public void flush() throws SQLException {
		if (batch) {
			if (mutex == null) {
				doFlush();
			} else {
				synchronized (mutex) {
					doFlush();
				}
			}
		}
	}

	@Override
	public void close() throws SQLException {
		if (mutex == null) {
			doClose();
		} else {
			synchronized (mutex) {
				doClose();
			}
		}
	}

	/**
	 * Unsynchronized method for inserting a log entry.
	 *
	 * @param logEntry
	 *            Log entry to insert
	 *
	 * @throws SQLException
	 *             Database access failed
	 */
	private void doWrite(final LogEntry logEntry) throws SQLException {
		if (checkConnection()) {
			if (batch) {
				batchCount += 1;
			}

			try {
				for (int i = 0; i < tokens.size(); ++i) {
					tokens.get(i).apply(logEntry, statement, i + 1);
				}
			} catch (SQLException ex) {
				resetConnection();
				throw ex;
			}

			try {
				if (batch) {
					statement.addBatch();
					if (batchCount >= MAX_BATCH_SIZE) {
						statement.executeBatch();
						batchCount = 0;
					}
				} else {
					statement.executeUpdate();
				}
			} catch (SQLException ex) {
				resetConnection();
				throw ex;
			}
		} else {
			lostCount += 1;
		}
	}

	/**
	 * Unsynchronized method for flushing all cached batch insert statements.
	 *
	 * @throws SQLException
	 *             Database access failed
	 */
	private void doFlush() throws SQLException {
		if (batchCount > 0) {
			try {
				statement.executeBatch();
				batchCount = 0;
			} catch (SQLException ex) {
				resetConnection();
				throw ex;
			}
		}
	}

	/**
	 * Unsynchronized method for closing database connection.
	 *
	 * @throws SQLException
	 *             Database access failed
	 */
	private void doClose() throws SQLException {
		try {
			if (batch) {
				doFlush();
			}
		} finally {
			if (lostCount > 0) {
				InternalLogger.log(Level.ERROR, "Lost log entries due to broken database connection: " + lostCount);
			}

			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Checks if database connection is opened. Regular attempts are made to reestablish a broken database connection.
	 *
	 * @return {@code true} if database connection is opened, otherwise {@code false}
	 */
	private boolean checkConnection() {
		if (connection == null) {
			if (System.currentTimeMillis() >= reconnectTimestamp) {
				long start = System.currentTimeMillis();
				try {
					connection = connect(url, user, password);
					statement = connection.prepareStatement(sql);
					InternalLogger.log(Level.ERROR, "Lost log entries due to broken database connection: " + lostCount);
					lostCount = 0;
					return true;
				} catch (NamingException ex) {
					long now = System.currentTimeMillis();
					reconnectTimestamp = now + Math.max(MIN_RETRY_INTERVAL, (now - start) * 2);
					closeConnectionSilently();
					return false;
				} catch (SQLException ex) {
					long now = System.currentTimeMillis();
					reconnectTimestamp = now + Math.max(MIN_RETRY_INTERVAL, (now - start) * 2);
					closeConnectionSilently();
					return false;
				}
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Resets the database connection after an error, if automatic reconnection is enabled.
	 */
	private void resetConnection() {
		if (reconnect) {
			closeConnectionSilently();
			statement = null;
			lostCount = batch ? batchCount : 1;
			batchCount = 0;
			reconnectTimestamp = 0;
		}
	}

	/**
	 * Closes the opened database connection without throwing any exceptions.
	 */
	private void closeConnectionSilently() {
		if (connection != null) {
			try {
				try {
					connection.close();
				} catch (SQLException ex) {
					// Ignore
				}
			} finally {
				connection = null;
			}
		}
	}

	/**
	 * Establishes the connection to the database.
	 *
	 * @param url
	 *            JDBC or data source URL
	 * @param user
	 *            User name for login (can be {@code null} if no login is required)
	 * @param password
	 *            Password for login (can be {@code null} if no login is required)
	 * @return Connection to the database
	 *
	 * @throws NamingException
	 *             Requested data source cannot be found
	 * @throws SQLException
	 *             Failed to connect to database
	 */
	private static Connection connect(final String url, final String user, final String password) throws NamingException, SQLException {
		if (url.toLowerCase(Locale.ROOT).startsWith("java:")) {
			DataSource source = (DataSource) new InitialContext().lookup(url);
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
	 * Extracts the URL to database or data source from configuration.
	 *
	 * @param properties
	 *            Configuration for writer
	 * @return Connection URL
	 *
	 * @throws IllegalArgumentException
	 *             URL is not defined in configuration
	 */
	private static String getUrl(final Map<String, String> properties) {
		String url = properties.get("url");
		if (url == null) {
			throw new IllegalArgumentException("URL is missing for JDBC writer");
		} else {
			return url;
		}
	}

	/**
	 * Extracts the database table name from configuration.
	 *
	 * @param properties
	 *            Configuration for writer
	 * @return Name of database table
	 *
	 * @throws IllegalArgumentException
	 *             Table is not defined in configuration
	 */
	private static String getTable(final Map<String, String> properties) {
		String table = properties.get("table");
		if (table == null) {
			throw new IllegalArgumentException("Name of database table is missing for JDBC writer");
		} else {
			return table;
		}
	}

	/**
	 * Generates an insert SQL statement for the configured table and its fields.
	 *
	 * @param properties
	 *            Properties that contains the configured table and fields
	 * @param quote
	 *            Character for quoting identifiers (can be a space if the database doesn't support quote characters)
	 * @return SQL statement for {@link PreparedStatement}
	 *
	 * @throws SQLException
	 *             Table or field names contain illegal characters
	 */
	private static String renderSql(final Map<String, String> properties, final String quote) throws SQLException {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		append(builder, getTable(properties), quote);
		builder.append(" (");

		int count = 0;

		for (Entry<String, String> entry : properties.entrySet()) {
			String key = entry.getKey();
			if (key.toLowerCase(Locale.ROOT).startsWith(FIELD_PREFIX)) {
				String column = key.substring(FIELD_PREFIX.length());

				if (count++ != 0) {
					builder.append(", ");
				}

				append(builder, column, quote);
			}
		}

		builder.append(") VALUES (");

		for (int i = 0; i < count; ++i) {
			if (i > 0) {
				builder.append(", ?");
			} else {
				builder.append("?");
			}
		}

		builder.append(")");

		return builder.toString();
	}

	/**
	 * Appends a database identifier securely to a builder that is building a SQL statement.
	 *
	 * @param builder
	 *            String builder that is building a SQL statement
	 * @param identifier
	 *            Identifier to add
	 * @param quote
	 *            Character for quoting the identifier (can be a space if the database doesn't support quote characters)
	 *
	 * @throws SQLException
	 *             Identifier contains an illegal character
	 */
	private static void append(final StringBuilder builder, final String identifier, final String quote) throws SQLException {
		if (identifier.indexOf('\n') >= 0 || identifier.indexOf('\r') >= 0) {
			throw new SQLException("Identifier contains line breaks: " + identifier);
		} else if (" ".equals(quote)) {
			for (int i = 0; i < identifier.length(); ++i) {
				char c = identifier.charAt(i);
				if (!Character.isLetterOrDigit(c) && c != '_' && c != '@' && c != '$' && c != '#') {
					throw new SQLException("Illegal identifier: " + identifier);
				}
			}
			builder.append(identifier);
		} else {
			builder.append(quote).append(identifier.replace(quote, quote + quote)).append(quote);
		}
	}

	/**
	 * Creates tokens for all configured fields.
	 *
	 * @param properties
	 *            Properties that contains the configured fields
	 * @return Tokens for filling a {@link PreparedStatement}
	 */
	private static List<Token> createTokens(final Map<String, String> properties) {
		List<Token> tokens = new ArrayList<Token>();
		for (Entry<String, String> entry : properties.entrySet()) {
			if (entry.getKey().toLowerCase(Locale.ROOT).startsWith(FIELD_PREFIX)) {
				tokens.add(FormatPatternParser.parse(entry.getValue()));
			}
		}
		return tokens;
	}

}
