package org.tinylog.impl.policy;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.TestClock;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Tinylog(configuration = "zone=UTC")
class MonthlyPolicyBuilderTest {

    @Inject
    private TinylogContext context;

    @Inject
    private TestClock clock;

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

    /**
     * Verifies that the created monthly policy will trigger a rollover event on the first day of the month at midnight.
     */
    @Test
    void defaultOnMidnight() throws Exception {
        clock.fixTo(Instant.parse("1999-12-31T23:59:59Z"));

        Policy policy = new MonthlyPolicyBuilder().create(context, null);
        policy.init(null);
        assertThat(policy.canAcceptDataRecord(0)).isTrue();

        clock.fixTo(Instant.parse("2000-01-01T00:00:00Z"));
        assertThat(policy.canAcceptDataRecord(0)).isFalse();
    }

    /**
     * Verifies that a custom time can be configured for monthly rollover events.
     */
    @Test
    void customTime() throws Exception {
        clock.fixTo(Instant.parse("2000-01-01T03:59:59Z"));

        Policy policy = new MonthlyPolicyBuilder().create(context, "04:00");
        policy.init(null);
        assertThat(policy.canAcceptDataRecord(0)).isTrue();

        clock.fixTo(Instant.parse("2000-01-01T04:00:00Z"));
        assertThat(policy.canAcceptDataRecord(0)).isFalse();
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
        Throwable throwable = catchThrowable(() -> new MonthlyPolicyBuilder().create(context, configurationValue));
        assertThat(throwable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(configurationValue)
            .hasCauseInstanceOf(DateTimeException.class);
    }

}
