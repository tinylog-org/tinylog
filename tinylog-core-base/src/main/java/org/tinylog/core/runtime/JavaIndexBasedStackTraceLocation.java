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
		if (callerClassGetter == null) {
			StackTraceElement[] trace = new Throwable().getStackTrace();
			if (index >= 0 && index < trace.length) {
				return trace[index].getClassName();
			} else {
				return null;
			}
		} else {
			try {
				Class<?> clazz = (Class<?>) callerClassGetter.invoke(index + 1);
				return clazz == null ? null : clazz.getName();
			} catch (IndexOutOfBoundsException ex) {
				return null;
			} catch (Throwable ex) {
				InternalLogger.error(ex, "Failed to extract class name at the stack trace depth of {}", index);
				return null;
			}
		}
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		StackTraceElement[] trace = new Throwable().getStackTrace();
		if (index >= 0 && index < trace.length) {
			return trace[index];
		} else {
			return null;
		}
	}

}
