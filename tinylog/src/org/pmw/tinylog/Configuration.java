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
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.Writer;

/**
 * Configuration for {@link org.pmw.tinylog.Logger Logger}. Use {@link org.pmw.tinylog.Configurator Configurator} to
 * create a configuration.
 */
public final class Configuration {
	
	private static final Level DEFAULT_LEVEL = Level.INFO;
	private static final String DEFAULT_FORMAT_PATTERN = "{date} [{thread}] {class}.{method}()\n{level}: {message}";
	private static final int DEFAULT_MAX_STACK_TRACE_ELEMENTS = 40;

	private final Configurator configurator;
	
	private final Level level;
	private final Level lowestLevel;
	private final Map<String, Level> customLevels;
	private final String formatPattern;
	private final Locale locale;
	private final List<Writer> writers;
	private final WritingThread writingThread;
	private final int maxStackTraceElements;

	private final Map<Level, Writer[]> effectiveWriters;
	private final Map<Level, List<Token>[]> effectiveFormatTokens;
	private final Map<Level, Set<LogEntryValue>> requiredLogEntryValues;
	private final Map<Level, StackTraceInformation> requiredStackTraceInformation;

	/**
	 * @param configurator
	 *            Copy of based configurator
	 * @param level
	 *            Severity level
	 * @param customLevels
	 *            Custom severity levels for specific packages and classes
	 * @param formatPattern
	 *            Format pattern for log entries
	 * @param locale
	 *            Locale for format pattern
	 * @param writerDefinitions
	 *            Writer definitions (can be <code>empty</code> to disable any output)
	 * @param writingThread
	 *            Writing thread (can be <code>null</code> to write log entries synchronously)
	 * @param maxStackTraceElements
	 *            Limit of stack traces for exceptions
	 */
	Configuration(final Configurator configurator, final Level level, final Map<String, Level> customLevels, final String formatPattern, final Locale locale,
			final List<WriterDefinition> writerDefinitions, final WritingThread writingThread, final Integer maxStackTraceElements) {
		this.configurator = configurator;
		
		this.level = level == null ? getLevel(writerDefinitions) : level;
		this.lowestLevel = getLowestLevel(this.level, customLevels, writerDefinitions);
		this.customLevels = customLevels;
		this.formatPattern = formatPattern == null ? DEFAULT_FORMAT_PATTERN : formatPattern;
		this.locale = locale == null ? Locale.getDefault() : locale;
		this.writers = getWriters(writerDefinitions);
		this.writingThread = writingThread;
		this.maxStackTraceElements = maxStackTraceElements == null ? DEFAULT_MAX_STACK_TRACE_ELEMENTS : maxStackTraceElements;

		this.effectiveWriters = getEffectiveWriters(writerDefinitions);
		this.effectiveFormatTokens = getEffectiveFormatTokens(writerDefinitions, this.formatPattern, this.locale, this.maxStackTraceElements);
		this.requiredLogEntryValues = getRequiredLogEntryValues(effectiveWriters, effectiveFormatTokens);
		this.requiredStackTraceInformation = getRequiredStackTraceInformation(requiredLogEntryValues, customLevels);
	}

	/**
	 * Get the global severity level.
	 *
	 * @return Global severity level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Check if there are custom severity levels.
	 *
	 * @return <code>true</code> if custom severity levels exist, <code>false</code> if not
	 */
	public boolean hasCustomLevels() {
		return !customLevels.isEmpty();
	}

	/**
	 * Get the severity level for a package or class.
	 *
	 * @param packageOrClass
	 *            Name of the package respectively class
	 *
	 * @return Severity level for the package respectively class
	 */
	public Level getLevel(final String packageOrClass) {
		String key = packageOrClass;
		while (true) {
			Level customLevel = customLevels.get(key);
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
	 * Get the locale that is used to render format patterns for log entries.
	 *
	 * @return Locale for format pattern
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Get the writers to output created log entries.
	 *
	 * @return Writers to output log entries
	 */
	public List<Writer> getWriters() {
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
	 * Get a new configurator, based on this configuration.
	 * @return New configurator
	 */
	Configurator getConfigurator() {
		return configurator.copy();
	}

	/**
	 * Fast check if output is possible.
	 *
	 * @param level
	 *            Severity level to check
	 * @return <code>true</code> if log entries with the defined severity level can be output, <code>false</code> if not
	 */
	boolean isOutputPossible(final Level level) {
		return lowestLevel.ordinal() <= level.ordinal();
	}

	/**
	 * Get the effective writers to be used by the logger.
	 *
	 * @param level
	 *            Severity level of log entry
	 * @return Effective writers
	 */
	Writer[] getEffectiveWriters(final Level level) {
		return effectiveWriters.get(level);
	}

	/**
	 * Get the effective format tokens for all effective writers to be used by the logger.
	 *
	 * @param level
	 *            Severity level of log entry
	 * @return Effective format tokens
	 */
	List<Token>[] getEffectiveFormatTokens(final Level level) {
		return effectiveFormatTokens.get(level);
	}

	/**
	 * Get all log entry values that are required by the writers.
	 *
	 * @param level
	 *            Severity level of log entry
	 * @return Required values for log entry
	 */
	Set<LogEntryValue> getRequiredLogEntryValues(final Level level) {
		return requiredLogEntryValues.get(level);
	}

	/**
	 * Get the required stack trace information.
	 *
	 * @param level
	 *            Severity level of log entry
	 * @return Required stack trace information
	 */
	StackTraceInformation getRequiredStackTraceInformation(final Level level) {
		return requiredStackTraceInformation.get(level);
	}

	private static Level getLevel(final List<WriterDefinition> definitions) {
		Level level = null;
		for (WriterDefinition definition : definitions) {
			if (definition.getLevel() != null) {
				if (level == null || definition.getLevel().ordinal() < level.ordinal()) {
					level = definition.getLevel();
				}
			}
		}
		return level == null ? DEFAULT_LEVEL : level;
	}

	private static Level getLowestLevel(final Level level, final Map<String, Level> customLevels, final List<WriterDefinition> definitions) {
		Level lowestLevel = level;
		for (Level customLevel : customLevels.values()) {
			if (lowestLevel.ordinal() > customLevel.ordinal()) {
				lowestLevel = customLevel;
			}
		}
		
		Level writerOutput = Level.OFF;
		for (WriterDefinition definition : definitions) {
			Level definitionLevel = definition.getLevel();
			if (definitionLevel == null) {
				definitionLevel = lowestLevel;
			}
			if (definitionLevel.ordinal() <= writerOutput.ordinal()) {
				writerOutput = definitionLevel;
			}
		}
		
		return writerOutput.ordinal() > lowestLevel.ordinal() ? writerOutput : lowestLevel;
	}

	private static List<Writer> getWriters(final List<WriterDefinition> definitions) {
		List<Writer> writers = new ArrayList<Writer>();
		for (WriterDefinition definition : definitions) {
			writers.add(definition.getWriter());
		}
		return writers.isEmpty() ? Collections.<Writer> emptyList() : writers;
	}

	private static Map<Level, Writer[]> getEffectiveWriters(final List<WriterDefinition> definitions) {
		Map<Level, Writer[]> map = new EnumMap<Level, Writer[]>(Level.class);
		for (Level level : Level.values()) {
			List<Writer> writers = new ArrayList<Writer>();
			for (WriterDefinition definition : definitions) {
				Level definitionLevel = definition.getLevel();
				if (definitionLevel == null) {
					definitionLevel = Level.TRACE;
				}
				if (level.ordinal() >= definitionLevel.ordinal()) {
					writers.add(definition.getWriter());
				}
			}
			map.put(level, writers.toArray(new Writer[writers.size()]));
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private static Map<Level, List<Token>[]> getEffectiveFormatTokens(final List<WriterDefinition> definitions, final String globalFormatPattern,
			final Locale locale, final int maxStackTraceElements) {
		Map<Writer, List<Token>> cache = new HashMap<Writer, List<Token>>();
		Tokenizer tokenizer = new Tokenizer(locale, maxStackTraceElements);

		Map<Level, List<Token>[]> map = new EnumMap<Level, List<Token>[]>(Level.class);
		for (Level level : Level.values()) {
			List<List<Token>> formatTokensOfLevel = new ArrayList<List<Token>>();
			for (WriterDefinition definition : definitions) {
				Level definitionLevel = definition.getLevel();
				if (definitionLevel == null) {
					definitionLevel = Level.TRACE;
				}
				if (level.ordinal() >= definitionLevel.ordinal()) {
					Writer writer = definition.getWriter();
					if (cache.containsKey(writer)) {
						formatTokensOfLevel.add(cache.get(writer));
					} else {
						Set<LogEntryValue> requiredLogEntryValuesOfWriter = writer.getRequiredLogEntryValues();
						if (requiredLogEntryValuesOfWriter == null || !requiredLogEntryValuesOfWriter.contains(LogEntryValue.RENDERED_LOG_ENTRY)) {
							formatTokensOfLevel.add(null);
							cache.put(writer, null);
						} else {
							String formatPattern = definition.getFormatPattern();
							if (formatPattern == null) {
								formatPattern = globalFormatPattern;
							}
							List<Token> formatTokens = tokenizer.parse(formatPattern);
							formatTokensOfLevel.add(formatTokens);
							cache.put(writer, formatTokens);
						}
					}
				}
			}
			map.put(level, formatTokensOfLevel.toArray(new List[formatTokensOfLevel.size()]));
		}
		return map;
	}

	private static Map<Level, Set<LogEntryValue>> getRequiredLogEntryValues(final Map<Level, Writer[]> writersMap,
			final Map<Level, List<Token>[]> formatTokensMap) {
		Map<Level, Set<LogEntryValue>> map = new EnumMap<Level, Set<LogEntryValue>>(Level.class);
		for (Entry<Level, Writer[]> entry : writersMap.entrySet()) {
			Level level = entry.getKey();
			Writer[] writers = entry.getValue();
			if (writers.length == 0) {
				map.put(level, Collections.<LogEntryValue> emptySet());
			} else {
				List<Token>[] formatTokens = formatTokensMap.get(level);
				Set<LogEntryValue> requiredLogEntryValues = EnumSet.noneOf(LogEntryValue.class);
				for (int i = 0; i < writers.length; ++i) {
					Set<LogEntryValue> requiredLogEntryValuesOfWriter = writers[i].getRequiredLogEntryValues();
					if (requiredLogEntryValuesOfWriter != null) {
						if (requiredLogEntryValuesOfWriter.contains(LogEntryValue.RENDERED_LOG_ENTRY)) {
							for (Token token : formatTokens[i]) {
								for (LogEntryValue logEntryValue : token.getRequiredLogEntryValues()) {
									requiredLogEntryValuesOfWriter.add(logEntryValue);
								}
							}
						}
						requiredLogEntryValues.addAll(requiredLogEntryValuesOfWriter);
					}
				}
				if (requiredLogEntryValues.isEmpty()) {
					map.put(level, Collections.<LogEntryValue> emptySet());
				} else {
					map.put(level, requiredLogEntryValues);
				}
			}
		}
		return map;
	}

	private static Map<Level, StackTraceInformation> getRequiredStackTraceInformation(final Map<Level, Set<LogEntryValue>> requiredLogEntryValues,
			final Map<String, Level> customLevels) {
		Map<Level, StackTraceInformation> map = new EnumMap<Level, StackTraceInformation>(Level.class);
		for (Entry<Level, Set<LogEntryValue>> entry : requiredLogEntryValues.entrySet()) {
			Level level = entry.getKey();
			Set<LogEntryValue> logEntryValues = entry.getValue();
			if (logEntryValues.contains(LogEntryValue.METHOD) || logEntryValues.contains(LogEntryValue.FILE) || logEntryValues.contains(LogEntryValue.LINE)) {
				map.put(level, StackTraceInformation.FULL);
			} else if (logEntryValues.contains(LogEntryValue.CLASS) || !customLevels.isEmpty()) {
				map.put(level, StackTraceInformation.CLASS_NAME);
			} else {
				map.put(level, StackTraceInformation.NONE);
			}
		}
		return map;
	}

}
