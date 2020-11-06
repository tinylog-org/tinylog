package org.tinylog.core.test;

import java.util.ArrayList;
import java.util.List;

import org.tinylog.core.Level;

/**
 * Storage for {@link LogEntry LogEntries}.
 */
public class Log {

	private Level minLevel;
	private List<LogEntry> entries;

	/** */
	public Log() {
		minLevel = Level.INFO;
		entries = new ArrayList<>();
	}

	/**
	 * Gets the new minimum severity level. All log entries with a severity level less severe than the minimum level are
	 * ignored.
	 *
	 * @return The actual configured minimum severity level
	 */
	public Level getMinLevel() {
		return minLevel;
	}

	/**
	 * Sets a new minimum severity level. All log entries with a severity level less severe than the minimum level are
	 * ignored.
	 *
	 * @param level New minimum severity level
	 */
	public void setMinLevel(Level level) {
		this.minLevel = level;
	}

	/**
	 * Appends a new log entry to the end of this log.
	 *
	 * @param entry Log entry to append to this log
	 */
	public void add(LogEntry entry) {
		if (entry.getLevel().ordinal() <= minLevel.ordinal()) {
			entries.add(entry);
		}
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

}
