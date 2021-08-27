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

class WriterMatrixParserTest {

	@Inject
	private Framework framework;

	/**
	 * Verifies that no writers are created for an empty configuration.
	 */
	@CaptureLogEntries(configuration = {})
	@Test
	void emptyConfiguration() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();
		assertThat(matrix.getAllWriters()).isEmpty();
	}

	/**
	 * Verifies that all severity levels will be enabled if the severity level is not defined.
	 */
	@CaptureLogEntries(configuration = "writer.type=console")
	@Test
	void writerWithoutAnySeverityLevel() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();

		assertThat(matrix.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(matrix.getUntaggedWriters(Level.TRACE)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(matrix.getTaggedWriters("foo", Level.TRACE)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the global severity level is applied to writer without any configured custom severity level.
	 */
	@CaptureLogEntries(configuration = {"level=INFO", "writer.type=console"})
	@Test
	void writerWithGlobalSeverityLevel() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();

		assertThat(matrix.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(matrix.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.DEBUG)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(matrix.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that a custom severity level can be defined for a writer.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithCustomSeverityLevel() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();

		assertThat(matrix.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(matrix.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(matrix.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the custom severity level will be used, if it is more restrict than the global severity level.
	 */
	@CaptureLogEntries(configuration = {"level=TRACE", "writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithHigherCustomSeverityLevel() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();

		assertThat(matrix.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(matrix.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(matrix.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the global severity level will be used, if it is more restrict than the custom severity level.
	 */
	@CaptureLogEntries(configuration = {"level=INFO", "writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithLowerCustomSeverityLevel() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();

		assertThat(matrix.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(matrix.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.DEBUG)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(matrix.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that no writer will be created, if the severity level is OFF.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=OFF"})
	@Test
	void writerWithDisabledSeverityLevel() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();
		assertThat(matrix.getAllWriters()).isEmpty();
	}

	/**
	 * Verifies that severity levels can be defined for tags.
	 */
	@CaptureLogEntries(configuration = {"level=", "writer.type=console", "writer.level=DEBUG@foo"})
	@Test
	void writerWithTaggedSeverityLevel() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();

		assertThat(matrix.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(matrix.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.DEBUG)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.INFO)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.WARN)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.ERROR)).isEmpty();

		assertThat(matrix.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that a generic and custom severity level can be combined.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=DEBUG,WARN@foo"})
	@Test
	void writerWithMixedSeverityLevels() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();

		assertThat(matrix.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(matrix.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.WARN)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.ERROR)).hasSize(1);

		assertThat(matrix.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.INFO)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.WARN)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that different severity levels can be defined for different writers.
	 */
	@CaptureLogEntries(configuration = {
		"writer1.type=console", "writer1.level=DEBUG", "writer2.type=logcat", "writer2.level=WARN"
	})
	@Test
	void multipleWriters() {
		WriterMatrix matrix = new WriterMatrixParser(framework).parse();

		assertThat(matrix.getAllWriters())
			.hasSize(2)
			.hasAtLeastOneElementOfType(ConsoleWriter.class)
			.hasAtLeastOneElementOfType(LogcatWriter.class);

		assertThat(matrix.getUntaggedWriters(Level.TRACE)).isEmpty();
		assertThat(matrix.getUntaggedWriters(Level.DEBUG)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.INFO)).hasSize(1);
		assertThat(matrix.getUntaggedWriters(Level.WARN)).hasSize(2);
		assertThat(matrix.getUntaggedWriters(Level.ERROR)).hasSize(2);

		assertThat(matrix.getTaggedWriters("foo", Level.TRACE)).isEmpty();
		assertThat(matrix.getTaggedWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.INFO)).hasSize(1);
		assertThat(matrix.getTaggedWriters("foo", Level.WARN)).hasSize(2);
		assertThat(matrix.getTaggedWriters("foo", Level.ERROR)).hasSize(2);
	}

}
