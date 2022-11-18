package org.tinylog.impl.format.json;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class NewlineDelimitedJsonBuilderTest {

    @Inject
    private Framework framework;

    @Inject
    private Log log;

    /**
     * Verifies that a warning will be logged and an empty JSON will be output, if no fields are defined.
     */
    @Test
    void noFields() {
        Configuration configuration = new Configuration();
        OutputFormat format = new NewlineDelimitedJsonBuilder().create(framework, configuration);

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getMessage()).contains("fields");
        });

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);
        LogEntry logEntry = new LogEntryBuilder().create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("{}" + System.lineSeparator());
    }

    /**
     * Verifies that a single field can be correctly output as JSON.
     */
    @Test
    void singleField() {
        Configuration configuration = new Configuration().set("fields.level", "level");
        OutputFormat format = new NewlineDelimitedJsonBuilder().create(framework, configuration);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);
        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"level\": \"INFO\"}" + System.lineSeparator());
    }

    /**
     * Verifies that multiple fields can be correctly output as JSON in the defined order.
     */
    @Test
    void multipleFields() {
        Configuration configuration = new Configuration()
            .set("fields.level", "{level}")
            .set("fields.foo", "bar")
            .set("fields.msg", "{message}");

        OutputFormat format = new NewlineDelimitedJsonBuilder().create(framework, configuration);
        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .message("Hello World!")
            .create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"level\": \"INFO\", \"foo\": \"bar\", \"msg\": \"Hello World!\"}" + System.lineSeparator());
    }

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

}
