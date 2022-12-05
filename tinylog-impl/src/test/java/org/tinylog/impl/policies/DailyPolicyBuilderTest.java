package org.tinylog.impl.policies;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.TestClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries(configuration = "zone=UTC")
class DailyPolicyBuilderTest {

    @Inject
    private LoggingContext context;

    @Inject
    private TestClock clock;

    /**
     * Verifies that the created daily policy will trigger a rollover event at midnight.
     */
    @Test
    void defaultOnMidnightWithSystemZone() throws Exception {
        clock.setInstant(Instant.parse("1999-12-31T23:59:59Z"));

        Policy policy = new DailyPolicyBuilder().create(context, null);
        policy.init(null);
        assertThat(policy.canAcceptLogEntry(0)).isTrue();

        clock.setInstant(Instant.parse("2000-01-01T00:00:00Z"));
        assertThat(policy.canAcceptLogEntry(0)).isFalse();
    }

    /**
     * Verifies that a custom time can be configured for daily rollover events.
     */
    @Test
    void customTime() throws Exception {
        clock.setInstant(Instant.parse("2000-01-01T03:59:59Z"));

        Policy policy = new DailyPolicyBuilder().create(context, "04:00");
        policy.init(null);
        assertThat(policy.canAcceptLogEntry(0)).isTrue();

        clock.setInstant(Instant.parse("2000-01-01T04:00:00Z"));
        assertThat(policy.canAcceptLogEntry(0)).isFalse();
    }

    /**
     * Verifies that an exception with a meaningful message will be thrown, if the configuration value contains an
     * invalid time or zone.
     *
     * @param configurationValue The configuration value with an invalid value for the daily policy
     */
    @ParameterizedTest
    @ValueSource(strings = {"foo", "foo UTC", "00:00 FOO"})
    void invalidConfiguration(String configurationValue) {
        Throwable throwable = catchThrowable(() -> new DailyPolicyBuilder().create(context, configurationValue));
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
            assertThat(builder).isInstanceOf(DailyPolicyBuilder.class);
            assertThat(builder.getName()).isEqualTo("daily");
        });
    }

}
