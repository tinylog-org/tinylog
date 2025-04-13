package org.tinylog.core.runtime;

import java.util.function.Function;

import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;

import dalvik.system.VMStack;

/**
 * Function for receiving the stack trace element of the caller of the class with the passed fully-qualified class name
 * on legacy Android runtimes that support {@link VMStack}.
 */
class VmStackTraceElementFunction implements Function<String, Object> {

    private final InternalLogger logger;
    private final int stackTraceSize;
    private final int stackTraceSkipCount;

    /**
     * @param logger The internal logger instance for issuing internal tinylog log entries
     * @param stackTraceSize The number of stack trace elements to load
     * @param stackTraceSkipCount The number of stack trace elements that can be skipped
     */
    VmStackTraceElementFunction(InternalLogger logger, int stackTraceSize, int stackTraceSkipCount) {
        this.logger = logger;
        this.stackTraceSize = stackTraceSize;
        this.stackTraceSkipCount = stackTraceSkipCount;
    }

    @Override
    public Object apply(String className) {
        StackTraceElement[] trace = new StackTraceElement[stackTraceSize];
        VMStack.fillStackTraceElements(Thread.currentThread(), trace);
        return findStackTraceElement(trace, className);
    }

    /**
     * Finds the first stace trace element of the caller of the class with passed fully-qualified class name.
     *
     * @param trace The stack trace in which to search for the caller
     * @param className The fully-qualified class name
     * @return The stace trace element of the caller if found, otherwise {@code null}
     */
    private StackTraceElement findStackTraceElement(StackTraceElement[] trace, String className) {
        int index = stackTraceSkipCount;

        while (index < trace.length && trace[index] != null && !className.equals(trace[index].getClassName())) {
            ++index;
        }

        while (index < trace.length && trace[index] != null && className.equals(trace[index].getClassName())) {
            ++index;
        }

        if (index < trace.length) {
            return trace[index];
        } else {
            logger.log(
                Level.WARN,
                "Class \"{}\" is expected to be in the stack trace but is actually missing",
                className
            );
            return null;
        }
    }

}
