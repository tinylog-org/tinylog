package org.tinylog.core.runtime;

/**
 * Runtime implementation for Android (API level 26 or later).
 */
public class AndroidRuntime implements RuntimeFlavor {

	/** */
	public AndroidRuntime() {
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
