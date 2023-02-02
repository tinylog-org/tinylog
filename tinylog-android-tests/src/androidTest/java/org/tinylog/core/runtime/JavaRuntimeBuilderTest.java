package org.tinylog.core.runtime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaRuntimeBuilderTest {

    /**
     * Verifies that the runtime is not supported on Android.
     */
    @Test
    void unsupportedOnAndroid() {
        assertThat(new JavaRuntimeBuilder().isSupported()).isFalse();
    }

}
