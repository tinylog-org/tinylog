package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;

/**
 * Utility class for resolving legacy Android methods for receiving specific elements from the stack trace.
 */
class AndroidStackTraceAccess extends BaseStackTraceAccess {

	private static final int STACK_TRACE_SIZE = 32;

	private int offset = -1;

	/** */
	AndroidStackTraceAccess() {
	}

	/**
	 * Gets the offset in filled stack traces to the caller.
	 *
	 * <p>
	 *     The method {@link #AndroidStackTraceAccess()} must be called at least once before getting a valid offset.
	 * </p>
	 *
	 * @return The offset to the caller
	 */
	int getOffset() {
		return offset;
	}

	/**
	 * Creates a method handle for {@code dalvik.system.VMStack.fillStackTraceElements(Thread, StackTraceElement[])}.
	 *
	 * @return Valid filler instance if the method is available, otherwise {@code null}
	 */
	MethodHandle getStackTraceElementsFiller() {
		FailableCheck<MethodHandle> check = handle -> {
			StackTraceElement[] trace = new StackTraceElement[STACK_TRACE_SIZE];
			int count = (Integer) handle.invoke(Thread.currentThread(), trace);
			for (int i = 0; i < count; ++i) {
				StackTraceElement element = trace[i];
				if (element.getClassName().startsWith(AndroidStackTraceAccess.class.getName())
					&& element.getMethodName().contains("getStackTraceElementsFiller")) {
					offset = i;
					return true;
				}
			}
			return false;
		};

		return getMethod(check, "dalvik.system.VMStack", "fillStackTraceElements", Thread.class,
			StackTraceElement[].class);
	}

}
