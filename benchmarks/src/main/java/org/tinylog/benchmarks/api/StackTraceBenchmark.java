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

package org.tinylog.benchmarks.api;

import java.lang.StackWalker.StackFrame;
import java.util.function.Function;
import java.util.stream.Stream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * Benchmark for comparing methods to extract a defined element from stack trace.
 *
 * @see StackWalker
 * @see sun.reflect.Reflection
 * @see Throwable
 */
public class StackTraceBenchmark {

	/** */
	public StackTraceBenchmark() {
	}

	/**
	 * Benchmarks extracting a stack frame from stack walker by using a lambda expression.
	 * 
	 * @return Found stack frame
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public StackFrame stackWalkerWithLambda() {
		return StackWalker.getInstance().walk(stream -> stream.skip(1).findFirst().get());
	}

	/**
	 * Benchmarks extracting a stack frame from stack walker by using an anonymous inner class.
	 * 
	 * @return Found stack frame
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public StackFrame stackWalkerWithAnonymousClass() {
		return StackWalker.getInstance().walk(new Function<Stream<StackFrame>, StackFrame>() {
			@Override
			public StackFrame apply(final Stream<StackFrame> stream) {
				return stream.skip(1).findFirst().get();
			}
		});
	}

	/**
	 * Benchmarks extracting a stack frame from stack walker by using a static inner class.
	 * 
	 * @return Found stack frame
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public StackFrame stackWalkerWithInnerClass() {
		return StackWalker.getInstance().walk(new StackFrameExtractor(1));
	}

	/**
	 * Benchmarks extracting a class via Sun reflection.
	 * 
	 * @return Found class
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@SuppressWarnings("deprecation")
	public Class<?> sunReflection() {
		return sun.reflect.Reflection.getCallerClass(1);
	}

	/**
	 * Benchmarks extracting a stack trace element from stack trace of a throwable.
	 * 
	 * @return Found stack trace element
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public StackTraceElement throwable() {
		return new Throwable().getStackTrace()[1];
	}

	/* Throwable.getStackTraceElement() is only available on Java 8 and prior */
	
	//	private static final Method stackTraceElementGetter = getStackTraceElementGetter();
	//
	//	/**
	//	 * Benchmarks extracting a stack trace element from of a throwable via {@link Throwable#getStackTraceElement(int)}.
	//	 * 
	//	 * @return Found stack trace element
	//	 * @throws ReflectiveOperationException
	//	 *             Failed to invoke {@link Throwable#getStackTraceElement(int)}.
	//	 */
	//	@Benchmark
	//	@BenchmarkMode(Mode.Throughput)
	//	public StackTraceElement stackTraceElement() throws ReflectiveOperationException {
	//		return (StackTraceElement) stackTraceElementGetter.invoke(new Throwable(), 1);
	//	}
	//
	//	/**
	//	 * Gets the package private method {@link Throwable#getStackTraceElement(int)}.
	//	 * 
	//	 * @return Method {@link Throwable#getStackTraceElement(int)}
	//	 */
	//	private static Method getStackTraceElementGetter() {
	//		try {
	//			Method method = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
	//			method.setAccessible(true);
	//			return method;
	//		} catch (NoSuchMethodException ex) {
	//			throw new RuntimeException(ex);
	//		}
	//	}

	/**
	 * Extractor for extracting a defined stack frame from stack trace.
	 */
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
