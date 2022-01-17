package org.tinylog.core.runtime;

import org.tinylog.core.internal.InternalLogger;

/**
 * Stack trace location implementation for legacy Java 8 that stores the fully qualified class name of a callee.
 */
public class JavaClassNameBasedStackTraceLocation extends AbstractJavaStackTraceLocation {

	private final String className;
	private final int offset;

	/**
	 * @param className The fully qualified class name of the callee
	 * @param offset The number of stack trace elements that can be skipped for sure
	 */
	JavaClassNameBasedStackTraceLocation(String className, int offset) {
		this.className = className;
		this.offset = offset;
	}

	@Override
	public JavaClassNameBasedStackTraceLocation push() {
		return new JavaClassNameBasedStackTraceLocation(className, offset + 1);
	}

	@Override
	public String getCallerClassName() {
		if (callerClassGetter == null) {
			StackTraceElement element = findCallerInStackTrace(new Throwable().getStackTrace());
			return element == null ? null : element.getClassName();
		} else {
			boolean foundClassName = false;

			for (int i = offset; ; ++i) {
				Class<?> clazz;
				try {
					clazz = (Class<?>) callerClassGetter.invoke(i + 1);
				} catch (IndexOutOfBoundsException ex) {
					return null;
				} catch (Throwable ex) {
					InternalLogger.error(ex, "Failed to extract caller class name from stack trace");
					return null;
				}

				if (clazz == null) {
					return null;
				} else if (foundClassName && !className.equals(clazz.getName())) {
					return clazz.getName();
				} else if (!foundClassName && className.equals(clazz.getName())) {
					foundClassName = true;
				}
			}
		}
	}

	@Override
	public StackTraceElement getCallerStackTraceElement() {
		Throwable throwable = new Throwable();

		if (stackTraceElementGetter == null) {
			return findCallerInStackTrace(throwable.getStackTrace());
		} else {
			boolean foundClassName = false;

			for (int i = offset; ; ++i) {
				StackTraceElement element;
				try {
					element = (StackTraceElement) stackTraceElementGetter.invoke(throwable, i);
				} catch (IndexOutOfBoundsException ex) {
					return null;
				} catch (Throwable ex) {
					InternalLogger.error(ex, "Failed to extract caller stack trace element from stack trace");
					return null;
				}

				if (foundClassName && !className.equals(element.getClassName())) {
					return element;
				} else if (!foundClassName && className.equals(element.getClassName())) {
					foundClassName = true;
				}
			}
		}
	}

	/**
	 * Tries to find the caller stack trace element in the passed stack trace.
	 *
	 * @param trace Stack trace that should contain a stack trace element of the caller
	 * @return The found stack trace element of the caller, or {@code null} if the caller could not be found
	 */
	private StackTraceElement findCallerInStackTrace(StackTraceElement[] trace) {
		boolean foundClassName = false;

		for (int i = offset; i < trace.length; ++i) {
			StackTraceElement element = trace[i];

			if (foundClassName && !className.equals(element.getClassName())) {
				return element;
			} else if (!foundClassName && className.equals(element.getClassName())) {
				foundClassName = true;
			}
		}

		return null;
	}

}
