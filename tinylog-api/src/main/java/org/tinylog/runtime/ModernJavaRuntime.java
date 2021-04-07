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
import java.lang.management.ManagementFactory;
import java.util.Iterator;
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

	private static final ClassContextSecurityManager securityManager = new ClassContextSecurityManager();

	private static final Timestamp startTime = new PreciseTimestamp(
		ManagementFactory.getRuntimeMXBean().getStartTime(),
		0
	);

	private final ProcessHandle currentProcess = getCurrentProcess();

	/** */
	ModernJavaRuntime() {
	}

	@Override
	public boolean isAndroid() {
		return false;
	}

	@Override
	public long getProcessId() {
		return currentProcess.pid();
	}

	@Override
	public Timestamp getStartTime() {
		return startTime;
	}

	@Override
	public String getCallerClassName(final int depth) {
		Class<?>[] classes = securityManager.getClassContext();
		return classes.length > depth + 1 ? classes[depth + 1].getName() : null;
	}

	@Override
	public String getCallerClassName(final String loggerClassName) {
		Class<?>[] classes = securityManager.getClassContext();
		int index = 0;

		while (index < classes.length) {
			if (loggerClassName.equals(classes[index++].getName())) {
				break;
			}
		}

		while (index < classes.length) {
			String className = classes[index++].getName();
			if (!loggerClassName.equals(className)) {
				return className;
			}
		}

		return null;
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final int depth) {
		StackFrame frame = StackWalker.getInstance().walk(new FixedStackFrameExtractor(depth));
		return frame == null ? null : frame.toStackTraceElement();
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final String loggerClassName) {
		StackFrame frame = StackWalker.getInstance().walk(new DynamicStackFrameExtractor(loggerClassName));
		return frame == null ? null : frame.toStackTraceElement();
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
	 * Security manager with accessible {@link SecurityManager#getClassContext()}.
	 */
	private static final class ClassContextSecurityManager extends SecurityManager {

		/** */
		private ClassContextSecurityManager() {
		}

		@Override
		protected Class<?>[] getClassContext() {
			return super.getClassContext();
		}

	}

	/**
	 * Extractor for extracting a stack frame from stack trace at a defined index.
	 */
	@IgnoreJRERequirement
	private static final class FixedStackFrameExtractor implements Function<Stream<StackFrame>, StackFrame> {

		private final int index;

		/**
		 * @param index
		 *            Index of stack frame in stack trace
		 */
		private FixedStackFrameExtractor(final int index) {
			this.index = index;
		}

		@Override
		public StackFrame apply(final Stream<StackFrame> stream) {
			return stream.skip(index).findFirst().orElse(null);
		}

	}

	/**
	 * Extractor for extracting a stack frame from stack trace that appears before an expected class name.
	 */
	@IgnoreJRERequirement
	private static final class DynamicStackFrameExtractor implements Function<Stream<StackFrame>, StackFrame> {

		private final String loggerClassName;

		/**
		 * @param loggerClassName
		 *            Name of expected logger class name
		 */
		private DynamicStackFrameExtractor(final String loggerClassName) {
			this.loggerClassName = loggerClassName;
		}

		@Override
		public StackFrame apply(final Stream<StackFrame> stream) {
			Iterator<StackFrame> iterator = stream.iterator();

			while (iterator.hasNext()) {
				if (loggerClassName.equals(iterator.next().getClassName())) {
					break;
				}
			}

			while (iterator.hasNext()) {
				StackFrame frame = iterator.next();
				if (!loggerClassName.equals(frame.getClassName())) {
					return frame;
				}
			}

			return null;
		}
	}
	
}
