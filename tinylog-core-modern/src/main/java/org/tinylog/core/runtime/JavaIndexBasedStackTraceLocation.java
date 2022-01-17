package org.tinylog.core.runtime;

import java.util.Optional;

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
		return StackWalker.getInstance()
			.walk(stream -> index < 0 ? Optional.<StackWalker.StackFrame>empty() : stream.skip(index).findFirst())
			.map(StackWalker.StackFrame::getClassName)
			.orElse(null);
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		return StackWalker.getInstance()
			.walk(stream -> index < 0 ? Optional.<StackWalker.StackFrame>empty() : stream.skip(index).findFirst())
			.map(StackWalker.StackFrame::toStackTraceElement)
			.orElse(null);
	}

}
