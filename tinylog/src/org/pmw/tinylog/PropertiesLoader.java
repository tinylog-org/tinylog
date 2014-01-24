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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

import org.pmw.tinylog.labellers.Labeller;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * Loads configuration for {@link org.pmw.tinylog.Logger Logger} from properties.
 */
final class PropertiesLoader {

	private static final String LEVEL_PROPERTY = "tinylog.level";
	private static final String FORMAT_PROPERTY = "tinylog.format";
	private static final String LOCALE_PROPERTY = "tinylog.locale";
	private static final String STACKTRACE_PROPERTY = "tinylog.stacktrace";
	private static final String WRITER_PROPERTY = "tinylog.writer";
	private static final String WRITING_THREAD_PROPERTY = "tinylog.writingthread";
	private static final String WRITING_THREAD_OBSERVE_PROPERTY = WRITING_THREAD_PROPERTY + ".observe";
	private static final String WRITING_THREAD_PRIORITY_PROPERTY = WRITING_THREAD_PROPERTY + ".priority";

	private static final String SERVICES_PREFIX = "META-INF/services/";
	private static final String PACKAGE_LEVEL_PREFIX = LEVEL_PROPERTY + "@";

	private PropertiesLoader() {
	}

	/**
	 * Load configuration from properties.
	 * 
	 * @param properties
	 *            Properties with configuration
	 * @return A new configurator
	 */
	static Configurator readProperties(final Properties properties) {
		Configurator configurator = Configurator.defaultConfig();
		readProperties(configurator, properties);
		return configurator;
	}

	/**
	 * Load configuration from properties.
	 * 
	 * @param configurator
	 *            Configurator to update
	 * @param properties
	 *            Properties with configuration
	 */
	static void readProperties(final Configurator configurator, final Properties properties) {
		String level = properties.getProperty(LEVEL_PROPERTY);
		if (level != null && level.length() > 0) {
			try {
				configurator.level(LoggingLevel.valueOf(level.toUpperCase(Locale.ENGLISH)));
			} catch (IllegalArgumentException ex) {
				InternalLogger.warn("\"{0}\" is an invalid logging level and will be ignored", level);
			}
		}

		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(PACKAGE_LEVEL_PREFIX)) {
				String packageName = key.substring(PACKAGE_LEVEL_PREFIX.length());
				String value = properties.getProperty(key);
				try {
					LoggingLevel loggingLevel = LoggingLevel.valueOf(value.toUpperCase(Locale.ENGLISH));
					configurator.level(packageName, loggingLevel);
				} catch (IllegalArgumentException ex) {
					InternalLogger.warn("\"{0}\" is an invalid logging level and will be ignored", value);
					configurator.level(packageName, null);
				}
			}
		}

		String format = properties.getProperty(FORMAT_PROPERTY);
		if (format != null && format.length() > 0) {
			configurator.formatPattern(format);
		}

		String localeString = properties.getProperty(LOCALE_PROPERTY);
		if (localeString != null && localeString.length() > 0) {
			String[] localeArray = localeString.split("_", 3);
			if (localeArray.length == 1) {
				configurator.locale(new Locale(localeArray[0]));
			} else if (localeArray.length == 2) {
				configurator.locale(new Locale(localeArray[0], localeArray[1]));
			} else if (localeArray.length >= 3) {
				configurator.locale(new Locale(localeArray[0], localeArray[1], localeArray[2]));
			}
		}

		String stacktace = properties.getProperty(STACKTRACE_PROPERTY);
		if (stacktace != null && stacktace.length() > 0) {
			try {
				int limit = Integer.parseInt(stacktace);
				configurator.maxStackTraceElements(limit);
			} catch (NumberFormatException ex) {
				InternalLogger.warn("\"{0}\" is an invalid stack trace size and will be ignored", stacktace);
			}
		}

		String writer = properties.getProperty(WRITER_PROPERTY);
		if (writer != null && writer.length() > 0) {
			if (writer.equalsIgnoreCase("null")) {
				configurator.writer(null);
			} else {
				for (Class<?> implementation : findImplementations(LoggingWriter.class)) {
					org.pmw.tinylog.writers.PropertiesSupport propertiesSupport = implementation.getAnnotation(org.pmw.tinylog.writers.PropertiesSupport.class);
					if (propertiesSupport != null) {
						if (writer.equalsIgnoreCase(propertiesSupport.name())) {
							LoggingWriter loggingWriter = loadAndSetWriter(properties, propertiesSupport.properties(), implementation);
							if (loggingWriter != null) {
								configurator.writer(loggingWriter);
								break;
							}
						}
					}
				}
			}
		}

		String writingThread = properties.getProperty(WRITING_THREAD_PROPERTY);
		if ("true".equalsIgnoreCase(writingThread) || "1".equalsIgnoreCase(writingThread)) {
			String observedThread = properties.getProperty(WRITING_THREAD_OBSERVE_PROPERTY);
			boolean observedThreadDefined = observedThread != null;
			if (observedThreadDefined && observedThread.equalsIgnoreCase("null")) {
				observedThread = null;
			}
			String priorityString = properties.getProperty(WRITING_THREAD_PRIORITY_PROPERTY);
			Integer priority;
			if (priorityString == null) {
				priority = null;
			} else {
				try {
					priority = Integer.parseInt(priorityString.trim());
				} catch (NumberFormatException ex) {
					priority = null;
					InternalLogger.warn("\"{0}\" is an invalid thread priority and will be ignored", priorityString);
				}
			}
			if (priority != null && observedThreadDefined) {
				configurator.writingThread(observedThread, priority);
			} else if (priority != null) {
				configurator.writingThread(priority);
			} else if (observedThreadDefined) {
				configurator.writingThread(observedThread);
			} else {
				configurator.writingThread(true);
			}
		} else {
			configurator.writingThread(false);
		}
	}

	private static Collection<Class<?>> findImplementations(final Class<?> service) {
		try {
			Enumeration<URL> urls = PropertiesLoader.class.getClassLoader().getResources(SERVICES_PREFIX + service.getPackage().getName());
			if (urls == null || !urls.hasMoreElements()) {
				return Collections.emptyList();
			} else {
				Collection<Class<?>> services = new ArrayList<Class<?>>();
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					InputStream inputStream = null;
					BufferedReader reader = null;
					try {
						inputStream = url.openStream();
						reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
						for (String line = reader.readLine(); line != null; line = reader.readLine()) {
							line = line.trim();
							if (line != null) {
								try {
									Class<?> implementation = Class.forName(line);
									if (service.isAssignableFrom(implementation)) {
										services.add(implementation);
									}
								} catch (ClassNotFoundException ex) {
									InternalLogger.warn("Cannot find class \"{0}\"", line);
								}
							}
						}
					} finally {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException ex) {
								// Ignore
							}
						}
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException ex) {
								// Ignore
							}
						}
					}
				}
				return services;
			}
		} catch (IOException ex) {
			InternalLogger.error(ex, "Failed to read services from \"{0}\"", SERVICES_PREFIX + service.getPackage().getName());
			return Collections.emptyList();
		}
	}

	private static LoggingWriter loadAndSetWriter(final Properties properties, final org.pmw.tinylog.writers.Property[] definition, final Class<?> writerClass) {
		Object[] parameters = loadParameters(properties, definition);

		if (parameters != null) {
			for (Constructor<?> constructor : writerClass.getConstructors()) {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				if (parameterTypes.length <= definition.length) {
					BitSet skiped = new BitSet(definition.length);
					boolean matches = true;
					int offset = 0;
					for (int i = 0; i < definition.length; ++i) {
						if (i - offset >= parameterTypes.length || !parameterTypes[i - offset].equals(definition[i].type())) {
							if (definition[i].optional() && parameters[i] == null) {
								skiped.set(i);
								++offset;
							} else {
								matches = false;
								break;
							}
						}
					}
					if (matches) {
						try {
							if (parameters.length > parameterTypes.length) {
								List<Object> list = new ArrayList<Object>();
								for (int i = 0; i < parameters.length; ++i) {
									if (!skiped.get(i)) {
										list.add(parameters[i]);
									}
								}
								parameters = list.toArray();
							}
							return (LoggingWriter) constructor.newInstance(parameters);
						} catch (IllegalArgumentException ex) {
							InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", writerClass.getName());
							return null;
						} catch (InstantiationException ex) {
							InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", writerClass.getName());
							return null;
						} catch (IllegalAccessException ex) {
							InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", writerClass.getName());
							return null;
						} catch (InvocationTargetException ex) {
							InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", writerClass.getName());
							return null;
						}
					}
				}
			}
		}

		return null;
	}

	private static Object[] loadParameters(final Properties properties, final org.pmw.tinylog.writers.Property[] definition) {
		Object[] parameters = new Object[definition.length];

		for (int i = 0; i < definition.length; ++i) {
			String name = definition[i].name();
			String value = properties.getProperty(WRITER_PROPERTY + "." + name);
			if (value == null) {
				if (definition[i].optional()) {
					parameters[i] = null;
				} else {
					return null;
				}
			} else {
				Class<?> type = definition[i].type();
				if (String.class.equals(type)) {
					parameters[i] = value;
				} else if (int.class.equals(type)) {
					try {
						parameters[i] = Integer.parseInt(value);
					} catch (NumberFormatException ex) {
						InternalLogger.warn("\"{1}\" for \"{0}\" is an invalid number and will be ignored", WRITER_PROPERTY + "." + name, value);
						return null;
					}
				} else if (boolean.class.equals(type)) {
					if ("true".equalsIgnoreCase(value)) {
						parameters[i] = Boolean.TRUE;
					} else if ("false".equalsIgnoreCase(value)) {
						parameters[i] = Boolean.FALSE;
					} else {
						InternalLogger.warn("\"{1}\" for \"{0}\" is an invalid boolean and will be ignored", WRITER_PROPERTY + "." + name, value);
						return null;
					}
				} else if (Labeller.class.equals(type)) {
					Object labeller = parseLabeller(value);
					if (labeller == null) {
						return null;
					} else {
						parameters[i] = labeller;
					}
				} else if (Policy.class.equals(type)) {
					Object policy = parsePolicy(value);
					if (policy == null) {
						return null;
					} else {
						parameters[i] = policy;
					}
				} else if (Policy[].class.equals(type)) {
					Policy[] policies = parsePolicies(value);
					if (policies == null) {
						return null;
					} else if (policies.length == 0) {
						return null;
					} else {
						parameters[i] = policies;
					}
				} else {
					return null;
				}
			}
		}

		return parameters;
	}

	private static Policy[] parsePolicies(final String string) {
		List<Policy> policies = new ArrayList<Policy>();
		for (String part : string.split(Pattern.quote(","))) {
			Policy policy = parsePolicy(part.trim());
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

		for (Class<?> implementation : findImplementations(Policy.class)) {
			org.pmw.tinylog.policies.PropertiesSupport propertiesSupport = implementation.getAnnotation(org.pmw.tinylog.policies.PropertiesSupport.class);
			if (propertiesSupport != null) {
				if (name.equalsIgnoreCase(propertiesSupport.name())) {
					return (Policy) createInstance(implementation, parameter);
				}
			}
		}

		return null;
	}

	private static Labeller parseLabeller(final String string) {
		int separator = string.indexOf(':');
		String name = separator > 0 ? string.substring(0, separator).trim() : string.trim();
		String parameter = separator > 0 ? string.substring(separator + 1).trim() : null;

		for (Class<?> implementation : findImplementations(Labeller.class)) {
			org.pmw.tinylog.labellers.PropertiesSupport propertiesSupport = implementation.getAnnotation(org.pmw.tinylog.labellers.PropertiesSupport.class);
			if (propertiesSupport != null) {
				if (name.equalsIgnoreCase(propertiesSupport.name())) {
					return (Labeller) createInstance(implementation, parameter);
				}
			}
		}

		return null;
	}

	private static Object createInstance(final Class<?> clazz, final String parameter) {
		try {
			if (parameter == null) {
				Constructor<?> constructor = clazz.getDeclaredConstructor();
				constructor.setAccessible(true);
				return constructor.newInstance();
			} else {
				Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
				constructor.setAccessible(true);
				return constructor.newInstance(parameter);
			}
		} catch (InstantiationException ex) {
			InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", clazz.getName());
			return null;
		} catch (IllegalAccessException ex) {
			InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", clazz.getName());
			return null;
		} catch (IllegalArgumentException ex) {
			InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", clazz.getName());
			return null;
		} catch (InvocationTargetException ex) {
			InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", clazz.getName());
			return null;
		} catch (NoSuchMethodException ex) {
			InternalLogger.error(ex, "Failed to create an instance of \"{0}\"", clazz.getName());
			return null;
		}
	}

}
