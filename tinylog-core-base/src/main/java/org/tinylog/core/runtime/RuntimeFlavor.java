package org.tinylog.core.runtime;

/**
 * Abstraction of API that depends on the Java version or flavor.
 */
public interface RuntimeFlavor {

	/**
	 * Gets the process ID of the current process.
	 *
	 * @return The process ID of the current process
	 */
	long getProcessId();

	/**
	 * Gets the stack trace location at a defined index.
	 *
	 * @param index Depth in the stack trace
	 * @return The found stack trace location
	 */
	StackTraceLocation getStackTraceLocationAtIndex(int index);

	/**
	 * Gets the first stack trace location after a defined class.
	 *
	 * @param className Fully-qualified class name
	 * @return The found stack trace location
	 */
	StackTraceLocation getStackTraceLocationAfterClass(String className);

}
