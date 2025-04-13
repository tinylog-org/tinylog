package org.tinylog.core.backend;

import java.time.Clock;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.RuntimeFlavor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TinylogContextTest {

    /**
     * Verifies that a new tinylog context can be created correctly.
     */
    @Test
    void creation() {
        ClassLoader loader = mock(ClassLoader.class);
        Clock clock = mock(Clock.class);
        RuntimeFlavor runtime = mock(RuntimeFlavor.class);
        Configuration configuration = mock(Configuration.class);
        InternalLogger logger = mock(InternalLogger.class);

        TinylogContext context = new TinylogContext(loader, clock, runtime, configuration, logger);

        assertThat(context.getLoader()).isSameAs(loader);
        assertThat(context.getClock()).isSameAs(clock);
        assertThat(context.getRuntime()).isSameAs(runtime);
        assertThat(context.getConfiguration()).isSameAs(configuration);
        assertThat(context.getLogger()).isSameAs(logger);
    }

    /**
     * Verifies that an existing tinylog context can be copied with another configuration.
     */
    @Test
    void copy() {
        ClassLoader loader = mock(ClassLoader.class);
        Clock clock = mock(Clock.class);
        RuntimeFlavor runtime = mock(RuntimeFlavor.class);
        Configuration originalConfiguration = mock(Configuration.class, "original configuration");
        Configuration newConfiguration = mock(Configuration.class, "new configuration");
        InternalLogger logger = mock(InternalLogger.class);

        TinylogContext originalContext = new TinylogContext(loader, clock, runtime, originalConfiguration, logger);
        TinylogContext newContext = originalContext.withConfiguration(newConfiguration);

        assertThat(newContext.getLoader()).isSameAs(loader);
        assertThat(newContext.getClock()).isSameAs(clock);
        assertThat(newContext.getRuntime()).isSameAs(runtime);
        assertThat(newContext.getConfiguration()).isSameAs(newConfiguration);
        assertThat(newContext.getLogger()).isSameAs(logger);
    }

}
