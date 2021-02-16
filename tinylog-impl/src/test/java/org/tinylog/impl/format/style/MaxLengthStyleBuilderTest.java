package org.tinylog.impl.format.style;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.placeholder.PackagePlaceholder;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries
class MaxLengthStyleBuilderTest {

	@Inject
	private Framework framework;

	/**
	 * Verifies that a max length style can be created for a {@link StaticTextPlaceholder} with maximum length passed as
	 * configuration value.
	 */
	@Test
	void creationForText() {
		Placeholder textPlaceholder = new StaticTextPlaceholder("foo");
		Placeholder stylePlaceholder = new MaxLengthStyleBuilder().create(framework, textPlaceholder, "2");

		PlaceholderRenderer renderer = new PlaceholderRenderer(stylePlaceholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("fo");
	}

	/**
	 * Verifies that a max length style can be created for a {@link PackagePlaceholder} with maximum length passed as
	 * configuration value.
	 */
	@Test
	void creationForPackage() {
		Placeholder packagePlaceholder = new PackagePlaceholder();
		Placeholder stylePlaceholder = new MaxLengthStyleBuilder().create(framework, packagePlaceholder, "5");

		PlaceholderRenderer renderer = new PlaceholderRenderer(stylePlaceholder);
		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("o.foo");
	}

	/**
	 * Verifies that the configuration value must not be {@code null}.
	 */
	@Test
	void creationWithMissingMaxLength() {
		Placeholder placeholder = new StaticTextPlaceholder("foo");
		Throwable throwable = catchThrowable(() -> new MaxLengthStyleBuilder().create(framework, placeholder, null));
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
		assertThat(throwable.getMessage()).containsIgnoringCase("maximum length");
	}

	/**
	 * Verifies that a configuration value with an illegal maximum size is rejected.
	 */
	@Test
	void creationWithInvalidMaxLength() {
		Placeholder placeholder = new StaticTextPlaceholder("foo");
		assertThatCode(() -> new MaxLengthStyleBuilder().create(framework, placeholder, "bar"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("bar");
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(StyleBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(MaxLengthStyleBuilder.class);
			assertThat(builder.getName()).isEqualTo("max-length");
		});
	}

}
