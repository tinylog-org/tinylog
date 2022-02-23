package org.tinylog.impl.path.segments;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries(configuration = "locale=en_US")
class DateTimeSegmentBuilderTest {

	@Inject
	private Framework framework;

	/**
	 * Verifies that {@code yyyy-MM-dd_HH-mm-ss} is used as default format pattern if none is explicitly configured.
	 */
	@Test
	void defaultFormat() throws Exception {
		StringBuilder builder = new StringBuilder("bar/");
		ZonedDateTime date = ZonedDateTime.ofInstant(Instant.parse("2000-01-01T12:30:59Z"), ZoneOffset.UTC);
		new DateTimeSegmentBuilder().create(framework, null).resolve(builder, date);
		assertThat(builder).asString().isEqualTo("bar/2000-01-01_12-30-59");

	}

	/**
	 * Verifies that a custom date-time format can be configured.
	 */
	@Test
	void customFormat() throws Exception {
		StringBuilder builder = new StringBuilder("bar/");
		ZonedDateTime date = ZonedDateTime.ofInstant(Instant.parse("2000-01-01T12:30:59Z"), ZoneOffset.UTC);
		new DateTimeSegmentBuilder().create(framework, "yyyy_MM_dd'T'HH_mm").resolve(builder, date);
		assertThat(builder).asString().isEqualTo("bar/2000_01_01T12_30");
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(PathSegmentBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(DateTimeSegmentBuilder.class);
			assertThat(builder.getName()).isEqualTo("date");
		});
	}

}
