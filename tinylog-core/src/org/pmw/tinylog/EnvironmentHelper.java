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

import org.pmw.tinylog.runtime.AndroidRuntime;
import org.pmw.tinylog.runtime.LegacyJavaRuntime;
import org.pmw.tinylog.runtime.ModernJavaRuntime;
import org.pmw.tinylog.runtime.RuntimeDialect;

/**
 * Encapsulate functionality that depends on the environment.
 */
public final class EnvironmentHelper {

	private static final RuntimeDialect DIALECT = resolveDialect();
	private static final String NEW_LINE = System.getProperty("line.separator");

	private EnvironmentHelper() {
	}

	/**
	 * Determine whether running on Java 9 or newer.
	 *
	 * @return <code>true</code> if Java version is 9 or higher, <code>false</code> if not
	 */
	public static boolean isAtLeastJava9() {
		String version = System.getProperty("java.version");
		if (version == null) {
			return false;
		} else {
			int index = version.indexOf('.');
			if (index > 0) {
				version = version.substring(0, index);
			}
			return version.matches("[0-9]{1,8}") && Integer.parseInt(version) >= 9;
		}
	}

	/**
	 * Determine whether running on Android.
	 *
	 * @return <code>true</code> if operating system is Android, <code>false</code> if not
	 */
	public static boolean isAndroid() {
		return "Android Runtime".equalsIgnoreCase(System.getProperty("java.runtime.name"));
	}

	/**
	 * Determine whether running on Windows.
	 *
	 * @return <code>true</code> if operating system is Windows, <code>false</code> if not
	 */
	public static boolean isWindows() {
		String operatingSystem = System.getProperty("os.name");
		return operatingSystem != null && operatingSystem.startsWith("Windows");
	}

	/**
	 * Get VM runtime depending functionality.
	 *
	 * @return Runtime dialect
	 */
	public static RuntimeDialect getRuntimeDialect() {
		return DIALECT;
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

	private static RuntimeDialect resolveDialect() {
		if (isAtLeastJava9()) {
			return new ModernJavaRuntime();
		} else if (isAndroid()) {
			return new AndroidRuntime();
		} else {
			return new LegacyJavaRuntime();
		}
	}

}
