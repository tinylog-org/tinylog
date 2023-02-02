package org.tinylog.core.runtime;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaRuntimeBuilderTest {

    /**
     * Verifies that the runtime is supported on standard Java.
     */
    @Test
    void supportedOnJvm() {
        assertThat(new JavaRuntimeBuilder().isSupported()).isTrue();
    }

    /**
     * Verifies that the priority is "0".
     */
    @Test
    void priority() {
        assertThat(new JavaRuntimeBuilder().getPriority()).isZero();
    }

    /**
     * Verifies that an instance {@link JavaRuntime} can be created on standard Java.
     */
    @Test
    void creation() {
        assertThat(new JavaRuntimeBuilder().create()).isInstanceOf(JavaRuntime.class);
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(RuntimeBuilder.class))
            .anyMatch(builder -> builder instanceof JavaRuntimeBuilder);
    }

}
