package org.tinylog.core.runtime;

import org.tinylog.core.internal.InternalLogger;

/**
 * Stack trace location implementation for legacy Java 8 that stores the location of a callee as numeric index.
 */
public class JavaIndexBasedStackTraceLocation extends AbstractJavaStackTraceLocation {

	private final int index;

	/**
	 * @param index The index of the callee in the stack trace
	 */
	JavaIndexBasedStackTraceLocation(int index) {
		this.index = index;
	}

	@Override
	public JavaIndexBasedStackTraceLocation push() {
		return new JavaIndexBasedStackTraceLocation(index + 1);
	}

	@Override
	public String getCallerClassName() {
		if (callerClassGetter != null) {
			try {
				Class<?> clazz = (Class<?>) callerClassGetter.invoke(index + 1);
				return clazz.getName();
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
		StackTraceElement[] trace = new Throwable().getStackTrace();
		if (index >= 0 && index < trace.length) {
			return trace[index];
		} else {
			InternalLogger.error(null, "There is no stack trace element at the depth of {}", index);
			return null;
		}
	}

}
