package org.tinylog.core.runtime;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidRuntimeBuilderTest {

    /**
     * Verifies that the runtime is supported on Android.
     */
    @EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    @Test
    void supportedOnAndroid() {
        assertThat(new AndroidRuntimeBuilder().isSupported()).isTrue();
    }

    /**
     * Verifies that the runtime is not supported on standard Java.
     */
    @DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
    @Test
    void unsupportedOnJvm() {
        assertThat(new AndroidRuntimeBuilder().isSupported()).isFalse();
    }

    /**
     * Verifies that an instance {@link AndroidRuntime} can be created on Android.
     */
    @EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
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
