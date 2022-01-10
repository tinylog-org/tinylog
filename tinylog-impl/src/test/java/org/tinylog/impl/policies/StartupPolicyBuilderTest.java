package org.tinylog.impl.policies;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class StartupPolicyBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	/**
	 * Verifies that the builder can create an instance of {@link StartupPolicy} without having a configuration value.
	 */
	@Test
	void creationWithoutConfigurationValue() {
		StartupPolicyBuilder builder = new StartupPolicyBuilder();
		assertThat(builder.create(framework, null)).isInstanceOf(StartupPolicy.class);
		assertThat(log.consume()).isEmpty();
	}

	/**
	 * Verifies that the builder can create an instance of {@link StartupPolicy} when having an unexpected configuration
	 * value.
	 */
	@Test
	void creationWithConfigurationValue() {
		StartupPolicyBuilder builder = new StartupPolicyBuilder();
		assertThat(builder.create(framework, "foo")).isInstanceOf(StartupPolicy.class);
		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.WARN);
			assertThat(entry.getMessage()).contains("foo");
		});
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		Assertions.assertThat(ServiceLoader.load(PolicyBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(StartupPolicyBuilder.class);
			assertThat(builder.getName()).isEqualTo("startup");
		});
	}

}
