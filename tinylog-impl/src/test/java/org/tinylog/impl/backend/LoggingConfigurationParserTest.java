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

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.OFF);
				assertThat(value.getLevel("foo")).isEqualTo(Level.OFF);
			});

		assertThat(configuration.getAllWriters()).isEmpty();
	}

	/**
	 * Verifies that all severity levels will be enabled if the severity level is not defined.
	 */
	@CaptureLogEntries(configuration = "writer.type=console")
	@Test
	void writerWithoutAnySeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.TRACE);
				assertThat(value.getLevel("foo")).isEqualTo(Level.TRACE);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(1);

		assertThat(configuration.getWriters("foo", Level.TRACE)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the global severity level is applied to writer without any configured custom severity level.
	 */
	@CaptureLogEntries(configuration = {"level=INFO", "writer.type=console"})
	@Test
	void writerWithGlobalSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.INFO);
				assertThat(value.getLevel("foo")).isEqualTo(Level.INFO);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).isEmpty();
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(1);

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that specific severity levels can be set for packages and classes.
	 */
	@CaptureLogEntries(configuration = {"level=INFO", "level@bar=WARN", "level@bar.Foo=DEBUG", "writer.type=console"})
	@Test
	void writerWithPackageAndClassSeverityLevels() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(3)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.INFO);
				assertThat(value.getLevel("foo")).isEqualTo(Level.INFO);
			})
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("bar");
				assertThat(value.getLevel("-")).isEqualTo(Level.WARN);
				assertThat(value.getLevel("foo")).isEqualTo(Level.WARN);
			})
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("bar.Foo");
				assertThat(value.getLevel("-")).isEqualTo(Level.DEBUG);
				assertThat(value.getLevel("foo")).isEqualTo(Level.DEBUG);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(1);

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that a custom severity level can be defined for a writer.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithCustomSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.DEBUG);
				assertThat(value.getLevel("foo")).isEqualTo(Level.DEBUG);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(1);

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that severity levels of packages and classes are adjusted to the custom severity level of the writer.
	 */
	@CaptureLogEntries(configuration = {"level=WARN", "level@bar=DEBUG", "writer.type=console", "writer.level=INFO"})
	@Test
	void writerWithCustomAndPackageSeverityLevels() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(2)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.WARN);
				assertThat(value.getLevel("foo")).isEqualTo(Level.WARN);
			})
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("bar");
				assertThat(value.getLevel("-")).isEqualTo(Level.INFO);
				assertThat(value.getLevel("foo")).isEqualTo(Level.INFO);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).isEmpty();
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(1);

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the custom severity level of a writer will be used, if it is more severe than the global severity
	 * level.
	 */
	@CaptureLogEntries(configuration = {"level=TRACE", "writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithMoreSevereLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.DEBUG);
				assertThat(value.getLevel("foo")).isEqualTo(Level.DEBUG);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(1);

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that the global severity level will be used, if it is more severe than the custom severity level of
	 * the writer.
	 */
	@CaptureLogEntries(configuration = {"level=INFO", "writer.type=console", "writer.level=DEBUG"})
	@Test
	void writerWithLessSevereLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.INFO);
				assertThat(value.getLevel("foo")).isEqualTo(Level.INFO);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).isEmpty();
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(1);

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that no writer will be created, if the severity level is OFF.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=OFF"})
	@Test
	void writerWithDisabledSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.OFF);
				assertThat(value.getLevel("foo")).isEqualTo(Level.OFF);
			});

		assertThat(configuration.getAllWriters()).isEmpty();
	}

	/**
	 * Verifies that severity levels can be defined for tags.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=DEBUG@foo"})
	@Test
	void writerWithTaggedSeverityLevel() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.OFF);
				assertThat(value.getLevel("foo")).isEqualTo(Level.DEBUG);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).isEmpty();
		assertThat(configuration.getWriters("-", Level.INFO)).isEmpty();
		assertThat(configuration.getWriters("-", Level.WARN)).isEmpty();
		assertThat(configuration.getWriters("-", Level.ERROR)).isEmpty();

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
	}

	/**
	 * Verifies that a generic and custom severity level can be combined.
	 */
	@CaptureLogEntries(configuration = {"writer.type=console", "writer.level=DEBUG,WARN@foo"})
	@Test
	void writerWithMixedSeverityLevels() {
		LoggingConfiguration configuration = new LoggingConfigurationParser(framework).parse();

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.DEBUG);
				assertThat(value.getLevel("foo")).isEqualTo(Level.WARN);
			});

		assertThat(configuration.getAllWriters()).hasExactlyElementsOfTypes(ConsoleWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(1);

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.INFO)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(1);
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

		assertThat(configuration.getSeverityLevels())
			.hasSize(1)
			.anySatisfy((key, value) -> {
				assertThat(key).isEqualTo("");
				assertThat(value.getLevel("-")).isEqualTo(Level.DEBUG);
				assertThat(value.getLevel("foo")).isEqualTo(Level.DEBUG);
			});

		assertThat(configuration.getAllWriters())
			.hasSize(2)
			.hasAtLeastOneElementOfType(ConsoleWriter.class)
			.hasAtLeastOneElementOfType(LogcatWriter.class);

		assertThat(configuration.getWriters("-", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("-", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("-", Level.WARN)).hasSize(2);
		assertThat(configuration.getWriters("-", Level.ERROR)).hasSize(2);

		assertThat(configuration.getWriters("foo", Level.TRACE)).isEmpty();
		assertThat(configuration.getWriters("foo", Level.DEBUG)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.INFO)).hasSize(1);
		assertThat(configuration.getWriters("foo", Level.WARN)).hasSize(2);
		assertThat(configuration.getWriters("foo", Level.ERROR)).hasSize(2);
	}

}
