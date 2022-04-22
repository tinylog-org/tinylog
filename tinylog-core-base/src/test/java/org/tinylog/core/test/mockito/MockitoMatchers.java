package org.tinylog.core.test.mockito;

import org.mockito.internal.progress.ThreadSafeMockingProgress;

/**
 * Custom matchers for Mockito.
 */
public final class MockitoMatchers {

	/** */
	private MockitoMatchers() {
	}

	/**
	 * Matches a {@link StackTraceElement}.
	 *
	 * @param declaringClass Expected fully-qualified class name
	 * @param methodName Expected method name
	 * @param fileName Expected file name
	 * @param lineNumber Expected line number
	 * @return Always {@code null}
	 */
	public static StackTraceElement isStackTraceElement(String declaringClass, String methodName, String fileName,
			int lineNumber) {
		ThreadSafeMockingProgress
			.mockingProgress()
			.getArgumentMatcherStorage()
			.reportMatcher(new StackTraceElementMatcher(declaringClass, methodName, fileName, lineNumber));
		return null;
	}

}
