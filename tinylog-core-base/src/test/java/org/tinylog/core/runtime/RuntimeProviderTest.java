package org.tinylog.core.runtime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeProviderTest {

	/**
	 * Verifies that an {@link AndroidRuntime} is provided on Android.
	 */
	@Test
	@EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	void androidRuntime() {
		RuntimeProvider backend = new RuntimeProvider();
		assertThat(backend.getRuntime()).isInstanceOf(AndroidRuntime.class);
	}

	/**
	 * Verifies that a {@link JavaRuntime} is provided on standard Java.
	 */
	@Test
	@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	void legacyJavaRuntime() {
		RuntimeProvider backend = new RuntimeProvider();
		assertThat(backend.getRuntime()).isInstanceOf(JavaRuntime.class);
	}

}
