package org.tinylog.core.runtime;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
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

    private static final int STACK_TRACE_DEPTH = 2;

    private final RuntimeMXBean runtimeBean;
    private final StackWalker defaultStackWalker;
    private final StackWalker retainClassStackWalker;

    /** */
    public JavaRuntime() {
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        defaultStackWalker = StackWalker.getInstance();
        retainClassStackWalker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
    }

    @Override
    public long getProcessId() {
        return ProcessHandle.current().pid();
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
                return () -> defaultStackWalker
                    .walk(stream -> stream.skip(STACK_TRACE_DEPTH).findFirst())
                    .map(StackFrame::toStackTraceElement)
                    .orElse(null);

            case ENABLED_WITH_CALLER_CLASS_NAME:
                return retainClassStackWalker::getCallerClass;

            default:
                return () -> null;
        }
    }

    @Override
    public Function<String, Object> getRelativeCaller(OutputDetails outputDetails) {
        switch (outputDetails) {
            case ENABLED_WITH_FULL_LOCATION_INFORMATION:
                return createCallerFunction(StackFrame::toStackTraceElement);

            case ENABLED_WITH_CALLER_CLASS_NAME:
                return createCallerFunction(StackFrame::getClassName);

            default:
                return className -> null;
        }
    }

    /**
     * Creates the function for extracting specific location information that of the relative caller. The relative
     * caller is the caller of the passed fully-qualified class name.
     *
     * @param mapper Mapping function that converts a {@link StackFrame} into the desired return type
     * @param <T> The desired return type with the expected location information
     * @return The function for resolving the caller of the class having the passed fully-qualified class name
     */
    private <T> Function<String, T> createCallerFunction(Function<StackFrame, T> mapper) {
        return className -> defaultStackWalker.walk(stream -> stream
            .skip(1)
            .dropWhile(frame -> !className.equals(frame.getClassName()))
            .dropWhile(frame -> className.equals(frame.getClassName()))
            .findFirst()
        ).map(mapper).orElseGet(() -> {
            InternalLogger.warn(
                null,
                "Class \"{}\" is expected in the stack trace for caller extraction but is actually missing",
                className
            );
            return null;
        });
    }

}
