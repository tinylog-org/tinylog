package org.tinylog.impl.format.json;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class NewlineDelimitedJsonBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    @Inject
    private Log log;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(OutputFormatBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(NewlineDelimitedJsonBuilder.class);
            assertThat(builder.getName()).isEqualTo("ndjson");
        });
    }

    /**
     * Verifies that a warning will be logged and an empty JSON will be output, if no fields are defined.
     */
    @Tinylog(configuration = {})
    @Test
    void noFields() {
        OutputFormat format = new NewlineDelimitedJsonBuilder().create(context);

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains("fields");
        });

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);
        LogEntry logEntry = new LogEntryBuilder().create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("{}" + System.lineSeparator());
    }

    /**
     * Verifies that a single field can be correctly output as JSON.
     */
    @Tinylog(configuration = "fields.level=level")
    @Test
    void singleField() {
        OutputFormat format = new NewlineDelimitedJsonBuilder().create(context);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);
        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"level\": \"INFO\"}" + System.lineSeparator());
    }

    /**
     * Verifies that multiple fields can be correctly output as JSON in the defined order.
     */
    @Tinylog(configuration = {"fields.level={level}", "fields.foo=bar", "fields.msg={message}"})
    @Test
    void multipleFields() {
        OutputFormat format = new NewlineDelimitedJsonBuilder().create(context);
        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .message("Hello World!")
            .create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"level\": \"INFO\", \"foo\": \"bar\", \"msg\": \"Hello World!\"}" + System.lineSeparator());
    }

}
