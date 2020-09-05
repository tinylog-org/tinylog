/*
 * Copyright 2016 Martin Winandy
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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tinylog.Level;
import org.tinylog.Supplier;
import org.tinylog.configuration.Configuration;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.format.MessageFormatter;
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.runtime.Timestamp;
import org.tinylog.writers.Writer;

public class TinylogLoggingConfiguration {
	public TinylogLoggingConfiguration() {
	}
	
	/**
	 * Creates a two-dimensional matrix with all configured writers. The first dimension represents the tags. It starts
	 * with untagged writers, following with all tagged writers and ending with writers that accepts all kind of log
	 * entries. The total number of entries is the number of defined tags plus two. The second dimension represents the
	 * severity level. It starts with {@link Level#TRACE} and ends with {@link Level#ERROR}.
	 *
	 * @param tags
	 *            Order of defined tags
	 * @param minimumLevel
	 *            Minimum global severity level
	 * @param writingThread
	 *            Defines whether {@link WritingThread} is enabled
	 * @return Matrix with all created writers
	 */
	@SuppressWarnings("unchecked")
	public Collection<Writer>[][] createWriters(final List<String> tags, final Level minimumLevel, final boolean writingThread) {
		if (RuntimeProvider.getProcessId() == Long.MIN_VALUE) {
			java.util.ServiceLoader.load(Writer.class); // Workaround for ProGuard (see issue #126)
		}
		
		Collection<Writer>[][] matrix = new Collection[tags.size() + 2][Level.values().length - 1];
		ServiceLoader<Writer> loader = new ServiceLoader<Writer>(Writer.class, Map.class);

		Map<String, String> writerProperties = Configuration.getSiblings("writer");

		if (writerProperties.isEmpty()) {
			writerProperties = Collections.singletonMap("writer", RuntimeProvider.getDefaultWriter());
		}

		for (Entry<String, String> entry : writerProperties.entrySet()) {
			Map<String, String> configuration = Configuration.getChildren(entry.getKey());
			String tag = configuration.get("tag");
			Level level = ConfigurationParser.parse(configuration.get("level"), minimumLevel);
			if (level.ordinal() < minimumLevel.ordinal()) {
				level = minimumLevel;
			}
			
			String exception = Configuration.get("exception");
			if (exception != null && !configuration.containsKey("exception")) {
				configuration.put("exception", exception);
			}

			configuration.put("ID", entry.getKey());

			configuration.put("writingthread", Boolean.toString(writingThread));

			Writer writer = loader.create(entry.getValue(), configuration);
			if (writer != null) {
				if (tag == null || tag.isEmpty()) {
					for (int tagIndex = 0; tagIndex < matrix.length; ++tagIndex) {
						addWriter(writer, matrix, tagIndex, level);
					}
				} else if (tag.equals("-")) {
					addWriter(writer, matrix, 0, level);
				} else {
					String[] tagArray = tag.split(",");
					for (String tagArrayItem : tagArray) {
						tagArrayItem = tagArrayItem.trim(); 
						if (!tagArrayItem.isEmpty()) {
							addWriter(writer, matrix, tags.indexOf(tagArrayItem) + 1, level);
						}
					}
				}
			}
		}

		for (int tagIndex = 0; tagIndex < matrix.length; ++tagIndex) {
			for (int levelIndex = 0; levelIndex < matrix[tagIndex].length; ++levelIndex) {
				if (matrix[tagIndex][levelIndex] == null) {
					matrix[tagIndex][levelIndex] = Collections.emptyList();
				}
			}
		}

		return matrix;
	}

	/**
	 * Adds a writer to a well-defined matrix. The given writer will be added only at the given tag index for severity
	 * levels equal or above the given severity level.
	 *
	 * @param writer
	 *            Writer to add
	 * @param matrix
	 *            Well-defined two-dimensional matrix
	 * @param tagIndex
	 *            Represents the tag (first dimension of matrix)
	 * @param level
	 *            Represents the severity level (second dimension of matrix)
	 */
	protected void addWriter(final Writer writer, final Collection<Writer>[][] matrix, final int tagIndex, final Level level) {
		for (int levelIndex = level.ordinal(); levelIndex < Level.OFF.ordinal(); ++levelIndex) {
			Collection<Writer> collection = matrix[tagIndex][levelIndex];
			if (collection == null) {
				collection = new ArrayList<Writer>();
				matrix[tagIndex][levelIndex] = collection;
			}
			collection.add(writer);
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
	public Level calculateMinimumLevel(final Level globalLevel, final Map<String, Level> customLevels) {
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
	public Collection<LogEntryValue>[][] calculateRequiredLogEntryValues(final Collection<Writer>[][] writers) {
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
	public BitSet calculateFullStackTraceRequirements(final Collection<LogEntryValue>[][] logEntryValues) {
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
	public WritingThread createWritingThread(final Collection<Writer>[][] matrix) {
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
	public static Collection<Writer> getAllWriters(final Collection<Writer>[][] matrix) {
		Collection<Writer> writers = Collections.newSetFromMap(new IdentityHashMap<Writer, Boolean>());
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				writers.addAll(matrix[i][j]);
			}
		}
		return writers;
	}
	
	/**
	 * Creates a new log entry.
	 *
	 * @param stackTraceElement
	 *            Optional stack trace element of caller
	 * @param tag
	 *            Tag name if issued from a tagged logger
	 * @param level
	 *            Severity level
	 * @param exception
	 *            Caught exception or throwable to log
	 * @param formatter
	 *            Formatter for text message
	 * @param obj
	 *            Message to log
	 * @param arguments
	 *            Arguments for message
	 * @param requiredLogEntryValues
	 *            The required log entry value array slice of the tag index of the used tag
	 * @param contextProvider
	 *            The context provider
	 * @return Filled log entry
	 */
	public static LogEntry createLogEntry(final StackTraceElement stackTraceElement, final String tag, 
		final Level level, final Throwable exception, final MessageFormatter formatter, final Object obj,
		final Object[] arguments, final Collection<LogEntryValue>[] requiredLogEntryValues, 
		final TinylogContextProvider contextProvider) {
		Collection<LogEntryValue> required = requiredLogEntryValues[level.ordinal()];

		Timestamp timestamp = RuntimeProvider.createTimestamp();
		Thread thread = required.contains(LogEntryValue.THREAD) ? Thread.currentThread() : null;
		Map<String, String> context = required.contains(LogEntryValue.CONTEXT) ? contextProvider.getMapping() : null;

		String className;
		String methodName;
		String fileName;
		int lineNumber;
		if (stackTraceElement == null) {
			className = null;
			methodName = null;
			fileName = null;
			lineNumber = -1;
		} else {
			className = stackTraceElement.getClassName();
			methodName = stackTraceElement.getMethodName();
			fileName = stackTraceElement.getFileName();
			lineNumber = stackTraceElement.getLineNumber();
		}

		String message;
		if (arguments == null || arguments.length == 0) {
			Object evaluatedObject = obj instanceof Supplier<?> ? ((Supplier<?>) obj).get() : obj;
			message = evaluatedObject == null ? null : evaluatedObject.toString();
		} else {
			message = formatter.format((String) obj, arguments);
		}

		return new LogEntry(timestamp, thread, context, className, methodName, fileName, lineNumber, tag, level, message, exception);
	}

}
