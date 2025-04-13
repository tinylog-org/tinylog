package org.tinylog.impl.format.placeholder;

import java.time.Instant;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
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

class DatePlaceholderBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    @Inject
    private Log log;

    /**
     * Verifies that the builder is registered as service.
     */
    @Tinylog
    @Test
    void service() {
        assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(DatePlaceholderBuilder.class);
            assertThat(builder.getName()).isEqualTo("date");
        });
    }

    /**
     * Verifies that the builder can create a valid {@link DatePlaceholder} with default pattern.
     */
    @Tinylog(configuration = {"locale=de_DE", "zone=Europe/Berlin"})
    @Test
    void defaultCreationForGermany() {
        Placeholder placeholder = new DatePlaceholderBuilder().create(context, null);
        assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01 01:00:00");
    }

    /**
     * Verifies that the builder can create a valid {@link DatePlaceholder} with default pattern.
     */
    @Tinylog(configuration = {"locale=en_NZ", "zone=Pacific/Auckland"})
    @Test
    void defaultCreationForNewZealand() {
        Placeholder placeholder = new DatePlaceholderBuilder().create(context, null);
        assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01 12:00:00");
    }

    /**
     * Verifies that the builder can create a valid {@link DatePlaceholder} with custom pattern.
     */
    @Tinylog(configuration = {"locale=de_DE", "zone=Europe/Berlin"})
    @Test
    void customCreationForGermany() {
        Placeholder placeholder = new DatePlaceholderBuilder().create(context, "d. MMMM y - HH:mm");
        assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1. Januar 1970 - 01:00");
    }

    /**
     * Verifies that the builder can create a valid {@link DatePlaceholder} with custom pattern.
     */
    @Tinylog(configuration = {"locale=en_NZ", "zone=Pacific/Auckland"})
    @Test
    void customCreationForNewZealand() {
        Placeholder placeholder = new DatePlaceholderBuilder().create(context, "d MMMM y - h.mm a");
        assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry)).matches("1 January 1970 - 12\\.00 (pm|PM)");
    }

    /**
     * Verifies that the builder can create a valid {@link DatePlaceholder} with fallback for invalid custom pattern.
     */
    @Tinylog(configuration = {"locale=en_US", "zone=UTC"})
    @Test
    void fallbackForInvalidPattern() {
        Placeholder placeholder = new DatePlaceholderBuilder().create(context, "INVALID <{|#|}>");
        assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01 00:00:00");
        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("INVALID <{|#|}>");
        });
    }

}
