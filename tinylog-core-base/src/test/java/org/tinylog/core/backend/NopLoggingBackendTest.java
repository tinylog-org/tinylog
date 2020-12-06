package org.tinylog.core.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Level;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.runtime.StackTraceLocation;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

class NopLoggingBackendTest {

	/**
	 * Verifies that the provided context storage does not store any context values.
	 */
	@Test
	void contextStorage() {
		ContextStorage storage = new NopLoggingBackend().getContextStorage();
		storage.put("foo", "42");
		assertThat(storage.getMapping()).isEmpty();
	}

	/**
	 * Verifies that all severity levels are disabled in the precalculated level visibility object.
	 *
	 * @param tag The category tag to test
	 */
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"tinylog", "foo"})
	void visibility(String tag) {
		LevelVisibility visibility = new NopLoggingBackend().getLevelVisibility(tag);

		assertThat(visibility.isTraceEnabled()).isFalse();
		assertThat(visibility.isDebugEnabled()).isFalse();
		assertThat(visibility.isInfoEnabled()).isFalse();
		assertThat(visibility.isWarnEnabled()).isFalse();
		assertThat(visibility.isErrorEnabled()).isFalse();
	}

	/**
	 * Verifies that logging is disabled for all severity levels.
	 *
	 * @param level The severity level to test
	 */
	@ParameterizedTest
	@EnumSource(Level.class)
	void allLevelsDisabled(Level level) {
		NopLoggingBackend backend = new NopLoggingBackend();
		assertThat(backend.isEnabled(mock(StackTraceLocation.class), null, level)).isFalse();
	}

	/**
	 * Verifies that log entries are accepted.
	 */
	@Test
	void acceptLogEntries() {
		new NopLoggingBackend().log(
			mock(StackTraceLocation.class), null, Level.TRACE, null, "Hello world!", null, null
		);
	}

}
