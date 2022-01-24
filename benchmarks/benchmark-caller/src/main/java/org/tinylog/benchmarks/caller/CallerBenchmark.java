package org.tinylog.benchmarks.caller;

import java.lang.StackWalker.Option;
import java.util.function.Supplier;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Benchmark for getting the caller class name.
 */
@State(Scope.Thread)
public class CallerBenchmark {

	private Thread thread;
	private StackWalker defaultInstance;
	private StackWalker retainClassInstance;
	private Supplier<Class<?>> supplier;

	/** */
	public CallerBenchmark() {
	}

	/**
	 * Initializes all member fields.
	 */
	@Setup
	public void init() {
		thread = Thread.currentThread();
		defaultInstance = StackWalker.getInstance();
		retainClassInstance = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
		supplier = retainClassInstance::getCallerClass;
	}

	/**
	 * Gets the caller from the stack trace of a new {@link Throwable}.
	 *
	 * @return The fully-qualified caller class name
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public String throwable() {
		return new Throwable().getStackTrace()[1].getClassName();
	}

	/**
	 * Gets the caller from the stack trace of the current {@link Thread}.
	 *
	 * @return The fully-qualified caller class name
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public String thread() {
		return thread.getStackTrace()[2].getClassName();
	}

	/**
	 * Gets the caller from the stack frame stream of a {@link StackWalker}.
	 *
	 * @return The fully-qualified caller class name
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public String stackFrameStream() {
		return defaultInstance.walk(stream -> stream.skip(1).findFirst()).get().getClassName();
	}

	/**
	 * Gets the caller from {@link StackWalker#getCallerClass()}.
	 *
	 * @return The fully-qualified caller class name
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public String callerClass() {
		return retainClassInstance.getCallerClass().getName();
	}

	/**
	 * Gets the caller from {@link StackWalker#getCallerClass()} wrapped as a {@link Supplier}.
	 *
	 * @return The fully-qualified caller class name
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public String callerSupplier() {
		return supplier.get().getName();
	}

	/**
	 * Gets the caller from the class context of a new {@link SecurityManager}.
	 *
	 * @return The fully-qualified caller class name
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@SuppressWarnings("removal")
	public String securityManager() {
		return new SecurityManager() {
			public Class<?> getCallerClass() {
				return super.getClassContext()[2];
			}
		}.getCallerClass().getName();
	}

}
