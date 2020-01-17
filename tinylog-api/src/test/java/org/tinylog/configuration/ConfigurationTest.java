/*
 * Copyright 2016 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.tinylog.util.Maps.doubletonMap;

/**
 * Tests for {@link Configuration}.
 */
public final class ConfigurationTest {

	private static final String PROPERTIES_PREFIX = "tinylog.";
	private static final String CONFIGURATION_PROPERTY = PROPERTIES_PREFIX + "configuration";

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Ensures that {@link Configuration} class is loaded.
	 *
	 * @throws ClassNotFoundException
	 *             Configuration class couldn't be found
	 */
	@BeforeClass
	public static void init() throws ClassNotFoundException {
		Class.forName(Configuration.class.getName());
	}

	/**
	 * Resets overridden properties.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link Configuration#load()}
	 */
	@AfterClass
	public static void reset() throws Exception {
		loadProperties(null);
	}

	/**
	 * Clears configuration and all tinylog properties.
	 */
	@After
	public void clear() {
		for (Enumeration<Object> enumeration = System.getProperties().keys(); enumeration.hasMoreElements();) {
			String property = (String) enumeration.nextElement();
			if (property.startsWith(PROPERTIES_PREFIX)) {
				System.clearProperty(property);
			}
		}

		Configuration.replace(Collections.emptyMap());
	}

	/**
	 * Verifies that {@code tinylog.properties} will be loaded.
	 *
	 * @throws Exception
	 *             Failed creating {@code tinylog.properties} or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void productionPropertiesFile() throws Exception {
		FileSystem.createResource("tinylog.properties", "level = off");
		try {
			loadProperties(null);
			assertThat(Configuration.get("level")).isEqualToIgnoringCase("off");
		} finally {
			FileSystem.deleteResource("tinylog.properties");
		}
	}

	/**
	 * Verifies that {@code tinylog-test.properties} will be loaded.
	 *
	 * @throws Exception
	 *             Failed creating {@code tinylog.properties} or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void testPropertiesFile() throws Exception {
		FileSystem.createResource("tinylog-test.properties", "level = off");

		try {
			loadProperties(null);
			assertThat(Configuration.get("level")).isEqualToIgnoringCase("off");
		} finally {
			FileSystem.deleteResource("tinylog-test.properties");
		}
	}

	/**
	 * Verifies that {@code tinylog-test.properties} will be loaded, if this file and {@code tinylog.properties} are available.
	 *
	 * @throws Exception
	 *             Failed creating {@code tinylog.properties} or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void testPropertiesFileBeforeRelease() throws Exception {
		FileSystem.createResource("tinylog-test.properties", "level = off");
		FileSystem.createResource("tinylog.properties", "level = trace");

		try {
			loadProperties(null);
			assertThat(Configuration.get("level")).isEqualToIgnoringCase("off");
		} finally {
			FileSystem.deleteResource("tinylog-dev.properties");
			FileSystem.deleteResource("tinylog-test.properties");
			FileSystem.deleteResource("tinylog.properties");
		}
	}

	/**
	 * Verifies that {@code tinylog-dev.properties} will be loaded.
	 *
	 * @throws Exception
	 *             Failed creating {@code tinylog.properties} or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void developmentPropertiesFile() throws Exception {
		FileSystem.createResource("tinylog-dev.properties", "level = off");

		try {
			loadProperties(null);
			assertThat(Configuration.get("level")).isEqualToIgnoringCase("off");
		} finally {
			FileSystem.deleteResource("tinylog-dev.properties");
		}
	}

	/**
	 * Verifies that {@code tinylog-dev.properties} will be loaded, if all three properties files are available.
	 *
	 * @throws Exception
	 *             Failed creating {@code tinylog.properties} or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void developmentPropertiesFileBeforeOthers() throws Exception {
		FileSystem.createResource("tinylog-dev.properties", "level = off");
		FileSystem.createResource("tinylog-test.properties", "level = trace");
		FileSystem.createResource("tinylog.properties", "level = trace");

		try {
			loadProperties(null);
			assertThat(Configuration.get("level")).isEqualToIgnoringCase("off");
		} finally {
			FileSystem.deleteResource("tinylog-dev.properties");
			FileSystem.deleteResource("tinylog-test.properties");
			FileSystem.deleteResource("tinylog.properties");
		}
	}

	/**
	 * Verifies that properties from a custom defined URL will be loaded.
	 *
	 * @throws Exception
	 *             Failed creating temporary file URL or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void propertiesFileFromUrl() throws Exception {
		String path = FileSystem.createTemporaryFile("level = debug");
		String url = new File(path).getAbsoluteFile().toURI().toURL().toString();
		loadProperties(url);
		assertThat(Configuration.get("level")).isEqualToIgnoringCase("debug");
	}

	/**
	 * Verifies that a custom defined resource will be loaded from class path.
	 *
	 * @throws Exception
	 *             Failed creating temporary resource or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void propertiesFileFromClassPath() throws Exception {
		loadProperties(FileSystem.createTemporaryResource("level = info"));
		assertThat(Configuration.get("level")).isEqualToIgnoringCase("info");
	}

	/**
	 * Verifies that a custom defined file will be loaded from file system.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void propertiesFileFromFileSystem() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("level = warn"));
		assertThat(Configuration.get("level")).isEqualToIgnoringCase("warn");
	}

	/**
	 * Verifies that an accurate error message will be output if a configuration file couldn't be found.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void nonExistingFile() throws Exception {
		String path = FileSystem.createTemporaryFile();
		Files.delete(Paths.get(path));
		loadProperties(path);

		assertThat(Configuration.get("level")).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(path);
	}

	/**
	 * Verifies that a system property overrides a property from properties file.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void overridingProperty() throws Exception {
		System.setProperty(PROPERTIES_PREFIX + "level", "debug");
		loadProperties(FileSystem.createTemporaryFile("level = info"));

		assertThat(Configuration.get("level")).isEqualToIgnoringCase("debug");
	}

	/**
	 * Verifies that {@link Locale#ROOT} will be used, if there is no defined locale.
	 */
	@Test
	public void defaultLocale() {
		Locale locale = Configuration.getLocale();
		assertThat(locale).isEqualTo(Locale.ROOT);
	}

	/**
	 * Verifies that an empty locale will be handled correctly.
	 */
	@Test
	public void emptyLocale() {
		Configuration.set("locale", "");
		Locale locale = Configuration.getLocale();
		assertThat(locale).isEqualTo(Locale.ROOT);
	}

	/**
	 * Verifies that a language only locale will be parsed correctly.
	 */
	@Test
	public void languageLocale() {
		Configuration.set("locale", "en");
		Locale locale = Configuration.getLocale();
		assertThat(locale).isEqualTo(new Locale("en"));
	}

	/**
	 * Verifies that a locale with language and country will be parsed correctly.
	 */
	@Test
	public void countryLocale() {
		Configuration.set("locale", "en_US");
		Locale locale = Configuration.getLocale();
		assertThat(locale).isEqualTo(new Locale("en", "US"));
	}

	/**
	 * Verifies that a full locale with language, country and variant will be parsed correctly.
	 */
	@Test
	public void fullLocale() {
		Configuration.set("locale", "no_NO_NY");
		Locale locale = Configuration.getLocale();
		assertThat(locale).isEqualTo(new Locale("no", "NO", "NY"));
	}

	/**
	 * Verifies that {@link Configuration#getSiblings(String)} finds the expected sibling properties.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void foundSiblings() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("level = info", "writer = console", "writer2 = file", "writer2.file = log.txt"));
		assertThat(Configuration.getSiblings("writer")).containsOnly(entry("writer", "console"), entry("writer2", "file"));
	}

	/**
	 * Verifies that {@link Configuration#getSiblings(String)} finds the expected sibling properties even if there are
	 * dots after a prefix ending with an at sign.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void foundSiblingsWithAtSign() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("level = info", "level@org.test = trace", "level@com = info"));
		assertThat(Configuration.getSiblings("level@"))
			.containsOnly(entry("level@org.test", "trace"), entry("level@com", "info"));
	}

	/**
	 * Verifies that {@link Configuration#getSiblings(String)} can handle requests without any matching sibling
	 * properties.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void noSiblings() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("level = info"));
		assertThat(Configuration.getSiblings("writer")).isEmpty();
	}

	/**
	 * Verifies that {@link Configuration#getChildren(String)} finds the expected child properties.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void foundChildren() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("level = info", "writer = file", "writer.file = log.txt", "writer.buffered = true"));
		assertThat(Configuration.getChildren("writer")).containsOnly(entry("file", "log.txt"), entry("buffered", "true"));
	}

	/**
	 * Verifies that {@link Configuration#getChildren(String)} can handle requests without any matching child
	 * properties.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void noChildren() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("level = info"));
		assertThat(Configuration.getChildren("writer")).isEmpty();
	}

	/**
	 * Verifies that system properties will be resolved.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void resolveSystemProperty() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = #{os.name}"));
		assertThat(Configuration.get("test")).isEqualTo(System.getProperty("os.name"));
	}

	/**
	 * Verifies that multiple system properties will be resolved in a text.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void resolveMultipleProperties() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = JRE #{java.version} from #{java.vendor}"));
		assertThat(Configuration.get("test"))
			.isEqualTo("JRE " + System.getProperty("java.version") + " from " + System.getProperty("java.vendor"));
	}

	/**
	 * Verifies that environment variables will be resolved.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void resolveEnvironmentVariable() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${PATH}"));
		assertThat(Configuration.get("test")).isEqualTo(System.getenv("PATH"));
	}

	/**
	 * Verifies that mixed environment variables and system properties will be resolved in a text.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void resolveMixedVariablesAndProperties() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${PATH} read by JRE #{java.version}"));
		assertThat(Configuration.get("test")).isEqualTo(System.getenv("PATH") + " read by JRE " + System.getProperty("java.version"));
	}

	/**
	 * Verifies that an accurate warning message will be output for empty variables.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void emptyVariable() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${}"));
		assertThat(Configuration.get("test")).isEqualTo("${}");
		assertThat(systemStream.consumeErrorOutput()).contains("WARN").containsOnlyOnce("${}");
	}

	/**
	 * Verifies that an accurate warning message will be output, if a closing curly bracket is missing.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void incompleteVariable() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${os.name"));
		assertThat(Configuration.get("test")).isEqualTo("${os.name");
		assertThat(systemStream.consumeErrorOutput()).contains("WARN").containsOnlyOnce("${os.name");
	}

	/**
	 * Verifies that an accurate warning message will be output, if a variable exists neither as system property nor as
	 * environment variable.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void nonExistentVariable() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${my.invalid.variable}"));
		assertThat(Configuration.get("test")).isEqualTo("${my.invalid.variable}");
		assertThat(systemStream.consumeErrorOutput()).contains("WARN").containsOnlyOnce("my.invalid.variable");
	}

	/**
	 * Verifies that environment variables will be resolved and the given default value is ignored.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void existingVariableWithUnusedDefaultValue() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${PATH:/a/default/path}"));
		assertThat(Configuration.get("test")).isEqualTo(System.getenv("PATH"));
	}

	/**
	 * Verifies that a given empty string is used as default value, if a variable exists neither as system property nor
	 * as environment variable..
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void nonExistingVariableWithEmptyDefaultValue() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${my.invalid.variable:}"));
		assertThat(Configuration.get("test")).isEqualTo("");
	}

	/**
	 * Verifies that a given default value is used, if a variable exists neither as system property nor
	 * as environment variable..
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void nonExistingVariableWithDefaultValue() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${my.invalid.variable:a_default_value}"));
		assertThat(Configuration.get("test")).isEqualTo("a_default_value");
	}

	/**
	 * Verifies that an accurate warning message will be output, if multiple default values were given.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void nonExistingVariableMultipleDefaultValues() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = ${PATH:a_default_value:an_other_default_value}"));
		assertThat(systemStream.consumeErrorOutput()).contains("WARN").containsOnlyOnce("PATH");
	}

	/**
	 * Verifies that a new property can be added.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link Configuration#load()}
	 */
	@Test
	public void addProperty() throws Exception {
		loadProperties(null);

		assertThat(Configuration.get("test")).isNull();
		Configuration.set("test", "Hello World!");
		assertThat(Configuration.get("test")).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that an already existing property can be overridden.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void overrideProperty() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("test = Hello World!"));

		assertThat(Configuration.get("test")).isEqualTo("Hello World!");
		Configuration.set("test", "Bye World!");
		assertThat(Configuration.get("test")).isEqualTo("Bye World!");
	}

	/**
	 * Verifies that the actual configuration can be replaced by a new one.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void replaceProperties() throws Exception {
		loadProperties(FileSystem.createTemporaryFile("a = 1", "b = 2"));

		assertThat(Configuration.get("a")).isEqualTo("1");
		assertThat(Configuration.get("b")).isEqualTo("2");

		Configuration.replace(doubletonMap("a", "0", "c", "42"));

		assertThat(Configuration.get("a")).isEqualTo("0");
		assertThat(Configuration.get("c")).isEqualTo("42");
	}

	/**
	 * Triggers (re-)loading properties.
	 *
	 * @param path
	 *            Path to properties configuration or {@code null} if undefined
	 * @throws Exception
	 *             Failed invoking private method {@link Configuration#load()}
	 */
	private static void loadProperties(final String path) throws Exception {
		if (path != null) {
			System.setProperty(CONFIGURATION_PROPERTY, path);
		}

		Properties properties = Whitebox.invokeMethod(Configuration.class, "load");
		Whitebox.setInternalState(Configuration.class, Properties.class, properties);
	}

}
