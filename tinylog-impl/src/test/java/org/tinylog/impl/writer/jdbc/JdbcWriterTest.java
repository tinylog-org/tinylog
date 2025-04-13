package org.tinylog.impl.writer.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.assertj.db.type.AssertDbConnection;
import org.assertj.db.type.AssertDbConnectionFactory;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedStatic;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.impl.format.placeholder.ClassPlaceholder;
import org.tinylog.impl.format.placeholder.LevelPlaceholder;
import org.tinylog.impl.format.placeholder.MessagePlaceholder;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.SeverityCodePlaceholder;
import org.tinylog.impl.format.placeholder.ValueType;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@Tinylog
class JdbcWriterTest {

    private static final String JDBC_URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1";
    private static final String DATA_SOURCE_URL = "java:comp/env/jdbc/DB";
    private static final String TABLE_NAME = "LOGS";
    private static final String SCHEMA_NAME = "FOO";

    @Inject
    private Configuration configuration;

    private String url;

    /**
     * Creates a URL to a new clean in-memory database.
     */
    @BeforeEach
    void createDatabase() {
        url = String.format(JDBC_URL, UUID.randomUUID());
    }

    /**
     * Shutdowns the in-memory database after each test.
     *
     * @throws SQLException If failed to shut down the database
     */
    @AfterEach
    void shutdownDatabase() throws SQLException {
        executeSql("SHUTDOWN");
    }

    /**
     * Verifies that the JDBC writer returns the output details from the field placeholders.
     */
    @Test
    void provideOutputDetails() throws Exception {
        createTable(TABLE_NAME, "LEVEL VARCHAR", "CLASS VARCHAR", "MESSAGE CLOB");

        Map<String, Placeholder> fields = new LinkedHashMap<>();
        fields.put("LEVEL", new LevelPlaceholder());
        fields.put("CLASS", new ClassPlaceholder());
        fields.put("MESSAGE", new MessagePlaceholder(configuration));

        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            assertThat(writer.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        }
    }

    /**
     * Verifies that the connection can be successfully established for a JDBC URL without required login.
     */
    @Test
    void establishAnonymousJdbcConnection() throws SQLException, NamingException {
        createTable(TABLE_NAME);
        new JdbcWriter(url, null, null, null, TABLE_NAME, Collections.emptyMap()).close();
    }

    /**
     * Verifies that the connection can be successfully established for a JDBC URL with required login.
     */
    @Test
    void establishAuthenticatedJdbcConnection() throws SQLException, NamingException {
        createTable(TABLE_NAME);
        executeSql("CREATE USER alice PASSWORD 'secret' ADMIN");
        executeSql("DROP USER \"\"", "alice", "secret");

        try {
            new JdbcWriter(url, "alice", "secret", null, TABLE_NAME, emptyMap()).close();
        } finally {
            executeSql("CREATE USER \"\" PASSWORD '' ADMIN", "alice", "secret");
        }
    }

    /**
     * Verifies that the connection can be successfully established for a data source URL without required login.
     */
    @Test
    void establishAnonymousDataSourceConnection() throws SQLException, NamingException {
        createTable(TABLE_NAME);

        InitialContext context = new InitialContext();
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).then(invocation -> DriverManager.getConnection(url));
        context.bind(DATA_SOURCE_URL, dataSource);

        try {
            new JdbcWriter(DATA_SOURCE_URL, null, null, null, TABLE_NAME, emptyMap()).close();
        } finally {
            context.unbind(DATA_SOURCE_URL);
        }
    }

    /**
     * Verifies that the connection can be successfully established for a data source URL with required login.
     */
    @Test
    void establishAuthenticatedDataSourceConnection() throws SQLException, NamingException {
        createTable(TABLE_NAME);
        executeSql("CREATE USER alice PASSWORD 'password' ADMIN");
        executeSql("DROP USER \"\"", "alice", "password");

        try {
            InitialContext context = new InitialContext();
            DataSource dataSource = mock(DataSource.class);
            when(dataSource.getConnection(any(), any())).then(invocation -> DriverManager.getConnection(
                url,
                invocation.getArgument(0),
                invocation.getArgument(1)
            ));
            context.bind(DATA_SOURCE_URL, dataSource);

            try {
                new JdbcWriter(DATA_SOURCE_URL, "alice", "password", null, TABLE_NAME, emptyMap()).close();
            } finally {
                context.unbind(DATA_SOURCE_URL);
            }
        } finally {
            executeSql("CREATE USER \"\" PASSWORD '' ADMIN", "alice", "password");
        }
    }

    /**
     * Verifies that a real existing value can be inserted as integer.
     *
     * @see ValueType#INTEGER
     */
    @Test
    void insertRealIntegerValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO INTEGER");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.INTEGER);
        when(placeholder.getValue(logEntry)).thenReturn(Integer.MAX_VALUE);

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues(Integer.MAX_VALUE);
    }

    /**
     * Verifies that {@code null} can be inserted as integer.
     *
     * @see ValueType#INTEGER
     */
    @Test
    void insertNullIntegerValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO INTEGER");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.INTEGER);
        when(placeholder.getValue(logEntry)).thenReturn(null);

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues((Integer) null);
    }

    /**
     * Verifies that a real existing value can be inserted as long.
     *
     * @see ValueType#LONG
     */
    @Test
    void insertRealLongValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO BIGINT");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.LONG);
        when(placeholder.getValue(logEntry)).thenReturn(Long.MAX_VALUE);

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues(Long.MAX_VALUE);
    }

    /**
     * Verifies that {@code null} can be inserted as long.
     *
     * @see ValueType#LONG
     */
    @Test
    void insertNullLongValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO BIGINT");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.LONG);
        when(placeholder.getValue(logEntry)).thenReturn(null);

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues((Long) null);
    }

    /**
     * Verifies that a real existing value can be inserted as decimal.
     *
     * @see ValueType#DECIMAL
     */
    @Test
    void insertRealDecimalValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO NUMERIC(20, 9)");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.DECIMAL);
        when(placeholder.getValue(logEntry)).thenReturn(BigDecimal.valueOf(3.14));

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues(BigDecimal.valueOf(3.14));
    }

    /**
     * Verifies that {@code null} can be inserted as decimal.
     *
     * @see ValueType#DECIMAL
     */
    @Test
    void insertNullDecimalValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO NUMERIC(20, 9)");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.DECIMAL);
        when(placeholder.getValue(logEntry)).thenReturn(null);

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues((BigDecimal) null);
    }

    /**
     * Verifies that a real existing value can be inserted as timestamp.
     *
     * @see ValueType#TIMESTAMP
     */
    @Test
    void insertRealTimestampValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO TIMESTAMP");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.TIMESTAMP);
        when(placeholder.getValue(logEntry)).thenReturn(new Timestamp(0L));

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues(new Timestamp(0L));
    }

    /**
     * Verifies that {@code null} can be inserted as timestamp.
     *
     * @see ValueType#TIMESTAMP
     */
    @Test
    void insertNullTimestampValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO TIMESTAMP");
        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.TIMESTAMP);
        when(placeholder.getValue(logEntry)).thenReturn(null);

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues((Timestamp) null);
    }

    /**
     * Verifies that a real existing value can be inserted as string.
     *
     * @see ValueType#STRING
     */
    @Test
    void insertRealStringValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO VARCHAR");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.STRING);
        when(placeholder.getValue(logEntry)).thenReturn("Hello World!");

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues("Hello World!");
    }

    /**
     * Verifies that {@code null} can be inserted as string.
     *
     * @see ValueType#STRING
     */
    @Test
    void insertNullStringValue() throws SQLException, NamingException {
        createTable(TABLE_NAME, "FOO VARCHAR");

        LogEntry logEntry = new LogEntryBuilder().create();

        Placeholder placeholder = mock(Placeholder.class);
        when(placeholder.getType()).thenReturn(ValueType.STRING);
        when(placeholder.getValue(logEntry)).thenReturn(null);

        Map<String, Placeholder> fields = Map.of("FOO", placeholder);
        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(logEntry);
        }

        assertThat(fetchTable(TABLE_NAME)).column().containsValues((String) null);
    }

    /**
     * Verifies that multiple values can be inserted for a log entry.
     */
    @Test
    void insertMultipleValues() throws SQLException, NamingException {
        createTable(TABLE_NAME, "SEVERITY_CODE INTEGER", "MESSAGE VARCHAR");

        Map<String, Placeholder> fields = new LinkedHashMap<>();
        fields.put("SEVERITY_CODE", new SeverityCodePlaceholder());
        fields.put("MESSAGE", new MessagePlaceholder(configuration));

        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            LogEntry entry = new LogEntryBuilder().severityLevel(Level.INFO).message("Hello World").create();
            writer.log(entry);
        }

        assertThat(fetchTable(TABLE_NAME))
            .column("SEVERITY_CODE").containsValues(3)
            .column("MESSAGE").containsValues("Hello World");
    }

    /**
     * Verifies that a table in a custom schema can be used for inserting log entries.
     */
    @Test
    void useTableWithCustomSchema() throws SQLException, NamingException {
        executeSql("CREATE SCHEMA " + SCHEMA_NAME);
        createTable(SCHEMA_NAME + "." + TABLE_NAME);

        try (JdbcWriter writer = new JdbcWriter(url, null, null, SCHEMA_NAME, TABLE_NAME, emptyMap())) {
            LogEntry entry = new LogEntryBuilder().create();
            writer.log(entry);
        }

        assertThat(fetchTable(SCHEMA_NAME + "." + TABLE_NAME)).hasNumberOfRows(1);
    }

    /**
     * Verifies that cached log entries can be flushed explicitly to be inserted into the table.
     */
    @Test
    void flushExplicitly() throws SQLException, NamingException {
        createTable(TABLE_NAME, "MESSAGE VARCHAR");

        Map<String, Placeholder> fields = Map.of("MESSAGE", new MessagePlaceholder(configuration));

        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, fields)) {
            writer.log(new LogEntryBuilder().message("Hello World!").create());
            writer.flush();
            writer.log(new LogEntryBuilder().message("See you later!").create());
            assertThat(fetchTable(TABLE_NAME)).column().containsValues("Hello World!");

            writer.flush();
            assertThat(fetchTable(TABLE_NAME)).column().containsValues("Hello World!", "See you later!");
        }
    }

    /**
     * Verifies that all cached log entries are flushed automatically after the 1024th log entry.
     */
    @Test
    void flushAutomatically() throws SQLException, NamingException {
        createTable(TABLE_NAME);

        try (JdbcWriter writer = new JdbcWriter(url, null, null, null, TABLE_NAME, emptyMap())) {
            for (int i = 0; i < 1023; ++i) {
                writer.log(new LogEntryBuilder().create());
                assertThat(fetchTable(TABLE_NAME)).hasNumberOfRows(0);
            }

            writer.log(new LogEntryBuilder().create());
            assertThat(fetchTable(TABLE_NAME)).hasNumberOfRows(1024);

            for (int i = 0; i < 1023; ++i) {
                writer.log(new LogEntryBuilder().create());
                assertThat(fetchTable(TABLE_NAME)).hasNumberOfRows(1024);
            }

            writer.log(new LogEntryBuilder().create());
            assertThat(fetchTable(TABLE_NAME)).hasNumberOfRows(2048);
        }
    }

    /**
     * Verifies that an {@link SQLException} is thrown if the scheme name contains an illegal character.
     *
     * @param character The illegal character to test
     * @param quote The quote character of the database
     */
    @ParameterizedTest(name = "character = `{0}`, quote= `{1}`")
    @ArgumentsSource(IllegalCharacterProvider.class)
    void reportIllegalSchemeNames(String character, String quote) throws SQLException {
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(metaData.getIdentifierQuoteString()).thenReturn(quote);

        Connection connection = mock(Connection.class);
        when(connection.getMetaData()).thenReturn(metaData);

        String schemeName = "SCHEME_" + character;

        try (MockedStatic<DriverManager> driverMock = mockStatic(DriverManager.class)) {
            driverMock.when(() -> DriverManager.getConnection(any())).thenReturn(connection);

            assertThatThrownBy(() ->
                new JdbcWriter(url, null, null, schemeName, "FOO", emptyMap()).close()
            )
                .isExactlyInstanceOf(SQLException.class)
                .hasMessageContaining(schemeName);
        }
    }

    /**
     * Verifies that an {@link SQLException} is thrown if the table name contains an illegal character.
     *
     * @param character The illegal character to test
     * @param quote The quote character of the database
     */
    @ParameterizedTest(name = "character = `{0}`, quote= `{1}`")
    @ArgumentsSource(IllegalCharacterProvider.class)
    void reportIllegalTableNames(String character, String quote) throws SQLException {
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(metaData.getIdentifierQuoteString()).thenReturn(quote);

        Connection connection = mock(Connection.class);
        when(connection.getMetaData()).thenReturn(metaData);

        String tableName = "TABLE_" + character;

        try (MockedStatic<DriverManager> driverMock = mockStatic(DriverManager.class)) {
            driverMock.when(() -> DriverManager.getConnection(any())).thenReturn(connection);

            assertThatThrownBy(() ->
                new JdbcWriter(url, null, null, null, tableName, emptyMap()).close()
            )
                .isExactlyInstanceOf(SQLException.class)
                .hasMessageContaining(tableName);
        }
    }

    /**
     * Verifies that an {@link SQLException} is thrown if a field name contains an illegal character.
     *
     * @param character The illegal character to test
     * @param quote The quote character of the database
     */
    @ParameterizedTest(name = "character = `{0}`, quote= `{1}`")
    @ArgumentsSource(IllegalCharacterProvider.class)
    void reportIllegalFieldNames(String character, String quote) throws SQLException {
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(metaData.getIdentifierQuoteString()).thenReturn(quote);

        Connection connection = mock(Connection.class);
        when(connection.getMetaData()).thenReturn(metaData);

        String fieldName = "FIELD" + character;
        Map<String, Placeholder> fields = Map.of(fieldName, new MessagePlaceholder(configuration));

        try (MockedStatic<DriverManager> driverMock = mockStatic(DriverManager.class)) {
            driverMock.when(() -> DriverManager.getConnection(any())).thenReturn(connection);

            assertThatThrownBy(() ->
                new JdbcWriter(url, null, null, null, "FOO", fields).close()
            )
                .isExactlyInstanceOf(SQLException.class)
                .hasMessageContaining(fieldName);
        }
    }

    /**
     * Creates a database table.
     *
     * @param tableName The name of the table
     * @param columns The columns (each column contains the name and the type, separated by a single space)
     * @throws SQLException If failed to create the table
     */
    private void createTable(String tableName, String... columns) throws SQLException {
        StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE ").append(tableName).append(" (");

        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(columns[i]);
        }

        builder.append(")");

        executeSql(builder.toString());
    }

    /**
     * Fetches the entire content of a table.
     *
     * @param tableName The name of the table to fetch
     * @return The fetched table
     * @see org.assertj.db.api.Assertions#assertThat
     */
    private Table fetchTable(String tableName) {
        AssertDbConnection connection = AssertDbConnectionFactory.of(url, null, null).create();
        return connection.table(tableName).build();
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

    /**
     * Arguments provider for illegal character for database identifier like scheme, table, and field names.
     */
    private static final class IllegalCharacterProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                /* Without quote character (space according to JDBC specification) */
                Arguments.of("\u0000", " "),
                Arguments.of("\r", " "),
                Arguments.of("\n", " "),
                Arguments.of("\t", " "),
                Arguments.of("\u001F", " "),
                Arguments.of(" ", " "),
                Arguments.of("(", " "),
                Arguments.of(")", " "),
                Arguments.of("'", " "),
                Arguments.of("\"", " "),
                /* With quote character (use single quote) */
                Arguments.of("\u0000", "'"),
                Arguments.of("\r", "'"),
                Arguments.of("\n", "'"),
                Arguments.of("\t", "'"),
                Arguments.of("\u001F", "'")
            );
        }

    }

}
