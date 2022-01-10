package org.tinylog.impl.format.pattern.placeholders;

import java.time.Instant;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class TimestampPlaceholderBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	/**
	 * Verifies that the configuration values {@code null}, "" (empty string), and "seconds" are resolved into
	 * timestamp placeholders that output the UNIX time of issue in seconds.
	 *
	 * @param configurationValue The time unit for the timestamp placeholder
	 */
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "seconds"})
	void creationWithSeconds(String configurationValue) {
		Placeholder placeholder = new TimestampPlaceholderBuilder().create(framework, configurationValue);
		assertThat(placeholder).isInstanceOf(TimestampPlaceholder.class);

		FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1234)).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1");
	}

	/**
	 * Verifies that the configuration value "milliseconds" is resolved into a timestamp placeholder that outputs the
	 * UNIX time of issue in seconds.
	 *
	 * @param configurationValue The time unit for the timestamp placeholder
	 */
	@ParameterizedTest
	@ValueSource(strings = "milliseconds")
	void creationWithMilliseconds(String configurationValue) {
		Placeholder placeholder = new TimestampPlaceholderBuilder().create(framework, configurationValue);
		assertThat(placeholder).isInstanceOf(TimestampPlaceholder.class);

		FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1234)).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1234");
	}

	/**
	 * Verifies that the configuration values with unsupported time units are resolved into timestamp placeholders that
	 * output the UNIX time of issue in seconds, and a warning is logged.
	 *
	 * @param configurationValue The time unit for the timestamp placeholder
	 */
	@ParameterizedTest
	@ValueSource(strings = {"foo", "minutes", "hours"})
	void creationWithUnsupportedTimeUnit(String configurationValue) {
		Placeholder placeholder = new TimestampPlaceholderBuilder().create(framework, configurationValue);
		assertThat(placeholder).isInstanceOf(TimestampPlaceholder.class);
		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.WARN);
			assertThat(entry.getMessage()).contains(configurationValue);
		});

		FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1234)).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1");
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(TimestampPlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("timestamp");
		});
	}

}
