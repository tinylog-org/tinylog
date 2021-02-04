package org.tinylog.impl.format;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class ProcessIdPlaceholderBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	/**
	 * Verifies that the builder can create an instance of {@link ProcessIdPlaceholder} without having a
	 * configuration value.
	 */
	@Test
	void creationWithoutConfigurationValue() {
		Placeholder placeholder = new ProcessIdPlaceholderBuilder().create(framework, null);
		assertThat(placeholder).isInstanceOf(ProcessIdPlaceholder.class);
		assertThat(log.consume()).isEmpty();

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo(Long.toString(framework.getRuntime().getProcessId()));
	}

	/**
	 * Verifies that the builder can create an instance of {@link ProcessIdPlaceholder} when having an unexpected
	 * configuration value.
	 */
	@Test
	void creationWithConfigurationValue() {
		Placeholder placeholder = new ProcessIdPlaceholderBuilder().create(framework, "foo");
		assertThat(placeholder).isInstanceOf(ProcessIdPlaceholder.class);
		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.WARN);
			assertThat(entry.getMessage()).contains("foo");
		});

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo(Long.toString(framework.getRuntime().getProcessId()));
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(ProcessIdPlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("process-id");
		});
	}

}
