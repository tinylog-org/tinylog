package org.tinylog.core.runtime;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaRuntimeTest {

	/**
	 * Verifies that a valid process ID is provided.
	 */
	@Test
	void processId() {
		long pid = new JavaRuntime().getProcessId();
		assertThat(pid).isGreaterThan(0);
	}

	/**
	 * Verifies that a valid stack location can be extracted from a defined index.
	 */
	@Test
	void stackTraceLocationAtIndex() {
		StackTraceLocation location = new JavaRuntime().getStackTraceLocationAtIndex(0);
		assertThat(location).isInstanceOf(JavaIndexBasedStackTraceLocation.class);
		assertThat(location.getCallerClassName()).isEqualTo(JavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that a valid stack location can be extracted via a passed callee class name.
	 */
	@Test
	void stackTraceLocationAfterClass() {
		StackTraceLocation location = new JavaRuntime().getStackTraceLocationAfterClass(Callee.class.getName());
		assertThat(location).isInstanceOf(JavaClassNameBasedStackTraceLocation.class);

		String className = Callee.execute(() -> getCallerClassName(location.push()));
		assertThat(className).isEqualTo(JavaRuntimeTest.class.getName());
	}

	/**
	 * Retrieves the caller class name from the passed stack trace location.
	 *
	 * @param location The stack trace location from which the caller is to be received
	 * @return The received caller class name
	 */
	private String getCallerClassName(StackTraceLocation location) {
		return location.getCallerClassName();
	}

	/**
	 * Helper class to simulate a callee.
	 */
	private static final class Callee {

		/**
		 * Executes the passed {@link Supplier}.
		 *
		 * @param supplier The supplier to execute
		 * @param <T> Return type
		 * @return The produced value from the passed supplier
		 */
		static <T> T execute(Supplier<T> supplier) {
			return supplier.get();
		}

	}

}
