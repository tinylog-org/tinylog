package org.tinylog.impl.format.json;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.impl.format.placeholder.DatePlaceholder;
import org.tinylog.impl.format.placeholder.LevelPlaceholder;
import org.tinylog.impl.format.placeholder.LinePlaceholder;
import org.tinylog.impl.format.placeholder.MessageOnlyPlaceholder;
import org.tinylog.impl.format.placeholder.MessagePlaceholder;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.impl.format.placeholder.TimestampPlaceholder;
import org.tinylog.impl.format.placeholder.UptimePlaceholder;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class NewlineDelimitedJsonTest {

    @Inject
    private RuntimeFlavor runtime;

    @Inject
    private Configuration configuration;

    /**
     * Verifies that a JSON without any field is correctly rendered.
     */
    @Test
    void renderWithoutFields() {
        Map<String, Placeholder> fields = emptyMap();
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        assertThat(format.getOutputDetails()).isEqualTo(OutputDetails.DISABLED);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);
        LogEntry logEntry = new LogEntryBuilder().create();

        assertThat(renderer.render(logEntry))
            .isEqualTo("{}" + System.lineSeparator());
    }

    /**
     * Verifies that a JSON with an integer field is correctly rendered.
     */
    @Test
    void renderWithIntegerField() {
        Map<String, Placeholder> fields = Map.of("line", new LinePlaceholder());
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        assertThat(format.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry filledLogEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 42)
            .create();
        assertThat(renderer.render(filledLogEntry))
            .isEqualTo("{\"line\": 42}" + System.lineSeparator());

        LogEntry emptyLogEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(emptyLogEntry))
            .isEqualTo("{\"line\": null}" + System.lineSeparator());
    }

    /**
     * Verifies that a JSON with a long field is correctly rendered.
     */
    @Test
    void renderWithLongField() {
        Map<String, Placeholder> fields = Map.of("timestamp", new TimestampPlaceholder(Instant::getEpochSecond));
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        assertThat(format.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"timestamp\": 0}" + System.lineSeparator());
    }

    /**
     * Verifies that a JSON with a decimal field is correctly rendered.
     */
    @Test
    void renderWithDecimalField() {
        Map<String, Placeholder> fields = Map.of("uptime", new UptimePlaceholder(runtime, "S", false));
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        assertThat(format.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder().uptime(Duration.ofMillis(42)).create();
        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"uptime\": 0.042000000}" + System.lineSeparator());
    }

    /**
     * Verifies that a JSON with a timestamp field is correctly rendered.
     */
    @Test
    void renderWithTimestampField() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT).withZone(ZoneOffset.UTC);
        Map<String, Placeholder> fields = Map.of("date", new DatePlaceholder(formatter, false));
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        assertThat(format.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"date\": \"1970-01-01\"}" + System.lineSeparator());
    }

    /**
     * Verifies that a JSON with a string field is correctly rendered.
     */
    @Test
    void renderWithStringField() {
        Map<String, Placeholder> fields = Map.of("message", new MessagePlaceholder(configuration));
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        assertThat(format.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry filledLogEntry = new LogEntryBuilder().message("Hello World!").create();
        assertThat(renderer.render(filledLogEntry))
            .isEqualTo("{\"message\": \"Hello World!\"}" + System.lineSeparator());

        LogEntry emptyLogEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(emptyLogEntry))
            .isEqualTo("{\"message\": null}" + System.lineSeparator());
    }

    /**
     * Verifies that a JSON with two fields is correctly rendered.
     */
    @Test
    void renderWithTwoFields() {
        Map<String, Placeholder> fields = new LinkedHashMap<>();
        fields.put("level", new LevelPlaceholder());
        fields.put("message", new MessageOnlyPlaceholder(configuration));
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        assertThat(format.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).message("Hello World!").create();
        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"level\": \"INFO\", \"message\": \"Hello World!\"}" + System.lineSeparator());
    }

    /**
     * Verifies that illegal characters in field names are escaped.
     *
     * @param originalName The original field name including an illegal character
     * @param escapedName The converted field name with correctly escaped character
     */
    @ParameterizedTest(name = "{1}")
    @CsvSource({
        " _\"_   , _\\\"_   ",
        " _\\_   , _\\\\_   ",
        "'_\b_'  , _\\b_    ",
        "'_\f_'  , _\\f_    ",
        "'_\n_'  , _\\n_    ",
        "'_\r_'  , _\\r_    ",
        "'_\t_'  , _\\t_    ",
        " _\0_   , _\\u0000_",
        " _\1_   , _\\u0001_",
        " _\37_  , _\\u001F_",
        " _\177_ , _\\u007F_",
        " _\237_ , _\\u009F_"
    })
    void escapeFieldName(String originalName, String escapedName) {
        Map<String, Placeholder> fields = Map.of(originalName, new MessageOnlyPlaceholder(configuration));
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder().message("foo").create();
        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"%s\": \"foo\"}" + System.lineSeparator(), escapedName);
    }

    /**
     * Verifies that illegal characters in string field values are escaped.
     *
     * @param originalValue The original field value including an illegal character
     * @param escapedValue The converted field value with correctly escaped character
     */
    @ParameterizedTest(name = "{1}")
    @CsvSource({
        " _\"_   , _\\\"_   ",
        " _\\_   , _\\\\_   ",
        "'_\b_'  , _\\b_    ",
        "'_\f_'  , _\\f_    ",
        "'_\n_'  , _\\n_    ",
        "'_\r_'  , _\\r_    ",
        "'_\t_'  , _\\t_    ",
        " _\0_   , _\\u0000_",
        " _\1_   , _\\u0001_",
        " _\37_  , _\\u001F_",
        " _\177_ , _\\u007F_",
        " _\237_ , _\\u009F_"
    })
    void escapeFieldValue(String originalValue, String escapedValue) {
        Map<String, Placeholder> fields = Map.of("foo", new StaticTextPlaceholder(originalValue));
        NewlineDelimitedJson format = new NewlineDelimitedJson(fields);

        FormatOutputRenderer renderer = new FormatOutputRenderer(format);

        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry))
            .isEqualTo("{\"foo\": \"%s\"}" + System.lineSeparator(), escapedValue);
    }

}
