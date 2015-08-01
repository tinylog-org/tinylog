/*
 * Copyright 2014 Martin Winandy
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

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * Encapsulate functionality that depends on the environment.
 */
public final class EnvironmentHelper {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private EnvironmentHelper() {
	}

	/**
	 * Get the line separator.
	 *
	 * @return Line separator
	 */
	public static String getNewLine() {
		return NEW_LINE;
	}

	/**
	 * Get the ID of the current process (pid).
	 *
	 * @return ID of the current process
	 */
	public static Object getProcessId() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		int index = name.indexOf('@');
		if (index > 0) {
			return name.substring(0, index);
		} else {
			return name;
		}
	}

	/**
	 * Make all nonexistent directories.
	 *
	 * @param file
	 *            Path to a file
	 */
	public static void makeDirectories(final File file) {
		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}
	}

}
