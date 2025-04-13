package org.tinylog.core.backend;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;

/**
 * A no operation implementation of {@link LoggingBackend}. All issued log entries are silently ignored.
 */
public class NopLoggingBackend implements LoggingBackend {

    private static final LevelVisibility VISIBILITY = new LevelVisibility(OutputDetails.DISABLED);

    private final ContextStorage contextStorage;

    /** */
    public NopLoggingBackend() {
        this.contextStorage = new NopContextStorage();
    }

    @Override
    public ContextStorage getContextStorage() {
        return contextStorage;
    }

    @Override
    public LevelVisibility getLevelVisibilityByClass(String className) {
        return VISIBILITY;
    }

    @Override
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        return VISIBILITY;
    }

    @Override
    public boolean isEnabled(Object location, String tag, Level level) {
        return false;
    }

    @Override
    public void output(LogEntry entry, boolean last) {
        // Ignore
    }

    @Override
    public void close() {
        // Ignore
    }

}
