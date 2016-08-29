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

package org.pmw.tinylog.writers;

import org.pmw.tinylog.InternalLogger;

/**
 * Resolves path names with placeholders for system properties and environment variables.
 */
final class PathResolver {

	private PathResolver() {
	}

	/**
	 * Replaces <code>${*}</code> placeholders with corresponding system property or environment variable. If both are
	 * existing, the system property will win.
	 * 
	 * @param path
	 *            Path with or without placeholders
	 * @return Path with resolved placeholders
	 */
	public static String resolve(final String path) {
		if (!path.contains("${")) {
			return path;
		}

		StringBuilder builder = new StringBuilder();
		int position = 0;

		for (int index = path.indexOf("${"); index != -1; index = path.indexOf("${", position)) {
			builder.append(path, position, index);

			int start = index + 2;
			int end = path.indexOf("}", start);

			if (end == -1) {
				InternalLogger.warn("Closing curly brace is missing for: {}", path);
				return path;
			}

			String name = path.substring(start, end);
			if (name.length() == 0) {
				InternalLogger.warn("Empty variable names cannot be resolved: {}", path);
				return path;
			}

			String variable = getVariable(name);
			if (variable == null) {
				InternalLogger.warn("\"{}\" could not be found in system properties nor in environment variables", name);
				return path;
			} else {
				builder.append(variable);
			}

			position = end + 1;
		}

		builder.append(path, position, path.length());
		return builder.toString();
	}

	private static String getVariable(final String name) {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(name);
		}
		return value;
	}

}
