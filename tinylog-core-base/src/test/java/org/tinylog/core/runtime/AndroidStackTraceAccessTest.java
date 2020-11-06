package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidStackTraceAccessTest {

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

		StackTraceElement[] trace = new StackTraceElement[access.getOffset() + 1];
		fillStackTraceElements.invoke(Thread.currentThread(), trace);
		assertThat(trace[trace.length - 1]).isEqualTo(new StackTraceElement(
			AndroidStackTraceAccessTest.class.getName(),
			"fillStackTraceElementsAvailable",
			AndroidStackTraceAccessTest.class.getSimpleName() + ".java",
			25
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

}
