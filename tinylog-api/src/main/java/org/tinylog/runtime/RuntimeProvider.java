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

package org.tinylog.runtime;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Provider for getting runtime specific data from Virtual Machine.
 */
public final class RuntimeProvider {

	private static final int MINIMUM_VERSION_MODERN_JAVA = 9;

	private static final RuntimeDialect dialect = resolveDialect();

	/** */
	private RuntimeProvider() {
	}

	/**
	 * Gets the name of the default writer.
	 *
	 * @return Name of default writer
	 */
	public static String getDefaultWriter() {
		return dialect.getDefaultWriter();
	}

	/**
	 * Gets the ID of the current process (pid).
	 *
	 * @return ID of the current process
	 */
	public static long getProcessId() {
		return dialect.getProcessId();
	}

	/**
	 * Gets the class name of a caller from stack trace. Any anonymous part will be stripped from class name.
	 *
	 * @param depth
	 *            Position of caller in stack trace
	 * @return Fully-qualified class name of caller
	 */
	public static String getCallerClassName(final int depth) {
		String caller = dialect.getCallerClassName(depth + 1);
		return stripAnonymousPart(caller);
	}

	/**
	 * Gets the complete stack trace element of a caller from stack trace. Any anonymous part will be stripped from
	 * class name.
	 *
	 * @param depth
	 *            Position of caller in stack trace
	 * @return Stack trace element of a caller
	 */
	public static StackTraceElement getCallerStackTraceElement(final int depth) {
		StackTraceElement element = dialect.getCallerStackTraceElement(depth + 1);
		String className = element.getClassName();
		int dollarIndex = className.indexOf("$");
		if (dollarIndex == -1) {
			return element;
		} else {
			className = stripAnonymousPart(className);
			return new StackTraceElement(className, element.getMethodName(), element.getFileName(), element.getLineNumber());
		}
	}

	/**
	 * Creates a timestamp with the current date and time. Only millisecond precision can be guaranteed even if the flag
	 * {@code millisecondPrecision} is set to {@code false}.
	 *
	 * @param millisecondPrecision
	 *            {@code true} if millisecond precision is sufficient, {@code false} if nanosecond precision is
	 *            preferred
	 * @return Timestamp with current date and time
	 */
	public static Timestamp createTimestamp(final boolean millisecondPrecision) {
		return dialect.createTimestamp(millisecondPrecision);
	}

	/**
	 * Creates a formatter for {@link Timestamp Timestamps}.
	 *
	 * @param pattern
	 *            Format pattern that is compatible with {@link DateTimeFormatter}
	 * @param locale
	 *            Locale for formatting
	 * @return Formatter for formatting timestamps
	 */
	public static TimestampFormatter createTimestampFormatter(final String pattern, final Locale locale) {
		return dialect.createTimestampFormatter(pattern, locale);
	}

	/**
	 * Resolves the runtime dialect for the current VM.
	 *
	 * @return Resolved runtime dialect
	 */
	private static RuntimeDialect resolveDialect() {
		if (getJavaVersion() >= MINIMUM_VERSION_MODERN_JAVA) {
			return new ModernJavaRuntime();
		} else if ("Android Runtime".equalsIgnoreCase(System.getProperty("java.runtime.name"))) {
			return new AndroidRuntime();
		} else {
			return new LegacyJavaRuntime();
		}
	}

	/**
	 * Gets the major version number of Java.
	 *
	 * @return Major version number of Java or -1 if unknown
	 */
	private static int getJavaVersion() {
		String version = System.getProperty("java.version");
		if (version == null) {
			return -1;
		} else {
			int index = version.indexOf('.');
			if (index > 0) {
				version = version.substring(0, index);
			}

			try {
				return Integer.parseInt(version);
			} catch (NumberFormatException ex) {
				return -1;
			}
		}
	}

	/**
	 * Strips the the anonymous part from a class name.
	 *
	 * @param className
	 *            Fully-qualified class name
	 * @return Human-readable class name without any anonymous part
	 */
	private static String stripAnonymousPart(final String className) {
		for (int index = className.indexOf("$", 0); index != -1; index = className.indexOf('$', index + 2)) {
			/* Trailing dollar sign */
			if (index >= className.length() - 1) {
				return className.substring(0, index);
			}

			char firstLetter = className.charAt(index + 1);
			/* First letter after dollar sign is not a capital letter of a named inner class */
			if (firstLetter < 'A' || firstLetter > 'Z') {
				return className.substring(0, index);
			}
		}

		return className;
	}

}
