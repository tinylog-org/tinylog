package org.tinylog.impl.policies;

import java.time.DateTimeException;
import java.time.Instant;
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

@CaptureLogEntries(configuration = {"locale=en_US", "zone=UTC"})
class WeeklyPolicyBuilderTest {

    @Inject
    private Framework framework;

    @Inject
    private TestClock clock;

    /**
     * Verifies that the created weekly policy will trigger a rollover event on the first day of the week at midnight.
     */
    @Test
    void defaultOnMidnight() throws Exception {
        clock.setInstant(Instant.parse("2000-01-01T23:59:59Z"));

        Policy policy = new WeeklyPolicyBuilder().create(framework, null);
        policy.init(null);
        assertThat(policy.canAcceptLogEntry(0)).isTrue();

        clock.setInstant(Instant.parse("2000-01-02T00:00:00Z"));
        assertThat(policy.canAcceptLogEntry(0)).isFalse();
    }

    /**
     * Verifies that a custom day of week can be configured for weekly rollover events without defining a time.
     *
     * @param configurationValue Different supported notations to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"MON", "Mon", "mon", "MONDAY", "Monday", "monday"})
    void customDay(String configurationValue) throws Exception {
        clock.setInstant(Instant.parse("2000-01-02T23:59:59Z"));

        Policy policy = new WeeklyPolicyBuilder().create(framework, configurationValue);
        policy.init(null);
        assertThat(policy.canAcceptLogEntry(0)).isTrue();

        clock.setInstant(Instant.parse("2000-01-03T00:00:00Z"));
        assertThat(policy.canAcceptLogEntry(0)).isFalse();
    }

    /**
     * Verifies that a custom day of week and custom time can be configured for weekly rollover events without defining.
     *
     * @param configurationValue Different supported notations to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"MON 4:00", "Monday 04:00"})
    void customDayAndTime(String configurationValue) throws Exception {
        clock.setInstant(Instant.parse("2000-01-03T03:59:59Z"));

        Policy policy = new WeeklyPolicyBuilder().create(framework, configurationValue);
        policy.init(null);
        assertThat(policy.canAcceptLogEntry(0)).isTrue();

        clock.setInstant(Instant.parse("2000-01-03T04:00:00Z"));
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
