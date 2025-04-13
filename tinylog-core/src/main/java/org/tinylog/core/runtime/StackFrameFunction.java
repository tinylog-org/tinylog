package org.tinylog.core.runtime;

import java.lang.StackWalker.StackFrame;
import java.util.function.Function;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;

/**
 * Function for receiving location information of the caller of the class with the passed fully-qualified class name
 * on modern Java and Android runtimes that support {@link StackWalker}.
 */
@IgnoreJRERequirement
class StackFrameFunction implements Function<String, Object> {

    private final InternalLogger logger;
    private final Function<StackFrame, Object> mapper;
    private final int stackTraceSkipCount;
    private final StackWalker stackWalker;

    /**
     * @param logger The internal logger instance for issuing internal tinylog log entries
     * @param mapper The function for mapping a stack frame into the desired type
     * @param stackTraceSkipCount The number of stack trace elements that can be skipped
     */
    StackFrameFunction(InternalLogger logger, Function<StackFrame, Object> mapper, int stackTraceSkipCount) {
        this.logger = logger;
        this.mapper = mapper;
        this.stackTraceSkipCount = stackTraceSkipCount;
        this.stackWalker = StackWalker.getInstance();
    }

    @Override
    public Object apply(String className) {
        return stackWalker.walk(stream -> stream
            .skip(stackTraceSkipCount)
            .dropWhile(frame -> !className.equals(frame.getClassName()))
            .dropWhile(frame -> className.equals(frame.getClassName()))
            .findFirst()
        ).map(mapper).orElseGet(() -> {
            logger.log(
                Level.WARN,
                "Class \"{}\" is expected to be in the stack trace but is actually missing",
                className
            );
            return null;
        });
    }

}
