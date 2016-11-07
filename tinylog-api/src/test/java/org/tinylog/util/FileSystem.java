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

package org.tinylog.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Utility class for simplifying frequent file system operations to have readable JUnit tests.
 */
public final class FileSystem {

	/** */
	private FileSystem() {
	}

	/**
	 * Creates a new temporary file. The created file will be deleted automatically when the virtual machine terminates.
	 *
	 * @param lines
	 *            Text content for created file
	 * @return Path to created file
	 * @throws IOException
	 *             Failed creating file
	 */
	public static String createTemporaryFile(final String... lines) throws IOException {
		Path path = Files.createTempFile(null, null);
		Files.write(path, Arrays.asList(lines));
		path.toFile().deleteOnExit();
		return path.toString();
	}

	/**
	 * Creates or overrides a defined resource in the default classpath. The created resource will be deleted
	 * automatically when the virtual machine terminates.
	 *
	 * @param name
	 *            File name of resource
	 * @param lines
	 *            Text content for created resource
	 * @throws IOException
	 *             Failed creating resource
	 * @throws URISyntaxException
	 *             Invalid classpath URL
	 */
	public static void createResource(final String name, final String... lines) throws IOException, URISyntaxException {
		URL defaultClasspath = FileSystem.class.getProtectionDomain().getCodeSource().getLocation();
		Path path = Paths.get(defaultClasspath.toURI()).resolve(name);
		Files.write(path, Arrays.asList(lines));
		path.toFile().deleteOnExit();
	}

	/**
	 * Creates a new temporary resource in the default classpath. The created resource will be deleted automatically
	 * when the virtual machine terminates.
	 *
	 * @param lines
	 *            Text content for created resource
	 * @return Path to created resource
	 * @throws IOException
	 *             Failed creating resource
	 * @throws URISyntaxException
	 *             Invalid classpath URL
	 */
	public static String createTemporaryResource(final String... lines) throws IOException, URISyntaxException {
		URL defaultClasspath = FileSystem.class.getProtectionDomain().getCodeSource().getLocation();
		Path path = Files.createTempFile(Paths.get(defaultClasspath.toURI()), null, null);
		Files.write(path, Arrays.asList(lines));
		path.toFile().deleteOnExit();
		return path.toFile().getName();
	}

	/**
	 * Deletes a resource from default classpath.
	 *
	 * @param name
	 *            File name of resource
	 * @throws IOException
	 *             Failed deleting resource
	 * @throws URISyntaxException
	 *             Invalid classpath URL
	 */
	public static void deleteResource(final String name) throws IOException, URISyntaxException {
		URL defaultClasspath = FileSystem.class.getProtectionDomain().getCodeSource().getLocation();
		Path path = Paths.get(defaultClasspath.toURI()).resolve(name);
		Files.delete(path);
	}

}
