package org.tinylog.core.backend;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class NopLoggingBackendBuilderTest {

    @Inject
    private TinylogContext context;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(LoggingBackendBuilder.class))
            .anyMatch(builder -> builder instanceof NopLoggingBackendBuilder);
    }

    /**
     * Verifies that the name is "nop".
     */
    @Test
    void name() {
        NopLoggingBackendBuilder builder = new NopLoggingBackendBuilder();
        assertThat(builder.getName()).isEqualTo("nop");
    }

    /**
     * Verifies that an instance of {@link NopLoggingBackend} can be created.
     */
    @Test
    void creation() {
        NopLoggingBackendBuilder builder = new NopLoggingBackendBuilder();
        LoggingBackend backend = builder.create(context);
        assertThat(backend).isInstanceOf(NopLoggingBackend.class);
    }

}
