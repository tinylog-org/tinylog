/*
 * Copyright 2017 Martin Winandy
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

import java.lang.StackWalker.StackFrame;
import java.util.function.Function;
import java.util.stream.Stream;

import org.pmw.tinylog.InternalLogger;

/**
 * Runtime dialect for modern Oracle's Java Virtual Machines for Java 9 and later.
 */
public final class ModernJavaRuntime implements RuntimeDialect {
	
	private static final ProcessHandle currentProcess = getCurrentProcess();

	/** */
	public ModernJavaRuntime() {
	}

	@Override
	public String getProcessId() {
		return Long.toString(currentProcess.pid());
	}

	@Override
	public String getClassName(final int depth) {
		return StackWalker.getInstance().walk(new StackTraceElementExtractor(depth)).getClassName();
	}

	@Override
	public StackTraceElement getStackTraceElement(final int depth) {
		return StackWalker.getInstance().walk(new StackTraceElementExtractor(depth)).toStackTraceElement();
	}

	private static ProcessHandle getCurrentProcess() {
		try {
			return (ProcessHandle) ProcessHandle.class.getDeclaredMethod("current").invoke(null);
		} catch (ReflectiveOperationException ex) {
			InternalLogger.error("Failed to receive the handle of the current process", ex);
			return null;
		}
	}

	private static final class StackTraceElementExtractor implements Function<Stream<StackFrame>, StackFrame> {

		private final int depth;

		private StackTraceElementExtractor(int depth) {
			this.depth = depth;
		}

		@Override
		public StackFrame apply(Stream<StackFrame> stream) {
			return stream.skip(depth).findFirst().get();
		}

	}

}
