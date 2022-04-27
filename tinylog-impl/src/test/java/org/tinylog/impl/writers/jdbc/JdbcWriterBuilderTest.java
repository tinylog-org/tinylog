package org.tinylog.impl.writers.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.inject.Inject;

import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.WriterBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.db.api.Assertions.assertThat;

@CaptureLogEntries
@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
class JdbcWriterBuilderTest {

	private static final String JDBC_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	private String url;

	/**
	 * Creates a URL for a new clean in-memory database.
	 */
	@BeforeEach
	void createDatabase() {
		url = String.format(JDBC_URL, UUID.randomUUID());
	}

	/**
	 * Verifies that a minimal configured {@link JdbcWriter} instance can be created.
	 */
	@Test
	void minimalConfiguredWriter() throws Exception {
		executeSql("CREATE TABLE LOGS");

		try {
			Configuration configuration = new Configuration();
			configuration.set("url", url);
			configuration.set("table", "LOGS");

			try (Writer writer = new JdbcWriterBuilder().create(framework, configuration)) {
				assertThat(log.consume()).hasSize(1).allSatisfy(entry -> {
					assertThat(entry.getLevel()).isEqualTo(Level.WARN);
					assertThat(entry.getMessage()).containsIgnoringCase("no fields");
				});

				LogEntry entry = new LogEntryBuilder().create();
				writer.log(entry);
			}

			Table table = new Table(new Source(url, null, null), "LOGS");
			assertThat(table).hasNumberOfRows(1);
		} finally {
			executeSql("SHUTDOWN");
		}
	}

	/**
	 * Verifies that a fully configured {@link JdbcWriter} instance can be created.
	 */
	@Test
	void fullyConfiguredWriter() throws Exception {
		executeSql("CREATE USER alice PASSWORD 'secret' ADMIN");
		executeSql("CREATE SCHEMA TINYLOG", "alice", "secret");
		executeSql("CREATE TABLE TINYLOG.LOGS (SEVERITY INTEGER, MESSAGE VARCHAR)", "alice", "secret");
		executeSql("DROP USER \"\"", "alice", "secret");

		try {
			Configuration configuration = new Configuration();
			configuration.set("url", url);
			configuration.set("schema", "TINYLOG");
			configuration.set("table", "LOGS");
			configuration.set("user", "alice");
			configuration.set("password", "secret");
			configuration.set("fields.SEVERITY", "severity-code");
			configuration.set("fields.MESSAGE", "{class}: {message}");

			try (Writer writer = new JdbcWriterBuilder().create(framework, configuration)) {
				LogEntry entry = new LogEntryBuilder()
					.severityLevel(Level.INFO)
					.className("Foo")
					.message("Hello World!")
					.create();
				writer.log(entry);
			}

			Table table = new Table(new Source(url, "alice", "secret"), "TINYLOG.LOGS");
			assertThat(table).row().hasValues(3, "Foo: Hello World!");
		} finally {
			executeSql("SHUTDOWN", "alice", "secret");
		}
	}

	/**
	 * Verifies that an exception with a meaningful message will be thrown, if URL is undefined.
	 */
	@Test
	void missingUrl() {
		Configuration configuration = new Configuration();
		configuration.set("table", "FOO");

		Throwable throwable = catchThrowable(() -> new JdbcWriterBuilder().create(framework, configuration).close());

		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
		assertThat(throwable.getMessage()).contains("URL");
	}

	/**
	 * Verifies that an exception with a meaningful message will be thrown, if table name is undefined.
	 */
	@Test
	void missingTableName() {
		Configuration configuration = new Configuration();
		configuration.set("url", url);

		Throwable throwable = catchThrowable(() -> new JdbcWriterBuilder().create(framework, configuration).close());

		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
		assertThat(throwable.getMessage()).containsIgnoringCase("table");
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(WriterBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(JdbcWriterBuilder.class);
			assertThat(builder.getName()).isEqualTo("jdbc");
		});
	}

	/**
	 * Executes an SQL statement using the default user.
	 *
	 * @param sql The SQL statement toe execute
	 * @throws SQLException Failed to execute the passed SQL statement
	 */
	private void executeSql(String sql) throws SQLException {
		executeSql(sql, null, null);
	}

	/**
	 * Executes an SQL statement using a custom user.
	 *
	 * @param sql The SQL statement toe execute
	 * @param user The username of the user for login
	 * @param password The password of the user for login
	 * @throws SQLException Failed to execute the passed SQL statement
	 */
	private void executeSql(String sql, String user, String password) throws SQLException {
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		}
	}

}
