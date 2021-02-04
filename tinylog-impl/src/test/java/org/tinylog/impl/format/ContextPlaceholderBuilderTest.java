package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
	void creationWithKeyOnly() throws SQLException {
		Placeholder placeholder = new ContextPlaceholderBuilder().create(framework, "foo");
		assertThat(placeholder).isInstanceOf(ContextPlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		assertThat(renderer.render(emptyLogEntry)).isEqualTo("<foo not set>");
		assertThat(renderer.render(filledLogEntry)).isEqualTo("boo");

		PreparedStatement statement = mock(PreparedStatement.class);
		placeholder.apply(statement, 42, emptyLogEntry);
		verify(statement).setString(42, null);
		placeholder.apply(statement, 42, filledLogEntry);
		verify(statement).setString(42, "boo");
	}

	/**
	 * Verifies that context placeholders with a custom default value are instantiated correctly.
	 */
	@Test
	void creationWithKeyAndDefaultValue() throws SQLException {
		Placeholder placeholder = new ContextPlaceholderBuilder().create(framework, "foo,bar");
		assertThat(placeholder).isInstanceOf(ContextPlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		assertThat(renderer.render(emptyLogEntry)).isEqualTo("bar");
		assertThat(renderer.render(filledLogEntry)).isEqualTo("boo");

		PreparedStatement statement = mock(PreparedStatement.class);
		placeholder.apply(statement, 42, emptyLogEntry);
		verify(statement).setString(42, "bar");
		placeholder.apply(statement, 42, filledLogEntry);
		verify(statement).setString(42, "boo");
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
