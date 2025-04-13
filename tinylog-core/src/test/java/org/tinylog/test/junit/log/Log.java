package org.tinylog.test.junit.log;

import java.util.ArrayList;
import java.util.List;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;

/**
 * Storage for {@link LogEntry log entries}.
 */
public class Log {

    private final Level minLevel;
    private List<LogEntry> entries;

    /**
     * @param minLevel The minimum severity level of log entries to store
     */
    public Log(Level minLevel) {
        this.minLevel = minLevel;
        this.entries = new ArrayList<>();
    }

    /**
     * Retrieves all stored log entries and clears the entire log afterward.
     *
     * @return All store log entries
     */
    public Iterable<LogEntry> consume() {
        try {
            return entries;
        } finally {
            entries = new ArrayList<>();
        }
    }

    /**
     * Appends a new log entry to the end of this log.
     *
     * @param entry Log entry to append to this log
     */
    void add(LogEntry entry) {
        if (entry.getSeverityLevel().isAtLeastAsSevereAs(minLevel)) {
            entries.add(entry);
        }
    }

}
