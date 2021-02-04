package org.tinylog.core.runtime;

import java.lang.management.ManagementFactory;

import org.tinylog.core.internal.InternalLogger;

/**
 * Runtime implementation for standard Java.
 */
public class JavaRuntime implements RuntimeFlavor {

	/** */
	public JavaRuntime() {
	}

	@Override
	public long getProcessId() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		int atIndex = name.indexOf('@');

		if (atIndex > 0) {
			try {
				return Long.parseLong(name.substring(0, atIndex));
			} catch (NumberFormatException ex) {
				InternalLogger.error(ex, "Runtime name \"{}\" does not contain a valid process ID", name);
			}
		} else {
			InternalLogger.error(null, "Runtime name \"{}\" does not contain a valid process ID", name);
		}

		return -1;
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
