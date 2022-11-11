package org.tinylog.impl.format.json;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.placeholders.DatePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.LevelPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.LinePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessageOnlyPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.TimestampPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.UptimePlaceholder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import com.google.common.collect.ImmutableMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class NewlineDelimitedJsonTest {

	/**
	 * Verifies that a JSON without any field is correctly rendered.
	 */
	@Test
	void renderWithoutFields() {
		NewlineDelimitedJson format = new NewlineDelimitedJson(emptyMap());

		assertThat(format.getRequiredLogEntryValues()).isEmpty();

		LogEntry logEntry = new LogEntryBuilder().create();
		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		assertThat(renderer.render(logEntry))
			.isEqualTo("{}" + System.lineSeparator());
	}

	/**
	 * Verifies that a JSON with an integer field is correctly rendered.
	 */
	@Test
	void renderWithIntegerField() {
		NewlineDelimitedJson format = new NewlineDelimitedJson(singletonMap("line", new LinePlaceholder()));
		assertThat(format.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LINE);

		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		LogEntry filledLogEntry = new LogEntryBuilder().lineNumber(42).create();
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
		NewlineDelimitedJson format = new NewlineDelimitedJson(singletonMap("timestamp", new TimestampPlaceholder(
			Instant::getEpochSecond
		)));
		assertThat(format.getRequiredLogEntryValues()).containsExactly(LogEntryValue.TIMESTAMP);

		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		LogEntry filledLogEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(filledLogEntry))
			.isEqualTo("{\"timestamp\": 0}" + System.lineSeparator());

		LogEntry emptyLogEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(emptyLogEntry))
			.isEqualTo("{\"timestamp\": null}" + System.lineSeparator());
	}

	/**
	 * Verifies that a JSON with a decimal field is correctly rendered.
	 */
	@Test
	void renderWithDecimalField() {
		NewlineDelimitedJson format = new NewlineDelimitedJson(singletonMap("uptime", new UptimePlaceholder(
			"S", false
		)));
		assertThat(format.getRequiredLogEntryValues()).containsExactly(LogEntryValue.UPTIME);

		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		LogEntry filledLogEntry = new LogEntryBuilder().uptime(Duration.ofMillis(42)).create();
		assertThat(renderer.render(filledLogEntry))
			.isEqualTo("{\"uptime\": 0.042000000}" + System.lineSeparator());

		LogEntry emptyLogEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(emptyLogEntry))
			.isEqualTo("{\"uptime\": null}" + System.lineSeparator());
	}

	/**
	 * Verifies that a JSON with a timestamp field is correctly rendered.
	 */
	@Test
	void renderWithTimestampField() {
		NewlineDelimitedJson format = new NewlineDelimitedJson(singletonMap("date", new DatePlaceholder(
			DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).withZone(ZoneOffset.UTC),
			false
		)));
		assertThat(format.getRequiredLogEntryValues()).containsExactly(LogEntryValue.TIMESTAMP);

		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		LogEntry filledLogEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(filledLogEntry))
			.isEqualTo("{\"date\": \"1970-01-01\"}" + System.lineSeparator());

		LogEntry emptyLogEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(emptyLogEntry))
			.isEqualTo("{\"date\": null}" + System.lineSeparator());
	}

	/**
	 * Verifies that a JSON with a string field is correctly rendered.
	 */
	@Test
	void renderWithStringField() {
		NewlineDelimitedJson format = new NewlineDelimitedJson(singletonMap("message", new MessagePlaceholder()));
		assertThat(format.getRequiredLogEntryValues())
			.containsExactlyInAnyOrder(LogEntryValue.EXCEPTION, LogEntryValue.MESSAGE);

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
		NewlineDelimitedJson format = new NewlineDelimitedJson(ImmutableMap.of(
			"level", new LevelPlaceholder(),
			"message", new MessageOnlyPlaceholder()
		));

		assertThat(format.getRequiredLogEntryValues())
			.containsExactlyInAnyOrder(LogEntryValue.LEVEL, LogEntryValue.MESSAGE);

		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).message("Hello World!").create();
		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

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
		NewlineDelimitedJson format = new NewlineDelimitedJson(singletonMap(
			originalName, new MessageOnlyPlaceholder()
		));

		LogEntry logEntry = new LogEntryBuilder().message("foo").create();
		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

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
		NewlineDelimitedJson format = new NewlineDelimitedJson(singletonMap(
			"foo", new StaticTextPlaceholder(originalValue)
		));

		LogEntry logEntry = new LogEntryBuilder().create();
		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		assertThat(renderer.render(logEntry))
			.isEqualTo("{\"foo\": \"%s\"}" + System.lineSeparator(), escapedValue);
	}

}
