package org.tinylog.core.runtime;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class JavaRuntimeBuilderTest {

    @Inject
    private InternalLogger logger;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(RuntimeBuilder.class))
            .anyMatch(builder -> builder instanceof JavaRuntimeBuilder);
    }

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
     * Verifies that an instance of {@link JavaRuntime} can be created on standard Java.
     */
    @Test
    void creation() {
        assertThat(new JavaRuntimeBuilder().create(logger)).isInstanceOf(JavaRuntime.class);
    }

}
