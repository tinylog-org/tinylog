package org.tinylog.impl.backend;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class TinylogLoggingBackendBuilderTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(LoggingBackendBuilder.class))
            .anyMatch(builder -> builder instanceof TinylogLoggingBackendBuilder);
    }

    /**
     * Verifies that the name is "tinylog".
     */
    @Test
    void name() {
        TinylogLoggingBackendBuilder builder = new TinylogLoggingBackendBuilder();
        assertThat(builder.getName()).isEqualTo("tinylog");
    }

    /**
     * Verifies that an instance of {@link TinylogLoggingBackend} can be created.
     */
    @Test
    void creation() {
        TinylogLoggingBackendBuilder builder = new TinylogLoggingBackendBuilder();
        LoggingBackend backend = builder.create(context);
        assertThat(backend).isInstanceOf(TinylogLoggingBackend.class);
    }

}
