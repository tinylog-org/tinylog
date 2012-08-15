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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.HourlyPolicy;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.MonthlyPolicy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.policies.WeeklyPolicy;
import org.pmw.tinylog.policies.YearlyPolicy;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import org.pmw.tinylog.writers.LoggingWriter;
import org.pmw.tinylog.writers.RollingFileWriter;

/**
 * Loads and sets properties for {@link Logger} from the properties files and from environment variables.
 */
public final class PropertiesLoader {

	private static final String LEVEL_PROPERTY = "tinylog.level";
	private static final String FORMAT_PROPERTY = "tinylog.format";
	private static final String LOCALE_PROPERTY = "tinylog.locale";
	private static final String STACKTRACE_PROPERTY = "tinylog.stacktrace";
	private static final String WRITER_PROPERTY = "tinylog.writer";

	private static final String PACKAGE_LEVEL_PREFIX = LEVEL_PROPERTY + ":";
	private static final String PROPERTIES_FILE = "/tinylog.properties";

	private PropertiesLoader() {
	}

	/**
	 * Reload properties from environment variables and from the default properties file ("/tinylog.properties").
	 */
	public static void reload() {
		Properties properties = getPropertiesFromFile(PROPERTIES_FILE);
		properties.putAll(System.getProperties());
		readProperties(properties);
	}

	/**
	 * Load properties from a properties file.
	 * 
	 * @param file
	 *            File in classpath to load
	 */
	public static void loadFromFile(final String file) {
		Properties properties = getPropertiesFromFile(file);
		readProperties(properties);
	}

	private static Properties getPropertiesFromFile(final String file) {
		Properties properties = new Properties();

		InputStream stream = Logger.class.getResourceAsStream(file);
		if (stream != null) {
			try {
				properties.load(stream);
			} catch (IOException ex) {
				// Ignore
			}
		}

		return properties;
	}

	private static void readProperties(final Properties properties) {
		String level = properties.getProperty(LEVEL_PROPERTY);
		if (level != null && !level.isEmpty()) {
			try {
				Logger.setLoggingLevel(ELoggingLevel.valueOf(level.toUpperCase(Locale.ENGLISH)));
			} catch (IllegalArgumentException ex) {
				// Ignore
			}
		}

		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(PACKAGE_LEVEL_PREFIX)) {
				String packageName = key.substring(PACKAGE_LEVEL_PREFIX.length());
				String value = properties.getProperty(key);
				try {
					ELoggingLevel loggingLevel = ELoggingLevel.valueOf(value.toUpperCase(Locale.ENGLISH));
					Logger.setLoggingLevel(packageName, loggingLevel);
				} catch (IllegalArgumentException ex) {
					// Illegal logging level => reset
					Logger.resetLoggingLevel(packageName);
				}
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
			} else {
				if (writer.equals("console")) {
					writer = ConsoleWriter.class.getName();
				} else if (writer.equals("file")) {
					writer = FileWriter.class.getName();
				} else if (writer.equals("rollingfile")) {
					writer = RollingFileWriter.class.getName();
				}
				loadAndSetWriter(properties, writer);
			}
		}
	}

	private static void loadAndSetWriter(final Properties properties, final String writer) {
		try {
			Class<?> writerClass = Class.forName(writer);
			if (LoggingWriter.class.isAssignableFrom(writerClass)) {
				String[][] supportedProperties = getSupportedProperties(writerClass);
				Constructor<?> foundConstructor = null;
				Object[] foundParameters = null;
				for (Constructor<?> constructor : writerClass.getConstructors()) {
					Class<?>[] parameterTypes = constructor.getParameterTypes();
					String[] propertiesNames = findPropertyNames(supportedProperties, parameterTypes.length);
					if (propertiesNames != null) {
						if (foundParameters == null || foundParameters.length < parameterTypes.length) {
							Object[] parameters = loadParameters(properties, propertiesNames, parameterTypes);
							if (parameters != null) {
								foundConstructor = constructor;
								foundParameters = parameters;
							}
						}
					}
				}
				if (foundConstructor != null) {
					Logger.setWriter((LoggingWriter) foundConstructor.newInstance(foundParameters));
				}
			}
		} catch (Exception ex) {
			// Failed to create writer => keep old writer
		}
	}

	private static String[][] getSupportedProperties(final Class<?> writerClass) {
		try {
			Method method = writerClass.getMethod("getSupportedProperties");
			if (method.getReturnType() == String[][].class && Modifier.isStatic(method.getModifiers())) {
				return (String[][]) method.invoke(null);
			} else {
				return new String[][] { new String[] {} };
			}
		} catch (Exception ex) {
			return new String[][] { new String[] {} };
		}
	}

	private static String[] findPropertyNames(final String[][] supportedProperties, final int numParameters) {
		for (String[] propertyNames : supportedProperties) {
			if (propertyNames.length == numParameters) {
				return propertyNames;
			}
		}
		return null;
	}

	private static Object[] loadParameters(final Properties properties, final String[] propertyNames, final Class<?>[] parameterTypes) {
		Object[] parameters = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; ++i) {
			String name = propertyNames[i];
			String value = properties.getProperty(WRITER_PROPERTY + "." + name);
			if (value != null) {
				Class<?> type = parameterTypes[i];
				if (String.class.equals(type)) {
					parameters[i] = value;
				} else if (int.class.equals(type)) {
					try {
						parameters[i] = Integer.parseInt(value);
					} catch (NumberFormatException ex) {
						return null;
					}
				} else if (boolean.class.equals(type)) {
					parameters[i] = "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value);
				} else if (Policy.class.equals(type)) {
					parameters[i] = parsePolicy(value);
				} else if (Policy[].class.equals(type)) {
					parameters[i] = parsePolicies(value);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return parameters;
	}

	private static Policy[] parsePolicies(final String string) {
		List<Policy> policies = new ArrayList<Policy>();
		for (String part : string.split(Pattern.quote(", "))) {
			Policy policy = parsePolicy(part);
			if (policy != null) {
				policies.add(policy);
			}
		}
		return policies.toArray(new Policy[0]);
	}

	private static Policy parsePolicy(final String string) {
		int separator = string.indexOf(':');
		String name = separator > 0 ? string.substring(0, separator).trim() : string.trim();
		String parameter = separator > 0 ? string.substring(separator + 1).trim() : null;

		if (name.equals("startup")) {
			name = StartupPolicy.class.getName();
		} else if (name.equals("size")) {
			name = SizePolicy.class.getName();
		} else if (name.equals("hourly")) {
			name = HourlyPolicy.class.getName();
		} else if (name.equals("daily")) {
			name = DailyPolicy.class.getName();
		} else if (name.equals("weekly")) {
			name = WeeklyPolicy.class.getName();
		} else if (name.equals("monthly")) {
			name = MonthlyPolicy.class.getName();
		} else if (name.equals("yearly")) {
			name = YearlyPolicy.class.getName();
		}

		try {
			Class<?> policyClass = Class.forName(name);
			if (Policy.class.isAssignableFrom(policyClass)) {
				if (parameter != null) {
					try {
						Constructor<?> constructor = policyClass.getDeclaredConstructor(String.class);
						constructor.setAccessible(true);
						return (Policy) constructor.newInstance(parameter);
					} catch (NoSuchMethodException ex) {
						// Continue
					}
				}
				Constructor<?> constructor = policyClass.getDeclaredConstructor();
				constructor.setAccessible(true);
				return (Policy) constructor.newInstance();
			} else {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

}
