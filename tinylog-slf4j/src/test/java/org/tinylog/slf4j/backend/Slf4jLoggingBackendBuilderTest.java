package org.tinylog.slf4j.backend;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class Slf4jLoggingBackendBuilderTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(LoggingBackendBuilder.class))
            .anyMatch(builder -> builder instanceof Slf4jLoggingBackendBuilder);
    }

    /**
     * Verifies that the name is "slf4j".
     */
    @Test
    void name() {
        Slf4jLoggingBackendBuilder builder = new Slf4jLoggingBackendBuilder();
        assertThat(builder.getName()).isEqualTo("slf4j");
    }

    /**
     * Verifies that an instance of {@link Slf4jLoggingBackend} can be created.
     */
    @Test
    void creation() {
        Slf4jLoggingBackendBuilder builder = new Slf4jLoggingBackendBuilder();
        LoggingBackend backend = builder.create(context);
        assertThat(backend).isInstanceOf(Slf4jLoggingBackend.class);
    }

}
