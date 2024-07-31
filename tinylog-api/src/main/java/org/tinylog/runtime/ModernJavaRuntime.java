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
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Runtime dialect implementation for Java 9+.
 */
@IgnoreJRERequirement
final class ModernJavaRuntime extends AbstractJavaRuntime {

	private static final Timestamp startTime = new PreciseTimestamp(
			System.currentTimeMillis(),
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
		StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		return walker.walk(new ClassNameExtractorByDepth(depth));
	}

	@Override
	public String getCallerClassName(final String loggerClassName) {
		StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		return walker.walk(new ClassNameExtractorByLoggerClassName(loggerClassName));
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
	@IgnoreJRERequirement
	private static ProcessHandle getCurrentProcess() {
		try {
			return ProcessHandle.current();
		} catch (SecurityException ex) {
			InternalLogger.log(Level.ERROR, ex, "Failed to receive the handle of the current process");
			return null;
		}
	}

	@IgnoreJRERequirement
	private static final class ClassNameExtractorByDepth implements Function<Stream<StackFrame>, String> {
		private final int depth;

		private ClassNameExtractorByDepth(final int depth) {
			this.depth = depth;
		}

		@Override
		public String apply(final Stream<StackFrame> frames) {
			return frames.skip(depth)
					.findFirst()
					.map(new ClassNameMapper())
					.orElse(null);
		}
	}

	@IgnoreJRERequirement
	private static final class ClassNameExtractorByLoggerClassName implements Function<Stream<StackFrame>, String> {
		private final String loggerClassName;

		private ClassNameExtractorByLoggerClassName(final String loggerClassName) {
			this.loggerClassName = loggerClassName;
		}

		@Override
		public String apply(final Stream<StackFrame> stream) {
			return stream.map(new ClassNameMapper()).dropWhile(new Predicate<String>() {
				@Override
				public boolean test(final String name) {
					return !name.equals(loggerClassName);
				}
			}).skip(1).findFirst().orElse(null);
		}
	}

	/**
	 * Mapper for getting the class name from a {@link StackFrame}.
	 */
	@IgnoreJRERequirement
	private static final class ClassNameMapper implements Function<StackFrame, String> {

		/** */
		private ClassNameMapper() {
		}

		@Override
		public String apply(final StackFrame stackFrame) {
			return stackFrame.getClassName();
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
