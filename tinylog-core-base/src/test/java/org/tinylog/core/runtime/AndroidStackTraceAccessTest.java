package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidStackTraceAccessTest {

	/**
	 * Verifies that {@code dalvik.system.VMStack.getStackClass2()} is available on Android.
	 */
	@EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	@Test
	void getCallClassAvailable() throws Throwable {
		AndroidStackTraceAccess access = new AndroidStackTraceAccess();
		MethodHandle getCallerClass = access.getCallerClassGetter();
		assertThat(getCallerClass).isNotNull();

		Callee callee = getCallerClass::invoke;
		assertThat(callee.execute()).isEqualTo(AndroidStackTraceAccessTest.class);
	}

	/**
	 * Verifies that {@code dalvik.system.VMStack.getStackClass2()} is not available on standard Java.
	 */
	@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	@Test
	void getCallerClassUnavailable() {
		assertThat(new AndroidStackTraceAccess().getCallerClassGetter()).isNull();
	}

	/**
	 * Verifies that {@code dalvik.system.VMStack.fillStackTraceElements(Thread, StackTraceElement[])} is available on
	 * Android.
	 */
	@EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	@Test
	void fillStackTraceElementsAvailable() throws Throwable {
		AndroidStackTraceAccess access = new AndroidStackTraceAccess();
		MethodHandle fillStackTraceElements = access.getStackTraceElementsFiller();
		assertThat(fillStackTraceElements).isNotNull();

		StackTraceElement[] trace = new StackTraceElement[2];
		fillStackTraceElements.invoke(Thread.currentThread(), trace);
		assertThat(trace[1]).isEqualTo(new StackTraceElement(
			AndroidStackTraceAccessTest.class.getName(),
			"fillStackTraceElementsAvailable",
			AndroidStackTraceAccessTest.class.getSimpleName() + ".java",
			48
		));
	}

	/**
	 * Verifies that {@code dalvik.system.VMStack.fillStackTraceElements(Thread, StackTraceElement[])} is not available
	 * on standard Java.
	 */
	@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	@Test
	void fillStackTraceElementsUnavailable() {
		assertThat(new AndroidStackTraceAccess().getStackTraceElementsFiller()).isNull();
	}

	/**
	 * Functional interface for simulating a callee.
	 */
	@FunctionalInterface
	private interface Callee {

		/**
		 * Executes the callee.
		 *
		 * @return The produced result
		 * @throws Throwable Failed to execute the implemented code
		 */
		Object execute() throws Throwable;

	}

}
