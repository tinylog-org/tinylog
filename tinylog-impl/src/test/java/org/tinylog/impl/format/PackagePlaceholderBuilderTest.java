package org.tinylog.impl.format;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class PackagePlaceholderBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	/**
	 * Verifies that the builder can create an instance of {@link PackagePlaceholder} without having a configuration
	 * value.
	 */
	@Test
	void creationWithoutConfigurationValue() {
		PackagePlaceholderBuilder builder = new PackagePlaceholderBuilder();
		assertThat(builder.create(framework, null)).isInstanceOf(PackagePlaceholder.class);
		assertThat(log.consume()).isEmpty();
	}

	/**
	 * Verifies that the builder can create an instance of {@link PackagePlaceholder} when having an unexpected
	 * configuration value.
	 */
	@Test
	void creationWithConfigurationValue() {
		PackagePlaceholderBuilder builder = new PackagePlaceholderBuilder();
		assertThat(builder.create(framework, "foo")).isInstanceOf(PackagePlaceholder.class);
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
			assertThat(builder).isInstanceOf(PackagePlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("package");
		});
	}

}
