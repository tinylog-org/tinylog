package org.tinylog.core.runtime;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaRuntimeBuilderTest {

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(RuntimeBuilder.class))
            .anyMatch(builder -> builder instanceof JavaRuntimeBuilder);
    }

    /**
     * Verifies that the runtime is not supported on Android.
     */
    @Test
    void unsupportedOnAndroid() {
        assertThat(new JavaRuntimeBuilder().isSupported()).isFalse();
    }

    /**
     * Verifies that the priority is "0".
     */
    @Test
    void priority() {
        assertThat(new JavaRuntimeBuilder().getPriority()).isZero();
    }

}
