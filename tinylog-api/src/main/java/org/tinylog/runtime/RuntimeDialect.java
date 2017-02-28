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
 * VM runtime specific functionality.
 */
interface RuntimeDialect {

	/**
	 * Gets the name of the default writer.
	 *
	 * @return Name of default writer
	 */
	String getDefaultWriter();

	/**
	 * Gets the ID of the current process (pid).
	 *
	 * @return ID of the current process
	 */
	int getProcessId();

	/**
	 * Gets the class name of a caller from stack trace.
	 *
	 * @param depth
	 *            Position of caller in stack trace
	 * @return Fully-qualified class name of caller
	 */
	String getCallerClassName(int depth);

	/**
	 * Gets the complete stack trace element of a caller from stack trace.
	 *
	 * @param depth
	 *            Position of caller in stack trace
	 * @return Stack trace element of a caller
	 */
	StackTraceElement getCallerStackTraceElement(int depth);

}
