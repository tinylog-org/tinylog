package org.tinylog.impl.format.json;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.placeholders.LevelPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessageOnlyPlaceholder;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholder;
import org.tinylog.impl.format.pattern.placeholders.StaticTextPlaceholder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import com.google.common.collect.ImmutableMap;

import static org.assertj.core.api.Assertions.assertThat;

class LineDelimitedJsonTest {

	/**
	 * Verifies that a JSON without any field is correctly rendered.
	 */
	@Test
	void renderWithoutFields() {
		LineDelimitedJson format = new LineDelimitedJson(Collections.emptyMap());

		assertThat(format.getRequiredLogEntryValues()).isEmpty();

		LogEntry logEntry = new LogEntryBuilder().create();
		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		assertThat(renderer.render(logEntry))
			.isEqualTo("{}" + System.lineSeparator());
	}

	/**
	 * Verifies that a JSON with a single field is correctly rendered.
	 */
	@Test
	void renderWithSingleField() {
		LineDelimitedJson format = new LineDelimitedJson(Collections.singletonMap("message", new MessagePlaceholder()));

		assertThat(format.getRequiredLogEntryValues())
			.containsExactlyInAnyOrder(LogEntryValue.EXCEPTION, LogEntryValue.MESSAGE);

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		assertThat(renderer.render(logEntry))
			.isEqualTo("{\"message\": \"Hello World!\"}" + System.lineSeparator());
	}

	/**
	 * Verifies that a JSON with two fields is correctly rendered.
	 */
	@Test
	void renderWithTwoFields() {
		LineDelimitedJson format = new LineDelimitedJson(ImmutableMap.of(
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
		LineDelimitedJson format = new LineDelimitedJson(Collections.singletonMap(
			originalName, new MessageOnlyPlaceholder()
		));

		LogEntry logEntry = new LogEntryBuilder().message("foo").create();
		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		assertThat(renderer.render(logEntry))
			.isEqualTo("{\"%s\": \"foo\"}" + System.lineSeparator(), escapedName);
	}

	/**
	 * Verifies that illegal characters in field values are escaped.
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
		LineDelimitedJson format = new LineDelimitedJson(Collections.singletonMap(
			"foo", new StaticTextPlaceholder(originalValue)
		));

		LogEntry logEntry = new LogEntryBuilder().create();
		FormatOutputRenderer renderer = new FormatOutputRenderer(format);

		assertThat(renderer.render(logEntry))
			.isEqualTo("{\"foo\": \"%s\"}" + System.lineSeparator(), escapedValue);
	}

}
