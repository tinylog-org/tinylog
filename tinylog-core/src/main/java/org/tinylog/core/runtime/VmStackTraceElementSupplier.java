package org.tinylog.core.runtime;

import java.util.function.Supplier;

import dalvik.system.VMStack;

/**
 * Supplier for receiving the stack trace element of the caller on legacy Android runtimes that support {@link VMStack}.
 */
class VmStackTraceElementSupplier implements Supplier<Object> {

    private final int stackTraceDepth;

    /**
     * @param stackTraceDepth The index of the stack trace element that contains the location information of the caller
     */
    VmStackTraceElementSupplier(int stackTraceDepth) {
        this.stackTraceDepth = stackTraceDepth;
    }

    @Override
    public Object get() {
        StackTraceElement[] trace = new StackTraceElement[stackTraceDepth + 1];
        VMStack.fillStackTraceElements(Thread.currentThread(), trace);
        return trace[stackTraceDepth];
    }

}
