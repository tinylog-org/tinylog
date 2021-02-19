package org.tinylog.impl.format.style;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.placeholder.ClassPlaceholder;
import org.tinylog.impl.format.placeholder.MessageOnlyPlaceholder;
import org.tinylog.impl.format.placeholder.PackagePlaceholder;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.StaticTextPlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries
class LengthStyleBuilderTest {

	@Inject
	private Framework framework;

	/**
	 * Verifies that a length style can be created for a placeholder with plain text output.
	 */
	@Test
	void creationForPText() {
		Placeholder placeholder = new MessageOnlyPlaceholder();
		Placeholder styled = new LengthStyleBuilder().create(framework, placeholder, "10");
		PlaceholderRenderer renderer = new PlaceholderRenderer(styled);

		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello W...");

		logEntry = new LogEntryBuilder().message("Hi World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hi World! ");
	}

	/**
	 * Verifies that a length style can be created for a {@link ClassPlaceholder} with length passed as configuration
	 * value.
	 */
	@Test
	void creationForClass() {
		Placeholder placeholder = new ClassPlaceholder();
		Placeholder styled = new LengthStyleBuilder().create(framework, placeholder, "12");
		PlaceholderRenderer renderer = new PlaceholderRenderer(styled);

		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("o.f.MyClass ");
	}

	/**
	 * Verifies that a length style can be created for a {@link PackagePlaceholder} with length passed as configuration
	 * value.
	 */
	@Test
	void creationForPackage() {
		Placeholder placeholder = new PackagePlaceholder();
		Placeholder styled = new LengthStyleBuilder().create(framework, placeholder, "4");
		PlaceholderRenderer renderer = new PlaceholderRenderer(styled);

		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("o.f ");
	}

	/**
	 * Verifies that a custom position can be passed via configuration value.
	 */
	@Test
	void creationWithCustomPosition() {
		Placeholder placeholder = new StaticTextPlaceholder("foo");
		Placeholder styled = new LengthStyleBuilder().create(framework, placeholder, "5,right");
		PlaceholderRenderer renderer = new PlaceholderRenderer(styled);

		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("  foo");
	}

	/**
	 * Verifies that the configuration value must not be {@code null}.
	 */
	@Test
	void creationWithMissingLength() {
		Placeholder placeholder = new StaticTextPlaceholder("foo");
		Throwable throwable = catchThrowable(() -> new LengthStyleBuilder().create(framework, placeholder, null));
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
		assertThat(throwable.getMessage()).containsIgnoringCase("length");
	}

	/**
	 * Verifies that a configuration value with an illegal length is rejected.
	 */
	@Test
	void creationWithInvalidLength() {
		Placeholder placeholder = new StaticTextPlaceholder("foo");
		assertThatCode(() -> new LengthStyleBuilder().create(framework, placeholder, "boo"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("boo");
	}

	/**
	 * Verifies that a configuration value with an illegal position is rejected.
	 */
	@Test
	void creationWithInvalidPosition() {
		Placeholder placeholder = new StaticTextPlaceholder("foo");
		assertThatCode(() -> new LengthStyleBuilder().create(framework, placeholder, "5,boo"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("boo");
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(StyleBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(LengthStyleBuilder.class);
			assertThat(builder.getName()).isEqualTo("length");
		});
	}

}
