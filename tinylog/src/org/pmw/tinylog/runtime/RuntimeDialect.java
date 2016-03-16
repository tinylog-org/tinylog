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

package org.pmw.tinylog.runtime;

/**
 * Functionality that depends on the VM runtime.
 */
public interface RuntimeDialect {

	/**
	 * Get the ID of the current process (pid).
	 *
	 * @return ID of the current process
	 */
	String getProcessId();

	/**
	 * Get a specific fully-qualified class name from current stack trace.
	 *
	 * @param deep
	 *            Position of stack trace element
	 * @return Fully-qualified class name from defined position
	 */
	String getClassName(final int deep);

	/**
	 * Get a specific stack trace element from current stack trace.
	 *
	 * @param deep
	 *            Position of stack trace element
	 * @return Stack trace element from defined position
	 */
	StackTraceElement getStackTraceElement(final int deep);

}
