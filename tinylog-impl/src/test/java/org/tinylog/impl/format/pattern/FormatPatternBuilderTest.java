package org.tinylog.impl.format.pattern;

import java.time.Instant;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog(configuration = {"locale=en_US", "zone=UTC"})
class FormatPatternBuilderTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(OutputFormatBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(FormatPatternBuilder.class);
            assertThat(builder.getName()).isEqualTo("pattern");
        });
    }

    /**
     * Verifies that the default format pattern will be used, if no custom format pattern is configured.
     */
    @Tinylog(configuration = {})
    @Test
    void defaultPattern() {
        OutputFormat format = new FormatPatternBuilder().create(context);
        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder()
            .timestamp(Instant.EPOCH)
            .thread(new Thread(() -> { }, "main"))
            .severityLevel(Level.INFO)
            .className("org.MyClass")
            .stackTraceElement("org.MyClass", "foo", "MyClass.java", -1)
            .message("Hello World!")
            .create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!" + System.lineSeparator());
    }

    /**
     * Verifies that a custom format pattern can be set via the configuration property "pattern".
     */
    @Tinylog(configuration = "pattern={level}: {message}")
    @Test
    void customPattern() {
        OutputFormat format = new FormatPatternBuilder().create(context);
        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .message("Hello World!")
            .create();

        assertThat(renderer.render(logEntry)).isEqualTo("INFO: Hello World!" + System.lineSeparator());
    }

}
