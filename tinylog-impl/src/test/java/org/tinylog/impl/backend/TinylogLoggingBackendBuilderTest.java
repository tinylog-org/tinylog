package org.tinylog.impl.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.impl.writers.file.FileWriter;

import static org.assertj.core.api.Assertions.assertThat;

class TinylogLoggingBackendBuilderTest {

    /**
     * Verifies that a {@link ImmutableLoggingBackend} with a {@link FileWriter} can be created and used for logging.
     */
    @Test
    void creation() throws IOException {
        Path logFile = Files.createTempFile("tinylog", ".log");
        logFile.toFile().deleteOnExit();

        try {
            Framework context = new Framework(false, false);
            context.getConfigurationBuilder(false)
                .set("level", "INFO")
                .set("writer.type", "file")
                .set("writer.file", logFile.toAbsolutePath().toString())
                .set("writer.pattern", "{level}:{message}")
                .activate();

            try {
                context.startUp();

                LoggingBackend backend = context.getLoggingBackend();
                assertThat(backend).isInstanceOf(ImmutableLoggingBackend.class);

                backend.log(null, null, Level.DEBUG, null, "Hello World!", null, null);
                backend.log(null, null, Level.INFO, null, "Hello World!", null, null);
            } finally {
                context.shutDown();
            }

            assertThat(logFile).hasContent("INFO:Hello World!" + System.lineSeparator());
        } finally {
            Files.deleteIfExists(logFile);
        }
    }

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(LoggingBackendBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(TinylogLoggingBackendBuilder.class);
            assertThat(builder.getName()).isEqualTo("tinylog");
        });
    }

}
