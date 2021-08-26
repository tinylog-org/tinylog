package org.tinylog.impl.format.placeholders;

import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class BundlePlaceholderTest {

	/**
	 * Verifies that all log entry values that are required by any child placeholder are also required by the bundle
	 * placeholder itself.
	 */
	@Test
	void requiredLogEntryValues() {
		DatePlaceholder firstChild = new DatePlaceholder(DateTimeFormatter.ISO_INSTANT, false);
		ClassPlaceholder secondChild = new ClassPlaceholder();
		BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

		assertThat(bundlePlaceholder.getRequiredLogEntryValues())
			.containsExactlyInAnyOrder(LogEntryValue.CLASS, LogEntryValue.TIMESTAMP);
	}

	/**
	 * Verifies that all child placeholders are rendered correctly and in the expected order.
	 */
	@Test
	void render() {
		StaticTextPlaceholder firstChild = new StaticTextPlaceholder("Class: ");
		ClassPlaceholder secondChild = new ClassPlaceholder();
		BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

		PlaceholderRenderer renderer = new PlaceholderRenderer(bundlePlaceholder);
		LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("Class: foo.MyClass");
	}

	/**
	 * Verifies that all child placeholders are resolved as a combined char sequence in the expected order.
	 */
	@Test
	void resolve() {
		StaticTextPlaceholder firstChild = new StaticTextPlaceholder("Class: ");
		ClassPlaceholder secondChild = new ClassPlaceholder();
		BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

		LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
		assertThat(bundlePlaceholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.LONGVARCHAR, "Class: foo.MyClass"));
	}

}
