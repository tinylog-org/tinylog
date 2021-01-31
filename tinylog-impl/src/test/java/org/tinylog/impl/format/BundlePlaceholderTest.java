package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

	/**
	 * Verifies that all child placeholders are applied as a combined string in the expected order to a
	 * {@link PreparedStatement}.
	 */
	@Test
	void apply() throws SQLException {
		StaticTextPlaceholder firstChild = new StaticTextPlaceholder("Class: ");
		ClassPlaceholder secondChild = new ClassPlaceholder();
		BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
		bundlePlaceholder.apply(statement, 42, logEntry);
		verify(statement).setString(42, "Class: foo.MyClass");
	}

}
