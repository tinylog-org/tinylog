/*
 * Copyright 2018 Martin Winandy
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

import java.lang.StackWalker.StackFrame;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Runtime dialect implementation for Java 9+.
 */
@IgnoreJRERequirement
final class ModernJavaRuntime extends AbstractJavaRuntime {

	private final ProcessHandle currentProcess = getCurrentProcess();

	/** */
	ModernJavaRuntime() {
	}

	@Override
	public long getProcessId() {
		return currentProcess.pid();
	}

	@Override
	public String getCallerClassName(final int depth) {
		return StackWalker.getInstance().walk(new StackFrameExtractor(depth)).getClassName();
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final int depth) {
		return StackWalker.getInstance().walk(new StackFrameExtractor(depth)).toStackTraceElement();
	}

	@Override
	public Timestamp createTimestamp() {
		return new PreciseTimestamp();
	}

	@Override
	public TimestampFormatter createTimestampFormatter(final String pattern, final Locale locale) {
		return new PreciseTimestampFormatter(pattern, locale);
	}

	/**
	 * Gets the process handle of the current process.
	 *
	 * @return Process handle of current process
	 */
	private static ProcessHandle getCurrentProcess() {
		try {
			return (ProcessHandle) ProcessHandle.class.getDeclaredMethod("current").invoke(null);
		} catch (ReflectiveOperationException ex) {
			InternalLogger.log(Level.ERROR, ex, "Failed to receive the handle of the current process");
			return null;
		}
	}

	/**
	 * Extractor for extracting a defined stack frame from stack trace.
	 */
	@IgnoreJRERequirement
	private static final class StackFrameExtractor implements Function<Stream<StackFrame>, StackFrame> {

		private final int index;

		/**
		 * @param index
		 *            Index of stack frame in stack trace
		 */
		private StackFrameExtractor(final int index) {
			this.index = index;
		}

		@Override
		public StackFrame apply(final Stream<StackFrame> stream) {
			return stream.skip(index).findFirst().get();
		}

	}

}
