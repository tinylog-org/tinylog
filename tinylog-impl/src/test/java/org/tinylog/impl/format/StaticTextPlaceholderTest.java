package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StaticTextPlaceholderTest {

	/**
	 * Verifies that none log entry values are defined as required by the static text placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		StaticTextPlaceholder placeholder = new StaticTextPlaceholder("Hello World!");
		assertThat(placeholder.getRequiredLogEntryValues()).isEmpty();
	}

	/**
	 * Verifies that the passed static text is output unchanged.
	 */
	@Test
	void render() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new StaticTextPlaceholder("Hello World!"));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that the passed static text is applied unchanged to a {@link PreparedStatement}.
	 */
	@Test
	void apply() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new StaticTextPlaceholder("Hello World!").apply(statement, 42, logEntry);
		verify(statement).setString(42, "Hello World!");
	}

}
