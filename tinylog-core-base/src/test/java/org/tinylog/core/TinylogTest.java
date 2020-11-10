package org.tinylog.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.test.system.CaptureSystemOutput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@CaptureSystemOutput(excludes = "TINYLOG WARN:.*tinylog-impl\\.jar.*")
class TinylogTest {

	/**
	 * Ensures that {@link Tinylog} is down before and after each test.
	 */
	@BeforeEach
	@AfterEach
	void shutDownTinylog() {
		Tinylog.shutDown();
	}

	/**
	 * Verifies that a {@link RuntimeFlavor} is provided.
	 */
	@Test
	void runtime() {
		assertThat(Tinylog.getRuntime()).isNotNull();
	}

	/**
	 * Verifies that a {@link Configuration} is provided.
	 */
	@Test
	void configuration() {
		assertThat(Tinylog.getConfiguration()).isNotNull();
	}

	/**
	 * Verifies that a {@link LoggingBackend} is provided.
	 */
	@Test
	void loggingBackend() {
		assertThat(Tinylog.getLoggingBackend()).isNotNull();
	}

	/**
	 * Verifies that the life cycle works including hook registration.
	 */
	@Test
	void lifeCycle() {
		Hook hook = mock(Hook.class);
		Tinylog.registerHook(hook);

		try {
			Tinylog.startUp();
			Tinylog.removeHook(hook);
		} finally {
			Tinylog.shutDown();
		}

		verify(hook).startUp();
		verify(hook, never()).shutDown();
	}

}
