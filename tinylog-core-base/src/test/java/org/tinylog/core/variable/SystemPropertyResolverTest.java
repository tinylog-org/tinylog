package org.tinylog.core.variable;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.assertj.core.api.Assertions.assertThat;

class SystemPropertyResolverTest {

	/**
	 * Verifies that the name is "system property".
	 */
	@Test
	void name() {
		SystemPropertyResolver resolver = new SystemPropertyResolver();
		assertThat(resolver.getName()).isEqualTo("system property");
	}

	/**
	 * Verifies that the prefix character is "#".
	 */
	@Test
	void prefix() {
		SystemPropertyResolver resolver = new SystemPropertyResolver();
		assertThat(resolver.getPrefix()).isEqualTo("#");
	}

	/**
	 * Verifies that the value of an existing system property can be resolved.
	 */
	@Test
	void resolveExistingProperty() throws Exception {
		SystemPropertyResolver resolver = new SystemPropertyResolver();

		restoreSystemProperties(() -> {
			System.setProperty("foo", "bar");
			assertThat(resolver.resolve("foo")).isEqualTo("bar");
		});
	}

	/**
	 * Verifies that {@code null} is returned for a non-existent system property.
	 */
	@Test
	void resolveMissingProperty() throws Exception {
		SystemPropertyResolver resolver = new SystemPropertyResolver();

		restoreSystemProperties(() -> {
			System.clearProperty("foo");
			assertThat(resolver.resolve("foo")).isNull();
		});
	}

	/**
	 * Verifies that the resolver is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(VariableResolver.class))
			.anyMatch(loader -> loader instanceof SystemPropertyResolver);
	}

}
