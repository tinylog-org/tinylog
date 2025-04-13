package org.tinylog.core.runtime;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Tinylog;

import android.os.Build;
import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class AndroidRuntimeBuilderTest {

    @Inject
    private InternalLogger logger;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(RuntimeBuilder.class))
            .anyMatch(builder -> builder instanceof AndroidRuntimeBuilder);
    }

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
     * Verifies that an instance of {@link LegacyAndroidRuntime} or {@link ModernAndroidRuntime} can be created on
     * Android.
     */
    @Test
    void creation() {
        RuntimeFlavor runtime = new AndroidRuntimeBuilder().create(logger);

        if (Build.VERSION.SDK_INT >= 34) {
            assertThat(runtime).isInstanceOf(ModernAndroidRuntime.class);
        } else {
            assertThat(runtime).isInstanceOf(LegacyAndroidRuntime.class);
        }
    }

}
