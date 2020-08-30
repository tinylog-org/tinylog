/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.runtime.RuntimeProvider;

/**
 * Framework that can extend the classpath by additional resource folders and service files.
 */
public class ExtendableFramework extends Framework {

	private final URL[] urls;

	/**
	 * @param urls Additional resource folders to add to the classpath
	 */
	protected ExtendableFramework(URL... urls) {
		this.urls = urls;
	}

	/**
	 * @param configuration Custom configuration
	 * @param urls Additional resource folders to add to the classpath
	 */
	protected ExtendableFramework(Configuration configuration, URL... urls) {
		super(new RuntimeProvider().getRuntime(), configuration, Collections.emptyList());
		this.urls = urls;
	}

	/**
	 * Creates a new {@link Framework} instance with a custom class loader that uses the passed folder as additional
	 * source for resources.
	 *
	 * @param folder Folder with resource files
	 * @return The created framework
	 * @throws MalformedURLException Failed to provide the passed folder as URL
	 */
	public static Framework create(Path folder) throws MalformedURLException {
		URL[] urls = new URL[] {folder.toUri().toURL()};
		ClassLoader defaultLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader wrappedLoader = new URLClassLoader(urls, defaultLoader);

		/* The provided URLs should be used already during the initialization for loading hooks and configuration. */

		Thread.currentThread().setContextClassLoader(wrappedLoader);
		try {
			return new ExtendableFramework(urls);
		} finally {
			Thread.currentThread().setContextClassLoader(defaultLoader);
		}
	}

	/**
	 * Creates a new {@link Framework} instance with a custom class loader that uses the passed folder as additional
	 * source for resources. All passed implementations will be registered as services.
	 *
	 * @param folder Folder for resource files
	 * @param service Service interface
	 * @param implementations Service implementations
	 * @param <T> Service interface
	 * @return The created framework
	 * @throws IOException Failed to create service file in the provided folder
	 */
	@SafeVarargs
	public static <T> Framework create(Path folder, Class<T> service, Class<? extends T>... implementations)
			throws IOException {
		createServiceFile(folder, service, implementations);
		return create(folder);
	}

	/**
	 * Creates a new {@link Framework} instance with a custom class loader that uses the passed folder as additional
	 * source for resources.
	 *
	 * @param configuration Custom configuration
	 * @param folder Folder with resource files
	 * @return The created framework
	 * @throws MalformedURLException Failed to provide the passed folder as URL
	 */
	public static Framework create(Configuration configuration, Path folder) throws MalformedURLException {
		return new ExtendableFramework(configuration, folder.toUri().toURL());
	}

	/**
	 * Creates a new {@link Framework} instance with a custom class loader that uses the passed folder as additional
	 * source for resources. All passed implementations will be registered as services.
	 *
	 * @param configuration Custom configuration
	 * @param folder Folder for resource files
	 * @param service Service interface
	 * @param implementations Service implementations
	 * @param <T> Service interface
	 * @return The created framework
	 * @throws IOException Failed to create service file in the provided folder
	 */
	@SafeVarargs
	public static <T> Framework create(Configuration configuration, Path folder, Class<T> service,
			Class<? extends T>... implementations) throws IOException {
		createServiceFile(folder, service, implementations);
		return create(configuration, folder);
	}

	@Override
	public ClassLoader getClassLoader() {
		return urls == null ? super.getClassLoader() : new URLClassLoader(urls, super.getClassLoader());
	}

	/**
	 * Creates a service file for the passed service with all passed implementations in the passed folder.
	 *
	 * @param folder Folder in which the service file should be created
	 * @param service Service interface
	 * @param implementations Service implementations
	 * @param <T> Service interface
	 * @throws IOException Failed to create the service file
	 */
	@SafeVarargs
	private static <T> void createServiceFile(Path folder, Class<T> service, Class<? extends T>... implementations)
			throws IOException {
		String services = Arrays.stream(implementations).map(Class::getName).collect(Collectors.joining("\n"));
		Path file = folder.resolve("META-INF").resolve("services").resolve(service.getName());
		Files.createDirectories(file.getParent());
		Files.write(file, services.getBytes(StandardCharsets.UTF_8));
	}

}
