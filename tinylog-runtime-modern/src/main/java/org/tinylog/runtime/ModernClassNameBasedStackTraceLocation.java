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

package org.tinylog.runtime;

/**
 * Stack trace location implementation for modern Java 9 and later that stores the fully qualified class name of a
 * callee.
 */
public final class ModernClassNameBasedStackTraceLocation implements StackTraceLocation {

	private final String className;

	/**
	 * @param className The fully qualified class name of the callee
	 */
	ModernClassNameBasedStackTraceLocation(String className) {
		this.className = className;
	}

	@Override
	public ModernClassNameBasedStackTraceLocation push() {
		return this;
	}

	@Override
	public String getCallerClassName() {
		return StackWalker.getInstance().walk(stream ->
			stream
				.dropWhile(frame -> !className.equals(frame.getClassName()))
				.dropWhile(frame -> className.equals(frame.getClassName()))
				.findFirst()
		).get().getClassName();
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		return StackWalker.getInstance().walk(stream ->
			stream
				.dropWhile(frame -> !className.equals(frame.getClassName()))
				.dropWhile(frame -> className.equals(frame.getClassName()))
				.findFirst()
		).get().toStackTraceElement();
	}

}
