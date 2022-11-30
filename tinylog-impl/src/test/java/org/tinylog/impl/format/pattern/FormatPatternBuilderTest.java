package org.tinylog.impl.format.pattern;

import java.time.Instant;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries(configuration = {"locale=en_US", "zone=UTC"})
class FormatPatternBuilderTest {

    @Inject
    private Framework framework;

    /**
     * Verifies that the default format pattern will be used, if no custom format pattern is configured.
     */
    @Test
    void defaultPattern() {
        Configuration configuration = new Configuration(emptyMap());
        OutputFormat format = new FormatPatternBuilder().create(framework, configuration);
        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder()
            .timestamp(Instant.EPOCH)
            .thread(new Thread(() -> { }, "main"))
            .severityLevel(Level.INFO)
            .className("org.MyClass")
            .methodName("foo")
            .message("Hello World!")
            .create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!" + System.lineSeparator());
    }

    /**
     * Verifies that a custom format pattern can be set via the configuration property "pattern".
     */
    @Test
    void customPattern() {
        Configuration configuration = new Configuration(singletonMap("pattern", "{level}: {message}"));
        OutputFormat format = new FormatPatternBuilder().create(framework, configuration);
        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder()
            .severityLevel(Level.INFO)
            .message("Hello World!")
            .create();

        assertThat(renderer.render(logEntry)).isEqualTo("INFO: Hello World!" + System.lineSeparator());
    }

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

}
