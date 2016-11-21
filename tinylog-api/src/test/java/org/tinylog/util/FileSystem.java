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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Utility class for simplifying frequent file system operations to have readable JUnit tests.
 */
public final class FileSystem {

	private static final String SERVICE_PREFIX = "META-INF/services/";

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
	 * Creates or overrides a service file in class path.
	 *
	 * @param service
	 *            Service interface
	 * @param implementations
	 *            Service implementation classes
	 * @throws IOException
	 *             Failed creating resource
	 * @throws URISyntaxException
	 *             Invalid class path URL
	 */
	public static void createServiceFile(final Class<?> service, final String... implementations)
		throws IOException, URISyntaxException {
		createResource(SERVICE_PREFIX + service.getName(), implementations);
	}

	/**
	 * Creates or overrides a defined resource in the default class path. The created resource will be deleted
	 * automatically when the virtual machine terminates.
	 *
	 * @param name
	 *            File name of resource
	 * @param lines
	 *            Text content for created resource
	 * @throws IOException
	 *             Failed creating resource
	 * @throws URISyntaxException
	 *             Invalid class path URL
	 */
	public static void createResource(final String name, final String... lines) throws IOException, URISyntaxException {
		Path path = Paths.get(getClassPathUri()).resolve(name);
		Path parent = path.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		Files.write(path, Arrays.asList(lines));
		path.toFile().deleteOnExit();
	}

	/**
	 * Creates a new temporary resource in the default class path. The created resource will be deleted automatically
	 * when the virtual machine terminates.
	 *
	 * @param lines
	 *            Text content for created resource
	 * @return Path to created resource
	 * @throws IOException
	 *             Failed creating resource
	 * @throws URISyntaxException
	 *             Invalid class path URL
	 */
	public static String createTemporaryResource(final String... lines) throws IOException, URISyntaxException {
		Path path = Files.createTempFile(Paths.get(getClassPathUri()), null, null);
		Files.write(path, Arrays.asList(lines));
		path.toFile().deleteOnExit();
		return path.toFile().getName();
	}

	/**
	 * Reads the content of a text file.
	 *
	 * @param path
	 *            Path to text file
	 * @return Content
	 * @throws IOException
	 *             Failed reading file
	 */
	public static String readFile(final String path) throws IOException {
		return readFile(path, Charset.defaultCharset());
	}

	/**
	 * Reads the content of a text file.
	 *
	 * @param path
	 *            Path to text file
	 * @param charset
	 *            Charset for decoding text
	 * @return Content
	 * @throws IOException
	 *             Failed reading file
	 */
	public static String readFile(final String path, final Charset charset) throws IOException {
		byte[] data = Files.readAllBytes(Paths.get(path));
		return new String(data, charset);
	}

	/**
	 * Deletes a service file from default class path.
	 *
	 * @param service
	 *            Service interface
	 * @throws IOException
	 *             Failed deleting resource
	 * @throws URISyntaxException
	 *             Invalid class path URL
	 */

	public static void deleteServiceFile(final Class<?> service) throws IOException, URISyntaxException {
		deleteResource(SERVICE_PREFIX + service.getName());
	}

	/**
	 * Deletes a resource from default class path.
	 *
	 * @param name
	 *            File name of resource
	 * @throws IOException
	 *             Failed deleting resource
	 * @throws URISyntaxException
	 *             Invalid class path URL
	 */
	public static void deleteResource(final String name) throws IOException, URISyntaxException {
		Path path = Paths.get(getClassPathUri()).resolve(name);
		Files.deleteIfExists(path);
	}

	/**
	 * Returns the path to class path as URI.
	 *
	 * @return URI of class path
	 * @throws URISyntaxException
	 *             Failed converting class path URL to an URI
	 */
	private static URI getClassPathUri() throws URISyntaxException {
		return FileSystem.class.getProtectionDomain().getCodeSource().getLocation().toURI();
	}

}
