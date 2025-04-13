package org.tinylog.core.runtime;

import java.util.function.Function;
import java.util.function.Supplier;

import org.tinylog.core.Level;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.internal.InternalLogger;

/**
 * Runtime implementation for Android 13 and older.
 */
public class LegacyAndroidRuntime extends AbstractAndroidRuntime {

    private static final int DIRECT_STACK_TRACE_DEPTH = 3;
    private static final int RELATIVE_STACK_TRACE_SIZE = 8;

    private final Supplier<Object> stackTraceElementSupplier;
    private final Supplier<Object> callerClassSupplier;
    private final Supplier<Object> nullSupplier;

    private final Function<String, Object> stackTraceElementFunction;
    private final Function<String, Object> nullFunction;

    /**
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public LegacyAndroidRuntime(InternalLogger logger) {
        super(logger);

        this.stackTraceElementSupplier = new VmStackTraceElementSupplier(DIRECT_STACK_TRACE_DEPTH);
        this.callerClassSupplier = new VmStackClassSupplier();
        this.nullSupplier = new NullSupplier();

        this.stackTraceElementFunction = new VmStackTraceElementFunction(
            logger,
            RELATIVE_STACK_TRACE_SIZE,
            DIRECT_STACK_TRACE_DEPTH - 1
        );
        this.nullFunction = new NullFunction();

        logger.log(Level.DEBUG, "Created legacy Android runtime based on dalvik.system.VMStack");
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
            case ENABLED_WITH_CALLER_CLASS_NAME:
                return stackTraceElementFunction;

            default:
                return nullFunction;
        }
    }

}
