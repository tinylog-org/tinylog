package org.tinylog.core.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.tinylog.core.Level;
import org.tinylog.core.runtime.StackTraceLocation;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

class NopLoggingBackendTest {

	/**
	 * Verifies that logging is disabled for all severity levels.
	 *
	 * @param level The severity level to test
	 */
	@ParameterizedTest
	@EnumSource(Level.class)
	public void allLevelsDisabled(Level level) {
		NopLoggingBackend backend = new NopLoggingBackend();
		assertThat(backend.isEnabled(mock(StackTraceLocation.class), null, level)).isFalse();
	}

	/**
	 * Verifies that log entries are accepted.
	 */
	@Test
	public void acceptLogEntries() {
		new NopLoggingBackend().log(
			mock(StackTraceLocation.class), null, Level.TRACE, null, "Hello world!", null, null
		);
	}

}
