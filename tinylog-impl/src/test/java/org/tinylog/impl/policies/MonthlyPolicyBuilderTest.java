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

@CaptureLogEntries
class MonthlyPolicyBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private TestClock clock;

	/**
	 * Verifies that the created monthly policy will trigger a rollover event on the first day of the month at midnight
	 * at the system default time zone.
	 */
	@Test
	void defaultOnMidnightWithSystemZone() throws Exception {
		clock.setZone(ZoneId.of("UTC-1"));
		clock.setInstant(Instant.parse("2000-01-01T00:59:59Z"));

		Policy policy = new MonthlyPolicyBuilder().create(framework, null);
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(0)).isTrue();

		clock.setInstant(Instant.parse("2000-01-01T01:00:00Z"));
		assertThat(policy.canAcceptLogEntry(0)).isFalse();
	}

	/**
	 * Verifies that a custom time for monthly rollover events can be configured without defining a time zone.
	 */
	@Test
	void customTimeWithSystemZone() throws Exception {
		clock.setZone(ZoneOffset.UTC);
		clock.setInstant(Instant.parse("2000-01-01T03:59:59Z"));

		Policy policy = new MonthlyPolicyBuilder().create(framework, "04:00");
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(0)).isTrue();

		clock.setInstant(Instant.parse("2000-01-01T04:00:00Z"));
		assertThat(policy.canAcceptLogEntry(0)).isFalse();
	}

	/**
	 * Verifies that a custom time and custom zone can be configured for monthly rollover events.
	 */
	@Test
	void customTimeAndZone() throws Exception {
		clock.setZone(ZoneOffset.UTC);
		clock.setInstant(Instant.parse("2000-01-01T02:59:59Z"));

		Policy policy = new MonthlyPolicyBuilder().create(framework, "04:00 CET");
		policy.init(null);
		assertThat(policy.canAcceptLogEntry(0)).isTrue();

		clock.setInstant(Instant.parse("2000-01-01T03:00:00Z"));
		assertThat(policy.canAcceptLogEntry(0)).isFalse();
	}

	/**
	 * Verifies that an exception with a meaningful message will be thrown, if the configuration value contains an
	 * invalid time or zone.
	 *
	 * @param configurationValue The configuration value with an invalid value for the monthly policy
	 */
	@ParameterizedTest
	@ValueSource(strings = {"foo", "foo UTC", "00:00 FOO"})
	void invalidConfiguration(String configurationValue) {
		Throwable throwable = catchThrowable(() -> new MonthlyPolicyBuilder().create(framework, configurationValue));
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
			assertThat(builder).isInstanceOf(MonthlyPolicyBuilder.class);
			assertThat(builder.getName()).isEqualTo("monthly");
		});
	}

}
