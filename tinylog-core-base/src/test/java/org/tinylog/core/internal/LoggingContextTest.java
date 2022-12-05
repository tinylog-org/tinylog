package org.tinylog.core.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoggingContextTest {

    @Mock
    private Framework framework;

    @Mock
    private Configuration configuration;

    /**
     * Verifies that the passed framework instance will is provided.
     */
    @Test
    void framework() {
        LoggingContext context = new LoggingContext(framework, configuration);
        assertThat(context.getFramework()).isSameAs(framework);
    }

    /**
     * Verifies that the passed configuration instance will is provided.
     */
    @Test
    void configuration() {
        LoggingContext context = new LoggingContext(framework, configuration);
        assertThat(context.getConfiguration()).isSameAs(configuration);
    }

}
