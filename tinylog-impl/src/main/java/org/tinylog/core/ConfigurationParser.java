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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.provider.InternalLogger;

/**
 * Parser for properties based configuration.
 *
 * @see Configuration
 */
public final class ConfigurationParser {

	/** */
	private ConfigurationParser() {
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
			if (tag != null && !tag.isEmpty() && !tag.equals("-")) {
				String[] tagArray = tag.split(",");
				for (String tagArrayItem : tagArray) {
					tagArrayItem = tagArrayItem.replaceAll("@.*", "").trim();
					if (!tags.contains(tagArrayItem) && !tagArrayItem.isEmpty()) {
						tags.add(tagArrayItem);
					}
				}
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
		String enabled = Configuration.get("writingthread");
		return enabled != null && Boolean.parseBoolean(enabled.trim());
	}

	/**
	 * Detects whether auto shutdown is enabled in configuration.
	 *
	 * @return {@code false} if auto shutdown is explicitly disabled, otherwise {@code true}
	 */
	public static boolean isAutoShutdownEnabled() {
		String enabled = Configuration.get("autoshutdown");
		return enabled == null || Boolean.parseBoolean(enabled.trim());
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
	public static Level parse(final String property, final Level defaultValue) {
		if (property == null) {
			return defaultValue;
		} else {
			try {
				return Level.valueOf(property.trim().toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException ex) {
				InternalLogger.log(Level.ERROR, "Illegal severity level: " + property);
				return defaultValue;
			}
		}
	}

}
