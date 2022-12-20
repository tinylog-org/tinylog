package org.tinylog.core.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.tinylog.core.Level;
import org.tinylog.core.context.BundleContextStorage;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.MessageFormatter;

/**
 * Wrapper for bundling multiple {@link LoggingBackend} instances.
 */
public class BundleLoggingBackend implements LoggingBackend {

    private static final LevelVisibility INVISIBLE = new LevelVisibility(
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.DISABLED,
        OutputDetails.DISABLED
    );

    private final List<LoggingBackend> backends;
    private final ContextStorage storage;

    /**
     * @param backends Logging backends to combine
     */
    public BundleLoggingBackend(List<LoggingBackend> backends) {
        List<ContextStorage> storages = backends.stream()
            .map(LoggingBackend::getContextStorage)
            .collect(Collectors.toList());

        this.storage = new BundleContextStorage(storages);
        this.backends = new ArrayList<>(backends);
    }

    /**
     * Gets all wrapped child logging backends.
     *
     * @return The wrapped child logging backends
     */
    public List<LoggingBackend> getChildren() {
        return Collections.unmodifiableList(backends);
    }

    @Override
    public ContextStorage getContextStorage() {
        return storage;
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
    public void log(Object location, String tag, Level level, Throwable throwable, Object message, Object[] arguments,
            MessageFormatter formatter) {
        for (LoggingBackend backend : backends) {
            backend.log(location, tag, level, throwable, message, arguments, formatter);
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
