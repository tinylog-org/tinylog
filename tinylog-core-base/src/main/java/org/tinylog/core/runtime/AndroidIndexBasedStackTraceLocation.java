package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;

import org.tinylog.core.internal.InternalLogger;

/**
 * Stack trace location implementation for Android that stores the location of a callee as numeric index.
 */
public class AndroidIndexBasedStackTraceLocation implements StackTraceLocation {

	private static final MethodHandle fillStackTraceElements;
	private static final int offset;

	private final int index;

	static {
		AndroidStackTraceAccess access = new AndroidStackTraceAccess();
		fillStackTraceElements = access.getStackTraceElementsFiller();
		offset = access.getOffset();

		if (fillStackTraceElements == null) {
			InternalLogger.debug(
				null,
				"Legacy dalvik.system.VMStack.fillStackTraceElements(Thread, StackTraceElement[]) is not available"
			);
		}
	}

	/**
	 * @param index The index of the callee in the stack trace
	 */
	AndroidIndexBasedStackTraceLocation(int index) {
		this.index = index;
	}

	@Override
	public AndroidIndexBasedStackTraceLocation push() {
		return new AndroidIndexBasedStackTraceLocation(index + 1);
	}

	@Override
	public String getCallerClassName() {
		if (fillStackTraceElements != null) {
			try {
				StackTraceElement[] trace = new StackTraceElement[index + offset + 1];
				fillStackTraceElements.invoke(Thread.currentThread(), trace);
				if (trace[trace.length - 1] != null) {
					return trace[trace.length - 1].getClassName();
				}
			} catch (Throwable ex) {
				InternalLogger.error(ex, "Failed to extract class name at the stack trace depth of {}", index);
			}
		}

		StackTraceElement[] trace = new Throwable().getStackTrace();
		if (index >= 0 && index < trace.length) {
			return trace[index].getClassName();
		} else {
			InternalLogger.error(null, "There is no class name at the stack trace depth of {}", index);
			return null;
		}
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		if (fillStackTraceElements != null) {
			try {
				StackTraceElement[] trace = new StackTraceElement[index + offset + 1];
				fillStackTraceElements.invoke(Thread.currentThread(), trace);
				if (trace[trace.length - 1] != null) {
					return trace[trace.length - 1];
				}
			} catch (Throwable ex) {
				InternalLogger.error(ex, "Failed to extract stack trace element at the depth of {}", index);
			}
		}

		StackTraceElement[] trace = new Throwable().getStackTrace();
		if (index >= 0 && index < trace.length) {
			return trace[index];
		} else {
			InternalLogger.error(null, "There is no stack trace element at the depth of {}", index);
			return null;
		}
	}

}
