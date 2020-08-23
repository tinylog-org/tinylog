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
 * Abstraction of API that depends on the Java version or flavor.
 */
public interface RuntimeFlavor {

	/**
	 * Gets the stack trace location at a defined index.
	 *
	 * @param index Depth in the stack trace
	 * @return The found stack trace location
	 */
	StackTraceLocation getStackTraceLocationAtIndex(int index);

	/**
	 * Gets the first stack trace location after a defined class.
	 *
	 * @param className Fully-qualified class name
	 * @return The found stack trace location
	 */
	StackTraceLocation getStackTraceLocationAfterClass(String className);

}
