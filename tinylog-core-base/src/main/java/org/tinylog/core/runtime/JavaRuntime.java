package org.tinylog.core.runtime;

/**
 * Runtime implementation for standard Java.
 */
public class JavaRuntime implements RuntimeFlavor {

	/** */
	public JavaRuntime() {
	}

	@Override
	public JavaIndexBasedStackTraceLocation getStackTraceLocationAtIndex(int index) {
		return new JavaIndexBasedStackTraceLocation(index + 1);
	}

	@Override
	public JavaClassNameBasedStackTraceLocation getStackTraceLocationAfterClass(String className) {
		return new JavaClassNameBasedStackTraceLocation(className, 1);
	}

}
