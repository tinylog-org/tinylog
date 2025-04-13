package org.tinylog.impl.backend;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.writer.Writer;
import org.tinylog.impl.writer.console.ConsoleWriter;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class WriterConfigurationTest {

    @Inject
    private TinylogContext context;

    @Inject
    private Configuration configuration;

    @Inject
    private Log log;

    /**
     * Verifies that a console writer without any explicit severity level definition can be created.
     */
    @Tinylog(configuration = "type=console")
    @Test
    void writerCreationWithDefaultSeverityLevel() {
        WriterConfiguration writerConfiguration = new WriterConfiguration(context);

        LevelConfiguration levelConfiguration = writerConfiguration.getLevelConfiguration();
        assertThat(levelConfiguration.getTags()).isEmpty();
        assertThat(levelConfiguration.getLevel("-")).isEqualTo(Level.TRACE);
        assertThat(levelConfiguration.getLevel("foo")).isEqualTo(Level.TRACE);

        Writer firstWriter = writerConfiguration.getOrCreateWriter();
        assertThat(firstWriter).isInstanceOf(ConsoleWriter.class);

        Writer secondWriter = writerConfiguration.getOrCreateWriter();
        assertThat(secondWriter).isSameAs(firstWriter);
    }

    /**
     * Verifies that a console writer with a custom severity level definition can be created.
     */
    @Tinylog(configuration = {"type=console", "level=debug"})
    @Test
    void writerCreationWithCustomSeverityLevel() {
        WriterConfiguration writerConfiguration = new WriterConfiguration(context);

        LevelConfiguration levelConfiguration = writerConfiguration.getLevelConfiguration();
        assertThat(levelConfiguration.getTags()).isEmpty();
        assertThat(levelConfiguration.getLevel("-")).isEqualTo(Level.DEBUG);
        assertThat(levelConfiguration.getLevel("foo")).isEqualTo(Level.DEBUG);

        Writer firstWriter = writerConfiguration.getOrCreateWriter();
        assertThat(firstWriter).isInstanceOf(ConsoleWriter.class);

        Writer secondWriter = writerConfiguration.getOrCreateWriter();
        assertThat(secondWriter).isSameAs(firstWriter);
    }

    /**
     * Verifies that a missing type property is reported.
     */
    @Tinylog(configuration = {})
    @Test
    void missingTypeProperty() {
        WriterConfiguration writerConfiguration = new WriterConfiguration(context);
        assertThat(writerConfiguration.getOrCreateWriter()).isNull();

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("type");
        });
    }

    /**
     * Verifies that an invalid writer name is reported.
     */
    @Tinylog(configuration = "type=foo")
    @Test
    void invalidWriterNameInTypeProperty() {
        WriterConfiguration writerConfiguration = new WriterConfiguration(context);
        assertThat(writerConfiguration.getOrCreateWriter()).isNull();

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("foo");
        });
    }

    /**
     * Verifies that a failed writer instantiation is reported.
     */
    @Tinylog(configuration = "type=file")
    @Test
    void writerCreationFailed() {
        WriterConfiguration writerConfiguration = new WriterConfiguration(context);
        assertThat(writerConfiguration.getOrCreateWriter()).isNull();

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getFormattedMessage(configuration)).contains("file");
        });
    }

}
