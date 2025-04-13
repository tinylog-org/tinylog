package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the source method name for a log entry.
 */
public class MethodPlaceholder implements Placeholder {

    /** */
    public MethodPlaceholder() {
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        return entry.getMethodName();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String methodName = entry.getMethodName();
        builder.append(methodName == null ? "<method unknown>" : methodName);
    }

}
