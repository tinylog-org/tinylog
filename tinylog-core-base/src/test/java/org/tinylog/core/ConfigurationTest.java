package org.tinylog.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tinylog.core.test.CaptureLogEntries;
import org.tinylog.core.test.Log;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@CaptureLogEntries
class ConfigurationTest {

	@Inject
	private Log log;

	/**
	 * Tests for getting and setting values.
	 */
	@Nested
	class Values {

		/**
		 * Verifies that a new value can be set.
		 */
		@Test
		void setNewValue() {
			Configuration configuration = new Configuration();
			Configuration other = configuration.set("foo", "42");

			assertThat(configuration.getValue("foo")).isEqualTo("42");
			assertThat(other).isSameAs(configuration);
		}

		/**
		 * Verifies that an existing value can be overwritten.
		 */
		@Test
		void overwriteExistingValue() {
			Configuration configuration = new Configuration();
			Configuration other = configuration.set("foo", "1").set("foo", "2");

			assertThat(configuration.getValue("foo")).isEqualTo("2");
			assertThat(other).isSameAs(configuration);
		}

		/**
		 * Verifies that an empty value for property "locale" is interpreted as {@link Locale#ROOT}.
		 */
		@Test
		void getExistingEmptyLocale() {
			Configuration configuration = new Configuration().set("locale", "");
			assertThat(configuration.getLocale()).isEqualTo(Locale.ROOT);
		}

		/**
		 * Verifies that a locale that contains only the language can be created from property "locale".
		 */
		@Test
		void getExistingLocaleWithLanguageOnly() {
			Configuration configuration = new Configuration().set("locale", "de");
			assertThat(configuration.getLocale()).isEqualTo(new Locale("de"));
		}

		/**
		 * Verifies that a locale that contains language and country can be created from property "locale".
		 */
		@Test
		void getExistingLocaleWithLanguageAndCountry() {
			Configuration configuration = new Configuration().set("locale", "it_CH");
			assertThat(configuration.getLocale()).isEqualTo(new Locale("it", "CH"));
		}

		/**
		 * Verifies that a locale that contains language, country, and variant can be created from property "locale".
		 */
		@Test
		void getExistingFullLocale() {
			Configuration configuration = new Configuration().set("locale", "en_US_UNIX");
			assertThat(configuration.getLocale()).isEqualTo(new Locale("en", "US", "UNIX"));
		}

		/**
		 * Verifies that {@link Locale#getDefault()} will be returned if property "locale" is not set.
		 */
		@Test
		void getMissingLocale() {
			Configuration configuration = new Configuration();
			assertThat(configuration.getLocale()).isSameAs(Locale.getDefault());
		}

		/**
		 * Verifies that an existing value can be received.
		 */
		@Test
		void getExistingStringValue() {
			Configuration configuration = new Configuration().set("foo", "42");
			assertThat(configuration.getValue("foo")).isEqualTo("42");
		}

		/**
		 * Verifies that {@code null} is returned for a missing value.
		 */
		@Test
		void getMissingStringValue() {
			Configuration configuration = new Configuration();
			assertThat(configuration.getValue("foo")).isNull();
		}

		/**
		 * Verifies that a single value can be returned as list.
		 */
		@Test
		void getSingleListValue() {
			Configuration configuration = new Configuration().set("foo", "42");
			assertThat(configuration.getList("foo")).containsExactly("42");
		}

		/**
		 * Verifies that multiple values, which are separated by commas, can be returned as list.
		 */
		@Test
		void getMultipleListValues() {
			Configuration configuration = new Configuration().set("foo", "1, 2, 3");
			assertThat(configuration.getList("foo")).containsExactly("1", "2", "3");
		}

		/**
		 * Verifies that an empty value is returned as empty list.
		 */
		@Test
		void getEmptyListValue() {
			Configuration configuration = new Configuration().set("foo", "");
			assertThat(configuration.getList("foo")).isEmpty();
		}

		/**
		 * Verifies that a missing value is returned as empty list.
		 */
		@Test
		void getMissingListValue() {
			Configuration configuration = new Configuration();
			assertThat(configuration.getList("foo")).isEmpty();
		}

		/**
		 * Verifies that no further modifications are allowed after freezing.
		 */
		@Test
		void freeze() {
			Configuration configuration = new Configuration();
			assertThat(configuration.isFrozen()).isFalse();

			configuration.freeze();
			assertThat(configuration.isFrozen()).isTrue();
			assertThatCode(() -> configuration.set("foo", "42")).isInstanceOf(UnsupportedOperationException.class);
		}

	}

	/**
	 * Tests for loading from file.
	 */
	@Nested
	class PropertiesFile {

		/**
		 * Custom temporary folder for creating files.
		 */
		@TempDir
		Path folder;

		/**
		 * Verifies that {@code tinylog.properties} will be loaded, if there is no other properties file.
		 */
		@Test
		void loadDefaultProductionPropertiesFile() throws IOException {
			createTextFile("tinylog.properties", "environment = production");

			Configuration configuration = new Configuration();
			configuration.loadPropertiesFile(generateClassLoader());
			assertThat(configuration.getValue("environment")).isEqualTo("production");
		}

		/**
		 * Verifies that {@code tinylog-test.properties} will be loaded, if there is no other properties file.
		 */
		@Test
		void loadDefaultTestPropertiesFile() throws IOException {
			createTextFile("tinylog-test.properties", "environment = test");

			Configuration configuration = new Configuration();
			configuration.loadPropertiesFile(generateClassLoader());
			assertThat(configuration.getValue("environment")).isEqualTo("test");
		}

		/**
		 * Verifies that {@code tinylog-dev.properties} will be loaded, if there is no other properties file.
		 */
		@Test
		void loadDefaultDevelopmentPropertiesFile() throws IOException {
			createTextFile("tinylog-dev.properties", "environment = development");

			Configuration configuration = new Configuration();
			configuration.loadPropertiesFile(generateClassLoader());
			assertThat(configuration.getValue("environment")).isEqualTo("development");
		}

		/**
		 * Verifies that {@code tinylog-test.properties} will be loaded, if {@code tinylog.properties} and
		 * {@code tinylog-test.properties} are available.
		 */
		@Test
		void preferTestOverProductionPropertiesFile() throws IOException {
			createTextFile("tinylog.properties", "production = yes");
			createTextFile("tinylog-test.properties", "test = yes");

			Configuration configuration = new Configuration();
			configuration.loadPropertiesFile(generateClassLoader());
			assertThat(configuration.getValue("production")).isNull();
			assertThat(configuration.getValue("test")).isEqualTo("yes");
		}

		/**
		 * Verifies that {@code tinylog-dev.properties} will be loaded, if {@code tinylog-test.properties} and
		 * {@code tinylog-dev.properties} are available.
		 */
		@Test
		void preferDevelopmentOverTestPropertiesFile() throws IOException {
			createTextFile("tinylog-test.properties", "test = yes");
			createTextFile("tinylog-dev.properties", "development = yes");

			Configuration configuration = new Configuration();
			configuration.loadPropertiesFile(generateClassLoader());
			assertThat(configuration.getValue("test")).isNull();
			assertThat(configuration.getValue("development")).isEqualTo("yes");
		}

		/**
		 * Verifies that a custom resource from the classpath can be provided as tinylog configuration.
		 */
		@Test
		void loadCustomResource() throws Exception {
			restoreSystemProperties(() -> {
				createTextFile("my-configuration.properties", "foo = bar");
				System.setProperty("tinylog.configuration", "my-configuration.properties");

				Configuration configuration = new Configuration();
				configuration.loadPropertiesFile(generateClassLoader());
				assertThat(configuration.getValue("foo")).isEqualTo("bar");
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

				Configuration configuration = new Configuration();
				configuration.loadPropertiesFile(getClass().getClassLoader());
				assertThat(configuration.getValue("foo")).isEqualTo("bar");
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

				Configuration configuration = new Configuration();
				configuration.loadPropertiesFile(getClass().getClassLoader());
				assertThat(configuration.getValue("foo")).isEqualTo("bar");
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

				Configuration configuration = new Configuration();
				configuration.loadPropertiesFile(generateClassLoader());
				assertThat(configuration.getValue("custom")).isEqualTo("yes");
				assertThat(configuration.getValue("production")).isNull();
				assertThat(configuration.getValue("test")).isNull();
				assertThat(configuration.getValue("development")).isNull();
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

				System.setProperty("tinylog.configuration", folder.resolve("tinylog-custom.properties").toString());
				Configuration configuration = new Configuration();
				configuration.loadPropertiesFile(generateClassLoader());

				assertThat(configuration.getValue("production")).isEqualTo("yes");
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

			Configuration configuration = new Configuration();
			configuration.loadPropertiesFile(classLoader);

			assertThat(log.consume()).hasSize(3).allSatisfy(entry -> {
				assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
				assertThat(entry.getThrowable()).hasMessage("Invalid resource");
			});
		}

		/**
		 * Verifies that loading properties file is not allowed after freezing.
		 */
		@Test
		void freeze() {
			Configuration configuration = new Configuration();
			assertThat(configuration.isFrozen()).isFalse();

			configuration.freeze();
			assertThat(configuration.isFrozen()).isTrue();
			assertThatCode(() -> configuration.loadPropertiesFile(getClass().getClassLoader()))
				.isInstanceOf(UnsupportedOperationException.class);
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

}
