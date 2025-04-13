package org.tinylog.impl.policy;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.service.RegisterService;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RegisterService(
    service = PolicyBuilder.class,
    implementations = {
        PolicyBuilderTest.FirstPolicyBuilder.class,
        PolicyBuilderTest.SecondPolicyBuilder.class
    }
)
class PolicyBuilderTest {

    @Inject
    private ClassLoader classLoader;

    /**
     * Verifies that all policy builders are loaded and mapped correctly.
     */
    @Test
    void load() {
        assertThat(PolicyBuilder.load(classLoader))
            .hasSize(2)
            .hasEntrySatisfying("foo", builder -> assertThat(builder).isInstanceOf(FirstPolicyBuilder.class))
            .hasEntrySatisfying("bar", builder -> assertThat(builder).isInstanceOf(SecondPolicyBuilder.class));
    }

    /**
     * First policy builder service implementation.
     */
    public static final class FirstPolicyBuilder implements PolicyBuilder {

        @Override
        public String getName() {
            return "FOO";
        }

        @Override
        public Policy create(TinylogContext context, String value) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Second policy builder service implementation.
     */
    public static final class SecondPolicyBuilder implements PolicyBuilder {

        @Override
        public String getName() {
            return "bar";
        }

        @Override
        public Policy create(TinylogContext context, String value) {
            throw new UnsupportedOperationException();
        }

    }

}
