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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.provider.InternalLogger;
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.writers.Writer;

/**
 * Parser for properties based configuration.
 *
 * @see Configuration
 */
public final class ConfigurationParser {

	private static final int MAX_LOCALE_ARGUMENTS = 3;

	/** */
	private ConfigurationParser() {
	}

	/**
	 * Loads the locale from configuration.
	 *
	 * @return Locale from configuration or {@link Locale#ROOT} if no locale is configured
	 */
	public static Locale getLocale() {
		String tag = Configuration.get("locale");
		if (tag == null) {
			return Locale.ROOT;
		} else {
			String[] splittedTag = tag.split("_", MAX_LOCALE_ARGUMENTS);
			if (splittedTag.length == 1) {
				return new Locale(splittedTag[0]);
			} else if (splittedTag.length == 2) {
				return new Locale(splittedTag[0], splittedTag[1]);
			} else {
				return new Locale(splittedTag[0], splittedTag[1], splittedTag[2]);
			}
		}
	}

	/**
	 * Loads the global severity level from configuration.
	 *
	 * @return Severity level from configuration or {@link Level#TRACE} if no severity level is configured
	 */
	public static Level getGlobalLevel() {
		return parse(Configuration.get("level"), Level.TRACE);
	}

	/**
	 * Loads custom severity levels for packages or classes from configuration.
	 *
	 * @return All found custom severity levels
	 */
	public static Map<String, Level> getCustomLevels() {
		Map<String, Level> levels = new HashMap<String, Level>();
		for (Entry<String, String> entry : Configuration.getSiblings("level@").entrySet()) {
			String packageOrClass = entry.getKey().substring("level@".length());
			Level level = parse(entry.getValue(), null);
			if (level != null) {
				levels.put(packageOrClass, level);
			}
		}
		return levels;
	}

	/**
	 * Loads all tags from writers in configuration.
	 *
	 * @return Found tags
	 */
	public static List<String> getTags() {
		List<String> tags = new ArrayList<String>();
		for (String writerProperty : Configuration.getSiblings("writer").keySet()) {
			String tag = Configuration.get(writerProperty + ".tag");
			if (tag != null && !tag.isEmpty() && !tag.equals("-") && !tags.contains(tag)) {
				tags.add(tag);
			}
		}
		return tags;
	}

	/**
	 * Detects whether writing thread is enabled in configuration.
	 *
	 * @return {@code true} if writing thread is explicitly enabled, otherwise {@code false}
	 */
	public static boolean isWritingThreadEnabled() {
		return "true".equalsIgnoreCase(Configuration.get("writingthread"));
	}

	/**
	 * Detects whether auto shutdown is enabled in configuration.
	 *
	 * @return {@code false} if auto shutdown is explicitly disabled, otherwise {@code true}
	 */
	public static boolean isAutoShutdownEnabled() {
		return !"false".equalsIgnoreCase(Configuration.get("autoshutdown"));
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
	public static Collection<Writer>[][] createWriters(final List<String> tags, final Level minimumLevel, final boolean writingThread) {
		Collection<Writer>[][] matrix = new Collection[tags.size() + 2][Level.values().length - 1];
		ServiceLoader<Writer> loader = new ServiceLoader<Writer>(Writer.class, Map.class);

		Map<String, String> writerProperties = Configuration.getSiblings("writer");

		if (writerProperties.isEmpty()) {
			writerProperties = Collections.singletonMap("writer", RuntimeProvider.getDefaultWriter());
		}

		for (Entry<String, String> entry : writerProperties.entrySet()) {
			Map<String, String> configuration = Configuration.getChildren(entry.getKey());

			String tag = configuration.get("tag");
			Level level = parse(configuration.get("level"), minimumLevel);
			if (level.ordinal() < minimumLevel.ordinal()) {
				level = minimumLevel;
			}

			configuration.remove("tag");
			configuration.remove("level");

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
					addWriter(writer, matrix, tags.indexOf(tag) + 1, level);
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
	private static void addWriter(final Writer writer, final Collection<Writer>[][] matrix, final int tagIndex, final Level level) {
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
	 * Reads a severity level from configuration.
	 *
	 * @param property
	 *            Key of property
	 * @param defaultValue
	 *            Default value, if property doesn't exist or is invalid
	 * @return Severity level
	 */
	private static Level parse(final String property, final Level defaultValue) {
		if (property == null) {
			return defaultValue;
		} else {
			try {
				return Level.valueOf(property.toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException ex) {
				InternalLogger.log(Level.ERROR, "Illegal severity level: " + property);
				return defaultValue;
			}
		}
	}

}
