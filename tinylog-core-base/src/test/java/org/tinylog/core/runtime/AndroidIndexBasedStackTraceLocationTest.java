package org.tinylog.core.runtime;

import org.junit.jupiter.api.Test;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class AndroidIndexBasedStackTraceLocationTest {

	/**
	 * Verifies that the fully-qualified caller class name can be resolved.
	 */
	@Test
	void validCallerClassName() {
		AndroidIndexBasedStackTraceLocation location = new AndroidIndexBasedStackTraceLocation(1);
		String caller = location.getCallerClassName();

		assertThat(caller).isEqualTo(AndroidIndexBasedStackTraceLocationTest.class.getName());
	}

	/**
	 * Verifies that {@code null} is returned for an invalid depth index.
	 */
	@Test
	void invalidCallerClassName() {
		AndroidIndexBasedStackTraceLocation location = new AndroidIndexBasedStackTraceLocation(Short.MAX_VALUE);
		String caller = location.getCallerClassName();

		assertThat(caller).isNull();
	}

	/**
	 * Verifies that the complete caller stack trace element can be resolved directly.
	 */
	@Test
	void validCallerStackTraceElement() {
		AndroidIndexBasedStackTraceLocation location = new AndroidIndexBasedStackTraceLocation(1);
		StackTraceElement caller = location.getCallerStackTraceElement();

		assertThat(caller).isNotNull();
		assertThat(caller.getFileName())
			.isEqualTo(AndroidIndexBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(AndroidIndexBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("validCallerStackTraceElement");
		assertThat(caller.getLineNumber()).isEqualTo(39);
	}

	/**
	 * Verifies that {@code null} is returned for an invalid depth index.
	 */
	@Test
	void invalidStackTraceElement() {
		AndroidIndexBasedStackTraceLocation location = new AndroidIndexBasedStackTraceLocation(Short.MAX_VALUE);
		StackTraceElement caller = location.getCallerStackTraceElement();

		assertThat(caller).isNull();
	}

	/**
	 * Verifies that the caller stack trace element can be resolved from called sub methods.
	 */
	@Test
	void passedStackTraceLocation() {
		AndroidIndexBasedStackTraceLocation location = new AndroidIndexBasedStackTraceLocation(1);
		StackTraceElement caller = getCallerStackTraceElement(location.push());

		assertThat(caller).isNotNull();
		assertThat(caller.getFileName())
			.isEqualTo(AndroidIndexBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(AndroidIndexBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("passedStackTraceLocation");
		assertThat(caller.getLineNumber()).isEqualTo(66);
	}

	/**
	 * Retrieves the caller stack trace element from the passed stack trace location.
	 *
	 * @param location The stack trace location from which the caller is to be received
	 * @return The received caller stack trace element
	 */
	private StackTraceElement getCallerStackTraceElement(AndroidIndexBasedStackTraceLocation location) {
		return location.getCallerStackTraceElement();
	}

}
