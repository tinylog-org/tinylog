package org.tinylog.core.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.context.BundleContextStorage;
import org.tinylog.core.context.ContextStorage;

/**
 * Wrapper for bundling multiple {@link LoggingBackend} instances.
 */
public class BundleLoggingBackend implements LoggingBackend {

    private static final LevelVisibility INVISIBLE = new LevelVisibility(OutputDetails.DISABLED);

    private final ContextStorage contextStorage;
    private final List<LoggingBackend> backends;

    /**
     * @param backends The logging backends to combine
     */
    public BundleLoggingBackend(Collection<LoggingBackend> backends) {
        List<ContextStorage> storages = backends.stream()
            .map(LoggingBackend::getContextStorage)
            .collect(Collectors.toList());

        this.contextStorage = new BundleContextStorage(storages);
        this.backends = new ArrayList<>(backends);
    }

    @Override
    public ContextStorage getContextStorage() {
        return contextStorage;
    }

    @Override
    public LevelVisibility getLevelVisibilityByClass(String className) {
        return backends.stream()
            .map(backend -> backend.getLevelVisibilityByClass(className))
            .reduce((first, second) -> new LevelVisibility(
                max(first.getTrace(), second.getTrace()),
                max(first.getDebug(), second.getDebug()),
                max(first.getInfo(), second.getInfo()),
                max(first.getWarn(), second.getWarn()),
                max(first.getError(), second.getError())
            ))
            .orElse(INVISIBLE);
    }

    @Override
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        return backends.stream()
            .map(backend -> backend.getLevelVisibilityByTag(tag))
            .reduce((first, second) -> new LevelVisibility(
                max(first.getTrace(), second.getTrace()),
                max(first.getDebug(), second.getDebug()),
                max(first.getInfo(), second.getInfo()),
                max(first.getWarn(), second.getWarn()),
                max(first.getError(), second.getError())
            ))
            .orElse(INVISIBLE);
    }

    @Override
    public boolean isEnabled(Object location, String tag, Level level) {
        for (LoggingBackend backend : backends) {
            if (backend.isEnabled(location, tag, level)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void output(LogEntry entry, boolean last) {
        for (LoggingBackend backend : backends) {
            backend.output(entry, last);
        }
    }

    @Override
    public void close() {
        for (LoggingBackend backend : backends) {
            backend.close();
        }
    }

    /**
     * Gets the most detailed output details of two candidates.
     *
     * @param first The first output details candidate
     * @param second The second output details candidate
     * @return The most detailed output details of both candidates
     */
    private OutputDetails max(OutputDetails first, OutputDetails second) {
        return first.ordinal() >= second.ordinal() ? first : second;
    }

}
