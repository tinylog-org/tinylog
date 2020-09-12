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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinylog.Level;
import org.tinylog.format.MessageFormatter;
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
	private final Level globalLevel;
	private final Map<String, Level> customLevels;
	private final List<String> knownTags;
	private final Collection<Writer>[][] writers;
	private final Collection<LogEntryValue>[][] requiredLogEntryValues;
	private final BitSet fullStackTraceRequired;
	private final WritingThread writingThread;

	/** */
	public TinylogLoggingProvider() {
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		context = new TinylogContextProvider();
		globalLevel = ConfigurationParser.getGlobalLevel();
		customLevels = ConfigurationParser.getCustomLevels();
		knownTags = ConfigurationParser.getTags();

		Level minimumLevel = config.calculateMinimumLevel(globalLevel, customLevels);
		boolean hasWritingThread = ConfigurationParser.isWritingThreadEnabled();

		writers = config.createWriters(knownTags, minimumLevel, hasWritingThread);
		requiredLogEntryValues = config.calculateRequiredLogEntryValues(writers);
		fullStackTraceRequired = config.calculateFullStackTraceRequirements(requiredLogEntryValues);
		writingThread = hasWritingThread ? config.createWritingThread(writers) : null;

		if (ConfigurationParser.isAutoShutdownEnabled()) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						shutdown();
					} catch (InterruptedException ex) {
						InternalLogger.log(Level.ERROR, ex, "Interrupted while waiting for shutdown");
					}
				}
			});
		}
	}

	@Override
	public ContextProvider getContextProvider() {
		return context;
	}

	@Override
	public Level getMinimumLevel() {
		Level level = Level.OFF;
		for (int tagIndex = 0; tagIndex < writers.length; ++tagIndex) {
			for (int levelIndex = Level.TRACE.ordinal(); levelIndex < level.ordinal(); ++levelIndex) {
				if (writers[tagIndex][levelIndex].size() > 0) {
					level = Level.values()[levelIndex];
				}
			}
		}
		return level;
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
	public void log(final int depth, final String tag, final Level level, final Throwable exception, final MessageFormatter formatter,
		final Object obj, final Object... arguments) {
		int tagIndex = getTagIndex(tag);

		StackTraceElement stackTraceElement;
		if (fullStackTraceRequired.get(tagIndex)) {
			stackTraceElement = RuntimeProvider.getCallerStackTraceElement(depth + 1);
		} else {
			stackTraceElement = null;
		}

		Level activeLevel;
		if (customLevels.isEmpty()) {
			if (stackTraceElement == null && requiredLogEntryValues[tagIndex][level.ordinal()].contains(LogEntryValue.CLASS)) {
				stackTraceElement = new StackTraceElement(RuntimeProvider.getCallerClassName(depth + 1), "<unknown>", null, -1);
			}
			activeLevel = globalLevel;
		} else {
			if (stackTraceElement == null) {
				stackTraceElement = new StackTraceElement(RuntimeProvider.getCallerClassName(depth + 1), "<unknown>", null, -1);
			}
			activeLevel = getLevel(stackTraceElement.getClassName());
		}

		if (activeLevel.ordinal() <= level.ordinal()) {
			LogEntry logEntry = TinylogLoggingConfiguration.createLogEntry(stackTraceElement, tag, level, exception, formatter, 
					obj, arguments, requiredLogEntryValues[tagIndex], context);
			output(logEntry, writers[tagIndex][logEntry.getLevel().ordinal()]);
		}
	}

	@Override
	public void log(final String loggerClassName, final String tag, final Level level, final Throwable exception,
		final MessageFormatter formatter, final Object obj, final Object... arguments) {
		int tagIndex = getTagIndex(tag);

		StackTraceElement stackTraceElement;
		if (fullStackTraceRequired.get(tagIndex)) {
			stackTraceElement = RuntimeProvider.getCallerStackTraceElement(loggerClassName);
		} else {
			stackTraceElement = null;
		}

		Level activeLevel;
		if (customLevels.isEmpty()) {
			if (stackTraceElement == null && requiredLogEntryValues[tagIndex][level.ordinal()].contains(LogEntryValue.CLASS)) {
				stackTraceElement = new StackTraceElement(RuntimeProvider.getCallerClassName(loggerClassName), "<unknown>", null, -1);
			}
			activeLevel = globalLevel;
		} else {
			if (stackTraceElement == null) {
				stackTraceElement = new StackTraceElement(RuntimeProvider.getCallerClassName(loggerClassName), "<unknown>", null, -1);
			}
			activeLevel = getLevel(stackTraceElement.getClassName());
		}

		if (activeLevel.ordinal() <= level.ordinal()) {
			LogEntry logEntry = TinylogLoggingConfiguration.createLogEntry(stackTraceElement, tag, level, exception, formatter,  
					obj, arguments, requiredLogEntryValues[tagIndex], context);
			output(logEntry, writers[tagIndex][logEntry.getLevel().ordinal()]);
		}
	}

	@Override
	public void shutdown() throws InterruptedException {
		if (writingThread == null) {
			for (Writer writer : TinylogLoggingConfiguration.getAllWriters(writers)) {
				try {
					writer.close();
				} catch (Exception ex) {
					InternalLogger.log(Level.ERROR, ex, "Failed to close writer");
				}
			}
		} else {
			writingThread.shutdown();
			writingThread.join();
		}
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
	 * Outputs a log entry to all passed writers.
	 * 
	 * @param logEntry
	 *            Log entry to be output
	 * @param writers
	 *            All writers for outputting the passed log entry
	 */
	private void output(final LogEntry logEntry, final Iterable<Writer> writers) {
		if (writingThread == null) {
			for (Writer writer : writers) {
				try {
					writer.write(logEntry);
				} catch (Exception ex) {
					InternalLogger.log(Level.ERROR, ex, "Failed to write log entry '" + logEntry.getMessage() + "'");
				}
			}
		} else {
			for (Writer writer : writers) {
				writingThread.add(writer, logEntry);
			}
		}
	}
	
	/**
	 * Gets all writers which belong to the given tag and a given level. A null tag is possible for the generic writer.
	 * 
	 * @param tag
	 *            The tag to find
	 * @param level
	 *            The level to find
	 * @return All writers
	 */
	public Collection<Writer> getWriters(final String tag, final Level level) {
		Set<Writer> collectedWriters = new HashSet<Writer>(); 
		int tagIndex = getTagIndex(tag);
		if (tagIndex > knownTags.size() || level == Level.OFF) {
			return collectedWriters;
		}

		collectedWriters.addAll(writers[tagIndex][level.ordinal()]);
		return collectedWriters;
	}
	
	/**
	 * Gets all writers of the provider which belong to the given tag. A null tag is possible for the generic writer.
	 *
	 * @param tag
	 *            The tag to find
	 * @return All writers
	 */
	public Collection<Writer> getWriters(final String tag) {
		Set<Writer> collectedWriters = new HashSet<Writer>(); 
		int tagIndex = getTagIndex(tag);
		if (tagIndex > knownTags.size()) {
			return collectedWriters;
		}
		
		for (int j = 0; j < writers[tagIndex].length; ++j) {
			collectedWriters.addAll(writers[tagIndex][j]);
		}
		return collectedWriters;
	}
	
	/**
	 * Gets all writers of the provider.
	 * 
	 * @return All writers
	 */
	public Collection<Writer> getWriters() {
		Set<Writer> collectedWriters = new HashSet<Writer>(); 
		
		for (int tagIndex = 0; tagIndex < writers.length; ++tagIndex) {
			for (int levelIndex = 0; levelIndex < writers[tagIndex].length; ++levelIndex) {
				collectedWriters.addAll(writers[tagIndex][levelIndex]);
			}
		}

		return collectedWriters;
	}	

}
