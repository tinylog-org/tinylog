package org.tinylog.impl.format.style;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries
class MinLengthStyleBuilderTest {

	private final Placeholder fooPlaceholder = new StaticTextPlaceholder("foo");

	@Inject
	private Framework framework;

	/**
	 * Verifies that a min length style can be created with minimum length passed as configuration value.
	 */
	@Test
	void creationWithMinLengthOnly() {
		Placeholder stylePlaceholder = new MinLengthStyleBuilder().create(framework, fooPlaceholder, "5");
		PlaceholderRenderer renderer = new PlaceholderRenderer(stylePlaceholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo  ");
	}

	/**
	 * Verifies that a min length style can be created with minimum length and position passed as configuration value.
	 */
	@Test
	void creationWithMinLengthAndPosition() {
		Placeholder stylePlaceholder = new MinLengthStyleBuilder().create(framework, fooPlaceholder, "5,center");
		PlaceholderRenderer renderer = new PlaceholderRenderer(stylePlaceholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo(" foo ");
	}

	/**
	 * Verifies that the configuration value must not be {@code null}.
	 */
	@Test
	void creationWithMissingMinLength() {
		Throwable throwable = catchThrowable(() -> new MinLengthStyleBuilder().create(framework, fooPlaceholder, null));
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
		assertThat(throwable.getMessage()).containsIgnoringCase("minimum length");
	}

	/**
	 * Verifies that a configuration value with an illegal minimum size is rejected.
	 */
	@Test
	void creationWithInvalidMinLength() {
		assertThatCode(() -> new MinLengthStyleBuilder().create(framework, fooPlaceholder, "boo"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("boo");
	}

	/**
	 * Verifies that a configuration value with an illegal position is rejected.
	 */
	@Test
	void creationWithInvalidPosition() {
		assertThatCode(() -> new MinLengthStyleBuilder().create(framework, fooPlaceholder, "5,boo"))
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
