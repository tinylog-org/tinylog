package org.tinylog.impl.format;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class BundlePlaceholderTest {

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

}
