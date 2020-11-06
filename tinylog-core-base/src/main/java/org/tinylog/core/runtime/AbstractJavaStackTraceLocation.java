package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;

import org.tinylog.core.internal.InternalLogger;

/**
 * Base class for stack trace location implementations for Java 8.
 */
abstract class AbstractJavaStackTraceLocation implements StackTraceLocation {

	/**
	 * Property is set to a valid handle for {@code sun.reflect.Reflection.getCallerClass(int)} if this method is
	 * available, or to {@code null} if this method is unavailable.
	 */
	protected static final MethodHandle callerClassGetter;

	/**
	 * Property is set to a valid handle for {@code Throwable.getStackTraceElement(int)} if this method is available,
	 * or to {@code null} if this method is unavailable.
	 */
	protected static final MethodHandle stackTraceElementGetter;

	static {
		LegacyStackTraceAccess access = new LegacyStackTraceAccess();

		callerClassGetter = access.getCallerClassGetter();
		if (callerClassGetter == null) {
			InternalLogger.debug(null, "Legacy sun.reflect.Reflection.getCallerClass(int) is not available");
		}

		stackTraceElementGetter = access.getStackTraceElementGetter();
		if (stackTraceElementGetter == null) {
			InternalLogger.debug(null, "Legacy Throwable.getStackTraceElement(int) is not available");
		}
	}

}
