package org.tinylog.impl.writer.jdbc;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.FormatPatternParser;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.writer.Writer;
import org.tinylog.impl.writer.WriterBuilder;

/**
 * Builder for creating an instance of {@link JdbcWriter}.
 */
public class JdbcWriterBuilder implements WriterBuilder {

    private static final String URL_KEY = "url";
    private static final String SCHEMA_KEY = "schema";
    private static final String TABLE_KEY = "table";
    private static final String FIELDS_KEY = "fields";
    private static final String USER_KEY = "user";
    private static final String PASSWORD_KEY = "password";

    /** */
    public JdbcWriterBuilder() {
    }

    @Override
    public String getName() {
        return "jdbc";
    }

    @Override
    public Writer create(TinylogContext context) throws SQLException, NamingException {
        Configuration configuration = context.getConfiguration();
        InternalLogger logger = context.getLogger();
        FormatPatternParser parser = new FormatPatternParser(context);

        String url = configuration.getValue(URL_KEY);
        if (url == null) {
            String fullKey = configuration.resolveFullKey(URL_KEY);
            throw new IllegalArgumentException("Database URL is missing in required property \"" + fullKey + "\"");
        }

        String table = configuration.getValue(TABLE_KEY);
        if (table == null) {
            String fullKey = configuration.resolveFullKey(TABLE_KEY);
            throw new IllegalArgumentException("Table name is missing in required property \"" + fullKey + "\"");
        }

        String schema = configuration.getValue(SCHEMA_KEY);
        String user = configuration.getValue(USER_KEY);
        String password = configuration.getValue(PASSWORD_KEY);
        Map<String, Placeholder> fields = parseFields(configuration.getSubConfiguration(FIELDS_KEY), parser, logger);

        return new JdbcWriter(url, user, password, schema, table, fields);
    }

    /**
     * Creates value placeholders for all fields.
     *
     * @param configuration The sub configuration with the mapping of column names and value placeholders
     * @param parser The parser to use for parsing the value placeholders
     * @param logger The internal logger instance for issuing internal tinylog log entries
     * @return All configured column names mapped to their value placeholders
     */
    private Map<String, Placeholder> parseFields(
        Configuration configuration,
        FormatPatternParser parser,
        InternalLogger logger
    ) {
        Map<String, Placeholder> fields = new LinkedHashMap<>();

        for (String key : configuration.getKeys()) {
            fields.put(key, parser.parse(configuration.getValue(key)));
        }

        if (fields.isEmpty()) {
            logger.log(Level.WARN, "No fields defined for the relational database table");
        }

        return fields;
    }

}
