package org.tinylog.impl.format.style;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.test.StyleRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries
class MinLengthStyleBuilderTest {

	@Inject
	private Framework framework;

	/**
	 * Verifies that a min length style can be created with minimum length passed as configuration value.
	 */
	@Test
	void creationWithMinLengthOnly() {
		Style style = new MinLengthStyleBuilder().create(framework, "5");
		StyleRenderer renderer = new StyleRenderer(style);
		assertThat(renderer.render("foo")).isEqualTo("foo  ");
	}

	/**
	 * Verifies that a min length style can be created with minimum length and position passed as configuration value.
	 */
	@Test
	void creationWithMinLengthAndPosition() {
		Style style = new MinLengthStyleBuilder().create(framework, "5,center");
		StyleRenderer renderer = new StyleRenderer(style);
		assertThat(renderer.render("foo")).isEqualTo(" foo ");
	}

	/**
	 * Verifies that the configuration value must not be {@code null}.
	 */
	@Test
	void creationWithMissingMinLength() {
		Throwable throwable = catchThrowable(() -> new MinLengthStyleBuilder().create(framework, null));
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
		assertThat(throwable.getMessage()).containsIgnoringCase("minimum length");
	}

	/**
	 * Verifies that a configuration value with an illegal minimum size is rejected.
	 */
	@Test
	void creationWithInvalidMinLength() {
		assertThatCode(() -> new MinLengthStyleBuilder().create(framework, "boo"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("boo");
	}

	/**
	 * Verifies that a configuration value with an illegal position is rejected.
	 */
	@Test
	void creationWithInvalidPosition() {
		assertThatCode(() -> new MinLengthStyleBuilder().create(framework, "5,boo"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("boo");
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(StyleBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(MinLengthStyleBuilder.class);
			assertThat(builder.getName()).isEqualTo("min-length");
		});
	}

}
