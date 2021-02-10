package org.tinylog.impl.format.placeholder;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Duration;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class UptimePlaceholderBuilderTest {

	@Inject
	private Framework framework;

	/**
	 * Verifies that the builder can create a valid {@link UptimePlaceholder} with default format pattern.
	 */
	@Test
	void creationWithDefaultPattern() {
		Placeholder placeholder = new UptimePlaceholderBuilder().create(framework, null);
		assertThat(placeholder).isInstanceOf(UptimePlaceholder.class);

		LogEntry logEntry = new LogEntryBuilder().uptime(Duration.ofHours(2).minusSeconds(30)).create();

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		assertThat(renderer.render(logEntry)).isEqualTo("01:59:30");

		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.NUMERIC, new BigDecimal("7170.000000000")));
	}

	/**
	 * Verifies that the builder can create a valid {@link UptimePlaceholder} with custom format pattern.
	 */
	@Test
	void creationWithCustomPattern() {
		Placeholder placeholder = new UptimePlaceholderBuilder().create(framework, "s.SSS");
		assertThat(placeholder).isInstanceOf(UptimePlaceholder.class);

		LogEntry logEntry = new LogEntryBuilder().uptime(Duration.ofHours(2).minusSeconds(30)).create();

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		assertThat(renderer.render(logEntry)).isEqualTo("7170.000");

		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "7170.000"));
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(UptimePlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("uptime");
		});
	}

}
