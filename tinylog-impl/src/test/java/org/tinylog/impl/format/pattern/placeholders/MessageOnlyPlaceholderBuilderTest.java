package org.tinylog.impl.format.pattern.placeholders;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class MessageOnlyPlaceholderBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	/**
	 * Verifies that the builder can create an instance of {@link MessageOnlyPlaceholder} without having a configuration
	 * value.
	 */
	@Test
	void creationWithoutConfigurationValue() {
		MessageOnlyPlaceholderBuilder builder = new MessageOnlyPlaceholderBuilder();
		assertThat(builder.create(framework, null)).isInstanceOf(MessageOnlyPlaceholder.class);
		assertThat(log.consume()).isEmpty();
	}

	/**
	 * Verifies that the builder can create an instance of {@link MessageOnlyPlaceholder} when having an unexpected
	 * configuration value.
	 */
	@Test
	void creationWithConfigurationValue() {
		MessageOnlyPlaceholderBuilder builder = new MessageOnlyPlaceholderBuilder();
		assertThat(builder.create(framework, "foo")).isInstanceOf(MessageOnlyPlaceholder.class);
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
			assertThat(builder).isInstanceOf(MessageOnlyPlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("message-only");
		});
	}

}
