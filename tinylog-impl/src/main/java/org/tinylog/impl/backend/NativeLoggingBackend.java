package org.tinylog.impl.backend;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.WritingThread;
import org.tinylog.impl.context.ThreadLocalContextStorage;
import org.tinylog.impl.writers.AsyncWriter;
import org.tinylog.impl.writers.Writer;

/**
 * Native logging backend for tinylog.
 */
public class NativeLoggingBackend implements LoggingBackend {

	private static final StackTraceElement EMPTY_STACK_TRACE_ELEMENT = new StackTraceElement(
		"<unknown>",
		"<unknown>",
		null,
		-1
	);

	private final Framework framework;
	private final ContextStorage contextStorage;
	private final LoggingConfiguration configuration;
	private final WritingThread writingThread;

	/**
	 * @param framework The actual framework instance
	 * @param configuration All configured writers mapped to severity levels and tags
	 * @param writingThread The writing thread for enqueuing log entries for async writers (can be {@code null} if there
	 *                      are no async writers)
	 */
	public NativeLoggingBackend(Framework framework, LoggingConfiguration configuration, WritingThread writingThread) {
		this.framework = framework;
		this.contextStorage = new ThreadLocalContextStorage();
		this.configuration = configuration;
		this.writingThread = writingThread;

		framework.registerHook(new LifeCycleHook(configuration.getAllWriters(), writingThread));
	}

	@Override
	public ContextStorage getContextStorage() {
		return contextStorage;
	}

	@Override
	public LevelVisibility getLevelVisibility(String tag) {
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

			for (Writer writer : repository.getSyncWriters()) {
				try {
					writer.log(logEntry);
				} catch (Exception ex) {
					if (!Objects.equals(InternalLogger.TAG, tag)) {
						InternalLogger.error(ex, "Failed to write log entry");
					}
				}
			}

			for (AsyncWriter writer : repository.getAsyncWriters()) {
				writingThread.enqueue(writer, logEntry);
			}
		}
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
		if (repository.getAllWriters().isEmpty()) {
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
		String caller;
		if (location instanceof StackTraceElement) {
			caller = ((StackTraceElement) location).getClassName();
		} else if (location instanceof Class) {
			caller = ((Class<?>) location).getName();
		} else if (location instanceof String) {
			caller = (String) location;
		} else {
			caller = "";
		}

		Map<String, LevelConfiguration> severityLevels = configuration.getSeverityLevels();
		if (severityLevels.size() == 1) {
			return severityLevels.get("");
		} else {
			String packageOrClass = caller;
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
		StackTraceElement stackTraceElement;
		if (location instanceof StackTraceElement) {
			stackTraceElement = (StackTraceElement) location;
		} else if (location instanceof Class) {
			stackTraceElement = new StackTraceElement(
				((Class<?>) location).getName(),
				EMPTY_STACK_TRACE_ELEMENT.getMethodName(),
				EMPTY_STACK_TRACE_ELEMENT.getMethodName(),
				EMPTY_STACK_TRACE_ELEMENT.getLineNumber()
			);
		} else if (location instanceof String) {
			stackTraceElement = new StackTraceElement(
				(String) location,
				EMPTY_STACK_TRACE_ELEMENT.getMethodName(),
				EMPTY_STACK_TRACE_ELEMENT.getMethodName(),
				EMPTY_STACK_TRACE_ELEMENT.getLineNumber()
			);
		} else {
			stackTraceElement = EMPTY_STACK_TRACE_ELEMENT;
		}

		return new LogEntry(
			logEntryValues.contains(LogEntryValue.TIMESTAMP) ? Instant.now() : null,
			logEntryValues.contains(LogEntryValue.UPTIME) ? framework.getRuntime().getUptime() : null,
			logEntryValues.contains(LogEntryValue.THREAD) ? Thread.currentThread() : null,
			contextStorage.getMapping(),
			stackTraceElement.getClassName(),
			stackTraceElement.getMethodName(),
			stackTraceElement.getFileName(),
			stackTraceElement.getLineNumber(),
			tag,
			level,
			formatter == null
				? message == null ? null : message.toString()
				: formatter.format((String) message, arguments),
			throwable
		);
	}

}
