package org.tinylog.impl.format.placeholder;

import java.time.Instant;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class TimestampPlaceholderBuilderTest {

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
        assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(TimestampPlaceholderBuilder.class);
            assertThat(builder.getName()).isEqualTo("timestamp");
        });
    }

    /**
     * Verifies that the configuration values {@code null}, "" (empty string), and "seconds" are resolved into
     * timestamp placeholders that output the UNIX time of issue in seconds.
     *
     * @param configurationValue The time unit for the timestamp placeholder
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "seconds"})
    void creationWithSeconds(String configurationValue) {
        Placeholder placeholder = new TimestampPlaceholderBuilder().create(context, configurationValue);
        assertThat(placeholder).isInstanceOf(TimestampPlaceholder.class);

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1234)).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1");
    }

    /**
     * Verifies that the configuration value "milliseconds" is resolved into a timestamp placeholder that outputs the
     * UNIX time of issue in seconds.
     *
     * @param configurationValue The time unit for the timestamp placeholder
     */
    @ParameterizedTest
    @ValueSource(strings = "milliseconds")
    void creationWithMilliseconds(String configurationValue) {
        Placeholder placeholder = new TimestampPlaceholderBuilder().create(context, configurationValue);
        assertThat(placeholder).isInstanceOf(TimestampPlaceholder.class);

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1234)).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1234");
    }

    /**
     * Verifies that the configuration values with unsupported time units are resolved into timestamp placeholders that
     * output the UNIX time of issue in seconds, and a warning is logged.
     *
     * @param configurationValue The time unit for the timestamp placeholder
     */
    @ParameterizedTest
    @ValueSource(strings = {"foo", "minutes", "hours"})
    void creationWithUnsupportedTimeUnit(String configurationValue) {
        Placeholder placeholder = new TimestampPlaceholderBuilder().create(context, configurationValue);
        assertThat(placeholder).isInstanceOf(TimestampPlaceholder.class);
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.WARN);
            assertThat(entry.getFormattedMessage(configuration)).contains(configurationValue);
        });

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1234)).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1");
    }

}
