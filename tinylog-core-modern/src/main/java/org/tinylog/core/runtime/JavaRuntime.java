package org.tinylog.core.runtime;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;

/**
 * Runtime implementation for modern Java.
 */
public class JavaRuntime implements RuntimeFlavor {

	private final RuntimeMXBean runtimeBean;

	/** */
	public JavaRuntime() {
		runtimeBean = ManagementFactory.getRuntimeMXBean();
	}

	@Override
	public long getProcessId() {
		return ProcessHandle.current().pid();
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
