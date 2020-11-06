package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.tinylog.core.internal.InternalLogger;

/**
 * Base utility class for resolving legacy methods for receiving specific elements from the stack trace.
 */
abstract class BaseStackTraceAccess {

	/** */
	protected BaseStackTraceAccess() {
	}

	/**
	 * Gets a verified method handle.
	 *
	 * @param check Verifier for the found method handle
	 * @param className Class name
	 * @param methodName Method name
	 * @param argumentTypes Method argument types
	 * @return The found method handle or {@code null} if no valid method can be resolved
	 */
	protected final MethodHandle getMethod(FailableCheck<MethodHandle> check, String className, String methodName,
			Class<?>... argumentTypes) {
		try {
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getDeclaredMethod(methodName, argumentTypes);
			method.setAccessible(true);
			MethodHandle handle = MethodHandles.lookup().unreflect(method);
			return check.test(handle) ? handle : null;
		} catch (Throwable ex) {
			InternalLogger.debug(ex, "Failed to load {}.{}()", className, methodName);
			return null;
		}
	}

}
