package org.tinylog.core.runtime;

import java.util.Optional;

import org.tinylog.core.internal.InternalLogger;

/**
 * Stack trace location implementation for modern Java 9 and later that stores the location of a callee as numeric
 * index.
 */
public class JavaIndexBasedStackTraceLocation implements StackTraceLocation {

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
		String className = StackWalker.getInstance()
			.walk(stream -> index < 0 ? Optional.<StackWalker.StackFrame>empty() : stream.skip(index).findFirst())
			.map(StackWalker.StackFrame::getClassName)
			.orElse(null);

		if (className == null) {
			InternalLogger.error(null, "There is no class name at the stack trace depth of {}", index);
		}

		return className;
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		StackTraceElement element = StackWalker.getInstance()
			.walk(stream -> index < 0 ? Optional.<StackWalker.StackFrame>empty() : stream.skip(index).findFirst())
			.map(StackWalker.StackFrame::toStackTraceElement)
			.orElse(null);

		if (element == null) {
			InternalLogger.error(null, "There is no stack trace element at the depth of {}", index);
		}

		return element;
	}

}
