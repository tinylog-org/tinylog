package org.tinylog.impl.format.placeholder;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class UptimePlaceholderBuilderTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(PlaceholderBuilder.class))
            .anyMatch(loader -> loader instanceof UptimePlaceholderBuilder);
    }

    /**
     * Verifies that the name is "uptime".
     */
    @Test
    void name() {
        UptimePlaceholderBuilder builder = new UptimePlaceholderBuilder();
        assertThat(builder.getName()).isEqualTo("uptime");
    }

    /**
     * Verifies that the builder can create a valid {@link UptimePlaceholder} with default format pattern.
     */
    @Test
    void creationWithDefaultPattern() {
        Duration uptime = Duration.ofHours(2).minusSeconds(30);
        LogEntry logEntry = new LogEntryBuilder().uptime(uptime).create();
        Placeholder placeholder = new UptimePlaceholderBuilder().create(context, null);
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);

        assertThat(placeholder.getValue(logEntry)).isEqualTo(new BigDecimal("7170.000000000"));
        assertThat(renderer.render(logEntry)).isEqualTo("01:59:30");
    }

    /**
     * Verifies that the builder can create a valid {@link UptimePlaceholder} with custom format pattern.
     */
    @Test
    void creationWithCustomPattern() {
        Duration uptime = Duration.ofHours(2).minusSeconds(30);
        LogEntry logEntry = new LogEntryBuilder().uptime(uptime).create();
        Placeholder placeholder = new UptimePlaceholderBuilder().create(context, "s.SSS");
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);

        assertThat(placeholder.getValue(logEntry)).isEqualTo("7170.000");
        assertThat(renderer.render(logEntry)).isEqualTo("7170.000");
    }

}

