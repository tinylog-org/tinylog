package org.tinylog.core.runtime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidRuntimeBuilderTest {

    /**
     * Verifies that the runtime is not supported on standard Java.
     */
    @Test
    void unsupportedOnJvm() {
        assertThat(new AndroidRuntimeBuilder().isSupported()).isFalse();
    }

}
