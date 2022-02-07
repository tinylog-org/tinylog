package org.tinylog.impl.policies;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@CaptureLogEntries
@ExtendWith(MockitoExtension.class)
class MonthlyPolicyBuilderTest {

	@Inject
	private Framework framework;

	@Mock
	private Clock clock;

	/**
	 * Verifies that the created monthly policy will trigger a rollover event on the first day of the month at midnight
	 * at the system default time zone.
	 */
	@Test
	void defaultOnMidnightWithSystemZone() throws Exception {
		try (MockedStatic<Clock> staticClockMock = mockStatic(Clock.class)) {
			staticClockMock.when(Clock::systemDefaultZone).thenReturn(clock);
			when(clock.getZone()).thenReturn(ZoneId.of("UTC-1"));
			when(clock.instant()).thenReturn(Instant.parse("2000-01-01T00:59:59Z"));

			Policy policy = new MonthlyPolicyBuilder().create(framework, null);
			policy.init(null);
			assertThat(policy.canAcceptLogEntry(0)).isTrue();

			when(clock.instant()).thenReturn(Instant.parse("2000-01-01T01:00:00Z"));
			assertThat(policy.canAcceptLogEntry(0)).isFalse();
		}
	}

	/**
	 * Verifies that a custom time for monthly rollover events can be configured without defining a time zone.
	 */
	@Test
	void customTimeWithSystemZone() throws Exception {
		try (MockedStatic<Clock> staticClockMock = mockStatic(Clock.class)) {
			staticClockMock.when(Clock::systemDefaultZone).thenReturn(clock);
			when(clock.getZone()).thenReturn(ZoneOffset.UTC);
			when(clock.instant()).thenReturn(Instant.parse("2000-01-01T03:59:59Z"));

			Policy policy = new MonthlyPolicyBuilder().create(framework, "04:00");
			policy.init(null);
			assertThat(policy.canAcceptLogEntry(0)).isTrue();

			when(clock.instant()).thenReturn(Instant.parse("2000-01-01T04:00:00Z"));
			assertThat(policy.canAcceptLogEntry(0)).isFalse();
		}
	}

	/**
	 * Verifies that a custom time and custom zone can be configured for monthly rollover events.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	void customTimeAndZone() throws Exception {
		try (MockedStatic<Clock> staticClockMock = mockStatic(Clock.class)) {
			staticClockMock.when(() -> Clock.system(ZoneId.of("CET"))).thenReturn(clock);
			when(clock.getZone()).thenReturn(ZoneId.of("CET"));
			when(clock.instant()).thenReturn(Instant.parse("2000-01-01T02:59:59Z"));

			Policy policy = new MonthlyPolicyBuilder().create(framework, "04:00@CET");
			policy.init(null);
			assertThat(policy.canAcceptLogEntry(0)).isTrue();

			when(clock.instant()).thenReturn(Instant.parse("2000-01-01T03:00:00Z"));
			assertThat(policy.canAcceptLogEntry(0)).isFalse();
		}
	}

	/**
	 * Verifies that an exception with a meaningful message will be thrown, if the configuration value contains an
	 * invalid time or zone.
	 *
	 * @param configurationValue The configuration value with an invalid value for the monthly policy
	 */
	@ParameterizedTest
	@ValueSource(strings = {"foo", "foo@UTC", "00:00@FOO"})
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
