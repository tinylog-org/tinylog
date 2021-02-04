package org.tinylog.core.runtime;

import android.os.Process;

/**
 * Runtime implementation for Android (API level 26 or later).
 */
public class AndroidRuntime implements RuntimeFlavor {

	/** */
	public AndroidRuntime() {
	}

	@Override
	public long getProcessId() {
		return Process.myPid();
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
