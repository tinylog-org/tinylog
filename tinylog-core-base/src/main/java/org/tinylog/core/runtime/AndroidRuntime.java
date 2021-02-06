package org.tinylog.core.runtime;

import java.time.Duration;

import android.os.Process;
import android.os.SystemClock;

/**
 * Runtime implementation for Android (API level 26 or later).
 */
public class AndroidRuntime implements RuntimeFlavor {

	private final long startTime;

	/** */
	public AndroidRuntime() {
		startTime = SystemClock.uptimeMillis();
	}

	@Override
	public long getProcessId() {
		return Process.myPid();
	}

	@Override
	public Duration getUptime() {
		return Duration.ofMillis(SystemClock.uptimeMillis() - startTime);
	}

	@Override
	public AndroidIndexBasedStackTraceLocation getStackTraceLocationAtIndex(int index) {
		return new AndroidIndexBasedStackTraceLocation(index + 1);
	}

	@Override
	public AndroidClassNameBasedStackTraceLocation getStackTraceLocationAfterClass(String className) {
		return new AndroidClassNameBasedStackTraceLocation(className);
	}

}
