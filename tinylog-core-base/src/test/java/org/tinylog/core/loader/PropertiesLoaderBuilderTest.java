package org.tinylog.core.loader;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesLoaderBuilderTest {

	/**
	 * Verifies that the name is "properties".
	 */
	@Test
	void name() {
		PropertiesLoaderBuilder builder = new PropertiesLoaderBuilder();
		assertThat(builder.getName()).isEqualTo("properties");
	}

	/**
	 * Verifies that the priority is "0".
	 */
	@Test
	void priority() {
		PropertiesLoaderBuilder builder = new PropertiesLoaderBuilder();
		assertThat(builder.getPriority()).isZero();
	}

	/**
	 * Verifies that an instance of {@link PropertiesLoader} can be created.
	 */
	@Test
	void creation() {
		PropertiesLoaderBuilder builder = new PropertiesLoaderBuilder();
		assertThat(builder.create()).isInstanceOf(PropertiesLoader.class);
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(ConfigurationLoaderBuilder.class))
			.anyMatch(builder -> builder instanceof PropertiesLoaderBuilder);
	}

}
