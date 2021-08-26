package org.tinylog.impl.format.placeholders;

import java.time.Instant;
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

class DatePlaceholderBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	/**
	 * Verifies that the builder can create a valid {@link DatePlaceholder} with default pattern.
	 */
	@CaptureLogEntries(configuration = {"locale=de_DE", "zone=Europe/Berlin"})
	@Test
	void defaultCreationForGermany() {
		Placeholder placeholder = new DatePlaceholderBuilder().create(framework, null);
		assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01 01:00:00");
	}

	/**
	 * Verifies that the builder can create a valid {@link DatePlaceholder} with default pattern.
	 */
	@CaptureLogEntries(configuration = {"locale=en_NZ", "zone=Pacific/Auckland"})
	@Test
	void defaultCreationForNewZealand() {
		Placeholder placeholder = new DatePlaceholderBuilder().create(framework, null);
		assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01 12:00:00");
	}

	/**
	 * Verifies that the builder can create a valid {@link DatePlaceholder} with custom pattern.
	 */
	@CaptureLogEntries(configuration = {"locale=de_DE", "zone=Europe/Berlin"})
	@Test
	void customCreationForGermany() {
		Placeholder placeholder = new DatePlaceholderBuilder().create(framework, "d. MMMM y - HH:mm");
		assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1. Januar 1970 - 01:00");
	}

	/**
	 * Verifies that the builder can create a valid {@link DatePlaceholder} with custom pattern.
	 */
	@CaptureLogEntries(configuration = {"locale=en_NZ", "zone=Pacific/Auckland"})
	@Test
	void customCreationForNewZealand() {
		Placeholder placeholder = new DatePlaceholderBuilder().create(framework, "d MMMM y - h.mm a");
		assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(logEntry)).matches("1 January 1970 - 12\\.00 (pm|PM)");
	}

	/**
	 * Verifies that the builder can create a valid {@link DatePlaceholder} with fallback for invalid custom pattern.
	 */
	@CaptureLogEntries(configuration = {"locale=en_US", "zone=UTC"})
	@Test
	void fallbackForInvalidPattern() {
		Placeholder placeholder = new DatePlaceholderBuilder().create(framework, "INVALID <{|#|}>");
		assertThat(placeholder).isInstanceOf(DatePlaceholder.class);

		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01 00:00:00");
		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("INVALID <{|#|}>");
		});
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@CaptureLogEntries
	@Test
	void service() {
		assertThat(ServiceLoader.load(PlaceholderBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(DatePlaceholderBuilder.class);
			assertThat(builder.getName()).isEqualTo("date");
		});
	}

}
