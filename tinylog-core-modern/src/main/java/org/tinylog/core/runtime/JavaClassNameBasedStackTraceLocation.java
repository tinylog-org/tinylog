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
 * Stack trace location implementation for modern Java 9 and later that stores the fully qualified class name of a
 * callee.
 */
public class JavaClassNameBasedStackTraceLocation implements StackTraceLocation {

	private final String className;
	private final int offset;

	/**
	 * @param className The fully qualified class name of the callee
	 * @param offset The number of stack trace elements that can be skipped for sure
	 */
	JavaClassNameBasedStackTraceLocation(String className, int offset) {
		this.className = className;
		this.offset = offset;
	}

	@Override
	public JavaClassNameBasedStackTraceLocation push() {
		return new JavaClassNameBasedStackTraceLocation(className, offset + 1);
	}

	@Override
	public String getCallerClassName() {
		return StackWalker.getInstance().walk(stream ->
			stream.skip(offset)
				.dropWhile(frame -> !className.equals(frame.getClassName()))
				.dropWhile(frame -> className.equals(frame.getClassName()))
				.findFirst()
		).map(StackWalker.StackFrame::getClassName).orElse(null);
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		return StackWalker.getInstance().walk(stream ->
			stream.skip(offset)
				.dropWhile(frame -> !className.equals(frame.getClassName()))
				.dropWhile(frame -> className.equals(frame.getClassName()))
				.findFirst()
		).map(StackWalker.StackFrame::toStackTraceElement).orElse(null);
	}

}
