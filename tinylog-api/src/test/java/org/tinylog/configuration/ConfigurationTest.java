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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.rule.SystemStreamCollector;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

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
		loadProperies(null);
	}

	/**
	 * Clears all tinylog properties.
	 */
	@After
	public void clear() {
		for (Enumeration<Object> enumeration = System.getProperties().keys(); enumeration.hasMoreElements();) {
			String property = (String) enumeration.nextElement();
			if (property.startsWith(PROPERTIES_PREFIX)) {
				System.clearProperty(property);
			}
		}
	}

	/**
	 * Verifies that <tt>tinylog.properties</tt> will be loaded by default.
	 *
	 * @throws Exception
	 *             Failed creating <tt>tinylog.properties</tt> or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void defaultPropertiesFile() throws Exception {
		FileSystem.createResource("tinylog.properties", "level = off");
		try {
			loadProperies(null);
			assertThat(Configuration.get("level")).isEqualToIgnoringCase("off");
		} finally {
			FileSystem.deleteResource("tinylog.properties");
		}
	}

	/**
	 * Verifies that a custom defined resource will be loaded from classpath.
	 *
	 * @throws Exception
	 *             Failed creating temporary resource or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void propertiesFileFromClasspath() throws Exception {
		loadProperies(FileSystem.createTemporaryResource("level = trace"));
		assertThat(Configuration.get("level")).isEqualToIgnoringCase("trace");
	}

	/**
	 * Verifies that a custom defined file will be loaded from file system.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void propertiesFileFromFileSystem() throws Exception {
		loadProperies(FileSystem.createTemporaryFile("level = info"));
		assertThat(Configuration.get("level")).isEqualToIgnoringCase("info");
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
		loadProperies(path);

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
		loadProperies(FileSystem.createTemporaryFile("level = info"));

		assertThat(Configuration.get("level")).isEqualToIgnoringCase("debug");
	}

	/**
	 * Verifies that {@link Configuration#getSiblings(String)} finds the expected sibling properties.
	 *
	 * @throws Exception
	 *             Failed creating temporary file or invoking private method {@link Configuration#load()}
	 */
	@Test
	public void foundSiblings() throws Exception {
		loadProperies(FileSystem.createTemporaryFile("level = info", "writer = console", "writer2 = file", "writer2.file = log.txt"));
		assertThat(Configuration.getSiblings("writer")).containsOnly(entry("writer", "console"), entry("writer2", "file"));
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
		loadProperies(FileSystem.createTemporaryFile("level = info"));
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
		loadProperies(FileSystem.createTemporaryFile("level = info", "writer = file", "writer.file = log.txt", "writer.buffered = true"));
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
		loadProperies(FileSystem.createTemporaryFile("level = info"));
		assertThat(Configuration.getChildren("writer")).isEmpty();
	}

	/**
	 * Triggers (re-)loading properties.
	 *
	 * @param path
	 *            Path to properties configuration or {@code null} if undefined
	 * @throws Exception
	 *             Failed invoking private method {@link Configuration#load()}
	 */
	private static void loadProperies(final String path) throws Exception {
		if (path != null) {
			System.setProperty(CONFIGURATION_PROPERTY, path);
		}

		Properties properties = Whitebox.invokeMethod(Configuration.class, "load");
		Whitebox.setInternalState(Configuration.class, Properties.class, properties);
	}

}
