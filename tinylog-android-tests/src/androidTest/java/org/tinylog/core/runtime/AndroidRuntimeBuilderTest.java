package org.tinylog.core.runtime;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidRuntimeBuilderTest {

    /**
     * Verifies that the runtime is supported on Android.
     */
    @Test
    void supportedOnAndroid() {
        assertThat(new AndroidRuntimeBuilder().isSupported()).isTrue();
    }

    /**
     * Verifies that the priority is "0".
     */
    @Test
    void priority() {
        assertThat(new AndroidRuntimeBuilder().getPriority()).isZero();
    }

    /**
     * Verifies that an instance {@link AndroidRuntime} can be created on Android.
     */
    @Test
    void creation() {
        assertThat(new AndroidRuntimeBuilder().create()).isInstanceOf(AndroidRuntime.class);
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(RuntimeBuilder.class))
            .anyMatch(builder -> builder instanceof AndroidRuntimeBuilder);
    }

}
