package org.tinylog.core.runtime;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;

import org.tinylog.core.internal.InternalLogger;

/**
 * Runtime implementation for standard Java.
 */
public class JavaRuntime implements RuntimeFlavor {

	private final RuntimeMXBean runtimeBean;

	/** */
	public JavaRuntime() {
		runtimeBean = ManagementFactory.getRuntimeMXBean();
	}

	@Override
	public long getProcessId() {
		String name = runtimeBean.getName();
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
	public Duration getUptime() {
		return Duration.ofMillis(runtimeBean.getUptime());
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
