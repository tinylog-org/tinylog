package org.tinylog.core.backend;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class InternalLoggingBackendBuilderTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(LoggingBackendBuilder.class))
            .anyMatch(builder -> builder instanceof InternalLoggingBackendBuilder);
    }

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
        InternalLoggingBackendBuilder builder = new InternalLoggingBackendBuilder();
        LoggingBackend backend = builder.create(context);
        assertThat(backend).isInstanceOf(InternalLoggingBackend.class);
    }

}
