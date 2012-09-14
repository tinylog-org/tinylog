/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import mockit.Mock;
import mockit.MockClass;

/**
 * Mock for {@link URLClassLoader}.
 */
@MockClass(realClass = URLClassLoader.class)
public final class MockClassLoader {

	private final Map<String, byte[]> content;

	/** */
	public MockClassLoader() {
		this.content = new HashMap<String, byte[]>();
	}

	/**
	 * Set an additional resource.
	 * 
	 * @param path
	 *            Path to resource
	 * @param content
	 *            Content of resource
	 */
	public void setContent(final String path, final String content) {
		if (content == null) {
			this.content.remove(path);
		} else {
			this.content.put(path, content.getBytes());
		}
	}

	/**
	 * Load a resource from classpath using the current class loader.
	 * 
	 * @param name
	 *            Name of the resource
	 * @return Loaded resource
	 */
	@Mock
	public InputStream getResourceAsStream(final String name) {
		if (name != null && name.endsWith(".properties")) {
			return getMockedResult(name);
		} else {
			URL url = getClass().getClassLoader().getResource(name);
			return getRealResult(url);
		}
	}

	/**
	 * Load a resource from classpath using the system class loader.
	 * 
	 * @param name
	 *            Name of the resource
	 * @return Loaded resource
	 */
	@Mock
	public InputStream getSystemResourceAsStream(final String name) {
		if (name != null && name.endsWith(".properties")) {
			return getMockedResult(name);
		} else {
			URL url = ClassLoader.getSystemResource(name);
			return getRealResult(url);
		}
	}

	private InputStream getMockedResult(final String name) {
		if (content.containsKey(name)) {
			return new ByteArrayInputStream(content.get(name));
		} else {
			return null;
		}
	}

	private InputStream getRealResult(final URL url) {
		try {
			return url == null ? null : url.openStream();
		} catch (IOException ex) {
			return null;
		}
	}

}
