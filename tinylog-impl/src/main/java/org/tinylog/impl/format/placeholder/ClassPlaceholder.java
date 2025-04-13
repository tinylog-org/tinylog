package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the fully-qualified class name for a log entry.
 */
public class ClassPlaceholder implements Placeholder {

    /** */
    public ClassPlaceholder() {
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        return entry.getClassName();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String className = entry.getClassName();
        builder.append(className == null ? "<class unknown>" : className);
    }

}
