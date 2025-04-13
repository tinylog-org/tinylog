package org.tinylog.core.runtime;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.function.Supplier;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * Supplier for receiving location information of the caller on modern Java and Android runtimes that support
 * {@link StackWalker}.
 */
@IgnoreJRERequirement
class StackFrameSupplier implements Supplier<Object> {

    private final Function<StackWalker.StackFrame, Object> mapper;
    private final int stackTraceDepth;
    private final StackWalker stackWalker;

    /**
     * @param mapper The function for mapping a stack frame into the desired type
     * @param stackTraceDepth The index of the stack frame that contains the location information of the caller
     */
    StackFrameSupplier(Function<StackFrame, Object> mapper, int stackTraceDepth) {
        this.mapper = mapper;
        this.stackTraceDepth = stackTraceDepth;
        this.stackWalker = StackWalker.getInstance(EnumSet.noneOf(Option.class), stackTraceDepth + 1);
    }

    @Override
    public Object get() {
        return stackWalker
            .walk(stream -> stream.skip(stackTraceDepth).findFirst())
            .map(mapper)
            .orElse(null);
    }

}
