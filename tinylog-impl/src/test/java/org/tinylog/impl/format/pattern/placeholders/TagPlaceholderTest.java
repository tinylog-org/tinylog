package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class TagPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#TAG} is defined as required by the tag code placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		TagPlaceholder placeholder = new TagPlaceholder(null, null);
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.TAG);
	}

	/**
	 * Verifies that the assigned tag of tagged log entries is output.
	 */
	@Test
	void renderWithTag() {
		TagPlaceholder placeholder = new TagPlaceholder("-", null);
		FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().tag("foo").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo");
	}

	/**
	 * Verifies that the default value is output for untagged log entries.
	 */
	@Test
	void renderWithoutTag() {
		TagPlaceholder placeholder = new TagPlaceholder("-", null);
		FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("-");
	}

	/**
	 * Verifies that the assigned tag of tagged log entries is resolved.
	 */
	@Test
	void resolveWithTag() {
		TagPlaceholder placeholder = new TagPlaceholder(null, "-");
		LogEntry logEntry = new LogEntryBuilder().tag("foo").create();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(SqlType.STRING, "foo"));
	}

	/**
	 * Verifies that the default value is resolved for untagged log entries.
	 */
	@Test
	void resolveWithoutTag() {
		TagPlaceholder placeholder = new TagPlaceholder(null, "-");
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(SqlType.STRING, "-"));
	}

}
