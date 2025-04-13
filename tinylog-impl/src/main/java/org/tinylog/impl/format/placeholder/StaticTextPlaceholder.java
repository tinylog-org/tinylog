package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Wrapper for outputting plain static text.
 */
public class StaticTextPlaceholder implements Placeholder {

    private final String text;

    /**
     * @param text Plain text to output
     */
    public StaticTextPlaceholder(String text) {
        this.text = text;
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITHOUT_LOCATION_INFO;
    }

    @Override
    public String getValue(LogEntry entry) {
        return text;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        builder.append(text);
    }

}
