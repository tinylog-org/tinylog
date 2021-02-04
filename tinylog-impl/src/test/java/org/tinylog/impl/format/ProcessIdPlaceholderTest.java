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

class ProcessIdPlaceholderTest {

	/**
	 * Verifies that no log entry values are defined as required by the process ID placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ProcessIdPlaceholder placeholder = new ProcessIdPlaceholder(42);
		assertThat(placeholder.getRequiredLogEntryValues()).isEmpty();
	}

	/**
	 * Verifies that the passed process ID is output.
	 */
	@Test
	void render() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ProcessIdPlaceholder(1000));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("1000");
	}

	/**
	 * Verifies that the passed process ID is applied to a {@link PreparedStatement}.
	 */
	@Test
	void apply() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new ProcessIdPlaceholder(1000).apply(statement, 42, logEntry);
		verify(statement).setLong(42, 1000);
	}

}
