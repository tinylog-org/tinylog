package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;

/**
 * Runtime implementation for standard Java.
 */
public class JavaRuntime implements RuntimeFlavor {

	private static final int STACK_TRACE_DEPTH_THROWABLE = 2;
	private static final int STACK_TRACE_DEPTH_CLASS_CONTEXT = 3;
	private static final int STACK_TRACE_DEPTH_CALLER_CLASS_GETTER = 4;

	private static final MethodHandle callerClassGetter;
	private static final MethodHandle stackTraceElementGetter;

	private final RuntimeMXBean runtimeBean;
	private final CallerClassProvider callerClassProvider;

	static {
		LegacyStackTraceAccess access = new LegacyStackTraceAccess();

		callerClassGetter = access.getCallerClassGetter();
		if (callerClassGetter == null) {
			InternalLogger.debug(null, "Legacy sun.reflect.Reflection.getCallerClass(int) is not available");
		}

		stackTraceElementGetter = access.getStackTraceElementGetter();
		if (stackTraceElementGetter == null) {
			InternalLogger.debug(null, "Legacy Throwable.getStackTraceElement(int) is not available");
		}
	}

	/** */
	public JavaRuntime() {
		runtimeBean = ManagementFactory.getRuntimeMXBean();
		callerClassProvider = new CallerClassProvider();
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
	public String getDefaultWriter() {
		return "console";
	}

	@Override
	public Supplier<Object> getDirectCaller(OutputDetails outputDetails) {
		switch (outputDetails) {
			case ENABLED_WITH_FULL_LOCATION_INFORMATION:
				if (stackTraceElementGetter == null) {
					return () -> new Throwable().getStackTrace()[STACK_TRACE_DEPTH_THROWABLE];
				} else {
					return this::invokeDirectStackTraceElementGetter;
				}

			case ENABLED_WITH_CALLER_CLASS_NAME:
				if (callerClassGetter == null) {
					return callerClassProvider::getDirectCallerClass;
				} else {
					return this::invokeDirectCallerClassGetter;
				}

			default:
				return () -> null;
		}
	}

	@Override
	public Function<String, Object> getRelativeCaller(OutputDetails outputDetails) {
		switch (outputDetails) {
			case ENABLED_WITH_FULL_LOCATION_INFORMATION:
				if (stackTraceElementGetter == null) {
					return this::findStackTraceElement;
				} else {
					return callerClassProvider::getRelativeCallerStackTraceElement;
				}

			case ENABLED_WITH_CALLER_CLASS_NAME:
				return callerClassProvider::getRelativeCallerClass;

			default:
				return className -> null;
		}
	}

	/**
	 * Invokes the {@link MethodHandle} for the stack trace element getter.
	 *
	 * @return The stack trace element of the caller
	 */
	private Object invokeDirectStackTraceElementGetter() {
		try {
			return stackTraceElementGetter.invoke(new Throwable(), STACK_TRACE_DEPTH_THROWABLE);
		} catch (Throwable ex) {
			InternalLogger.error(
				ex,
				"Failed to get the stack trace element of the caller class"
			);
			return null;
		}
	}

	/**
	 * Invokes the {@link MethodHandle} for the caller class getter.
	 *
	 * @return The class of the caller
	 */
	private Object invokeDirectCallerClassGetter() {
		try {
			return callerClassGetter.invoke(STACK_TRACE_DEPTH_CALLER_CLASS_GETTER);
		} catch (Throwable ex) {
			InternalLogger.error(ex, "Failed to get the caller class name");
			return null;
		}
	}

	/**
	 * Finds the stack trace element of the caller of the passed fully-qualified class name without using any
	 * internal or deprecated API.
	 *
	 * @param className The fully-qualified class name
	 * @return The stack trace element of the caller
	 */
	private StackTraceElement findStackTraceElement(String className) {
		StackTraceElement[] trace = new Throwable().getStackTrace();
		int index = 0;

		while (index < trace.length && !className.equals(trace[index].getClassName())) {
			++index;
		}

		while (index < trace.length && className.equals(trace[index].getClassName())) {
			++index;
		}

		if (index < trace.length) {
			return trace[index];
		} else {
			InternalLogger.warn(
				null,
				"Class \"{}\" is expected in the stack trace for caller extraction but is actually missing",
				className
			);
			return null;
		}
	}

	/**
	 * Finds the caller of the passed fully-qualified class name.
	 *
	 * @param classes All classes of the stack trace
	 * @param className The fully-qualified class name
	 * @return The index of the caller class or {@code -1} if the caller couldn't be found
	 */
	private static int findCaller(Class<?>[] classes, String className) {
		int index = 0;

		while (index < classes.length && !className.equals(classes[index].getName())) {
			++index;
		}

		while (index < classes.length && className.equals(classes[index].getName())) {
			++index;
		}

		if (index < classes.length) {
			return index;
		} else {
			InternalLogger.warn(
				null,
				"Class \"{}\" is expected in the class context for caller extraction but is actually missing",
				className
			);
			return -1;
		}
	}

	/**
	 * Security manager implementation that uses {@link SecurityManager#getClassContext()} for finding caller classes.
	 */
	private static final class CallerClassProvider extends SecurityManager {

		/** */
		private CallerClassProvider() {
		}

		/**
		 * Gets the direct caller class. The direct caller is the caller of the method that invokes this method.
		 *
		 * @return The direct caller class
		 */
		public Class<?> getDirectCallerClass() {
			return getClassContext()[STACK_TRACE_DEPTH_CLASS_CONTEXT];
		}

		/**
		 * Gets the relative caller class. The relative caller is the caller of the passed fully-qualified class name.
		 *
		 * @param className The fully-qualified class name
		 * @return The caller class
		 */
		public Class<?> getRelativeCallerClass(String className) {
			Class<?>[] classes = getClassContext();
			int index = findCaller(classes, className);
			return index >= 0 ? classes[index] : null;
		}

		/**
		 * Gets the stack trace element of the relative caller class. The relative caller is the caller of the passed
		 * fully-qualified class name.
		 *
		 * @param className The fully-qualified class name
		 * @return The stack trace element of the caller
		 */
		public StackTraceElement getRelativeCallerStackTraceElement(String className) {
			Class<?>[] classes = getClassContext();
			int index = findCaller(classes, className);

			if (index >= 0) {
				try {
					return (StackTraceElement) stackTraceElementGetter.invoke(new Throwable(), index - 1);
				} catch (Throwable ex) {
					InternalLogger.error(ex, "Failed to extract caller stack trace element from stack trace");
				}
			}

			return null;
		}

	}

}
