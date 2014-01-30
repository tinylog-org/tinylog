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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * Configurator to configure {@link org.pmw.tinylog.Logger Logger}.
 */
public final class Configurator {

	private static final String DEFAULT_PROPERTIES_FILE = "tinylog.properties";
	private static final int DEFAULT_MAX_STACK_TRACE_ELEMENTS = 40;
	private static final String DEFAULT_FORMAT_PATTERN = "{date} [{thread}] {class}.{method}()\n{level}: {message}";
	private static final String DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD = "main";
	private static final int DEFAULT_PRIORITY_FOR_WRITING_THREAD = (Thread.MIN_PRIORITY + Thread.NORM_PRIORITY) / 2;

	private static WritingThread activeWritingThread = null;
	private static final Object lock = new Object();

	private LoggingLevel level;
	private Map<String, LoggingLevel> customLevels;
	private String formatPattern;
	private Locale locale;
	private LoggingWriter writer;
	private WritingThreadData writingThreadData;
	private int maxStackTraceElements;

	/**
	 * @param level
	 *            Logging level
	 * @param customLevels
	 *            Custom logging levels for specific packages and classes
	 * @param formatPattern
	 *            Format pattern for log entries
	 * @param locale
	 *            Locale for format pattern
	 * @param writer
	 *            Logging writer (can be <code>null</code> to disable any output)
	 * @param writingThreadData
	 *            Data for writing thread (can be <code>null</code> to write log entries synchronously)
	 * @param maxStackTraceElements
	 *            Limit of stack traces for exceptions
	 */
	Configurator(final LoggingLevel level, final Map<String, LoggingLevel> customLevels, final String formatPattern, final Locale locale,
			final LoggingWriter writer, final WritingThreadData writingThreadData, final int maxStackTraceElements) {
		this.level = level;
		this.customLevels = customLevels;
		this.formatPattern = formatPattern;
		this.locale = locale;
		this.writer = writer;
		this.writingThreadData = writingThreadData;
		this.maxStackTraceElements = maxStackTraceElements;
	}

	/**
	 * Create a new configurator, based on the default configuration.
	 * 
	 * @return A new configurator
	 */
	public static Configurator defaultConfig() {
		return new Configurator(LoggingLevel.INFO, Collections.<String, LoggingLevel> emptyMap(), DEFAULT_FORMAT_PATTERN, Locale.getDefault(),
				new ConsoleWriter(), null, DEFAULT_MAX_STACK_TRACE_ELEMENTS);
	}

	/**
	 * Create a new configurator, based on the current configuration.
	 * 
	 * @return A new configurator
	 */
	public static Configurator currentConfig() {
		return Logger.getConfiguration();
	}

	/**
	 * Load a properties file from classpath.
	 * 
	 * @param file
	 *            Path to file to load
	 * @return A new configurator
	 * @throws IOException
	 *             Failed to load and read file
	 */
	public static Configurator fromResource(final String file) throws IOException {
		Properties properties = new Properties();

		InputStream stream = Configurator.class.getClassLoader().getResourceAsStream(file);
		if (stream == null) {
			throw new FileNotFoundException(file);
		} else {
			try {
				shutdownWritingThread(true);
				properties.load(stream);
			} finally {
				stream.close();
			}
		}

		return PropertiesLoader.readProperties(properties);
	}

	/**
	 * Load a properties file from file system.
	 * 
	 * @param file
	 *            File to load
	 * @return A new configurator
	 * @throws IOException
	 *             Failed to load and read file
	 */
	public static Configurator fromFile(final File file) throws IOException {
		Properties properties = new Properties();

		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			shutdownWritingThread(true);
			properties.load(stream);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

		return PropertiesLoader.readProperties(properties);
	}

	/**
	 * Change the logging level. The logger creates and outputs only log entries for the current logging level and
	 * higher.
	 * 
	 * @param level
	 *            New logging level
	 * @return The current configurator
	 */
	public Configurator level(final LoggingLevel level) {
		if (level == null) {
			this.level = LoggingLevel.OFF;
		} else {
			this.level = level;
		}
		return this;
	}

	/**
	 * Set a custom logging level for a package.
	 * 
	 * This will override the default logging level for this package.
	 * 
	 * @param packageObject
	 *            Package
	 * @param level
	 *            The logging level (or <code>null</code> to reset it to the default logging level)
	 * @return The current configurator
	 */
	public Configurator level(final Package packageObject, final LoggingLevel level) {
		return level(packageObject.getName(), level);
	}

	/**
	 * Set a custom logging level for a class.
	 * 
	 * This will override the default logging level for this class.
	 * 
	 * @param classObject
	 *            Class
	 * @param level
	 *            The logging level (or <code>null</code> to reset it to the default logging level)
	 * @return The current configurator
	 */
	public Configurator level(final Class<?> classObject, final LoggingLevel level) {
		return level(classObject.getName(), level);
	}

	/**
	 * Set a custom logging level for a package or class.
	 * 
	 * This will override the default logging level for this package respectively class.
	 * 
	 * @param nameOfpackageOrClass
	 *            Name of a package or class
	 * @param level
	 *            The logging level (or <code>null</code> to reset it to the default logging level)
	 * @return The current configurator
	 */
	public Configurator level(final String nameOfpackageOrClass, final LoggingLevel level) {
		if (level == null) {
			if (!customLevels.isEmpty()) {
				customLevels.remove(nameOfpackageOrClass);
				if (customLevels.isEmpty()) {
					customLevels = Collections.emptyMap();
				}
			}
		} else {
			if (customLevels.isEmpty()) {
				customLevels = new HashMap<String, LoggingLevel>();
			}
			customLevels.put(nameOfpackageOrClass, level);
		}
		return this;
	}

	/**
	 * Reset all custom logging levels (to use the default logging level again).
	 * 
	 * @return The current configurator
	 */
	public Configurator resetCustomLevels() {
		customLevels = Collections.emptyMap();
		return this;
	}

	/**
	 * Set the format pattern for log entries.
	 * <code>"{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}()\n{level}: {message}"</code> is the default format
	 * pattern. The date format pattern is compatible with {@link SimpleDateFormat}.
	 * 
	 * @param formatPattern
	 *            Format pattern for log entries (or <code>null</code> to reset to default)
	 * @return The current configurator
	 * 
	 * @see SimpleDateFormat
	 */
	public Configurator formatPattern(final String formatPattern) {
		if (formatPattern == null) {
			this.formatPattern = DEFAULT_FORMAT_PATTERN;
		} else {
			this.formatPattern = formatPattern;
		}
		return this;
	}

	/**
	 * Set the locale that is used to render format patterns for log entries.
	 * 
	 * It will be used e. g. to format numbers and dates.
	 * 
	 * @param locale
	 *            Locale for format patterns
	 * @return The current configurator
	 */
	public Configurator locale(final Locale locale) {
		if (locale == null) {
			this.locale = Locale.getDefault();
		} else {
			this.locale = locale;
		}
		return this;
	}

	/**
	 * Set a logging writer for outputting the created log entries.
	 * 
	 * @param writer
	 *            Logging writer (can be <code>null</code> to disable any output)
	 * @return The current configurator
	 */
	public Configurator writer(final LoggingWriter writer) {
		this.writer = writer;
		return this;
	}

	/**
	 * The writing thread can writes log entries asynchronously. This thread will automatically shutdown, if the main
	 * thread is dead.
	 * 
	 * @param enable
	 *            <code>true</code> to enable the writing thread, <code>false</code> to disable it
	 * @return The current configurator
	 */
	public Configurator writingThread(final boolean enable) {
		if (enable) {
			return writingThread(DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD, DEFAULT_PRIORITY_FOR_WRITING_THREAD);
		} else {
			this.writingThreadData = null;
			return this;
		}
	}

	/**
	 * The writing thread can writes log entries asynchronously. This thread will automatically shutdown, if the main
	 * thread is dead.
	 * 
	 * @param priority
	 *            Priority of the writing thread (must be between {@link Thread#MIN_PRIORITY} and
	 *            {@link Thread#MAX_PRIORITY})
	 * @return The current configurator
	 */
	public Configurator writingThread(final int priority) {
		return writingThread(DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD, priority);
	}

	/**
	 * The writing thread can writes log entries asynchronously. This thread will automatically shutdown, if the
	 * observed thread is dead.
	 * 
	 * @param threadToObserve
	 *            Name of the tread to observe (e.g. "main" for the main thread) or <code>null</code> to disable
	 *            automatic shutdown
	 * @return The current configurator
	 */
	public Configurator writingThread(final String threadToObserve) {
		return writingThread(threadToObserve, DEFAULT_PRIORITY_FOR_WRITING_THREAD);
	}

	/**
	 * The writing thread can writes log entries asynchronously. This thread will automatically shutdown, if the
	 * observed thread is dead.
	 * 
	 * @param threadToObserve
	 *            Name of the tread to observe (e.g. "main" for the main thread) or <code>null</code> to disable
	 *            automatic shutdown
	 * @param priority
	 *            Priority of the writing thread (must be between {@link Thread#MIN_PRIORITY} and
	 *            {@link Thread#MAX_PRIORITY})
	 * @return The current configurator
	 */
	public Configurator writingThread(final String threadToObserve, final int priority) {
		this.writingThreadData = new WritingThreadData(threadToObserve, priority);
		return this;
	}

	/**
	 * Set the limit of stack traces for exceptions (default is 40). Can be set to "-1" for no limitation and to "0" to
	 * disable any stack traces.
	 * 
	 * @param maxStackTraceElements
	 *            Limit of stack traces
	 * @return The current configurator
	 */
	public Configurator maxStackTraceElements(final int maxStackTraceElements) {
		if (maxStackTraceElements < 0) {
			this.maxStackTraceElements = Integer.MAX_VALUE;
		} else {
			this.maxStackTraceElements = maxStackTraceElements;
		}
		return this;
	}

	/**
	 * Activate the configuration.
	 * 
	 * @return <code>true</code> if the configuration has been successfully activated, <code>false</code> if the
	 *         activation failed
	 */
	public boolean activate() {
		synchronized (lock) {
			if (activeWritingThread != null && (writingThreadData == null || !writingThreadData.covers(activeWritingThread))) {
				activeWritingThread.shutdown();
				activeWritingThread = null;
			}

			Configuration configuration = create();
			try {
				Logger.setConfirguration(configuration);
			} catch (Exception ex) {
				InternalLogger.error(ex, "Failed to activate configuration");
				return false;
			}

			if (activeWritingThread == null && writingThreadData != null) {
				activeWritingThread = configuration.getWritingThread();
				activeWritingThread.start();
			}

			return true;
		}
	}

	/**
	 * Manually shutdown of writing thread.
	 * 
	 * @param wait
	 *            <code>true</code> to wait for the successful shutdown, <code>false</code> for an asynchronous shutdown
	 */
	public static void shutdownWritingThread(final boolean wait) {
		synchronized (lock) {
			if (activeWritingThread != null) {
				activeWritingThread.shutdown();
				if (wait) {
					boolean finished;
					do {
						try {
							activeWritingThread.join();
							finished = true;
						} catch (InterruptedException ex) {
							finished = false;
						}
					} while (!finished);
				}
				activeWritingThread = null;
			}
		}
	}

	/**
	 * Manually shutdown of configuration observer.
	 * 
	 * @param wait
	 *            <code>true</code> to wait for the successful shutdown, <code>false</code> for an asynchronous shutdown
	 */
	public static void shutdownConfigurationObserver(final boolean wait) {
		ConfigurationObserver observer = ConfigurationObserver.getActiveObserver();
		if (observer != null) {
			observer.shutdown();
			if (wait) {
				while (true) {
					try {
						observer.join();
						break;
					} catch (InterruptedException ex) {
						continue;
					}
				}
			}
		}
	}

	/**
	 * Load properties from environment variables (also know as "-D" parameter) and from the default properties file
	 * "tinylog.properties", which must be placed in the default package.
	 * 
	 * @return A new configurator
	 */
	static Configurator init() {
		Properties properties = new Properties();

		String file = System.getProperty("tinylog.configuration", DEFAULT_PROPERTIES_FILE);
		InputStream stream = Configurator.class.getClassLoader().getResourceAsStream(file);
		boolean isResource = true;
		if (stream == null) {
			try {
				stream = new FileInputStream(file);
				isResource = false;
			} catch (FileNotFoundException ex) {
				if (file != DEFAULT_PROPERTIES_FILE) {
					InternalLogger.error(ex, "Cannot find \"{0}\"", file);
				}
			}
		}
		if (stream != null) {
			try {
				try {
					properties.load(stream);
				} finally {
					stream.close();
				}
			} catch (IOException ex) {
				InternalLogger.error(ex, "Failed to read properties file \"{0}\"", file);
			}
		}

		if (stream == null) {
			return Configurator.defaultConfig();
		} else {
			Properties systemProperties = System.getProperties();
			for (Object key : systemProperties.keySet()) {
				String name = (String) key;
				if (name.startsWith("tinylog.")) {
					properties.put(name, systemProperties.getProperty(name));
				}
			}

			if ("true".equalsIgnoreCase(properties.getProperty("tinylog.configuration.observe"))) {
				shutdownWritingThread(true);
				Configurator configurator = PropertiesLoader.readProperties(properties);
				if (isResource) {
					ConfigurationObserver.createResourceConfigurationObserver(configurator, properties, file).start();
				} else {
					ConfigurationObserver.createFileConfigurationObserver(configurator, properties, file).start();
				}
				return configurator;
			} else {
				shutdownWritingThread(true);
				return PropertiesLoader.readProperties(properties);
			}
		}
	}

	/**
	 * Copy the configurator.
	 * 
	 * @return A new configurator with the same configuration
	 */
	Configurator copy() {
		WritingThreadData writingThreadDataCopy = writingThreadData == null ? null : new WritingThreadData(writingThreadData.threadToObserve,
				writingThreadData.priority);
		return new Configurator(level, customLevels, formatPattern, locale, writer, writingThreadDataCopy, maxStackTraceElements);
	}

	/**
	 * Create the configuration.
	 * 
	 * @return The created configuration
	 */
	Configuration create() {
		WritingThread writingThread;
		if (writingThreadData == null) {
			writingThread = null;
		} else {
			writingThread = new WritingThread(writingThreadData.threadToObserve, writingThreadData.priority);
			if (writingThreadData.threadToObserve != null && writingThread.getThreadToObserve() == null) {
				InternalLogger.warn("Thread \"{0}\" couldn't be found, writing thread won't be used", writingThreadData.threadToObserve);
				writingThread = null;
			}
		}

		return new Configuration(level, customLevels, formatPattern, locale, writer, writingThread, maxStackTraceElements);
	}

	/**
	 * Data for {@link org.pmw.tinylog.WritingThread}.
	 */
	static final class WritingThreadData {

		private final String threadToObserve;
		private final int priority;

		/**
		 * @param threadToObserve
		 *            Name of the tread to observe (e.g. "main" for the main thread) or <code>null</code> to disable
		 *            automatic shutdown
		 * @param priority
		 *            Priority of the writing thread (must be between {@link Thread#MIN_PRIORITY} and
		 *            {@link Thread#MAX_PRIORITY})
		 */
		WritingThreadData(final String threadToObserve, final int priority) {
			this.threadToObserve = threadToObserve;
			this.priority = priority;
		}

		/**
		 * Test if this writing thread has the same parameters as another writing thread.
		 * 
		 * @param writingThread
		 *            Writing thread to compare
		 * @return <code>true</code> if both writing threads have the same parameters, <code>false</code> if not
		 */
		boolean covers(final WritingThread writingThread) {
			if (writingThread == null) {
				return false;
			}
			if (threadToObserve == null) {
				if (writingThread.getNameOfThreadToObserve() != null) {
					return false;
				}
			} else if (!threadToObserve.equals(writingThread.getNameOfThreadToObserve())) {
				return false;
			}
			return priority == writingThread.getPriority();
		}
	}

}
