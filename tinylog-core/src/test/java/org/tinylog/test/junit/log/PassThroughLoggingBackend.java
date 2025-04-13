package org.tinylog.test.junit.log;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;

/**
 * Logging backend implementation that adds output log entries to a {@link Log} instance.
 */
class PassThroughLoggingBackend implements LoggingBackend {

    private final Log log;

    /**
     * @param log The log to which output log entries should be added
     */
    PassThroughLoggingBackend(Log log) {
        this.log = log;
    }

    @Override
    public ContextStorage getContextStorage() {
        return new NopContextStorage();
    }

    @Override
    public LevelVisibility getLevelVisibilityByClass(String className) {
        return new LevelVisibility(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
    }

    @Override
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        return new LevelVisibility(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
    }

    @Override
    public boolean isEnabled(Object location, String tag, Level level) {
        return true;
    }

    @Override
    public void output(LogEntry entry, boolean last) {
        log.add(entry);
    }

    @Override
    public void close() {
        // Nothing to do
    }

}
