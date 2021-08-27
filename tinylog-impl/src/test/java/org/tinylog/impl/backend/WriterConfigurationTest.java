package org.tinylog.impl.backend;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.writers.ConsoleWriter;
import org.tinylog.impl.writers.Writer;

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
		Configuration consoleConfiguration = new Configuration();
		consoleConfiguration.set("type", "console");

		WriterConfiguration writerConfiguration = new WriterConfiguration(framework, consoleConfiguration);

		LevelConfiguration levelConfiguration = writerConfiguration.getLevelConfiguration();
		assertThat(levelConfiguration.getTags()).isEmpty();
		assertThat(levelConfiguration.getUntaggedLevel()).isEqualTo(Level.TRACE);
		assertThat(levelConfiguration.getDefaultTaggedLevel()).isEqualTo(Level.TRACE);
		assertThat(levelConfiguration.getTaggedLevel("foo")).isEqualTo(Level.TRACE);

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
		Configuration consoleConfiguration = new Configuration();
		consoleConfiguration.set("type", "console");
		consoleConfiguration.set("level", "debug");

		WriterConfiguration writerConfiguration = new WriterConfiguration(framework, consoleConfiguration);

		LevelConfiguration levelConfiguration = writerConfiguration.getLevelConfiguration();
		assertThat(levelConfiguration.getTags()).isEmpty();
		assertThat(levelConfiguration.getUntaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(levelConfiguration.getDefaultTaggedLevel()).isEqualTo(Level.DEBUG);
		assertThat(levelConfiguration.getTaggedLevel("foo")).isEqualTo(Level.DEBUG);

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
		WriterConfiguration configuration = new WriterConfiguration(framework, new Configuration());
		assertThat(configuration.getOrCreateWriter()).isNull();

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("type");
		});
	}

	/**
	 * Verifies that an invalid writer name is reported.
	 */
	@Test
	void invalidWriterNameInTypeProperty() {
		Configuration invalidConfiguration = new Configuration();
		invalidConfiguration.set("type", "foo");

		WriterConfiguration writerConfiguration = new WriterConfiguration(framework, invalidConfiguration);
		assertThat(writerConfiguration.getOrCreateWriter()).isNull();

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("foo");
		});
	}

	/**
	 * Verifies that a failed writer instantiation is reported.
	 */
	@Test
	void writerCreationFailed() {
		Configuration fileWriter = new Configuration();
		fileWriter.set("type", "file");

		WriterConfiguration writerConfiguration = new WriterConfiguration(framework, fileWriter);
		assertThat(writerConfiguration.getOrCreateWriter()).isNull();

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("file");
		});
	}

}
