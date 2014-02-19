/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.pmw.tinylog.Configurator.WritingThreadData;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * Configuration for {@link org.pmw.tinylog.Logger Logger}. Use {@link org.pmw.tinylog.Configurator Configurator} to
 * create a configuration.
 */
public final class Configuration {

	private final LoggingLevel level;
	private final LoggingLevel lowestLevel;
	private final Map<String, LoggingLevel> customLevels;
	private final String formatPattern;
	private final Locale locale;
	private final List<LoggingWriter> writers;
	private final List<LoggingWriter> effectiveWriters;
	private final WritingThread writingThread;
	private final int maxStackTraceElements;

	private final List<Token> formatTokens;
	private final Set<LogEntryValue> requiredLogEntryValues;
	private final StackTraceInformation requiredStackTraceInformation;

	/**
	 * @param level
	 *            Logging level
	 * @param customLevels
	 *            Custom logging levels for specific packages and classes
	 * @param formatPattern
	 *            Format pattern for log entries
	 * @param locale
	 *            Locale for format pattern
	 * @param writers
	 *            Logging writers (can be <code>null</code> or <code>empty</code> to disable any output)
	 * @param writingThread
	 *            Writing thread (can be <code>null</code> to write log entries synchronously)
	 * @param maxStackTraceElements
	 *            Limit of stack traces for exceptions
	 */
	Configuration(final LoggingLevel level, final Map<String, LoggingLevel> customLevels, final String formatPattern, final Locale locale,
			final List<LoggingWriter> writers, final WritingThread writingThread, final int maxStackTraceElements) {
		this.level = level;
		this.lowestLevel = writers == null || writers.isEmpty() ? LoggingLevel.OFF : getLowestLevel(level, customLevels);
		this.customLevels = customLevels;
		this.formatPattern = formatPattern;
		this.locale = locale;
		this.writers = writers == null || writers.isEmpty() ? Collections.<LoggingWriter> emptyList() : new ArrayList<LoggingWriter>(writers);
		this.effectiveWriters = this.writers.isEmpty() || lowestLevel == LoggingLevel.OFF ? Collections.<LoggingWriter> emptyList() : this.writers;
		this.writingThread = writingThread;
		this.maxStackTraceElements = maxStackTraceElements;

		this.formatTokens = Tokenizer.parse(formatPattern, locale);
		this.requiredLogEntryValues = requiredLogEntryValues(this.effectiveWriters, this.formatTokens);
		this.requiredStackTraceInformation = this.effectiveWriters.isEmpty() ? StackTraceInformation.NONE : getRequiredStackTraceInformation(customLevels,
				requiredLogEntryValues);
	}

	/**
	 * Get the global logging level.
	 * 
	 * @return Global logging level
	 */
	public LoggingLevel getLevel() {
		return level;
	}

	/**
	 * Check if there are custom logging levels.
	 * 
	 * @return <code>true</code> if custom logging levels exist, <code>false</code> if not
	 */
	public boolean hasCustomLevels() {
		return !customLevels.isEmpty();
	}

	/**
	 * Get the logging level for a package or class.
	 * 
	 * @param packageOrClass
	 *            Name of the package respectively class
	 * 
	 * @return Logging level for the package respectively class
	 */
	public LoggingLevel getLevel(final String packageOrClass) {
		String key = packageOrClass;
		while (true) {
			LoggingLevel customLevel = customLevels.get(key);
			if (customLevel != null) {
				return customLevel;
			}
			int index = key.lastIndexOf('.');
			if (index > 0) {
				key = key.substring(0, index);
			} else {
				return level;
			}
		}
	}

	/**
	 * Get the format pattern for log entries.
	 * 
	 * @return Format pattern for log entries
	 */
	public String getFormatPattern() {
		return formatPattern;
	}

	/**
	 * Get the format tokens for log entries (= rendered format pattern).
	 * 
	 * @return Format tokens for log entries
	 */
	public List<Token> getFormatTokens() {
		return formatTokens;
	}

	/**
	 * Get the locale that is used to render format patterns for log entries.
	 * 
	 * @return Locale for format pattern
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Get the logging writers.
	 * 
	 * @return Logging writers
	 */
	public List<LoggingWriter> getWriters() {
		return writers;
	}

	/**
	 * Get the writing thread (writes log entries asynchronously).
	 * 
	 * @return Writing thread
	 */
	public WritingThread getWritingThread() {
		return writingThread;
	}

	/**
	 * Get the limit of stack traces for exceptions.
	 * 
	 * @return Limit of stack traces
	 */
	public int getMaxStackTraceElements() {
		return maxStackTraceElements;
	}

	/**
	 * Get all log entry values that are required by logging writer.
	 * 
	 * @return Required values for log entry
	 */
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return requiredLogEntryValues;
	}

	/**
	 * Get the required stack trace information.
	 * 
	 * @return Required stack trace information
	 */
	public StackTraceInformation getRequiredStackTraceInformation() {
		return requiredStackTraceInformation;
	}

	/**
	 * Fast check if output is possible.
	 * 
	 * @param level
	 *            Logging level to check
	 * @return <code>true</code> if log entries with the defined logging level can be output, <code>false</code> if not
	 */
	boolean isOutputPossible(final LoggingLevel level) {
		return lowestLevel.ordinal() <= level.ordinal();
	}

	/**
	 * Get the effective logging writers to be used by the logger.
	 * 
	 * @return Effective logging writers
	 */
	// TODO LoggingLevel-Uebergabe
	List<LoggingWriter> getEffectiveWriters() {
		return effectiveWriters;
	}

	/**
	 * Create a copy of this configuration.
	 * 
	 * @return Copy of this configuration
	 */
	Configurator copy() {
		Map<String, LoggingLevel> copyOfCustomLevels;
		if (customLevels.isEmpty()) {
			copyOfCustomLevels = Collections.emptyMap();
		} else {
			copyOfCustomLevels = new HashMap<String, LoggingLevel>(customLevels);
		}

		WritingThreadData writingThreadData;
		if (writingThread == null) {
			writingThreadData = null;
		} else {
			writingThreadData = new WritingThreadData(writingThread.getNameOfThreadToObserve(), writingThread.getPriority());
		}

		return new Configurator(level, copyOfCustomLevels, formatPattern, locale, writers, writingThreadData, maxStackTraceElements);
	}

	private static LoggingLevel getLowestLevel(final LoggingLevel level, final Map<String, LoggingLevel> customLevels) {
		LoggingLevel lowestLevel = level;
		for (LoggingLevel customLevel : customLevels.values()) {
			if (lowestLevel.ordinal() > customLevel.ordinal()) {
				lowestLevel = customLevel;
			}
		}
		return lowestLevel;
	}

	private static Set<LogEntryValue> requiredLogEntryValues(final List<LoggingWriter> writers, final Collection<Token> formatTokens) {
		if (writers.isEmpty()) {
			return Collections.emptySet();
		} else {
			Set<LogEntryValue> requiredLogEntryValues = EnumSet.noneOf(LogEntryValue.class);
			for (LoggingWriter writer : writers) {
				Set<LogEntryValue> requiredLogEntryValuesOfWriter = writer.getRequiredLogEntryValues();
				if (requiredLogEntryValuesOfWriter != null) {
					requiredLogEntryValues.addAll(requiredLogEntryValuesOfWriter);
				}
			}
			if (requiredLogEntryValues.contains(LogEntryValue.RENDERED_LOG_ENTRY)) {
				for (Token token : formatTokens) {
					LogEntryValue logEntryValue = token.getType().getRequiredLogEntryValue();
					if (logEntryValue != null) {
						requiredLogEntryValues.add(logEntryValue);
					}
				}
			}
			return requiredLogEntryValues.isEmpty() ? Collections.<LogEntryValue> emptySet() : requiredLogEntryValues;
		}
	}

	private static StackTraceInformation getRequiredStackTraceInformation(final Map<String, LoggingLevel> customLevels, final Set<LogEntryValue> logEntryValues) {
		if (logEntryValues.contains(LogEntryValue.METHOD) || logEntryValues.contains(LogEntryValue.FILE) || logEntryValues.contains(LogEntryValue.LINE_NUMBER)) {
			return StackTraceInformation.FULL;
		} else if (logEntryValues.contains(LogEntryValue.CLASS) || !customLevels.isEmpty()) {
			return StackTraceInformation.CLASS_NAME;
		} else {
			return StackTraceInformation.NONE;
		}
	}

}
