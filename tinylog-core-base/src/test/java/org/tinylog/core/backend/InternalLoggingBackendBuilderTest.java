package org.tinylog.core.backend;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;

import static org.assertj.core.api.Assertions.assertThat;

class InternalLoggingBackendBuilderTest {

    /**
     * Verifies that the name is "internal".
     */
    @Test
    void name() {
        InternalLoggingBackendBuilder builder = new InternalLoggingBackendBuilder();
        assertThat(builder.getName()).isEqualTo("internal");
    }

    /**
     * Verifies that an instance of {@link InternalLoggingBackend} can be created.
     */
    @Test
    void creation() {
        Framework framework = new Framework(false, false);
        InternalLoggingBackendBuilder builder = new InternalLoggingBackendBuilder();
        assertThat(builder.create(framework)).isInstanceOf(InternalLoggingBackend.class);
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(LoggingBackendBuilder.class))
            .anyMatch(builder -> builder instanceof InternalLoggingBackendBuilder);
    }

}
