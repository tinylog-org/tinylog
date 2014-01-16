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

import java.util.Collections;
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

	private final Set<LogEntryValue> requiredLogEntryValues;
	private final List<Token> formatTokens;
	private final boolean fullStackTraceElemetRequired;

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

		this.requiredLogEntryValues = writer == null ? Collections.<LogEntryValue> emptySet() : writer.getRequiredLogEntryValues();
		this.formatTokens = Tokenizer.parse(formatPattern, locale);
		this.fullStackTraceElemetRequired = fullStackTraceElemetRequired(requiredLogEntryValues, formatTokens);
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
	 * Get all log entry values that are required by logging writer.
	 * 
	 * @return Required values for log entry
	 */
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return requiredLogEntryValues;
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
	 * Check if a full stack trace element is required.
	 * 
	 * @return <code>true</code> if a full stack trace element is required, <code>false</code> if not
	 */
	public boolean isFullStackTraceElemetRequired() {
		return fullStackTraceElemetRequired;
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

	private static boolean fullStackTraceElemetRequired(final Set<LogEntryValue> logEntryValues, final List<Token> tokens) {
		for (LogEntryValue logEntryValue : logEntryValues) {
			switch (logEntryValue) {
				case METHOD:
				case FILE:
				case LINE_NUMBER:
					return true;
				case RENDERED_LOG_ENTRY:
					for (Token token : tokens) {
						switch (token.getType()) {
							case METHOD:
							case FILE:
							case LINE_NUMBER:
								return true;
							default:
								break;
						}
					}
					break;
				default:
					break;
			}
		}
		return false;
	}

}
