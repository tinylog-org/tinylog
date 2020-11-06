package org.tinylog.core.backend;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;

import static org.assertj.core.api.Assertions.assertThat;

class NopLoggingBackendBuilderTest {

	/**
	 * Verifies that the name is "nop".
	 */
	@Test
	void name() {
		NopLoggingBackendBuilder builder = new NopLoggingBackendBuilder();
		assertThat(builder.getName()).isEqualTo("nop");
	}

	/**
	 * Verifies that an instance of {@link NopLoggingBackend} can be created.
	 */
	@Test
	void creation() {
		Framework framework = new Framework(false, false);
		NopLoggingBackendBuilder builder = new NopLoggingBackendBuilder();
		assertThat(builder.create(framework)).isInstanceOf(NopLoggingBackend.class);
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(LoggingBackendBuilder.class))
			.anyMatch(builder -> builder instanceof NopLoggingBackendBuilder);
	}

}
