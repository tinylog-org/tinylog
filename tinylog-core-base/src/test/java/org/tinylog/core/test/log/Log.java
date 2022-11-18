package org.tinylog.core.test.log;

import java.util.ArrayList;
import java.util.List;

import org.tinylog.core.Level;

/**
 * Storage for {@link LogEntry log entries}.
 */
public class Log {

    private Level level;
    private List<LogEntry> entries;

    /** */
    public Log() {
        level = Level.INFO;
        entries = new ArrayList<>();
    }

    /**
     * Gets the actual severity level. All log entries with a severity level less severe than this severity level are
     * ignored.
     *
     * @return The actual severity level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Sets a new severity level. All log entries with a severity level less severe than this severity level are
     * ignored.
     *
     * @param level The new severity level
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * Retrieves all stored log entries and clears the entire log afterwards.
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
        if (entry.getLevel().isAtLeastAsSevereAs(level)) {
            entries.add(entry);
        }
    }

}
