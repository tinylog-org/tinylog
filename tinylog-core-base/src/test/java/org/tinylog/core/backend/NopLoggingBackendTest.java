package org.tinylog.core.backend;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.runtime.StackTraceLocation;

import static org.mockito.Mockito.mock;

class NopLoggingBackendTest {

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
