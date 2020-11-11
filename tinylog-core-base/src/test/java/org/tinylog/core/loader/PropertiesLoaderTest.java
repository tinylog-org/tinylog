package org.tinylog.core.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@CaptureLogEntries
class PropertiesLoaderTest {

	/**
	 * Custom temporary folder for creating files.
	 */
	@TempDir
	Path folder;

	@Inject
	private Log log;

	/**
	 * Verifies that {@code tinylog.properties} will be loaded, if there is no other properties file.
	 */
	@Test
	void loadDefaultProductionPropertiesFile() throws IOException {
		createTextFile("tinylog.properties", "environment = production");

		Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
		assertThat(configuration).containsExactly(entry("environment", "production"));
	}

	/**
	 * Verifies that {@code tinylog-test.properties} will be loaded, if there is no other properties file.
	 */
	@Test
	void loadDefaultTestPropertiesFile() throws IOException {
		createTextFile("tinylog-test.properties", "environment = test");

		Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
		assertThat(configuration).containsExactly(entry("environment", "test"));
	}

	/**
	 * Verifies that {@code tinylog-dev.properties} will be loaded, if there is no other properties file.
	 */
	@Test
	void loadDefaultDevelopmentPropertiesFile() throws IOException {
		createTextFile("tinylog-dev.properties", "environment = development");

		Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
		assertThat(configuration).containsExactly(entry("environment", "development"));
	}

	/**
	 * Verifies that {@code tinylog-test.properties} will be loaded, if {@code tinylog.properties} and
	 * {@code tinylog-test.properties} are available.
	 */
	@Test
	void preferTestOverProductionPropertiesFile() throws IOException {
		createTextFile("tinylog.properties", "production = yes");
		createTextFile("tinylog-test.properties", "test = yes");

		Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
		assertThat(configuration).containsExactly(entry("test", "yes"));
	}

	/**
	 * Verifies that {@code tinylog-dev.properties} will be loaded, if {@code tinylog-test.properties} and
	 * {@code tinylog-dev.properties} are available.
	 */
	@Test
	void preferDevelopmentOverTestPropertiesFile() throws IOException {
		createTextFile("tinylog-test.properties", "test = yes");
		createTextFile("tinylog-dev.properties", "development = yes");

		Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
		assertThat(configuration).containsExactly(entry("development", "yes"));
	}

	/**
	 * Verifies that a custom resource from the classpath can be provided as tinylog configuration.
	 */
	@Test
	void loadCustomResource() throws Exception {
		restoreSystemProperties(() -> {
			createTextFile("my-configuration.properties", "foo = bar");
			System.setProperty("tinylog.configuration", "my-configuration.properties");

			Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
			assertThat(configuration).containsExactly(entry("foo", "bar"));
		});
	}

	/**
	 * Verifies that a custom local file can be provided as tinylog configuration.
	 */
	@Test
	void loadCustomLocalFile() throws Exception {
		restoreSystemProperties(() -> {
			Path file = createTextFile("my-configuration.properties", "foo = bar");
			System.setProperty("tinylog.configuration", file.toString());

			Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
			assertThat(configuration).containsExactly(entry("foo", "bar"));
		});
	}

	/**
	 * Verifies that a custom URL can be provided as tinylog configuration.
	 */
	@Test
	void loadCustomUrl() throws Exception {
		restoreSystemProperties(() -> {
			Path file = createTextFile("my-configuration.properties", "foo = bar");
			System.setProperty("tinylog.configuration", file.toUri().toURL().toString());

			Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
			assertThat(configuration).containsExactly(entry("foo", "bar"));
		});
	}

	/**
	 * Verifies that no default properties files will be loaded, if a custom configuration is provided.
	 */
	@Test
	void preferCustomOverDefaultPropertiesFile() throws Exception {
		restoreSystemProperties(() -> {
			createTextFile("tinylog-custom.properties", "custom = yes");
			createTextFile("tinylog.properties", "production = yes");
			createTextFile("tinylog-test.properties", "test = yes");
			createTextFile("tinylog-dev.properties", "development = yes");

			System.setProperty("tinylog.configuration", "tinylog-custom.properties");

			Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
			assertThat(configuration).containsExactly(entry("custom", "yes"));
		});
	}

	/**
	 * Verifies that an error will be output and configuration will be loaded from an available classpath resource,
	 * if the defined custom configuration does not exist.
	 */
	@Test
	void printErrorIfCustomPropertiesFileDoesNotExist() throws Exception {
		restoreSystemProperties(() -> {
			createTextFile("tinylog.properties", "production = yes");
			System.setProperty("tinylog.configuration", "tinylog-custom.properties");

			Map<Object, Object> configuration = new PropertiesLoader().load(generateClassLoader());
			assertThat(configuration).containsExactly(entry("production", "yes"));

			assertThat(log.consume()).hasSize(1).allSatisfy(entry -> {
				assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
				assertThat(entry.getMessage()).contains("tinylog-custom.properties");
			});
		});
	}

	/**
	 * Verifies that an error message will be output, if a resource stream throws an {@link IOException}.
	 */
	@Test
	void printErrorIfLoadingPropertiesFileFails() {
		ClassLoader classLoader = new URLClassLoader(new URL[0], getClass().getClassLoader()) {
			@Override
			public InputStream getResourceAsStream(String name) {
				try {
					InputStream stream = mock(InputStream.class);
					when(stream.read(any(byte[].class))).thenThrow(new IOException("Invalid resource"));
					return stream;
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		};

		new PropertiesLoader().load(classLoader);

		assertThat(log.consume()).hasSize(3).allSatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getThrowable()).hasMessage("Invalid resource");
		});
	}

	/**
	 * Generates a class loader that contains the current temporary folder as source for loading resource files.
	 *
	 * @return The created class loader
	 * @throws MalformedURLException Failed to provide the current temporary folder as URL
	 */
	private ClassLoader generateClassLoader() throws MalformedURLException {
		URL[] urls = new URL[] {folder.toUri().toURL()};
		return new URLClassLoader(urls, getClass().getClassLoader());
	}

	/**
	 * Creates a text file in the current temporary folder.
	 *
	 * @param fileName File name for the text file
	 * @param lines Lines to write to the text file
	 * @return The created file
	 * @throws IOException Failed to create a text file
	 */
	private Path createTextFile(String fileName, String... lines) throws IOException {
		Path file = folder.resolve(fileName);
		String content = String.join("\n", lines);
		Files.write(file, content.getBytes(StandardCharsets.UTF_8));
		return file;
	}

}
