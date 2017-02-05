/*
 * Copyright 2017 Martin Winandy
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

package org.pmw.tinylog;

/**
 * Resolver for getting a non-null instance of {@link ClassLoader}.
 */
final class ClassLoaderResolver {

	private ClassLoaderResolver() {
	}

	/**
	 * Return the class loader for a class. If there is no assigned class loader, the system class loader will be
	 * returned.
	 * 
	 * @param clazz
	 *            Main source for getting class loader
	 * @return An instance of {@link ClassLoader}
	 */
	static ClassLoader resolve(final Class<?> clazz) {
		return clazz.getClassLoader() == null ? ClassLoader.getSystemClassLoader() : clazz.getClassLoader();
	}

}
