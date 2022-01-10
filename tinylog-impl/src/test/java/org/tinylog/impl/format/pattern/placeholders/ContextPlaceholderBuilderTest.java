package org.tinylog.impl.format.pattern.placeholders;

import java.sql.Types;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@CaptureLogEntries
class ContextPlaceholderBuilderTest {

	private static final LogEntry emptyLogEntry = new LogEntryBuilder().create();
	private static final LogEntry filledLogEntry = new LogEntryBuilder().context("foo", "boo").create();

	@Inject
	private Framework framework;

	/**
	 * Verifies that an {@link IllegalArgumentException} with a meaningful message description will be thrown, if the
	 * thread context key is missing.
	 */
	@Test
	void creationWithoutConfigurationValue() {
		assertThatThrownBy(() -> new ContextPlaceholderBuilder().create(framework, null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("key");
	}

	/**
	 * Verifies that context placeholders without any custom default value are instantiated correctly.
	 */
	@Test
	void creationWithKeyOnly() {
		Placeholder placeholder = new ContextPlaceholderBuilder().create(framework, "foo");
		assertThat(placeholder).isInstanceOf(ContextPlaceholder.class);

		FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
		assertThat(renderer.render(emptyLogEntry)).isEqualTo("<foo not set>");
		assertThat(renderer.render(filledLogEntry)).isEqualTo("boo");

		assertThat(placeholder.resolve(emptyLogEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));

		assertThat(placeholder.resolve(filledLogEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "boo"));
	}

	/**
	 * Verifies that context placeholders with a custom default value are instantiated correctly.
	 */
	@Test
	void creationWithKeyAndDefaultValue() {
		Placeholder placeholder = new ContextPlaceholderBuilder().create(framework, "foo,bar");
		assertThat(placeholder).isInstanceOf(ContextPlaceholder.class);

		FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
		assertThat(renderer.render(emptyLogEntry)).isEqualTo("bar");
		assertThat(renderer.render(filledLogEntry)).isEqualTo("boo");

		assertThat(placeholder.resolve(emptyLogEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "bar"));

		assertThat(placeholder.resolve(filledLogEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "boo"));
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(ContextPlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("context");
		});
	}

}
