package org.tinylog.impl.writer.console;

import java.io.PrintStream;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.writer.Writer;

/**
 * Writer that outputs formatted log entries to {@link System#out} and {@link System#err} respectively.
 */
public class ConsoleWriter implements Writer {

    private static final int BUILDER_CAPACITY = 1024;

    private final OutputFormat format;
    private final int threshold;

    /**
     * @param format The output format for log entries
     * @param threshold Log entries with a severity less than this threshold are output to {@link System#out}. Log
     *                  entries with a severity greater than or equal to this threshold are output to
     *                  {@link System#err}.
     */
    public ConsoleWriter(OutputFormat format, Level threshold) {
        this.format = format;
        this.threshold = threshold.ordinal();
    }

    @Override
    public OutputDetails getOutputDetails() {
        return format.getOutputDetails();
    }

    @Override
    public void log(LogEntry entry) throws Exception {
        StringBuilder builder = new StringBuilder(BUILDER_CAPACITY);
        format.render(builder, entry);

        PrintStream stream = entry.getSeverityLevel().ordinal() <= threshold ? System.err : System.out;
        stream.print(builder);
    }

    @Override
    public void flush() {
        // Ignore
    }

    @Override
    public void close() {
        // Ignore
    }

}
