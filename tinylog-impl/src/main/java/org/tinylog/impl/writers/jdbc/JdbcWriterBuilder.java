package org.tinylog.impl.writers.jdbc;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.pattern.FormatPatternParser;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.WriterBuilder;

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
    public Writer create(Framework framework, Configuration configuration) throws SQLException, NamingException {
        FormatPatternParser parser = new FormatPatternParser(framework);

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
        Map<String, Placeholder> fields = parseFields(configuration.getSubConfiguration(FIELDS_KEY), parser);

        return new JdbcWriter(url, user, password, schema, table, fields);
    }

    /**
     * Creates value placeholder for all fields.
     *
     * @param configuration The sub configuration with the mapping of column names and value placeholders
     * @param parser The parser for the value placeholders
     * @return All configured column names mapped to their value placeholder
     */
    private Map<String, Placeholder> parseFields(Configuration configuration, FormatPatternParser parser) {
        Map<String, Placeholder> fields = new LinkedHashMap<>();

        for (String key : configuration.getKeys()) {
            fields.put(key, parser.parse(configuration.getValue(key)));
        }

        if (fields.isEmpty()) {
            InternalLogger.warn(null, "No fields defined for relational database table");
        }

        return fields;
    }

}
