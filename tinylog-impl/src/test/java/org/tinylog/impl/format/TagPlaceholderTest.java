package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().tag("foo").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo");
	}

	/**
	 * Verifies that the default value is output for untagged log entries.
	 */
	@Test
	void renderWithoutTag() {
		TagPlaceholder placeholder = new TagPlaceholder("-", null);
		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("-");
	}

	/**
	 * Verifies that the assigned tag of tagged log entries is applied to a {@link PreparedStatement}.
	 */
	@Test
	void applyWithTag() throws SQLException {
		TagPlaceholder placeholder = new TagPlaceholder(null, "-");
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().tag("foo").create();
		placeholder.apply(statement, 42, logEntry);
		verify(statement).setString(42, "foo");
	}

	/**
	 * Verifies that the default value is applied to a {@link PreparedStatement} for untagged log entries.
	 */
	@Test
	void applyWithoutTag() throws SQLException {
		TagPlaceholder placeholder = new TagPlaceholder(null, "-");
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		placeholder.apply(statement, 42, logEntry);
		verify(statement).setString(42, "-");
	}

}
