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
 * Configuration for {@link org.pmw.tinylog.Logger Logger}.
 */
final class Configuration {

	private final LoggingLevel level;
	private final Map<String, LoggingLevel> packageLevels;
	private final String formatPattern;
	private final Locale locale;
	private final LoggingWriter writer;
	private final WritingThread writingThread;
	private final int maxStackTraceElements;

	private final List<Token> formatTokens;
	private final Set<LogEntryValue> requiredLogEntryValues;
	private final StackTraceInformation requiredStackTraceInformation;

	/**
	 * @param level
	 *            Logging level
	 * @param packageLevels
	 *            Separate logging levels for particular packages
	 * @param formatPattern
	 *            Format pattern for log entries
	 * @param locale
	 *            Locale for format pattern
	 * @param writer
	 *            Logging writer (can be <code>null</code> to disable any output)
	 * @param writingThread
	 *            Writing thread (can be <code>null</code> to write log entries synchronously)
	 * @param maxStackTraceElements
	 *            Limit of stack traces for exceptions
	 */
	Configuration(final LoggingLevel level, final Map<String, LoggingLevel> packageLevels, final String formatPattern, final Locale locale,
			final LoggingWriter writer, final WritingThread writingThread, final int maxStackTraceElements) {
		this.level = level;
		this.packageLevels = packageLevels;
		this.formatPattern = formatPattern;
		this.locale = locale;
		this.writer = writer;
		this.writingThread = writingThread;
		this.maxStackTraceElements = maxStackTraceElements;

		this.formatTokens = Tokenizer.parse(formatPattern, locale);
		this.requiredLogEntryValues = requiredLogEntryValues(writer, formatTokens);
		this.requiredStackTraceInformation = getRequiredStackTraceInformation(packageLevels, requiredLogEntryValues);
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
	 * Check if there are custom logging levels for one or more packages.
	 * 
	 * @return <code>true</code> if custom logging levels exist, <code>false</code> if not
	 */
	public boolean hasCustomLoggingLevelsForPackages() {
		return !packageLevels.isEmpty();
	}

	/**
	 * Get the logging level for a class.
	 * 
	 * @param className
	 *            Name of the class
	 * 
	 * @return Logging level for class
	 */
	public LoggingLevel getLevelOfClass(final String className) {
		int index = className.lastIndexOf('.');
		if (index > 0) {
			return getLevelOfPackage(className.substring(0, index));
		} else {
			return level;
		}
	}

	/**
	 * Get the logging level for a package.
	 * 
	 * @param packageName
	 *            Name of the package
	 * 
	 * @return Logging level for package
	 */
	public LoggingLevel getLevelOfPackage(final String packageName) {
		String packageKey = packageName;
		while (true) {
			LoggingLevel levelOfPackage = packageLevels.get(packageKey);
			if (levelOfPackage != null) {
				return levelOfPackage;
			}
			int index = packageKey.lastIndexOf('.');
			if (index > 0) {
				packageKey = packageKey.substring(0, index);
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
	 * Get the logging writer.
	 * 
	 * @return Logging writer
	 */
	public LoggingWriter getWriter() {
		return writer;
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
	 * Create a copy of this configuration.
	 * 
	 * @return Copy of this configuration
	 */
	Configurator copy() {
		Map<String, LoggingLevel> copyOfPackageLevels = packageLevels.isEmpty() ? Collections.<String, LoggingLevel> emptyMap()
				: new HashMap<String, LoggingLevel>(packageLevels);
		WritingThreadData writingThreadData = writingThread == null ? null : new WritingThreadData(writingThread.getNameOfThreadToObserve(),
				writingThread.getPriority());
		return new Configurator(level, copyOfPackageLevels, formatPattern, locale, writer, writingThreadData, maxStackTraceElements);
	}

	private static Set<LogEntryValue> requiredLogEntryValues(final LoggingWriter writer, final Collection<Token> formatTokens) {
		if (writer == null) {
			return Collections.emptySet();
		} else {
			Set<LogEntryValue> logEntryValuesOfWriter = writer.getRequiredLogEntryValues();
			if (logEntryValuesOfWriter == null || logEntryValuesOfWriter.isEmpty()) {
				return Collections.emptySet();
			} else if (logEntryValuesOfWriter.contains(LogEntryValue.RENDERED_LOG_ENTRY)) {
				Set<LogEntryValue> requiredLogEntryValues = EnumSet.copyOf(logEntryValuesOfWriter);
				for (Token token : formatTokens) {
					LogEntryValue logEntryValue = getRequiredLogEntryValue(token);
					if (logEntryValue != null) {
						requiredLogEntryValues.add(logEntryValue);
					}
				}
				return requiredLogEntryValues;
			} else {
				return logEntryValuesOfWriter;
			}
		}
	}

	private static StackTraceInformation getRequiredStackTraceInformation(final Map<String, LoggingLevel> packageLevels, final Set<LogEntryValue> logEntryValues) {
		if (logEntryValues.contains(LogEntryValue.METHOD) || logEntryValues.contains(LogEntryValue.FILE) || logEntryValues.contains(LogEntryValue.LINE_NUMBER)) {
			return StackTraceInformation.FULL;
		} else if (logEntryValues.contains(LogEntryValue.CLASS) || !packageLevels.isEmpty()) {
			return StackTraceInformation.CLASS_NAME;
		} else {
			return StackTraceInformation.NONE;
		}
	}

	private static LogEntryValue getRequiredLogEntryValue(final Token token) {
		switch (token.getType()) {
			case THREAD:
			case THREAD_ID:
				return LogEntryValue.THREAD;

			case CLASS:
			case CLASS_NAME:
			case PACKAGE:
				return LogEntryValue.CLASS;

			case METHOD:
				return LogEntryValue.METHOD;

			case FILE:
				return LogEntryValue.FILE;

			case LINE_NUMBER:
				return LogEntryValue.LINE_NUMBER;

			case LOGGING_LEVEL:
				return LogEntryValue.LOGGING_LEVEL;

			case DATE:
				return LogEntryValue.DATE;

			case MESSAGE:
				return LogEntryValue.MESSAGE;

			default:
				return null;
		}
	}

}
