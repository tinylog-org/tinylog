package org.tinylog.core.runtime;

/**
 * Stack trace location implementation for Android that stores the fully qualified class name of a callee.
 */
public class AndroidClassNameBasedStackTraceLocation implements StackTraceLocation {

	private final String className;

	/**
	 * @param className The fully qualified class name of the callee
	 */
	public AndroidClassNameBasedStackTraceLocation(final String className) {
		this.className = className;
	}

	@Override
	public AndroidClassNameBasedStackTraceLocation push() {
		return this;
	}

	@Override
	public String getCallerClassName() {
		StackTraceElement element = push().getCallerStackTraceElement();
		return element == null ? null : element.getClassName();
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		StackTraceElement[] trace = new Throwable().getStackTrace();
		boolean foundClassName = false;

		for (StackTraceElement element : trace) {
			if (foundClassName && !className.equals(element.getClassName())) {
				return element;
			} else if (!foundClassName && className.equals(element.getClassName())) {
				foundClassName = true;
			}
		}

		return null;
	}

}
