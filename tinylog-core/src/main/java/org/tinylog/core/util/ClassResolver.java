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

/**
 * Utility class for resolving classes.
 */
public final class ClassResolver {

	/** */
	private ClassResolver() {
	}

	/**
	 * Checks if a class is available in the classpath.
	 *
	 * @param className Fully-qualified class name
	 * @return {@code true} if the class is available in the classpath, {@code false} if unavailable
	 */
	public static boolean isAvailable(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

}
