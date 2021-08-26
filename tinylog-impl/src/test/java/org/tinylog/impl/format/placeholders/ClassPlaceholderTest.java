package org.tinylog.impl.format.placeholders;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class ClassPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#CLASS} is defined as required by the class placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ClassPlaceholder placeholder = new ClassPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CLASS);
	}

	/**
	 * Verifies that the source class name of a log entry will be output, if set.
	 */
	@Test
	void renderWithClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo.MyClass");
	}

	/**
	 * Verifies that {@code <class unknown>} will be output, if the class name is not set.
	 */
	@Test
	void renderWithoutClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<class unknown>");
	}

	/**
	 * Verifies that the source class name of a log entry will be resolved, if set.
	 */
	@Test
	void resolveWithClassName() {
		LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
		ClassPlaceholder placeholder = new ClassPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "foo.MyClass"));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the source class name is not set.
	 */
	@Test
	void resolveWithoutClassName() {
		LogEntry logEntry = new LogEntryBuilder().create();
		ClassPlaceholder placeholder = new ClassPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));
	}

}
