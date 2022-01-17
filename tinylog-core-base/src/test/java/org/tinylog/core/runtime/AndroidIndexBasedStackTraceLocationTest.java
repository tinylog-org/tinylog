package org.tinylog.core.runtime;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class AndroidIndexBasedStackTraceLocationTest {

	@Inject
	private Log log;

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
	 *
	 * @param depth The invalid depth index to test
	 */
	@ParameterizedTest
	@ValueSource(ints = {Integer.MIN_VALUE, Integer.MAX_VALUE})
	void invalidCallerClassName(int depth) {
		AndroidIndexBasedStackTraceLocation location = new AndroidIndexBasedStackTraceLocation(depth);
		String caller = location.getCallerClassName();

		assertThat(caller).isNull();
		assertThat(log.consume()).singleElement().satisfies(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains(Integer.toString(depth));
		});
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
		assertThat(caller.getLineNumber()).isEqualTo(55);
	}

	/**
	 * Verifies that {@code null} is returned for an invalid depth index.
	 *
	 * @param depth The invalid depth index to test
	 */
	@ParameterizedTest
	@ValueSource(ints = {Integer.MIN_VALUE, Integer.MAX_VALUE})
	void invalidStackTraceElement(int depth) {
		AndroidIndexBasedStackTraceLocation location = new AndroidIndexBasedStackTraceLocation(depth);
		StackTraceElement caller = location.getCallerStackTraceElement();

		assertThat(caller).isNull();
		assertThat(log.consume()).singleElement().satisfies(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains(Integer.toString(depth));
		});
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
		assertThat(caller.getLineNumber()).isEqualTo(89);
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
