package org.tinylog.impl.test;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.OutputFormat;

/**
 * Renderer for {@link OutputFormat} implementations.
 */
public class FormatOutputRenderer {

    private final OutputFormat format;

    /**
     * @param format The output format to render
     */
    public FormatOutputRenderer(OutputFormat format) {
        this.format = format;
    }

    /**
     * Renders the stored output format with the passed log entry as input.
     *
     * @param logEntry The log entry as input for {@link OutputFormat#render(StringBuilder, LogEntry)}
     * @return The render result
     */
    public String render(LogEntry logEntry) {
        StringBuilder builder = new StringBuilder();
        format.render(builder, logEntry);
        return builder.toString();
    }

}
