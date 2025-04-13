package org.tinylog.core.runtime;

import java.lang.StackWalker.StackFrame;
import java.util.function.Function;
import java.util.function.Supplier;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.tinylog.core.Level;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;

/**
 * Runtime implementation for Android 14 and later.
 */
@IgnoreJRERequirement
public class ModernAndroidRuntime extends AbstractAndroidRuntime {

    private static final int STACK_TRACE_DEPTH = 2;

    private final Supplier<Object> stackTraceElementSupplier;
    private final Supplier<Object> callerClassSupplier;
    private final Supplier<Object> nullSupplier;

    private final Function<String, Object> stackTraceElementFunction;
    private final Function<String, Object> callerClassFunction;
    private final Function<String, Object> nullFunction;

    /**
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public ModernAndroidRuntime(InternalLogger logger) {
        super(logger);

        this.stackTraceElementSupplier = new StackFrameSupplier(
            StackFrame::toStackTraceElement,
            STACK_TRACE_DEPTH
        );
        this.callerClassSupplier = new StackFrameSupplier(
            StackFrame::getClassName,
            STACK_TRACE_DEPTH
        );
        this.nullSupplier = new NullSupplier();

        this.stackTraceElementFunction = new StackFrameFunction(
            logger,
            StackFrame::toStackTraceElement,
            STACK_TRACE_DEPTH - 1
        );
        this.callerClassFunction = new StackFrameFunction(
            logger,
            StackFrame::getClassName,
            STACK_TRACE_DEPTH - 1
        );
        this.nullFunction = new NullFunction();

        logger.log(Level.DEBUG, "Created modern Android runtime based on java.lang.StackWalker");
    }

    @Override
    public Supplier<Object> getDirectCaller(OutputDetails outputDetails) {
        switch (outputDetails) {
            case ENABLED_WITH_FULL_LOCATION_INFO:
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
            case ENABLED_WITH_FULL_LOCATION_INFO:
                return stackTraceElementFunction;

            case ENABLED_WITH_CALLER_CLASS_NAME:
                return callerClassFunction;

            default:
                return nullFunction;
        }
    }

}
