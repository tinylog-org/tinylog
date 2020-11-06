package org.tinylog.core.runtime;

/**
 * Stack trace location implementation for modern Java 9 and later that stores the fully qualified class name of a
 * callee.
 */
public class JavaClassNameBasedStackTraceLocation implements StackTraceLocation {

	private final String className;
	private final int offset;

	/**
	 * @param className The fully qualified class name of the callee
	 * @param offset The number of stack trace elements that can be skipped for sure
	 */
	JavaClassNameBasedStackTraceLocation(String className, int offset) {
		this.className = className;
		this.offset = offset;
	}

	@Override
	public JavaClassNameBasedStackTraceLocation push() {
		return new JavaClassNameBasedStackTraceLocation(className, offset + 1);
	}

	@Override
	public String getCallerClassName() {
		return StackWalker.getInstance().walk(stream ->
			stream.skip(offset)
				.dropWhile(frame -> !className.equals(frame.getClassName()))
				.dropWhile(frame -> className.equals(frame.getClassName()))
				.findFirst()
		).map(StackWalker.StackFrame::getClassName).orElse(null);
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		return StackWalker.getInstance().walk(stream ->
			stream.skip(offset)
				.dropWhile(frame -> !className.equals(frame.getClassName()))
				.dropWhile(frame -> className.equals(frame.getClassName()))
				.findFirst()
		).map(StackWalker.StackFrame::toStackTraceElement).orElse(null);
	}

}
