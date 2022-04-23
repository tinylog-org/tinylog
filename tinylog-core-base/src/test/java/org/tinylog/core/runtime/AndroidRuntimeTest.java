package org.tinylog.core.runtime;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.tinylog.core.Level;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

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
	 * Verifies that {@code logcat} the default writer.
	 */
	@Test
	void defaultWriter() {
		AndroidRuntime runtime = new AndroidRuntime();
		assertThat(runtime.getDefaultWriter()).isEqualTo("logcat");
	}

	/**
	 * Tests for {@link AndroidRuntime#getDirectCaller(OutputDetails)}.
	 */
	@Nested
	@CaptureLogEntries
	class DirectCaller {

		/**
		 * Verifies that the expected {@link StackTraceElement} is returned for
		 * {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION}.
		 */
		@Test
		void getFullLocationInformation() {
			AndroidRuntime runtime = new AndroidRuntime();

			Supplier<?> supplier = runtime.getDirectCaller(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION);
			Object result = Callee.execute(supplier);

			assertThat(result).isInstanceOfSatisfying(StackTraceElement.class, element -> {
				assertThat(element.getClassName()).isEqualTo(DirectCaller.class.getName());
				assertThat(element.getMethodName()).isEqualTo("getFullLocationInformation");
				assertThat(element.getFileName()).isEqualTo(AndroidRuntimeTest.class.getSimpleName() + ".java");
				assertThat(element.getLineNumber()).isEqualTo(74);
			});
		}

		/**
		 * Verifies that the expected caller class is returned for
		 * {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME}.
		 */
		@Test
		void getCallerClass() {
			AndroidRuntime runtime = new AndroidRuntime();

			Supplier<?> supplier = runtime.getDirectCaller(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
			Object result = Callee.execute(supplier);

			assertThat(result).isEqualTo(DirectCaller.class);
		}

		/**
		 * Verifies that {@code null} is returned for {@link OutputDetails#DISABLED} and
		 * {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}.
		 *
		 * @param outputDetails {@link OutputDetails#DISABLED} or
		 *                      {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}
		 */
		@ParameterizedTest
		@EnumSource(value = OutputDetails.class, names = {"DISABLED", "ENABLED_WITHOUT_LOCATION_INFORMATION"})
		void getDisabledOrWithoutLocationInformation(OutputDetails outputDetails) {
			AndroidRuntime runtime = new AndroidRuntime();

			Supplier<?> supplier = runtime.getDirectCaller(outputDetails);
			Object result = Callee.execute(supplier);

			assertThat(result).isNull();
		}

	}

	/**
	 * Tests for {@link AndroidRuntime#getRelativeCaller(OutputDetails)}.
	 */
	@Nested
	@CaptureLogEntries
	class RelativeCaller {

		@Inject
		private Log log;

		/**
		 * Verifies that the expected {@link StackTraceElement} is returned
		 * for {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} and {@link
		 * OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION} if a class
		 * name is passed that actually exists in the stack trace.
		 *
		 * @param outputDetails {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} or
		 *                      {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION}
		 */
		@ParameterizedTest
		@EnumSource(value = OutputDetails.class, names = {
			"ENABLED_WITH_CALLER_CLASS_NAME", "ENABLED_WITH_FULL_LOCATION_INFORMATION"
		})
		void getValidLocationInformation(OutputDetails outputDetails) {
			AndroidRuntime runtime = new AndroidRuntime();

			Function<String, ?> function = runtime.getRelativeCaller(outputDetails);
			Object result = Callee.execute(function, Callee.class.getName());

			assertThat(result).isInstanceOfSatisfying(StackTraceElement.class, element -> {
				assertThat(element.getClassName()).isEqualTo(RelativeCaller.class.getName());
				assertThat(element.getMethodName()).isEqualTo("getValidLocationInformation");
				assertThat(element.getFileName()).isEqualTo(AndroidRuntimeTest.class.getSimpleName() + ".java");
				assertThat(element.getLineNumber()).isEqualTo(145);
			});
		}

		/**
		 * Verifies that {@code null} is returned and a warning log entry is logged
		 * for {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} and {@link
		 * OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION} if a class name is
		 * passed that does not exist in the stack trace.
		 *
		 * @param outputDetails {@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME} or
		 *                      {@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION}
		 */
		@ParameterizedTest
		@EnumSource(value = OutputDetails.class, names = {
			"ENABLED_WITH_CALLER_CLASS_NAME", "ENABLED_WITH_FULL_LOCATION_INFORMATION"
		})
		void getInvalidLocationInformation(OutputDetails outputDetails) {
			AndroidRuntime runtime = new AndroidRuntime();

			Function<String, ?> function = runtime.getRelativeCaller(outputDetails);
			Object result = Callee.execute(function, "org.tinylog.invalid.Foo");

			assertThat(result).isNull();
			assertThat(log.consume()).singleElement().satisfies(entry -> {
				assertThat(entry.getLevel()).isEqualTo(Level.WARN);
				assertThat(entry.getMessage()).contains("org.tinylog.invalid.Foo");
			});
		}

		/**
		 * Verifies that {@code null} is returned for {@link OutputDetails#DISABLED} and
		 * {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}.
		 *
		 * @param outputDetails {@link OutputDetails#DISABLED} or
		 *                      {@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}
		 */
		@ParameterizedTest
		@EnumSource(value = OutputDetails.class, names = {"DISABLED", "ENABLED_WITHOUT_LOCATION_INFORMATION"})
		void getNoLocationInformation(OutputDetails outputDetails) {
			AndroidRuntime runtime = new AndroidRuntime();

			Function<String, ?> function = runtime.getRelativeCaller(outputDetails);
			Object result = Callee.execute(function, Callee.class.getName());

			assertThat(result).isNull();
		}

	}

	/**
	 * Helper class for simulating a callee.
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

		/**
		 * Executes the passed {@link Function}.
		 *
		 * @param function The function to execute
		 * @param argument The argument for the passed function
		 * @param <T> Argument type
		 * @param <R> Return type
		 * @return The produced value from the passed function
		 */
		static <T, R> R execute(Function<T, R> function, T argument) {
			return function.apply(argument);
		}

	}

}
