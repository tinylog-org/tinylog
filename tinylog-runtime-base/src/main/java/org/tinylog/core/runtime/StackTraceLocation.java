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

package org.tinylog.core.runtime;

/**
 * Representation for a defined stack trace element in the current stack trace.
 */
public interface StackTraceLocation {

	/**
	 * Adjusts the stack trace location for passing it to a method call.
	 *
	 * <p>
	 *     Whenever a stack trace location should be passed to another method, the result of this methods has to passed
	 *     instead of the original stack location object. The reason is that every method call increases the stack trace
	 *     by pushing an addition stack trace element.
	 * </p>
	 *
	 * @return Stack trace location that can be passed to another method
	 */
	StackTraceLocation push();

	/**
	 * Retrieved the fully-qualified class name of the caller.
	 *
	 * @return The fully-qualified class name of the caller
	 */
	String getCallerClassName();

	/**
	 * Retrieved the complete stack trace element of the caller.
	 *
	 * @return The complete stack trace element of the caller
	 */
	StackTraceElement getCallerStackTraceElement();

}
