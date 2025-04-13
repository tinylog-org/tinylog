package org.tinylog.slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.JavaRuntime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TinylogLoggerFactoryTest {

    private Framework framework;

    /**
     * Creates the framework.
     */
    @BeforeEach
    void create() {
        framework = mock(Framework.class);

        JavaRuntime runtime = new JavaRuntime(mock(InternalLogger.class));
        when(framework.getRuntime()).thenReturn(runtime);

        LevelVisibility visibility = new LevelVisibility(OutputDetails.DISABLED);
        when(framework.getLevelVisibilityByClass(any())).thenReturn(visibility);
    }


    /**
     * Verifies that the factory provides the same logger instance for the same name.
     */
    @Test
    void getSameLoggerInstanceForSameName() {
        TinylogLoggerFactory factory = new TinylogLoggerFactory(framework);
        TinylogLogger logger = factory.getLogger("Foo");
        TinylogLogger other = factory.getLogger("Foo");
        assertThat(other).isSameAs(logger);
    }

    /**
     * Verifies that the factory provides another same logger instance for another name.
     */
    @Test
    void getDifferentLoggerInstanceForDifferentName() {
        TinylogLoggerFactory factory = new TinylogLoggerFactory(framework);
        TinylogLogger logger = factory.getLogger("Foo");
        TinylogLogger other = factory.getLogger("Bar");
        assertThat(other).isNotSameAs(logger);
    }

}
