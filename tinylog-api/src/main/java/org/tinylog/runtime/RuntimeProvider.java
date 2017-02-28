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

/**
 * Provider for getting runtime specific data from Virtual Machine.
 */
public final class RuntimeProvider {

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
	public static int getProcessId() {
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
	 * Resolves the runtime dialect for the current VM.
	 *
	 * @return Resolved runtime dialect
	 */
	private static RuntimeDialect resolveDialect() {
		if ("Android Runtime".equalsIgnoreCase(System.getProperty("java.runtime.name"))) {
			return new AndroidRuntime();
		} else {
			return new JavaRuntime();
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
