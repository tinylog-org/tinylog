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
 * Stack trace location implementation for Android that stores the fully qualified class name of a callee.
 */
public class AndroidClassNameBasedStackTraceLocation implements StackTraceLocation {

	private final String className;

	/**
	 * @param className The fully qualified class name of the callee
	 */
	public AndroidClassNameBasedStackTraceLocation(final String className) {
		this.className = className;
	}

	@Override
	public AndroidClassNameBasedStackTraceLocation push() {
		return this;
	}

	@Override
	public String getCallerClassName() {
		StackTraceElement element = push().getCallerStackTraceElement();
		return element == null ? null : element.getClassName();
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		StackTraceElement[] trace = new Throwable().getStackTrace();
		boolean foundClassName = false;

		for (StackTraceElement element : trace) {
			if (foundClassName && !className.equals(element.getClassName())) {
				return element;
			} else if (!foundClassName && className.equals(element.getClassName())) {
				foundClassName = true;
			}
		}

		return null;
	}

}
