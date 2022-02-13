package org.tinylog.impl.policies;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.TestClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries(configuration = "locale=en_US")
class WeeklyPolicyBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private TestClock clock;

	/**
	 * Verifies that the created weekly policy will trigger a rollover event on the first day of the week at midnight
	 * at the system default time zone.
	 */
	@Test
	void defaultOnMidnightWithSystemZone() throws Exception {
		clock.setZone(ZoneId.of("UTC-1"));
		clock.setInstant(Instant.parse("2000-01-02T00:59:59Z"));

		Policy policy = new WeeklyPolicyBuilder().create(framework, null);
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(0)).isTrue();

		clock.setInstant(Instant.parse("2000-01-02T01:00:00Z"));
		assertThat(policy.canAcceptLogEntry(0)).isFalse();
	}

	/**
	 * Verifies that a custom day of week can be configured for weekly rollover events without defining a time nor
	 * a zone.
	 *
	 * @param configurationValue Different supported notations to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"MON", "Mon", "mon", "MONDAY", "Monday", "monday"})
	void customDay(String configurationValue) throws Exception {
		clock.setZone(ZoneId.of("UTC-1"));
		clock.setInstant(Instant.parse("2000-01-03T00:59:59Z"));

		Policy policy = new WeeklyPolicyBuilder().create(framework, configurationValue);
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(0)).isTrue();

		clock.setInstant(Instant.parse("2000-01-03T01:00:00Z"));
		assertThat(policy.canAcceptLogEntry(0)).isFalse();
	}

	/**
	 * Verifies that a custom day of week and custom time can be configured for weekly rollover events without defining
	 * a zone.
	 *
	 * @param configurationValue Different supported notations to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"MON 4:00", "Monday 04:00"})
	void customDayAndTime(String configurationValue) throws Exception {
		clock.setZone(ZoneOffset.UTC);
		clock.setInstant(Instant.parse("2000-01-03T03:59:59Z"));

		Policy policy = new WeeklyPolicyBuilder().create(framework, configurationValue);
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(0)).isTrue();

		clock.setInstant(Instant.parse("2000-01-03T04:00:00Z"));
		assertThat(policy.canAcceptLogEntry(0)).isFalse();
	}

	/**
	 * Verifies that a custom day of week, custom time and custom zone can be configured for weekly rollover events.
	 *
	 * @param configurationValue Different supported notations to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"MON 4:00 CET", "Monday 04:00 CET"})
	void customDayAndTimeAndZone(String configurationValue) throws Exception {
		clock.setZone(ZoneOffset.UTC);
		clock.setInstant(Instant.parse("2000-01-03T02:59:59Z"));

		Policy policy = new WeeklyPolicyBuilder().create(framework, configurationValue);
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(0)).isTrue();

		clock.setInstant(Instant.parse("2000-01-03T03:00:00Z"));
		assertThat(policy.canAcceptLogEntry(0)).isFalse();
	}

	/**
	 * Verifies that an exception with a meaningful message will be thrown, if the configuration value contains an
	 * invalid time or zone.
	 *
	 * @param configurationValue Invalid notations for the configuration value to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"FOO", "FOO 00:00", "FOO 00:00 UTC", "SUN foo", "SUN foo UTC", "SUN 00:00 FOO"})
	void invalidConfiguration(String configurationValue) {
		Throwable throwable = catchThrowable(() -> new WeeklyPolicyBuilder().create(framework, configurationValue));
		assertThat(throwable)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(configurationValue)
			.hasCauseInstanceOf(DateTimeException.class);
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(PolicyBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(WeeklyPolicyBuilder.class);
			assertThat(builder.getName()).isEqualTo("weekly");
		});
	}

}
