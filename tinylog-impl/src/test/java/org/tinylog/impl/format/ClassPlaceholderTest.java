package org.tinylog.impl.format;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class ClassPlaceholderTest {

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
	 * Verifies that "&lt;unknown&gt;" will be output, if the class name is not set.
	 */
	@Test
	void renderWithoutClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<unknown>");
	}

}
