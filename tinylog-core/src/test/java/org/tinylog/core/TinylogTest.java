package org.tinylog.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.test.system.CaptureSystemOutput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@CaptureSystemOutput(excludes = "TINYLOG WARN:.*tinylog-impl\\.jar.*")
class TinylogTest {

    /**
     * Ensures that {@link Tinylog} is down before and after each test.
     */
    @BeforeEach
    @AfterEach
    void shutDownTinylog() {
        Tinylog.shutDown();
    }

    /**
     * Verifies that an empty {@link ConfigurationBuilder} can be provided.
     */
    @Test
    void emptyConfiguration() {
        assertThat(Tinylog.getConfigurationBuilder(false)).isNotNull();
    }

    /**
     * Verifies that an inherited {@link ConfigurationBuilder} can be provided.
     */
    @Test
    void inheritedConfiguration() {
        assertThat(Tinylog.getConfigurationBuilder(true)).isNotNull();
    }

    /**
     * Verifies that the {@link Framework} instance is provided.
     */
    @Test
    void framework() {
        assertThat(Tinylog.getFramework()).isNotNull();
    }

    /**
     * Verifies that the life cycle works including hook registration.
     */
    @Test
    void lifeCycle() {
        Hook hook = mock(Hook.class);
        Tinylog.registerHook(hook);

        try {
            Tinylog.startUp();
            Tinylog.removeHook(hook);
        } finally {
            Tinylog.shutDown();
        }

        verify(hook).startUp();
        verify(hook, never()).shutDown();
    }

}
