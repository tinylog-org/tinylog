package org.tinylog.impl.writer.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;

import org.assertj.db.type.AssertDbConnection;
import org.assertj.db.type.AssertDbConnectionFactory;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.writer.Writer;
import org.tinylog.impl.writer.WriterBuilder;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.db.api.Assertions.assertThat;

@Tinylog
class JdbcWriterBuilderTest {

    private static final String JDBC_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";

    @Inject
    private TinylogContext context;

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
     * Verifies that a minimal configured {@link JdbcWriter} instance can be created.
     */
    @Test
    void minimalConfiguredWriter() throws Exception {
        executeSql("CREATE TABLE LOGS");
        applyConfiguration(Map.of("url", url, "table", "LOGS"));

        try {
            try (Writer writer = new JdbcWriterBuilder().create(context)) {
                assertThat(log.consume()).singleElement().satisfies(entry -> {
                    assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
                    assertThat(entry.getFormattedMessage(context.getConfiguration())).containsIgnoringCase("no fields");
                });

                LogEntry entry = new LogEntryBuilder().create();
                writer.log(entry);
            }

            AssertDbConnection connection = AssertDbConnectionFactory.of(url, null, null).create();
            Table table = connection.table("LOGS").build();
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
            applyConfiguration(Map.of(
                "url", url,
                "schema", "TINYLOG",
                "table", "LOGS",
                "user", "alice",
                "password", "secret",
                "fields.SEVERITY", "severity-code",
                "fields.MESSAGE", "{class}: {message}"
            ));

            try (Writer writer = new JdbcWriterBuilder().create(context)) {
                LogEntry entry = new LogEntryBuilder()
                    .severityLevel(Level.INFO)
                    .className("Foo")
                    .message("Hello World!")
                    .create();
                writer.log(entry);
            }

            AssertDbConnection connection = AssertDbConnectionFactory.of(url, "alice", "secret").create();
            Table table = connection.table("TINYLOG.LOGS").build();
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
        applyConfiguration(Map.of("table", "FOO"));

        Throwable throwable = catchThrowable(() -> new JdbcWriterBuilder().create(context).close());

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).contains("URL");
    }

    /**
     * Verifies that an exception with a meaningful message will be thrown, if table name is undefined.
     */
    @Test
    void missingTableName() {
        applyConfiguration(Map.of("url", url));

        Throwable throwable = catchThrowable(() -> new JdbcWriterBuilder().create(context).close());

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).containsIgnoringCase("table");
    }

    /**
     * Overwrites the current tinylog context configuration with the passed properties.
     *
     * @param properties The properties for the new configuration to apply
     */
    private void applyConfiguration(Map<String, String> properties) {
        context = context.withConfiguration(new Configuration(properties, context.getLogger()));
    }

    /**
     * Executes an SQL statement using the default user.
     *
     * @param sql The SQL statement to execute
     * @throws SQLException If failed to execute the passed SQL statement
     */
    private void executeSql(String sql) throws SQLException {
        executeSql(sql, null, null);
    }

    /**
     * Executes an SQL statement using a custom user.
     *
     * @param sql The SQL statement to execute
     * @param user The username of the user for login
     * @param password The password of the user for login
     * @throws SQLException If failed to execute the passed SQL statement
     */
    private void executeSql(String sql, String user, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

}
