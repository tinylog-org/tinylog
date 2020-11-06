package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;

/**
 * Utility class for resolving legacy Java methods for receiving specific elements from the stack trace.
 */
class LegacyStackTraceAccess extends BaseStackTraceAccess {

	/** */
	LegacyStackTraceAccess() {
	}

	/**
	 * Creates a method handle for {@code sun.reflect.Reflection.getCallerClass(int)}.
	 *
	 * @return Valid method handle if the method is available, otherwise {@code null}
	 */
	MethodHandle getCallerClassGetter() {
		FailableCheck<MethodHandle> check = handle -> LegacyStackTraceAccess.class.equals(handle.invoke(1));
		return getMethod(check, "sun.reflect.Reflection", "getCallerClass", int.class);
	}

	/**
	 * Creates a method handle for {@code Throwable.getStackTraceElement(int)}.
	 *
	 * @return Valid method handle if the method is available, otherwise {@code null}
	 */
	MethodHandle getStackTraceElementGetter() {
		FailableCheck<MethodHandle> check = handle -> {
			StackTraceElement stackTraceElement = (StackTraceElement) handle.invoke(new Throwable(), 0);
			return LegacyStackTraceAccess.class.getName().equals(stackTraceElement.getClassName());
		};

		return getMethod(check, Throwable.class.getName(), "getStackTraceElement", int.class);
	}

}
