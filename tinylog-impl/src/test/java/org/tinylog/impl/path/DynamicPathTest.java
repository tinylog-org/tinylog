package org.tinylog.impl.path;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import javax.inject.Inject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@CaptureLogEntries
class DynamicPathTest {

	@TempDir
	private Path directory;

	@Inject
	private Framework framework;

	/**
	 * Verifies that a file path with dynamic placeholders can be generated.
	 */
	@Test
	void generateDynamicPath() throws Exception {
		Clock clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
		Framework epochFramework = spy(framework);
		when(epochFramework.getClock()).thenReturn(clock);

		String file = directory + directory.getFileSystem().getSeparator() + "{date: yyyy-MM-dd}.log";
		DynamicPath path = new DynamicPath(epochFramework, file);
		assertThat(path.generateNewPath()).isEqualTo(directory.resolve("1970-01-01.log"));
	}

	/**
	 * Verifies that the directory for the generated file will be created.
	 */
	@Test
	void createDirectories() throws Exception {
		Path file = directory.resolve("sub").resolve("folder").resolve("foo.log");
		DynamicPath path = new DynamicPath(framework, file.toString());
		assertThat(path.generateNewPath())
			.isEqualTo(file)
			.doesNotExist()
			.extracting(Path::getParent, as(InstanceOfAssertFactories.PATH)).isDirectory();
	}

}
