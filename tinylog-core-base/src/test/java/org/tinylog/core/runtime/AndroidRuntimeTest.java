package org.tinylog.core.runtime;

import java.time.Duration;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
class AndroidRuntimeTest {

	/**
	 * Verifies that a valid process ID is provided.
	 */
	@Test
	void processId() {
		long pid = new AndroidRuntime().getProcessId();
		assertThat(pid).isGreaterThan(0);
	}

	/**
	 * Verifies that valid uptime values are provided.
	 */
	@Test
	void uptime() throws InterruptedException {
		AndroidRuntime runtime = new AndroidRuntime();

		Duration time1 = runtime.getUptime();
		assertThat(time1).isBetween(Duration.ZERO, Duration.ofHours(1));

		Thread.sleep(1);

		Duration time2 = runtime.getUptime();
		assertThat(time2).isGreaterThan(time1);
	}

	/**
	 * Verifies that a valid stack location can be extracted from a defined index.
	 */
	@Test
	void stackTraceLocationAtIndex() {
		StackTraceLocation location = new AndroidRuntime().getStackTraceLocationAtIndex(0);
		assertThat(location).isInstanceOf(AndroidIndexBasedStackTraceLocation.class);
		assertThat(location.getCallerClassName()).isEqualTo(AndroidRuntimeTest.class.getName());
	}

	/**
	 * Verifies that a valid stack location can be extracted via a passed callee class name.
	 */
	@Test
	void stackTraceLocationAfterClass() {
		StackTraceLocation location = new AndroidRuntime().getStackTraceLocationAfterClass(Callee.class.getName());
		assertThat(location).isInstanceOf(AndroidClassNameBasedStackTraceLocation.class);

		String className = Callee.execute(() -> getCallerClassName(location.push()));
		assertThat(className).isEqualTo(AndroidRuntimeTest.class.getName());
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
