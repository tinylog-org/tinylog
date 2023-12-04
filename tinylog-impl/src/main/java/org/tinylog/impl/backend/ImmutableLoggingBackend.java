package org.tinylog.impl.backend;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.WritingThread;
import org.tinylog.impl.context.ThreadLocalContextStorage;
import org.tinylog.impl.writers.Writer;

/**
 * Native logging backend for tinylog.
 */
public class ImmutableLoggingBackend implements LoggingBackend {

    private final LoggingContext context;
    private final ContextStorage contextStorage;
    private final LoggingConfiguration configuration;
    private final WritingThread writingThread;

    /**
     * @param context The current logging context
     * @param configuration All configured writers mapped to severity levels and tags
     * @param writingThread The writing thread for enqueuing log entries for writers
     */
    public ImmutableLoggingBackend(
        LoggingContext context,
        LoggingConfiguration configuration,
        WritingThread writingThread
    ) {
        this.context = context;
        this.contextStorage = new ThreadLocalContextStorage();
        this.configuration = configuration;
        this.writingThread = writingThread;

        context.getFramework().registerHook(new LifeCycleHook(configuration.getAllWriters(), writingThread));
    }

    @Override
    public ContextStorage getContextStorage() {
        return contextStorage;
    }

    @Override
    public LevelVisibility getLevelVisibilityByClass(String className) {
        LevelConfiguration levelConfiguration = getLevelConfiguration(className);

        return new LevelVisibility(
            getOutputDetails(levelConfiguration, Level.TRACE),
            getOutputDetails(levelConfiguration, Level.DEBUG),
            getOutputDetails(levelConfiguration, Level.INFO),
            getOutputDetails(levelConfiguration, Level.WARN),
            getOutputDetails(levelConfiguration, Level.ERROR)
        );
    }

    @Override
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        if (tag == null) {
            tag = LevelConfiguration.UNTAGGED_PLACEHOLDER;
        }

        return new LevelVisibility(
            getOutputDetails(tag, Level.TRACE),
            getOutputDetails(tag, Level.DEBUG),
            getOutputDetails(tag, Level.INFO),
            getOutputDetails(tag, Level.WARN),
            getOutputDetails(tag, Level.ERROR)
        );
    }

    @Override
    public boolean isEnabled(Object location, String tag, Level level) {
        if (tag == null) {
            tag = LevelConfiguration.UNTAGGED_PLACEHOLDER;
        }

        Level effectiveLevel = getLevelConfiguration(location).getLevel(tag);
        return level.isAtLeastAsSevereAs(effectiveLevel);
    }

    @Override
    public void log(Object location, String tag, Level level, Throwable throwable, Object message, Object[] arguments,
            MessageFormatter formatter) {
        String internalTag = tag == null ? LevelConfiguration.UNTAGGED_PLACEHOLDER : tag;
        Level effectiveLevel = getLevelConfiguration(location).getLevel(internalTag);

        if (level.isAtLeastAsSevereAs(effectiveLevel)) {
            WriterRepository repository = configuration.getWriters(internalTag, level);

            LogEntry logEntry = createLogEntry(
                location,
                tag,
                level,
                throwable,
                message,
                arguments,
                formatter,
                repository.getRequiredLogEntryValues()
            );

            for (Writer writer : repository.getWriters()) {
                writingThread.enqueue(writer, logEntry);
            }
        }
    }

    @Override
    public LoggingBackend reconfigure() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the configured output details for the passed tag and severity level.
     *
     * @param tag The category tag
     * @param level The severity level
     * @return The configured output details
     */
    private OutputDetails getOutputDetails(String tag, Level level) {
        WriterRepository repository = configuration.getWriters(tag, level);
        if (repository.getWriters().isEmpty()) {
            return OutputDetails.DISABLED;
        }

        Set<LogEntryValue> values = repository.getRequiredLogEntryValues();
        if (values.contains(LogEntryValue.FILE) || values.contains(LogEntryValue.METHOD)
            || values.contains(LogEntryValue.LINE)) {
            return OutputDetails.ENABLED_WITH_FULL_LOCATION_INFORMATION;
        } else if (values.contains(LogEntryValue.CLASS)) {
            return OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME;
        } else {
            return OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION;
        }
    }

    /**
     * Gets the configured output details for the passed level configuration and severity level.
     *
     * @param configuration The level configuration
     * @param level The severity level
     * @return The configured output details
     */
    private OutputDetails getOutputDetails(LevelConfiguration configuration, Level level) {
        if (level.isAtLeastAsSevereAs(configuration.getLeastSevereLevel())) {
            Set<String> tags = new HashSet<>(configuration.getTags());
            tags.add(LevelConfiguration.UNTAGGED_PLACEHOLDER);
            tags.add(LevelConfiguration.TAGGED_PLACEHOLDER);

            return tags.stream()
                .filter(tag -> level.isAtLeastAsSevereAs(configuration.getLevel(tag)))
                .map(tag -> getOutputDetails(tag, level))
                .max(OutputDetails::compareTo)
                .orElse(OutputDetails.DISABLED);
        } else {
            return OutputDetails.DISABLED;
        }
    }

    /**
     * Gets the assigned level configuration for the passed stack trace location.
     *
     * <p>
     *     The level configuration can depend on the actual package or class name.
     * </p>
     *
     * @param location The location information of the caller
     * @return The assigned level configuration
     */
    private LevelConfiguration getLevelConfiguration(Object location) {
        Map<String, LevelConfiguration> severityLevels = configuration.getSeverityLevels();
        if (severityLevels.size() == 1) {
            return severityLevels.get("");
        } else {
            String packageOrClass = LocationInfo.resolveClassName(location);
            while (true) {
                LevelConfiguration levelConfiguration = severityLevels.get(packageOrClass);
                if (levelConfiguration == null) {
                    packageOrClass = reducePackageOrClass(packageOrClass);
                } else {
                    return levelConfiguration;
                }
            }
        }
    }

    /**
     * Removes the last segment of a package or class name.
     *
     * <p>
     *     For example, "com.example" will be returned for "com.example.foo" or "com.example.Foo" will be returned for
     *     "com.example.Foo$Bar".
     * </p>
     *
     * @param packageOrClass The package or class name to reduce
     * @return The passed package or class name without its last segment
     */
    private static String reducePackageOrClass(String packageOrClass) {
        int index = packageOrClass.length();

        while (index-- > 0) {
            char character = packageOrClass.charAt(index);
            if (character == '.' || character == '$') {
                return packageOrClass.substring(0, index);
            }
        }

        return "";
    }

    /**
     * Creates a log entry.
     *
     * @param location The location information of the caller
     * @param tag The assigned tag
     * @param level The severity level
     * @param throwable The logged exception or any other kind of throwable
     * @param message The logged text message
     * @param arguments The argument values for all placeholders in the text message
     * @param formatter The message formatter for replacing placeholder with the provided arguments
     * @param logEntryValues Only log entry values in this set have to be filled with real data
     * @return The created log entry
     */
    private LogEntry createLogEntry(Object location, String tag, Level level, Throwable throwable, Object message,
            Object[] arguments, MessageFormatter formatter, Set<LogEntryValue> logEntryValues) {
        StackTraceElement stackTraceElement = LocationInfo.resolveStackTraceElement(location);

        return new LogEntry(
            logEntryValues.contains(LogEntryValue.TIMESTAMP) ? Instant.now() : null,
            logEntryValues.contains(LogEntryValue.UPTIME) ? context.getFramework().getRuntime().getUptime() : null,
            logEntryValues.contains(LogEntryValue.THREAD) ? Thread.currentThread() : null,
            contextStorage.getMapping(),
            stackTraceElement.getClassName(),
            stackTraceElement.getMethodName(),
            stackTraceElement.getFileName(),
            stackTraceElement.getLineNumber(),
            tag,
            level,
            formatter == null || arguments == null
                ? message == null ? null : message.toString()
                : formatter.format(context, (String) message, arguments),
            throwable
        );
    }

}