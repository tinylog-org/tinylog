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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.powermock.reflect.Whitebox;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.core.LogEntryValue;
import org.tinylog.rules.InitialContextRule;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.LogEntryBuilder;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.db.api.Assertions.assertThat;
import static org.tinylog.util.Maps.doubletonMap;
import static org.tinylog.util.Maps.tripletonMap;

/**
 * Tests for {@link JdbcWriter}.
 */
@RunWith(Enclosed.class)
public final class JdbcWriterTest {

	private static final String JDBC_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";
	private static final String DATA_SOURCE_URL = "java:comp/env/jdbc/ExampleDS";
	private static final String TABLE_NAME = "LOGS";

	/**
	 * Tests related to login to database.
	 */
	public static final class Login extends AbstractTest {

		/**
		 * Verifies that login with valid user credentials works for a JDBC connection URL.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void jdbcWithValidPassword() throws NamingException, SQLException {
			createTable();
			executeSql("CREATE USER BOB PASSWORD '123' ADMIN");

			new JdbcWriter(createProperties(emptyMap(), doubletonMap("user", "BOB", "password", "123"))).close();
		}

		/**
		 * Verifies that login with invalid user credentials will fail for a JDBC connection URL.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void jdbcWithInvalidPassword() throws NamingException, SQLException {
			createTable();
			executeSql("CREATE USER BOB PASSWORD '123' ADMIN");

			assertThatThrownBy(() -> {
				new JdbcWriter(createProperties(emptyMap(), doubletonMap("user", "BOB", "password", "invalid")));
			}).isInstanceOf(SQLException.class);
		}

		/**
		 * Verifies that login with valid user credentials works for a {@link DataSource}.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void dataSourceWithValidPassword() throws NamingException, SQLException {
			createTable();
			executeSql("CREATE USER BOB PASSWORD '123' ADMIN");

			new InitialContext().bind(DATA_SOURCE_URL, createDataSource());
			new JdbcWriter(createProperties(emptyMap(), tripletonMap("url", DATA_SOURCE_URL, "user", "BOB", "password", "123"))).close();
		}

		/**
		 * Verifies that login with invalid user credentials will fail for a {@link DataSource}.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void dataSourceWithInvalidPassword() throws NamingException, SQLException {
			createTable();
			executeSql("CREATE USER BOB PASSWORD '123' ADMIN");
			new InitialContext().bind(DATA_SOURCE_URL, createDataSource());

			assertThatThrownBy(() -> {
				new JdbcWriter(createProperties(emptyMap(), tripletonMap("url", DATA_SOURCE_URL, "user", "BOB", "password", "invalid")));
			}).isInstanceOf(SQLException.class);
		}

	}

	/**
	 * Tests related to inserting into database.
	 */
	@RunWith(Parameterized.class)
	public static final class Inserting extends AbstractTest {

		private final boolean writingThread;

		/**
		 * @param writingThread
		 *            Value for writing thread property
		 */
		public Inserting(final boolean writingThread) {
			this.writingThread = writingThread;
		}

		/**
		 * Returns both parameters for writing thread (disabled and enabled).
		 *
		 * @return {@code false} and {@code true}, each encapsulated in an array
		 */
		@Parameters(name = "writingthread={0}")
		public static Collection<Object[]> getParameters() {
			return Arrays.asList(new Object[] { false }, new Object[] { true });
		}

		/**
		 * Verifies that all required log entry values will be detected from configured fields.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void requiredLogEntryValues() throws NamingException, SQLException {
			createTable("LEVEL VARCHAR(7) NOT NULL", "MESSAGE CLOB NULL");

			JdbcWriter writer = new JdbcWriter(createProperties(doubletonMap("LEVEL", "{level}", "MESSAGE", "{message}")));
			assertThat(writer.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LEVEL, LogEntryValue.MESSAGE,
				LogEntryValue.EXCEPTION);
			writer.close();
		}

		/**
		 * Verifies that there is only a mutex object, if writhing thread is disabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void mutex() throws NamingException, SQLException {
			createTable();

			JdbcWriter writer = new JdbcWriter(createProperties(emptyMap()));
			Object mutex = Whitebox.getInternalState(writer, "mutex");
			if (writingThread) {
				assertThat(mutex).isNull();
			} else {
				assertThat(mutex).isNotNull();
			}
			writer.close();
		}

		/**
		 * Verifies that log entries will be inserted immediately into the database table, if batch execution is
		 * disabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void immediateInsertion() throws NamingException, SQLException {
			createTable("MESSAGE CLOB NULL");

			JdbcWriter writer = new JdbcWriter(createProperties(singletonMap("MESSAGE", "{message}"), singletonMap("batch", "false")));

			writer.write(LogEntryBuilder.empty().message("Hello World!").create());
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Hello World!");

			writer.close();
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Hello World!");
		}

		/**
		 * Verifies that log entries will be inserted into the database table after reaching a defined threshold and if
		 * batch execution is enabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void batchedInsertionAutoFlush() throws NamingException, SQLException {
			createTable("MESSAGE CLOB NULL");

			JdbcWriter writer = new JdbcWriter(createProperties(singletonMap("MESSAGE", "{message}"), singletonMap("batch", "true")));

			for (int i = 0; i < 100; ++i) {
				assertThat(fetchTable(TABLE_NAME)).isEmpty();
				writer.write(LogEntryBuilder.empty().message("Hello World!").create());
			}

			assertThat(fetchTable(TABLE_NAME))
				.hasNumberOfRows(100)
				.column("MESSAGE").hasOnlyNotNullValues()
				.value(0).isEqualTo("Hello World!")
				.value(99).isEqualTo("Hello World!");

			writer.close();
		}

		/**
		 * Verifies that log entries will be inserted into the database table after explicit flushing, if batch
		 * execution is enabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void batchedInsertionManualFlush() throws NamingException, SQLException {
			createTable("MESSAGE CLOB NULL");

			JdbcWriter writer = new JdbcWriter(createProperties(singletonMap("MESSAGE", "{message}"), singletonMap("batch", "true")));

			writer.write(LogEntryBuilder.empty().message("Hello World!").create());
			assertThat(fetchTable(TABLE_NAME)).isEmpty();

			writer.flush();
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Hello World!");

			writer.close();
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Hello World!");
		}

		/**
		 * Verifies that cached log entries will be inserted into the database table while closing, if batch execution
		 * is enabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void batchedInsertionCloseFlush() throws NamingException, SQLException {
			createTable("MESSAGE CLOB NULL");

			JdbcWriter writer = new JdbcWriter(createProperties(singletonMap("MESSAGE", "{message}"), singletonMap("batch", "true")));

			writer.write(LogEntryBuilder.empty().message("Hello World!").create());
			assertThat(fetchTable(TABLE_NAME)).isEmpty();

			writer.close();
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Hello World!");
		}

		/**
		 * Verifies that log entries can be written to a {@link DataSource}.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void dataSourceInsertion() throws NamingException, SQLException {
			createTable("MESSAGE CLOB NULL");
			new InitialContext().bind(DATA_SOURCE_URL, createDataSource());

			Map<String, String> properties = createProperties(singletonMap("MESSAGE", "{message}"), singletonMap("url", DATA_SOURCE_URL));
			JdbcWriter writer = new JdbcWriter(properties);
			writer.write(LogEntryBuilder.empty().message("Hello World!").create());
			writer.close();

			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Hello World!");
		}

		@Override
		protected Map<String, String> createProperties(final Map<String, String> fields, final Map<String, String> extras) {
			Map<String, String> properties = super.createProperties(fields, extras);
			if (!extras.containsKey("writingthread")) {
				properties.put("writingthread", Boolean.toString(writingThread));
			}
			return properties;
		}

	}

	/**
	 * Tests related to re-establishing the connection to the database after an error.
	 */
	public static final class Reconnection extends AbstractTest {

		/**
		 * Redirects and collects system output streams.
		 */
		@Rule
		public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

		/**
		 * Verifies that a broken connection will be not re-establishing, if reconnecting is disabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void keepBrokenConnection() throws NamingException, SQLException {
			createTable("MESSAGE CLOB NULL");

			JdbcWriter writer = new JdbcWriter(createProperties(singletonMap("MESSAGE", "{message}"), singletonMap("reconnect", "false")));

			writer.write(LogEntryBuilder.empty().message("Hello World!").create());
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Hello World!");

			shutdownDatabase();
			createTable("MESSAGE CLOB NULL");

			assertThatThrownBy(() -> {
				writer.write(LogEntryBuilder.empty().message("Hello World!").create());
			}).isInstanceOf(SQLException.class);

			assertThat(fetchTable(TABLE_NAME)).isEmpty();
		}

		/**
		 * Verifies that lost log entries due to a broken connection will be logged, if reconnecting is enabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void logLostEntriesWhileClsoing() throws NamingException, SQLException {
			createTable("MESSAGE CLOB NULL");

			JdbcWriter writer = new JdbcWriter(createProperties(singletonMap("MESSAGE", "{message}"), singletonMap("reconnect", "true")));

			writer.write(LogEntryBuilder.empty().message("One").create());
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("One");

			shutdownDatabase();

			assertThatThrownBy(() -> {
				writer.write(LogEntryBuilder.empty().message("Two").create());
			}).isInstanceOf(SQLException.class);

			writer.write(LogEntryBuilder.empty().message("Three").create());
			writer.write(LogEntryBuilder.empty().message("Four").create());

			writer.close();

			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("3");
		}

		/**
		 * Verifies that a broken connection will be re-establishing, if reconnecting is enabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 * @throws InterruptedException
		 *             Failed to sleep before reconnecting try
		 */
		@Test
		public void repairBrokenConnection() throws NamingException, SQLException, InterruptedException {
			createTable("MESSAGE CLOB NULL");

			JdbcWriter writer = new JdbcWriter(createProperties(singletonMap("MESSAGE", "{message}"), singletonMap("reconnect", "true")));

			writer.write(LogEntryBuilder.empty().message("One").create());
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("One");

			shutdownDatabase();

			assertThatThrownBy(() -> {
				writer.write(LogEntryBuilder.empty().message("Two").create());
			}).isInstanceOf(SQLException.class);

			writer.write(LogEntryBuilder.empty().message("Three").create());

			createTable("MESSAGE CLOB NULL");

			Thread.sleep(1000);

			writer.write(LogEntryBuilder.empty().message("Four").create());
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("2");

			writer.close();

			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Four");
		}

		/**
		 * Verifies that a broken connection to a temporary disappeared {@link DataSource} will be re-establishing, if
		 * reconnecting is enabled.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 * @throws InterruptedException
		 *             Failed to sleep before reconnecting try
		 */
		@Test
		public void disappearedDataSource() throws NamingException, SQLException, InterruptedException {
			InitialContext context = new InitialContext();

			createTable("MESSAGE CLOB NULL");
			context.bind(DATA_SOURCE_URL, createDataSource());

			Map<String, String> properties = createProperties(singletonMap("MESSAGE", "{message}"));
			properties.put("url", DATA_SOURCE_URL);
			properties.put("reconnect", "true");

			JdbcWriter writer = new JdbcWriter(properties);

			writer.write(LogEntryBuilder.empty().message("One").create());
			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("One");

			shutdownDatabase();
			context.unbind(DATA_SOURCE_URL);

			assertThatThrownBy(() -> {
				writer.write(LogEntryBuilder.empty().message("Two").create());
			}).isInstanceOf(SQLException.class);

			writer.write(LogEntryBuilder.empty().message("Three").create());

			createTable("MESSAGE CLOB NULL");
			context.bind(DATA_SOURCE_URL, createDataSource());

			Thread.sleep(1000);

			writer.write(LogEntryBuilder.empty().message("Four").create());
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("2");

			writer.close();

			assertThat(fetchTable(TABLE_NAME)).column("MESSAGE").containsValues("Four");
		}

	}

	/**
	 * Tests related to validation of configuration properties.
	 */
	public static final class Validation extends AbstractTest {

		/**
		 * Verifies that an exception will be thrown, if no connection URL has been defined. The message of the thrown
		 * exception should contain "URL" or "url".
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void missingConnectionUrl() throws NamingException, SQLException {
			createTable();

			assertThatThrownBy(() -> {
				new JdbcWriter(createProperties(emptyMap(), singletonMap("url", null)));
			}).hasMessageMatching("(?i).*URL.*");
		}

		/**
		 * Verifies that a {@link SQLException} will be thrown, if a JDBC connection URL cannot be resolved.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void invalidJdbcConnectionUrl() throws NamingException, SQLException {
			createTable();

			assertThatThrownBy(() -> {
				new JdbcWriter(createProperties(emptyMap(), singletonMap("url", "jdbc:invalid")));
			}).isInstanceOf(SQLException.class);
		}

		/**
		 * Verifies that a {@link NamingException} will be thrown, if a {@link DataSource} doesn't exist.
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void invalidDataSource() throws NamingException, SQLException {
			createTable();
			new InitialContext().bind(DATA_SOURCE_URL, createDataSource());

			assertThatThrownBy(() -> {
				new JdbcWriter(createProperties(emptyMap(), singletonMap("url", "java:invalid")));
			}).isInstanceOf(NamingException.class);
		}

		/**
		 * Verifies that an exception will be thrown, if no database table has been defined. The message of the thrown
		 * exception should contain "table".
		 *
		 * @throws NamingException
		 *             Failed to find data source
		 * @throws SQLException
		 *             Failed to access database
		 */
		@Test
		public void missingTableName() throws NamingException, SQLException {
			createTable();

			assertThatThrownBy(() -> {
				new JdbcWriter(createProperties(emptyMap(), singletonMap("table", null)));
			}).hasMessageMatching("(?i).*table.*");
		}

		/**
		 * Verifies that line breaks in database identifiers will be detected and reported.
		 */
		@Test
		public void lineBreaksInColumnName() {
			assertThatThrownBy(() -> {
				Whitebox.invokeMethod(JdbcWriter.class, "append", new StringBuilder(), "MESSAGE\n", "'");
			}).hasMessageMatching("(?si).*line.*");

			assertThatThrownBy(() -> {
				Whitebox.invokeMethod(JdbcWriter.class, "append", new StringBuilder(), "MESSAGE\r", "'");
			}).hasMessageMatching("(?si).*line.*");
		}

		/**
		 * Verifies that a database identifier with spaces will be quoted.
		 *
		 * @throws Exception
		 *             Failed to call private append() method
		 */
		@Test
		public void quoteIdentifiereWithSpaces() throws Exception {
			StringBuilder builder = new StringBuilder();
			Whitebox.invokeMethod(JdbcWriter.class, "append", builder, "TEXT MESSAGE", "'");
			assertThat(builder.toString()).isEqualTo("'TEXT MESSAGE'");
		}

		/**
		 * Verifies that a database identifier with special characters will be quoted.
		 *
		 * @throws Exception
		 *             Failed to call {@link JdbcWriter#append(StringBuilder, String, String)}
		 */
		@Test
		public void quoteIdentifiereWithSpecialCharacters() throws Exception {
			StringBuilder builder = new StringBuilder();
			Whitebox.invokeMethod(JdbcWriter.class, "append", builder, "M€SS@GE", "'");
			assertThat(builder.toString()).isEqualTo("'M€SS@GE'");
		}

		/**
		 * Verifies that identifiers will be accepted, if they contain only legal letters and quotes are not supported.
		 *
		 * @throws Exception
		 *             Failed to call {@link JdbcWriter#append(StringBuilder, String, String)}
		 */
		@Test
		public void accpetIdentifiersWithLegalLetters() throws Exception {
			StringBuilder builder = new StringBuilder();
			Whitebox.invokeMethod(JdbcWriter.class, "append", builder, "@TEXT_MESSAGE@", " ");
			assertThat(builder.toString()).isEqualTo("@TEXT_MESSAGE@");
		}

		/**
		 * Verifies that an exception will be thrown, if a database identifier contains spaces and quotes are not
		 * supported.
		 */
		@Test
		public void refuseIdentifiereWithSpaces() {
			assertThatThrownBy(() -> {
				Whitebox.invokeMethod(JdbcWriter.class, "append", new StringBuilder(), "TEXT MESSAGE", " ");
			}).hasMessageContaining("TEXT MESSAGE");
		}

		/**
		 * Verifies that an exception will be thrown, if a database identifier contains special characters and quotes
		 * are not supported.
		 */
		@Test
		public void refuseIdentifiereWithSpecialCharacters() {
			assertThatThrownBy(() -> {
				Whitebox.invokeMethod(JdbcWriter.class, "append", new StringBuilder(), "M€SS@GE", " ");
			}).hasMessageContaining("M€SS@GE");
		}

		/**
		 * Verifies that writer is registered as service under the name "jdbc".
		 *
		 * @throws SQLException
		 *             Failed to create table
		 */
		@Test
		public void isRegistered() throws SQLException {
			createTable();

			Writer writer = new ServiceLoader<>(Writer.class, Map.class).create("jdbc", createProperties(emptyMap()));
			assertThat(writer).isInstanceOf(JdbcWriter.class);
		}

	}

	/**
	 * Base class with common methods for inner test classes.
	 */
	protected abstract static class AbstractTest {

		/**
		 * Provides a simple implementation for {@link InitialContext}.
		 */
		@Rule
		public final InitialContextRule initialContextRule = new InitialContextRule();

		private String url;

		/**
		 * Creates a URL for a new clean database.
		 */
		@Before
		public void createDatabase() {
			url = String.format(JDBC_URL, UUID.randomUUID());
		}

		/**
		 * Shutdowns the in-memory database after each test.
		 *
		 * @throws SQLException
		 *             Failed to shutdown database
		 */
		@After
		public void shutdownDatabase() throws SQLException {
			executeSql("SHUTDOWN");
		}

		/**
		 * Creates properties for instancing a JDBC writer. The defined default database URL and table name will be
		 * used.
		 *
		 * @param fields
		 *            Mapping of database column names and format patterns
		 * @return Generated properties
		 */
		protected Map<String, String> createProperties(final Map<String, String> fields) {
			return createProperties(fields, emptyMap());
		}

		/**
		 * Creates properties for instancing a JDBC writer. The defined default database URL and table name will be
		 * used.
		 *
		 * @param fields
		 *            Mapping of database column names and format patterns
		 * @param extras
		 *            Additional properties that should be added to the returned properties
		 * @return Generated properties
		 */
		protected Map<String, String> createProperties(final Map<String, String> fields, final Map<String, String> extras) {
			Map<String, String> properties = new HashMap<>();
			properties.put("url", url + ";IFEXISTS=TRUE");
			properties.put("table", TABLE_NAME);
			fields.forEach((key, value) -> properties.put("field." + key, value));
			properties.putAll(extras);
			return properties;
		}

		/**
		 * Creates a data source for the defined default database.
		 *
		 * @return Created data source
		 */
		protected DataSource createDataSource() {
			JdbcDataSource source = new JdbcDataSource();
			source.setUrl(url);
			return source;
		}

		/**
		 * Creates a table in the defined default database. The table name is the defined default table.
		 *
		 * @param columns
		 *            Column definitions including name and type
		 * @throws SQLException
		 *             Failed to create table
		 */
		protected void createTable(final String... columns) throws SQLException {
			StringBuilder builder = new StringBuilder();

			builder.append("CREATE TABLE ");
			builder.append(TABLE_NAME);
			builder.append(" (");

			boolean first = true;
			for (String column : columns) {
				if (first) {
					first = false;
				} else {
					builder.append(", ");
				}
				builder.append(column);
			}

			builder.append(")");

			executeSql(builder.toString());
		}

		/**
		 * Executes a SQL statement for the default database.
		 *
		 * @param sql
		 *            SQL statement to execute
		 * @throws SQLException
		 *             Failed to execute SQL statement
		 */
		protected void executeSql(final String sql) throws SQLException {
			try (Connection connection = DriverManager.getConnection(url)) {
				try (Statement statement = connection.createStatement()) {
					statement.execute(sql);
				}
			}
		}

		/**
		 * Creates a table object with connection data for testing a table with AssertJ.
		 *
		 * @param table
		 *            Name of table
		 * @return Connection data
		 */
		protected Table fetchTable(final String table) {
			return new Table(new Source(url, null, null), table);
		}

	}

}
