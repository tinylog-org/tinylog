package org.tinylog.core.test.mockito;

import java.io.Serializable;
import java.util.Objects;

import org.mockito.ArgumentMatcher;

/**
 * Mockito argument matcher for {@link  StackTraceElement}.
 */
public class StackTraceElementMatcher implements ArgumentMatcher<StackTraceElement>, Serializable {

	private final String declaringClass;
	private final String methodName;
	private final String fileName;
	private final int lineNumber;

	/**
	 * @param declaringClass Expected fully-qualified class name
	 * @param methodName Expected method name
	 * @param fileName Expected file name
	 * @param lineNumber Expected line number
	 */
	public StackTraceElementMatcher(String declaringClass, String methodName, String fileName, int lineNumber) {
		this.declaringClass = declaringClass;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	@Override
	public boolean matches(StackTraceElement stackTraceElement) {
		return Objects.equals(declaringClass, stackTraceElement.getClassName())
			&& Objects.equals(methodName, stackTraceElement.getMethodName())
			&& Objects.equals(fileName, stackTraceElement.getFileName())
			&& lineNumber == stackTraceElement.getLineNumber();
	}

	@Override
	public String toString() {
		return "isStackTraceElement(" + declaringClass + "." + methodName + "(" + fileName + ":" + lineNumber + "))";
	}

}
