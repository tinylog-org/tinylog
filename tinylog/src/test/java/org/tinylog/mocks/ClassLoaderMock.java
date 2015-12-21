/*
 * Copyright 2013 Martin Winandy
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

package org.tinylog.mocks;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.util.FileHelper;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;

/**
 * Mock for class loader that allows to define removable additional resources.
 * 
 * @see URLClassLoader
 */
public final class ClassLoaderMock extends MockUp<URLClassLoader> implements Closeable {

	private final URLClassLoader classLoader;
	private final Map<String, File> resources;

	/**
	 * @param classLoader
	 *            Class loader instance to mock
	 */
	public ClassLoaderMock(final URLClassLoader classLoader) {
		this.classLoader = classLoader;
		this.resources = new HashMap<>();
	}

	/**
	 * Get the real file of a defined resource.
	 * 
	 * @param resource
	 *            Name of resource
	 * @return Real file behind the resource
	 */
	public File get(final String resource) {
		return resources.get(resource);
	}

	/**
	 * Create or override a resource.
	 * 
	 * @param resource
	 *            Name of resource
	 * @param lines
	 *            Text lines of resource
	 * @return Real file behind the resource
	 * @throws IOException
	 *             Failed to create a temporary file for resource
	 */
	public File set(final String resource, final String... lines) throws IOException {
		File file = getOrCreateFile(resource);
		FileHelper.write(file, lines);
		resources.put(resource, file);
		return file;
	}

	/**
	 * Create or override a resource.
	 * 
	 * @param resource
	 *            Name of resource
	 * @param content
	 *            Text of resource
	 * @return Real file behind the resource
	 * @throws IOException
	 *             Failed to create a temporary file for resource
	 */
	public File set(final String resource, final String content) throws IOException {
		if (content == null) {
			File file = resources.get(resource);
			if (file != null) {
				file.delete();
			}
			resources.put(resource, null);
			return null;
		} else {
			File file = getOrCreateFile(resource);
			FileHelper.write(file, content);
			resources.put(resource, file);
			return file;
		}
	}

	/**
	 * Remove a defined resource.
	 * 
	 * @param resource
	 *            Name of resource
	 */
	public void remove(final String resource) {
		File file = resources.remove(resource);
		if (file != null) {
			file.delete();
		}
	}

	@Override
	public void close() {
		for (File file : resources.values()) {
			if (file != null) {
				file.delete();
			}
		}
	}

	/**
	 * Mocked method {@link URLClassLoader#findResource(String)}.
	 * 
	 * @param invocation
	 *            Context of the current invocation
	 * @param name
	 *            Path to resource
	 * @return URL to resource or <code>null</code> if requested resource doesn't exist
	 */
	@Mock
	protected URL findResource(final Invocation invocation, final String name) {
		URL url = invocation.proceed(name);
		if (url == null && classLoader == invocation.getInvokedInstance()) {
			if (resources.containsKey(name)) {
				File file = resources.get(name);
				if (file == null) {
					url = null;
				} else {
					try {
						url = file.toURI().toURL();
					} catch (MalformedURLException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return url;
	}

	/**
	 * Mocked method {@link URLClassLoader#findResources(String)}.
	 * 
	 * @param invocation
	 *            Context of the current invocation
	 * @param name
	 *            Path to resource
	 * @return Found URLs to requested resource
	 * @throws IOException
	 *             Failed to get resources
	 */
	@Mock
	protected Enumeration<URL> findResources(final Invocation invocation, final String name) throws IOException {
		if (classLoader == invocation.getInvokedInstance()) {
			Enumeration<URL> enumeration = invocation.proceed(name);
			List<URL> urls = new ArrayList<>(Collections.list(enumeration));
			if (resources.containsKey(name)) {
				File file = resources.get(name);
				if (file == null) {
					urls.clear();
				} else {
					try {
						urls.add(file.toURI().toURL());
					} catch (MalformedURLException ex) {
						ex.printStackTrace();
					}
				}
			}
			return Collections.enumeration(urls);
		} else {
			return invocation.proceed(name);
		}
	}

	private static String getExtension(final String resource) {
		int index = resource.lastIndexOf('.');
		if (resource.lastIndexOf('/') < index) {
			return resource.substring(index);
		} else {
			return null;
		}
	}

	private File getOrCreateFile(final String resource) throws IOException {
		File file = resources.get(resource);
		if (file == null) {
			return FileHelper.createTemporaryFile(getExtension(resource));
		} else {
			return file;
		}
	}

}
