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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@CaptureLogEntries
class TagPlaceholderBuilderTest {

	private static final LogEntry emptyLogEntry = new LogEntryBuilder().create();
	private static final LogEntry filledLogEntry = new LogEntryBuilder().tag("foo").create();

	@Inject
	private Framework framework;

	/**
	 * Verifies that tag placeholders without any custom default value are instantiated correctly.
	 */
	@Test
	void creationWithoutDefaultValue() throws SQLException {
		Placeholder placeholder = new TagPlaceholderBuilder().create(framework, null);
		assertThat(placeholder).isInstanceOf(TagPlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		assertThat(renderer.render(emptyLogEntry)).isEqualTo("<untagged>");
		assertThat(renderer.render(filledLogEntry)).isEqualTo("foo");

		PreparedStatement statement = mock(PreparedStatement.class);
		placeholder.apply(statement, 42, emptyLogEntry);
		verify(statement).setString(42, null);
		placeholder.apply(statement, 42, filledLogEntry);
		verify(statement).setString(42, "foo");
	}

	/**
	 * Verifies that tag placeholders with a custom default value are instantiated correctly.
	 */
	@Test
	void creationWithDefaultValue() throws SQLException {
		Placeholder placeholder = new TagPlaceholderBuilder().create(framework, "none");
		assertThat(placeholder).isInstanceOf(TagPlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		assertThat(renderer.render(emptyLogEntry)).isEqualTo("none");
		assertThat(renderer.render(filledLogEntry)).isEqualTo("foo");

		PreparedStatement statement = mock(PreparedStatement.class);
		placeholder.apply(statement, 42, emptyLogEntry);
		verify(statement).setString(42, "none");
		placeholder.apply(statement, 42, filledLogEntry);
		verify(statement).setString(42, "foo");
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(TagPlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("tag");
		});
	}

}
