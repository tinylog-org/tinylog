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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.Properties;

/**
 * Load properties form file and from environment variables.
 */
final class PropertiesLoader {

	private static final String LEVEL_PROPERTY = "tinylog.level";
	private static final String FORMAT_PROPERTY = "tinylog.format";
	private static final String LOCALE_PROPERTY = "tinylog.locale";
	private static final String STACKTRACE_PROPERTY = "tinylog.stacktrace";
	private static final String WRITER_PROPERTY = "tinylog.writer";

	private static final String[] ALL_PROPERTIES = new String[] { LEVEL_PROPERTY, FORMAT_PROPERTY, LOCALE_PROPERTY, STACKTRACE_PROPERTY, WRITER_PROPERTY };
	private static final String PROPERTIES_FILE = "/tinylog.properties";

	private PropertiesLoader() {
	}

	/**
	 * Do load properties.
	 */
	static void load() {
		Properties properties = getPropertiesFromFile();
		properties.putAll(getPropertiesFromEnviroment());
		readProperties(properties);
	}

	private static Properties getPropertiesFromFile() {
		Properties properties = new Properties();

		InputStream stream = Logger.class.getResourceAsStream(PROPERTIES_FILE);
		if (stream != null) {
			try {
				properties.load(stream);
			} catch (IOException ex) {
				// Ignore
			}
		}

		return properties;
	}

	private static Properties getPropertiesFromEnviroment() {
		Properties properties = new Properties();

		for (String key : ALL_PROPERTIES) {
			String value = System.getProperty(key);
			if (value != null && !value.isEmpty()) {
				properties.setProperty(key, value);
			}
		}

		return properties;
	}

	private static void readProperties(final Properties properties) {
		String level = properties.getProperty(LEVEL_PROPERTY);
		if (level != null && !level.isEmpty()) {
			try {
				Logger.setLoggingLevel(ELoggingLevel.valueOf(level.toUpperCase()));
			} catch (IllegalArgumentException ex) {
				// Ignore
			}
		}

		String format = properties.getProperty(FORMAT_PROPERTY);
		if (format != null && !format.isEmpty()) {
			Logger.setLoggingFormat(format);
		}

		String localeString = properties.getProperty(LOCALE_PROPERTY);
		if (localeString != null && !localeString.isEmpty()) {
			String[] localeArray = localeString.split("_", 3);
			if (localeArray.length == 1) {
				Logger.setLocale(new Locale(localeArray[0]));
			} else if (localeArray.length == 2) {
				Logger.setLocale(new Locale(localeArray[0], localeArray[1]));
			} else if (localeArray.length >= 3) {
				Logger.setLocale(new Locale(localeArray[0], localeArray[1], localeArray[2]));
			}
		}

		String stacktace = properties.getProperty(STACKTRACE_PROPERTY);
		if (stacktace != null && !stacktace.isEmpty()) {
			try {
				int limit = Integer.parseInt(stacktace);
				Logger.setMaxStackTraceElements(limit);
			} catch (NumberFormatException ex) {
				// Ignore
			}
		}

		String writer = properties.getProperty(WRITER_PROPERTY);
		if (writer != null && !writer.isEmpty()) {
			if (writer.equals("null")) {
				Logger.setWriter(null);
			} else if (writer.equals("console")) {
				Logger.setWriter(new ConsoleLoggingWriter());
			} else if (writer.startsWith("file:")) {
				String filename = writer.substring(5);
				if (filename != null && !filename.isEmpty()) {
					try {
						Logger.setWriter(new FileLoggingWriter(filename));
					} catch (IOException ex) {
						// Ignore
					}
				}
			} else {
				ILoggingWriter writerInstance;
				if (writer.contains(":")) {
					String className = writer.substring(0, writer.indexOf(':'));
					String parameter = writer.substring(writer.indexOf(':') + 1);
					writerInstance = createInstance(className, parameter);
				} else {
					String className = writer;
					writerInstance = createInstance(className, null);
				}
				if (writerInstance != null) {
					Logger.setWriter(writerInstance);
				}
			}
		}
	}

	private static ILoggingWriter createInstance(final String className, final String parameter) {
		try {
			Class<?> clazz = Class.forName(className);
			if (ILoggingWriter.class.isAssignableFrom(clazz)) {
				if (parameter == null) {
					Constructor<?> constructor = clazz.getConstructor();
					if (constructor != null) {
						return (ILoggingWriter) constructor.newInstance();
					}
				} else {
					Constructor<?> constructor = clazz.getConstructor(String.class);
					if (constructor != null) {
						return (ILoggingWriter) constructor.newInstance(parameter);
					}
				}
			}
		} catch (Exception ex) {
			// Ignore
		}
		return null;
	}

}
