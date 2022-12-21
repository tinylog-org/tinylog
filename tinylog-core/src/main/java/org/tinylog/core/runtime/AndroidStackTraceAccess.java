package org.tinylog.core.runtime;

import java.lang.invoke.MethodHandle;

/**
 * Utility class for resolving legacy Android methods for receiving specific elements from the stack trace.
 */
class AndroidStackTraceAccess extends BaseStackTraceAccess {

    private static final int STACK_TRACE_DEPTH = 4;

    /** */
    AndroidStackTraceAccess() {
    }

    /**
     * Creates a method handle for {@code dalvik.system.VMStack.getStackClass2()}.
     *
     * @return Valid getter instance if the method is available, otherwise {@code null}
     */
    MethodHandle getCallerClassGetter() {
        FailableCheck<MethodHandle> check = handle -> {
            Class<?> caller = (Class<?>) handle.invoke();
            return BaseStackTraceAccess.class.equals(caller);
        };

        return getMethod(check, "dalvik.system.VMStack", "getStackClass2");
    }

    /**
     * Creates a method handle for {@code dalvik.system.VMStack.fillStackTraceElements(Thread, StackTraceElement[])}.
     *
     * @return Valid filler instance if the method is available, otherwise {@code null}
     */
    MethodHandle getStackTraceElementsFiller() {
        FailableCheck<MethodHandle> check = handle -> {
            StackTraceElement[] trace = new StackTraceElement[STACK_TRACE_DEPTH + 1];
            handle.invoke(Thread.currentThread(), trace);
            StackTraceElement element = trace[STACK_TRACE_DEPTH];
            return element.getClassName().startsWith(AndroidStackTraceAccess.class.getName())
                && element.getMethodName().contains("getStackTraceElementsFiller");
        };

        return getMethod(check, "dalvik.system.VMStack", "fillStackTraceElements", Thread.class,
            StackTraceElement[].class);
    }

}
