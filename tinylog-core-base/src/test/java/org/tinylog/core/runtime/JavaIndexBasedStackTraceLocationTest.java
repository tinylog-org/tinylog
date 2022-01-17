package org.tinylog.core.runtime;

import org.junit.jupiter.api.Test;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class JavaIndexBasedStackTraceLocationTest {

	/**
	 * Verifies that the fully-qualified caller class name can be resolved.
	 */
	@Test
	void validCallerClassName() {
		JavaIndexBasedStackTraceLocation location = new JavaIndexBasedStackTraceLocation(1);
		String caller = location.getCallerClassName();

		assertThat(caller).isEqualTo(JavaIndexBasedStackTraceLocationTest.class.getName());
	}

	/**
	 * Verifies that {@code null} is returned for an invalid depth index.
	 */
	@Test
	void invalidCallerClassName() {
		JavaIndexBasedStackTraceLocation location = new JavaIndexBasedStackTraceLocation(Short.MAX_VALUE);
		String caller = location.getCallerClassName();

		assertThat(caller).isNull();
	}

	/**
	 * Verifies that the complete caller stack trace element can be resolved directly.
	 */
	@Test
	void validCallerStackTraceElement() {
		JavaIndexBasedStackTraceLocation location = new JavaIndexBasedStackTraceLocation(1);
		StackTraceElement caller = location.getCallerStackTraceElement();

		assertThat(caller).isNotNull();
		assertThat(caller.getFileName())
			.isEqualTo(JavaIndexBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(JavaIndexBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("validCallerStackTraceElement");
		assertThat(caller.getLineNumber()).isEqualTo(39);
	}

	/**
	 * Verifies that {@code null} is returned for an invalid depth index.
	 */
	@Test
	void invalidStackTraceElement() {
		JavaIndexBasedStackTraceLocation location = new JavaIndexBasedStackTraceLocation(Short.MAX_VALUE);
		StackTraceElement caller = location.getCallerStackTraceElement();

		assertThat(caller).isNull();
	}

	/**
	 * Verifies that the caller stack trace element can be resolved from called sub methods.
	 */
	@Test
	void passedStackTraceLocation() {
		JavaIndexBasedStackTraceLocation location = new JavaIndexBasedStackTraceLocation(1);
		StackTraceElement caller = getCallerStackTraceElement(location.push());

		assertThat(caller).isNotNull();
		assertThat(caller.getFileName())
			.isEqualTo(JavaIndexBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(JavaIndexBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("passedStackTraceLocation");
		assertThat(caller.getLineNumber()).isEqualTo(66);
	}

	/**
	 * Retrieves the caller stack trace element from the passed stack trace location.
	 *
	 * @param location The stack trace location from which the caller is to be received
	 * @return The received caller stack trace element
	 */
	private StackTraceElement getCallerStackTraceElement(JavaIndexBasedStackTraceLocation location) {
		return location.getCallerStackTraceElement();
	}

}
