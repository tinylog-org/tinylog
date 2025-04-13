package org.tinylog.core.runtime;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidRuntimeBuilderTest {

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(RuntimeBuilder.class))
            .anyMatch(builder -> builder instanceof AndroidRuntimeBuilder);
    }

    /**
     * Verifies that the runtime is not supported on standard Java.
     */
    @Test
    void unsupportedOnJvm() {
        assertThat(new AndroidRuntimeBuilder().isSupported()).isFalse();
    }

    /**
     * Verifies that the priority is "0".
     */
    @Test
    void priority() {
        assertThat(new AndroidRuntimeBuilder().getPriority()).isZero();
    }

}
