/*
 * Copyright 2017 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.core;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinylog.Level;
import org.tinylog.provider.ContextProvider;
import org.tinylog.provider.InternalLogger;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.writers.Writer;

/**
 * tinylog's native logging provider implementation.
 */
public class TinylogLoggingProvider implements LoggingProvider {

	private final TinylogContextProvider context;
	private final MessageFormatter formatter;
	private final Level globalLevel;
	private final Map<String, Level> customLevels;
	private final List<String> knownTags;
	private final Collection<Writer>[][] writers;
	private final Collection<LogEntryValue>[][] requiredLogEntryValues;
	private final BitSet fullStackTraceRequired;
	private final WritingThread writingThread;

	/** */
	public TinylogLoggingProvider() {
		context = new TinylogContextProvider();
		formatter = new MessageFormatter(ConfigurationParser.getLocale());
		globalLevel = ConfigurationParser.getGlobalLevel();
		customLevels = ConfigurationParser.getCustomLevels();
		knownTags = ConfigurationParser.getTags();

		Level minimumLevel = calculateMinimumLevel(globalLevel, customLevels);
		boolean hasWritingThread = ConfigurationParser.isWritingThreadEnabled();

		writers = ConfigurationParser.createWriters(knownTags, minimumLevel, hasWritingThread);
		requiredLogEntryValues = calculateRequiredLogEntryValues(writers);
		fullStackTraceRequired = calculateFullStackTraceRequirements(requiredLogEntryValues);
		writingThread = hasWritingThread ? createWritingThread(writers) : null;

		if (ConfigurationParser.isAutoShutdownEnabled()) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					shutdown();
				}
			});
		}
	}

	@Override
	public ContextProvider getContextProvider() {
		return context;
	}

	@Override
	public Level getMinimumLevel(final String tag) {
		int tagIndex = getTagIndex(tag);
		for (int levelIndex = Level.TRACE.ordinal(); levelIndex < Level.OFF.ordinal(); ++levelIndex) {
			if (writers[tagIndex][levelIndex].size() > 0) {
				return Level.values()[levelIndex];
			}
		}
		return Level.OFF;
	}

	@Override
	public boolean isEnabled(final int depth, final String tag, final Level level) {
		Level activeLevel;

		if (customLevels.isEmpty()) {
			activeLevel = globalLevel;
		} else {
			String className = RuntimeProvider.getCallerClassName(depth + 1);
			activeLevel = getLevel(className);
		}

		return activeLevel.ordinal() <= level.ordinal() && writers[getTagIndex(tag)][level.ordinal()].size() > 0;
	}

	@Override
	public void log(final int depth, final String tag, final Level level, final Throwable exception, final Object obj,
		final Object... arguments) {
		int tagIndex = getTagIndex(tag);

		StackTraceElement stackTraceElement;
		if (fullStackTraceRequired.get(tagIndex)) {
			stackTraceElement = RuntimeProvider.getCallerStackTraceElement(depth + 1);
		} else {
			stackTraceElement = null;
		}

		Level activeLevel;

		if (customLevels.isEmpty()) {
			activeLevel = globalLevel;
		} else {
			if (stackTraceElement == null) {
				String className = RuntimeProvider.getCallerClassName(depth + 1);
				stackTraceElement = new StackTraceElement(className, "<unknown>", "<unknown>", -1);
			}
			activeLevel = getLevel(stackTraceElement.getClassName());
		}

		if (activeLevel.ordinal() <= level.ordinal()) {
			String message = arguments == null || arguments.length == 0 ? String.valueOf(obj) : formatter.format((String) obj, arguments);
			LogEntry logEntry = createLogEntry(depth + 1, tag, tagIndex, level, exception, message, stackTraceElement);
			if (writingThread == null) {
				for (Writer writer : writers[tagIndex][level.ordinal()]) {
					try {
						writer.write(logEntry);
					} catch (Exception ex) {
						InternalLogger.log(Level.ERROR, ex, "Failed to write log entry '" + logEntry.getMessage() + "'");
					}
				}
			} else {
				for (Writer writer : writers[tagIndex][level.ordinal()]) {
					writingThread.add(writer, logEntry);
				}
			}
		}
	}

	@Override
	public void shutdown() {
		if (writingThread == null) {
			for (Writer writer : getAllWriters(writers)) {
				try {
					writer.close();
				} catch (Exception ex) {
					InternalLogger.log(Level.ERROR, ex, "Failed to close writer");
				}
			}
		} else {
			writingThread.shutdown();
		}
	}

	/**
	 * Calculates the minimum severity level that can output any log entries.
	 *
	 * @param globalLevel
	 *            Global severity level
	 * @param customLevels
	 *            Custom severity levels for packages and classes
	 * @return Minimum severity level
	 */
	private static Level calculateMinimumLevel(final Level globalLevel, final Map<String, Level> customLevels) {
		Level minimumLevel = globalLevel;
		for (Level level : customLevels.values()) {
			if (level.ordinal() < minimumLevel.ordinal()) {
				minimumLevel = level;
			}
		}
		return minimumLevel;
	}

	/**
	 * Creates a matrix with all required log entry values for each tag and severity level.
	 *
	 * @param writers
	 *            Matrix with registered writers
	 * @return Matrix with all required log entry values
	 */
	@SuppressWarnings("unchecked")
	private static Collection<LogEntryValue>[][] calculateRequiredLogEntryValues(final Collection<Writer>[][] writers) {
		Collection<LogEntryValue>[][] logEntryValues = new Collection[writers.length][Level.values().length - 1];

		for (int tagIndex = 0; tagIndex < writers.length; ++tagIndex) {
			for (int levelIndex = 0; levelIndex < Level.OFF.ordinal(); ++levelIndex) {
				Set<LogEntryValue> values = EnumSet.noneOf(LogEntryValue.class);
				for (Writer writer : writers[tagIndex][levelIndex]) {
					values.addAll(writer.getRequiredLogEntryValues());
				}
				logEntryValues[tagIndex][levelIndex] = values;
			}
		}

		return logEntryValues;
	}

	/**
	 * Calculates for which tag a full stack trace element with method name, file name and line number is required.
	 *
	 * @param logEntryValues
	 *            Matrix with required log entry values
	 * @return Each set bit represents a tag that requires a full stack trace element
	 */
	private static BitSet calculateFullStackTraceRequirements(final Collection<LogEntryValue>[][] logEntryValues) {
		BitSet result = new BitSet(logEntryValues.length);
		for (int i = 0; i < logEntryValues.length; ++i) {
			Collection<LogEntryValue> values = logEntryValues[i][Level.ERROR.ordinal()];
			if (values.contains(LogEntryValue.METHOD) || values.contains(LogEntryValue.FILE) || values.contains(LogEntryValue.LINE)) {
				result.set(i);
			}
		}
		return result;
	}

	/**
	 * Creates a writing thread for a matrix of writers.
	 *
	 * @param matrix
	 *            All writers
	 * @return Initialized and running writhing thread
	 */
	private static WritingThread createWritingThread(final Collection<Writer>[][] matrix) {
		Collection<Writer> writers = getAllWriters(matrix);
		WritingThread thread = new WritingThread(writers);
		thread.start();
		return thread;
	}

	/**
	 * Collects all writer instances from a matrix of writers.
	 *
	 * @param matrix
	 *            All writers
	 * @return Collection that contains each writer only once
	 */
	private static Collection<Writer> getAllWriters(final Collection<Writer>[][] matrix) {
		Collection<Writer> writers = Collections.newSetFromMap(new IdentityHashMap<Writer, Boolean>());
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				writers.addAll(matrix[i][j]);
			}
		}
		return writers;
	}

	/**
	 * Gets the index of a tag.
	 *
	 * @param tag
	 *            Name of tag
	 * @return Index of tag
	 */
	private int getTagIndex(final String tag) {
		if (tag == null) {
			return 0;
		} else {
			int index = knownTags.indexOf(tag);
			return index == -1 ? knownTags.size() + 1 : index + 1;
		}
	}

	/**
	 * Gets the severity level for a class. If there is no custom severity level for the class or one of it's
	 * (sub-)packages, the global severity level will be returned.
	 *
	 * @param className
	 *            Fully-qualified class name
	 * @return Severity level for given class
	 */
	private Level getLevel(final String className) {
		String key = className;
		while (true) {
			Level customLevel = customLevels.get(key);
			if (customLevel == null) {
				int index = key.lastIndexOf('.');
				if (index == -1) {
					return globalLevel;
				} else {
					key = key.substring(0, index);
				}
			} else {
				return customLevel;
			}
		}
	}

	/**
	 * Creates a new log entry.
	 *
	 * @param depth
	 *            Position of caller in stack trace
	 * @param tag
	 *            Tag name if issued from a tagged logger
	 * @param tagIndex
	 *            Index of tag
	 * @param level
	 *            Severity level
	 * @param exception
	 *            Caught exception or throwable to log
	 * @param message
	 *            Text message to log
	 * @param stackTraceElement
	 *            Stack trace element of caller if already fetched
	 * @return Filled log entry
	 */
	private LogEntry createLogEntry(final int depth, final String tag, final int tagIndex, final Level level, final Throwable exception,
		final String message, final StackTraceElement stackTraceElement) {
		Collection<LogEntryValue> required = requiredLogEntryValues[tagIndex][level.ordinal()];

		Date date = required.contains(LogEntryValue.DATE) ? new Date() : null;
		Thread thread = required.contains(LogEntryValue.THREAD) ? Thread.currentThread() : null;
		Map<String, String> context = required.contains(LogEntryValue.CONTEXT) ? this.context.getMapping() : null;

		String className;
		String methodName;
		String fileName;
		int lineNumber;
		if (stackTraceElement == null) {
			className = required.contains(LogEntryValue.CLASS) ? RuntimeProvider.getCallerClassName(depth + 1) : null;
			methodName = null;
			fileName = null;
			lineNumber = -1;
		} else {
			className = stackTraceElement.getClassName();
			methodName = stackTraceElement.getMethodName();
			fileName = stackTraceElement.getFileName();
			lineNumber = stackTraceElement.getLineNumber();
		}

		return new LogEntry(date, thread, context, className, methodName, fileName, lineNumber, tag, level, message, exception);
	}

}
