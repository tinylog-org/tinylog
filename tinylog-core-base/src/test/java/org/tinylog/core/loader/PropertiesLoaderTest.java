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
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@CaptureLogEntries
class PropertiesLoaderTest {

	/**
	 * Custom temporary folder for creating files.
	 */
	@TempDir
	Path folder;

	@Inject
	private Framework actualFramework;

	@Inject
	private Log log;

	private Framework enrichedFramework;

	/**
	 * Enriches the actual framework by adding the actual temporary folder to the classpath.
	 *
	 * @throws MalformedURLException Failed to provide the current temporary folder as URL
	 */
	@BeforeEach
	void enrichFramework() throws MalformedURLException {
		URL[] urls = new URL[] {folder.toUri().toURL()};
		URLClassLoader classLoader = new URLClassLoader(urls, actualFramework.getClassLoader());

		enrichedFramework = spy(actualFramework);
		when(enrichedFramework.getClassLoader()).thenReturn(classLoader);
	}

	/**
	 * Verifies that the name is "properties".
	 */
	@Test
	void name() {
		PropertiesLoader loader = new PropertiesLoader();
		assertThat(loader.getName()).isEqualTo("properties");
	}

	/**
	 * Verifies that the priority is "0".
	 */
	@Test
	void priority() {
		PropertiesLoader loader = new PropertiesLoader();
		assertThat(loader.getPriority()).isZero();
	}

	/**
	 * Verifies that {@code tinylog.properties} will be loaded, if there is no other properties file.
	 */
	@Test
	void loadDefaultProductionPropertiesFile() throws IOException {
		createTextFile("tinylog.properties", "environment = production");

		Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
		assertThat(configuration).containsExactly(entry("environment", "production"));
	}

	/**
	 * Verifies that {@code tinylog-test.properties} will be loaded, if there is no other properties file.
	 */
	@Test
	void loadDefaultTestPropertiesFile() throws IOException {
		createTextFile("tinylog-test.properties", "environment = test");

		Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
		assertThat(configuration).containsExactly(entry("environment", "test"));
	}

	/**
	 * Verifies that {@code tinylog-dev.properties} will be loaded, if there is no other properties file.
	 */
	@Test
	void loadDefaultDevelopmentPropertiesFile() throws IOException {
		createTextFile("tinylog-dev.properties", "environment = development");

		Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
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

		Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
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

		Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
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

			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
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

			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
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

			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
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

			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
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

			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
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
		ClassLoader classLoader = new URLClassLoader(new URL[0], actualFramework.getClassLoader()) {
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

		when(enrichedFramework.getClassLoader()).thenReturn(classLoader);
		new PropertiesLoader().load(enrichedFramework);

		assertThat(log.consume()).hasSize(3).allSatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getThrowable()).hasMessage("Invalid resource");
		});
	}

	/**
	 * Verifies that the original order of all properties is preserved.
	 */
	@Test
	void preserveOrderOfProperties() throws IOException {
		createTextFile("tinylog.properties", "b=1", "c=2", "a=3");

		Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);
		assertThat(configuration).containsExactly(entry("b", "1"), entry("c", "2"), entry("a", "3"));
	}

	/**
	 * Verifies that a system property without any default value can be resolved.
	 */
	@Test
	void resolveExistingSystemPropertyWithoutDefault() throws Exception {
		createTextFile("tinylog.properties", "example = #{foo}");

		restoreSystemProperties(() -> {
			System.setProperty("foo", "42");
			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);

			assertThat(configuration).containsExactly(entry("example", "42"));
		});
	}

	/**
	 * Verifies that a system property without any default value will be kept unchanged, if it cannot be resolved.
	 */
	@Test
	void resolveMissingSystemPropertyWithoutDefault() throws Exception {
		createTextFile("tinylog.properties", "example = #{foo}");

		restoreSystemProperties(() -> {
			System.clearProperty("foo");
			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);

			assertThat(configuration).containsExactly(entry("example", "#{foo}"));
			assertThat(log.consume()).anySatisfy(entry -> {
				assertThat(entry.getLevel()).isEqualTo(Level.WARN);
				assertThat(entry.getMessage()).contains("foo");
			});
		});
	}

	/**
	 * Verifies that a system property with a defined default value can be resolved.
	 */
	@Test
	void resolveExistingSystemPropertyWithDefault() throws Exception {
		createTextFile("tinylog.properties", "example = #{ foo | default }");

		restoreSystemProperties(() -> {
			System.setProperty("foo", "42");
			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);

			assertThat(configuration).containsExactly(entry("example", "42"));
		});
	}

	/**
	 * Verifies that the default value of a system property with a defined default value will be used, if the system
	 * property cannot be resolved.
	 */
	@Test
	void resolveMissingSystemPropertyWithDefault() throws Exception {
		createTextFile("tinylog.properties", "example = #{ foo | default }");

		restoreSystemProperties(() -> {
			System.clearProperty("foo");
			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);

			assertThat(configuration).containsExactly(entry("example", "default"));
		});
	}

	/**
	 * Verifies that multiple system properties can be resolved.
	 */
	@Test
	void resolveMultipleSystemProperties() throws Exception {
		createTextFile("tinylog.properties", "example = <#{foo}> <#{bar}>");

		restoreSystemProperties(() -> {
			System.setProperty("foo", "1");
			System.setProperty("bar", "2");
			Map<String, String> configuration = new PropertiesLoader().load(enrichedFramework);

			assertThat(configuration).containsExactly(entry("example", "<1> <2>"));
		});
	}

	/**
	 * Verifies that the loader is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(ConfigurationLoader.class))
			.anyMatch(loader -> loader instanceof PropertiesLoader);
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
