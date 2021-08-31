package org.tinylog.impl.backend;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.writers.ConsoleWriter;
import org.tinylog.impl.writers.LogcatWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tinylog.impl.test.InternalAssertions.assertThat;

class LoggingConfigurationParserTest {

	@Inject
	private Framework framework;

	/**
	 * Verifies that no writers are created for an empty configuration.
	 */
	@CaptureLogEntries(configuration = {})
	@Test
	void emptyConfiguration() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();
		assertThat(configuration.getAllWriters()).isEmpty();
	}

	/**
	 * Verifies that all severity levels will be enabled if the severity level is not defined.
	 */
	@CaptureLogEntries(configuration = "writer.type=console")
	@Test
	void writerWithoutAnySeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getUntaggedWriters(Level.TRACE)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(configuration.getTaggedWriters("foo", Level.TRACE)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the global severity level is applied to writer without any configured custom severity level.
	 */
	@CaptureLogEntries(configuration = {"level=INFO", "writer.type=console"})
	@Test
	void writerWithGlobalSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.DEBUG)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(configuration.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that a custom severity level can be defined for a writer.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithCustomSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(configuration.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the custom severity level will be used, if it is more restrict than the global severity level.
	 */
	@CaptureLogEntries(configuration = {"level=TRACE", "writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithHigherCustomSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(configuration.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the global severity level will be used, if it is more restrict than the custom severity level.
	 */
	@CaptureLogEntries(configuration = {"level=INFO", "writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithLowerCustomSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.DEBUG)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(configuration.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that no writer will be created, if the severity level is OFF.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=OFF"})
	@Test
	void writerWithDisabledSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();
		assertThat(configuration.getAllWriters()).isEmpty();
	}

	/**
	 * Verifies that severity levels can be defined for tags.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=DEBUG@foo"})
	@Test
	void writerWithTaggedSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.DEBUG)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.INFO)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.WARN)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.ERROR)).isEmpty();

		assertThat(configuration.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that a generic and custom severity level can be combined.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=DEBUG,WARN@foo"})
	@Test
	void writerWithMixedSeverityLevels() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(configuration.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.INFO)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that different severity levels can be defined for different writers.
	 */
	@CaptureLogEntries(configuration = {
		"writer1.type=console", "writer1.level=DEBUG", "writer2.type=logcat", "writer2.level=WARN"
	})
	@Test
	void multipleWriters() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getAllWriters())
			.hasSize(2)
			.hasAtLeastOneElementOfType(ConsoleWriter.class)
			.hasAtLeastOneElementOfType(LogcatWriter.class);

		assertThat(configuration.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(configuration.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(configuration.getUntaggedWriters(Level.WARN)).hasSize(2);
		assertThat(configuration.getUntaggedWriters(Level.ERROR)).hasSize(2);

		assertThat(configuration.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getTaggedWriters("foo", Level.WARN)).hasSize(2);
		assertThat(configuration.getTaggedWriters("foo", Level.ERROR)).hasSize(2);
	}

}
