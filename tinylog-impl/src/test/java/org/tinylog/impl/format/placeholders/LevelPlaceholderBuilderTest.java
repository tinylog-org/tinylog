package org.tinylog.impl.format.placeholders;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class LevelPlaceholderBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	/**
	 * Verifies that the builder can create an instance of {@link LevelPlaceholder} without having a configuration
	 * value.
	 */
	@Test
	void creationWithoutConfigurationValue() {
		LevelPlaceholderBuilder builder = new LevelPlaceholderBuilder();
		assertThat(builder.create(framework, null)).isInstanceOf(LevelPlaceholder.class);
		assertThat(log.consume()).isEmpty();
	}

	/**
	 * Verifies that the builder can create an instance of {@link LevelPlaceholder} when having an unexpected
	 * configuration value.
	 */
	@Test
	void creationWithConfigurationValue() {
		LevelPlaceholderBuilder builder = new LevelPlaceholderBuilder();
		assertThat(builder.create(framework, "foo")).isInstanceOf(LevelPlaceholder.class);
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
		assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(LevelPlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("level");
		});
	}

}
