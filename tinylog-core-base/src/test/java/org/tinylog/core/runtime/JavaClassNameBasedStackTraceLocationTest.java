package org.tinylog.core.runtime;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class JavaClassNameBasedStackTraceLocationTest {

	/**
	 * Verifies that the fully-qualified caller class name can be resolved.
	 */
	@Test
	void validCallerClassName() {
		String callee = Callee.class.getName();
		JavaClassNameBasedStackTraceLocation location = new JavaClassNameBasedStackTraceLocation(callee, 1);
		String caller = Callee.execute(location::getCallerClassName);

		assertThat(caller).isEqualTo(JavaClassNameBasedStackTraceLocationTest.class.getName());
	}

	/**
	 * Verifies that {@code null} is returned for a non-existent caller class name.
	 */
	@Test
	void invalidCallerClassName() {
		String callee = "non.existent.InvalidClass";
		JavaClassNameBasedStackTraceLocation location = new JavaClassNameBasedStackTraceLocation(callee, 1);
		String caller = Callee.execute(location::getCallerClassName);

		assertThat(caller).isNull();
	}

	/**
	 * Verifies that the complete caller stack trace element can be resolved.
	 */
	@Test
	void validCallerStackTraceElement() {
		String callee = Callee.class.getName();
		JavaClassNameBasedStackTraceLocation location = new JavaClassNameBasedStackTraceLocation(callee, 1);
		StackTraceElement caller = Callee.execute(location::getCallerStackTraceElement);

		assertThat(caller).isNotNull();
		assertThat(caller.getFileName())
			.isEqualTo(JavaClassNameBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(JavaClassNameBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("validCallerStackTraceElement");
		assertThat(caller.getLineNumber()).isEqualTo(44);
	}

	/**
	 * Verifies that {@code null} is returned for a non-existent caller class name.
	 */
	@Test
	void invalidCallerStackTraceElement() {
		String callee = "non.existent.InvalidClass";
		JavaClassNameBasedStackTraceLocation location = new JavaClassNameBasedStackTraceLocation(callee, 1);
		StackTraceElement caller = Callee.execute(location::getCallerStackTraceElement);

		assertThat(caller).isNull();
	}

	/**
	 * Verifies that the caller stack trace element can be resolved from called sub methods.
	 */
	@Test
	void passedStackTraceLocation() {
		String callee = Callee.class.getName();
		JavaClassNameBasedStackTraceLocation location = new JavaClassNameBasedStackTraceLocation(callee, 1);
		StackTraceElement caller = Callee.execute(() -> getCallerStackTraceElement(location.push()));

		assertThat(caller).isNotNull();
		assertThat(caller.getFileName())
			.isEqualTo(JavaClassNameBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(JavaClassNameBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("passedStackTraceLocation");
		assertThat(caller.getLineNumber()).isEqualTo(73);
	}

	/**
	 * Retrieves the caller stack trace element from the passed stack trace location.
	 *
	 * @param location The stack trace location from which the caller is to be received
	 * @return The received caller stack trace element
	 */
	private StackTraceElement getCallerStackTraceElement(JavaClassNameBasedStackTraceLocation location) {
		return location.getCallerStackTraceElement();
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
