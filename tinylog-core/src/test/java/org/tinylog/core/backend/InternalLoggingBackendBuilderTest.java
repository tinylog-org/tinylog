package org.tinylog.core.backend;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class InternalLoggingBackendBuilderTest {

    @Inject
    private LoggingContext context;

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
        assertThat(builder.create(context)).isInstanceOf(InternalLoggingBackend.class);
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
