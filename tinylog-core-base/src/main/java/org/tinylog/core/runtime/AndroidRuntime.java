package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;

import android.os.Process;
import android.os.SystemClock;

/**
 * Runtime implementation for Android (API level 26 or later).
 */
public class AndroidRuntime implements RuntimeFlavor {

    private static final int STACK_TRACE_DEPTH_THROWABLE = 2;
    private static final int STACK_TRACE_DEPTH_METHOD_HANDLE = 3;
    private static final int MAX_STACK_TRACE_SIZE_METHOD_HANDLE = 8;

    private final long startTime;

    private final Supplier<Object> stackTraceElementSupplier = new StackTraceElementSupplier();
    private final Supplier<Object> callerClassSupplier = new CallerClassSupplier();
    private final Supplier<Object> nullSupplier = new NullSupplier();

    private final Function<String, Object> stackTraceElementFunction = new StackTraceElementFunction();
    private final Function<String, Object> nullFunction = new NullFunction();

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
    public String getDefaultWriter() {
        return "logcat";
    }

    @Override
    public Supplier<Object> getDirectCaller(OutputDetails outputDetails) {
        switch (outputDetails) {
            case ENABLED_WITH_FULL_LOCATION_INFORMATION:
                return stackTraceElementSupplier;

            case ENABLED_WITH_CALLER_CLASS_NAME:
                return callerClassSupplier;

            default:
                return nullSupplier;
        }
    }

    @Override
    public Function<String, Object> getRelativeCaller(OutputDetails outputDetails) {
        switch (outputDetails) {
            case ENABLED_WITH_FULL_LOCATION_INFORMATION:
            case ENABLED_WITH_CALLER_CLASS_NAME:
                return stackTraceElementFunction;

            default:
                return nullFunction;
        }
    }

    /**
     * Supplier that resolves the stack trace element of the direct caller. The direct caller is the caller of the
     * method that resolves this supplier.
     */
    private static final class StackTraceElementSupplier implements Supplier<Object> {

        private static final MethodHandle fillStackTraceElements;

        static {
            fillStackTraceElements = new AndroidStackTraceAccess().getStackTraceElementsFiller();
            if (fillStackTraceElements == null) {
                InternalLogger.debug(
                    null,
                    "Legacy dalvik.system.VMStack.fillStackTraceElements() is not available"
                );
            }
        }

        /** */
        private StackTraceElementSupplier() {
        }

        @Override
        public Object get() {
            if (fillStackTraceElements == null) {
                return new Throwable().getStackTrace()[STACK_TRACE_DEPTH_THROWABLE];
            } else {
                StackTraceElement[] trace = new StackTraceElement[STACK_TRACE_DEPTH_METHOD_HANDLE + 1];
                try {
                    fillStackTraceElements.invoke(Thread.currentThread(), trace);
                } catch (Throwable ex) {
                    InternalLogger.error(ex, "Failed to get the stack trace element of the caller");
                }
                return trace[STACK_TRACE_DEPTH_METHOD_HANDLE];
            }
        }

    }

    /**
     * Supplier that resolves the class or stack trace element of the direct caller. The direct caller is the caller of
     * the method that resolves this supplier.
     */
    private static final class CallerClassSupplier implements Supplier<Object> {

        private static final MethodHandle callerClassGetter;

        static {
            callerClassGetter = new AndroidStackTraceAccess().getCallerClassGetter();
            if (callerClassGetter == null) {
                InternalLogger.debug(null, "Legacy dalvik.system.VMStack.getStackClass2() is not available");
            }
        }

        /** */
        private CallerClassSupplier() {
        }

        @Override
        public Object get() {
            if (callerClassGetter == null) {
                return new Throwable().getStackTrace()[STACK_TRACE_DEPTH_THROWABLE];
            } else {
                try {
                    return callerClassGetter.invoke();
                } catch (Throwable ex) {
                    InternalLogger.error(ex, "Failed to get the caller class name");
                    return null;
                }
            }
        }

    }

    /**
     * Suppler that returns always {@code null}.
     */
    private static final class NullSupplier implements Supplier<Object> {

        /** */
        private NullSupplier() {
        }

        @Override
        public Object get() {
            return null;
        }

    }

    /**
     * Function for returning the stack trace element of the caller of the passed fully-qualified class name.
     */
    private static final class StackTraceElementFunction implements Function<String, Object> {

        private static final MethodHandle fillStackTraceElements;

        static {
            fillStackTraceElements = new AndroidStackTraceAccess().getStackTraceElementsFiller();
            if (fillStackTraceElements == null) {
                InternalLogger.debug(
                    null,
                    "Legacy dalvik.system.VMStack.fillStackTraceElements() is not available"
                );
            }
        }

        /** */
        private StackTraceElementFunction() {
        }

        @Override
        public Object apply(String className) {
            if (fillStackTraceElements != null) {
                StackTraceElement[] trace = new StackTraceElement[MAX_STACK_TRACE_SIZE_METHOD_HANDLE];

                try {
                    fillStackTraceElements.invoke(Thread.currentThread(), trace);
                } catch (Throwable ex) {
                    InternalLogger.error(ex, "Failed to get the stack trace element of the caller");
                }

                int index = findStackTraceElement(trace, className);
                if (index < trace.length) {
                    StackTraceElement element = trace[index];
                    if (element == null) {
                        InternalLogger.warn(
                            null,
                            "Class \"{}\" is expected in the stack trace for caller extraction but is actually missing",
                            className
                        );
                    }
                    return element;
                }
            }

            StackTraceElement[] trace = new Throwable().getStackTrace();
            int index = findStackTraceElement(trace, className);

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
         * Finds the stace trace element of the caller of the passed fully-qualified class name.
         *
         * @param trace The stack trace to search for the caller
         * @param className The fully-qualified class name
         * @return The index of the stace trace element of the caller
         */
        private int findStackTraceElement(StackTraceElement[] trace, String className) {
            int index = 0;

            while (index < trace.length && trace[index] != null && !className.equals(trace[index].getClassName())) {
                ++index;
            }

            while (index < trace.length && trace[index] != null && className.equals(trace[index].getClassName())) {
                ++index;
            }

            return index;
        }

    }

    /**
     * Function for returning {@code null} for every passed class name.
     */
    private static final class NullFunction implements Function<String, Object> {

        /** */
        private NullFunction() {
        }

        @Override
        public Object apply(String className) {
            return null;
        }

    }

}
