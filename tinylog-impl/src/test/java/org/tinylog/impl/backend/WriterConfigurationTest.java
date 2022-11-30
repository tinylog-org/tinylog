package org.tinylog.impl.backend;

import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.console.ConsoleWriter;

import com.google.common.collect.ImmutableMap;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class WriterConfigurationTest {

    @Inject
    private Framework framework;

    @Inject
    private Log log;

    /**
     * Verifies that a console writer without any explicit severity level definition can be created.
     */
    @Test
    void writerCreationWithDefaultSeverityLevel() {
        Map<String, String> properties = ImmutableMap.of("type", "console");
        Configuration consoleConfiguration = new Configuration(properties);
        WriterConfiguration writerConfiguration = new WriterConfiguration(framework, consoleConfiguration);

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
    @Test
    void writerCreationWithCustomSeverityLevel() {
        Map<String, String> properties = ImmutableMap.of("type", "console", "level", "debug");
        Configuration consoleConfiguration = new Configuration(properties);
        WriterConfiguration writerConfiguration = new WriterConfiguration(framework, consoleConfiguration);

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
    @Test
    void missingTypeProperty() {
        WriterConfiguration configuration = new WriterConfiguration(framework, new Configuration(emptyMap()));
        assertThat(configuration.getOrCreateWriter()).isNull();

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getMessage()).contains("type");
        });
    }

    /**
     * Verifies that an invalid writer name is reported.
     */
    @Test
    void invalidWriterNameInTypeProperty() {
        Map<String, String> properties = ImmutableMap.of("type", "foo");
        Configuration invalidConfiguration = new Configuration(properties);

        WriterConfiguration writerConfiguration = new WriterConfiguration(framework, invalidConfiguration);
        assertThat(writerConfiguration.getOrCreateWriter()).isNull();

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getMessage()).contains("foo");
        });
    }

    /**
     * Verifies that a failed writer instantiation is reported.
     */
    @Test
    void writerCreationFailed() {
        Map<String, String> properties = ImmutableMap.of("type", "file");
        Configuration fileWriter = new Configuration(properties);

        WriterConfiguration writerConfiguration = new WriterConfiguration(framework, fileWriter);
        assertThat(writerConfiguration.getOrCreateWriter()).isNull();

        assertThat(log.consume()).singleElement().satisfies(entry -> {
            assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getMessage()).contains("file");
        });
    }

}
