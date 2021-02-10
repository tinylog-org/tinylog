package org.tinylog.impl.format.placeholder;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class ClassNamePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#CLASS} is defined as required by the class name
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CLASS);
	}

	/**
	 * Verifies that the simple source class name of a log entry will be output, if a simple class name is set.
	 */
	@Test
	void renderWithSimpleClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassNamePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("MyClass");
	}

	/**
	 * Verifies that the simple source class name of a log entry will be output, if a fully-qualified class name is set.
	 */
	@Test
	void renderWithFullyQualifiedClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassNamePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("MyClass");
	}

	/**
	 * Verifies that {@code <class unknown>} will be output, if the class name is not set.
	 */
	@Test
	void renderWithoutClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassNamePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<class unknown>");
	}

	/**
	 * Verifies that the simple source class name of a log entry will be resolved, if a simple class name is set.
	 */
	@Test
	void resolveWithSimpleClassName() {
		LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
		ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "MyClass"));
	}

	/**
	 * Verifies that the simple source class name of a log entry will be resolved, if a fully-qualified class name is
	 * set.
	 */
	@Test
	void resolveWithFullyQualifiedClassName() {
		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "MyClass"));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the source class name is not set.
	 */
	@Test
	void resolveWithoutClassName() {
		LogEntry logEntry = new LogEntryBuilder().create();
		ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));
	}

}
