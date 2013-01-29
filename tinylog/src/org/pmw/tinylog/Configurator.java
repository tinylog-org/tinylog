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
 * Configurator to configure {@link Logger}.
 */
public final class Configurator {

	private static final String DEFAULT_PROPERTIES_FILE = "/tinylog.properties";
	private static final int DEFAULT_MAX_STACK_TRACE_ELEMENTS = 40;
	private static final String DEFAULT_FORMAT_PATTERN = "{date} [{thread}] {class}.{method}()\n{level}: {message}";
	private static final String DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD = "main";
	private static final int DEFAULT_PRIORITY_FOR_WRITING_THREAD = 2;

	private static WritingThread activeWritingThread = null;
	private static final Object lock = new Object();

	private LoggingLevel level;
	private Map<String, LoggingLevel> packageLevels;
	private String formatPattern;
	private Locale locale;
	private LoggingWriter writer;
	private WritingThreadData writingThreadData;
	private int maxStackTraceElements;

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
	 * @param writingThreadData
	 *            Data for writing thread (can be <code>null</code> to write log entries synchronously)
	 * @param maxStackTraceElements
	 *            Limit of stack traces for exceptions
	 */
	Configurator(final LoggingLevel level, final Map<String, LoggingLevel> packageLevels, final String formatPattern, final Locale locale,
			final LoggingWriter writer, final WritingThreadData writingThreadData, final int maxStackTraceElements) {
		this.level = level;
		this.packageLevels = packageLevels;
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

		InputStream stream = Configurator.class.getResourceAsStream(file);
		if (stream != null) {
			try {
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
	 * Set a separate logging level for a particular package.
	 * 
	 * This will override the default logging level for this package.
	 * 
	 * @param packageName
	 *            Name of the package
	 * @param level
	 *            The logging level (or <code>null</code> to reset it to the default logging level)
	 * @return The current configurator
	 */
	public Configurator level(final String packageName, final LoggingLevel level) {
		if (level == null) {
			if (!packageLevels.isEmpty()) {
				packageLevels.remove(packageName);
				if (packageLevels.isEmpty()) {
					packageLevels = Collections.emptyMap();
				}
			}
		} else {
			if (packageLevels.isEmpty()) {
				packageLevels = new HashMap<String, LoggingLevel>();
			}
			packageLevels.put(packageName, level);
		}
		return this;
	}

	/**
	 * Reset all package depending logging levels (to use the default logging level again).
	 * 
	 * @return The current configurator
	 */
	public Configurator resetAllLevelsForPackages() {
		packageLevels = Collections.emptyMap();
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
		return writingThread(enable, DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD, DEFAULT_PRIORITY_FOR_WRITING_THREAD);
	}

	/**
	 * The writing thread can writes log entries asynchronously. This thread will automatically shutdown, if the main
	 * thread is dead.
	 * 
	 * @param enable
	 *            <code>true</code> to enable the writing thread, <code>false</code> to disable it
	 * @param priority
	 *            Priority of the writing thread (must be between {@link Thread#MIN_PRIORITY} and
	 *            {@link Thread#MAX_PRIORITY})
	 * @return The current configurator
	 */
	public Configurator writingThread(final boolean enable, final int priority) {
		return writingThread(enable, DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD, priority);
	}

	/**
	 * The writing thread can writes log entries asynchronously. This thread will automatically shutdown, if the
	 * observed thread is dead.
	 * 
	 * @param enable
	 *            <code>true</code> to enable the writing thread, <code>false</code> to disable it
	 * @param threadToObserve
	 *            Name of the tread to observe (e.g. "main" for the main thread) or <code>null</code> to disable
	 *            automatic shutdown
	 * @return The current configurator
	 */
	public Configurator writingThread(final boolean enable, final String threadToObserve) {
		return writingThread(enable, threadToObserve, DEFAULT_PRIORITY_FOR_WRITING_THREAD);
	}

	/**
	 * The writing thread can writes log entries asynchronously. This thread will automatically shutdown, if the
	 * observed thread is dead.
	 * 
	 * @param enable
	 *            <code>true</code> to enable the writing thread, <code>false</code> to disable it
	 * @param threadToObserve
	 *            Name of the tread to observe (e.g. "main" for the main thread) or <code>null</code> to disable
	 *            automatic shutdown
	 * @param priority
	 *            Priority of the writing thread (must be between {@link Thread#MIN_PRIORITY} and
	 *            {@link Thread#MAX_PRIORITY})
	 * @return The current configurator
	 */
	public Configurator writingThread(final boolean enable, final String threadToObserve, final int priority) {
		if (enable) {
			this.writingThreadData = new WritingThreadData(threadToObserve, priority);
		} else {
			this.writingThreadData = null;
		}
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
	 */
	public void activate() {
		synchronized (lock) {
			if (activeWritingThread != null && writingThreadData != null && writingThreadData.covers(activeWritingThread)) {
				activeWritingThread.shutdown();
				activeWritingThread = null;
			}

			Configuration configuration = create();
			Logger.setConfirguration(configuration);

			if (activeWritingThread == null && writingThreadData != null) {
				activeWritingThread = configuration.getWritingThread();
				activeWritingThread.start();
			}
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
					System.err.println("Error: cannot find '" + file + "'");
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
				ex.printStackTrace(System.err);
			}
		}

		if (stream != null && "true".equalsIgnoreCase(properties.getProperty("tinylog.configuration.observe"))) {
			Configurator configurator = PropertiesLoader.readProperties(System.getProperties());
			if (isResource) {
				ConfigurationObserver.createResourceConfigurationObserver(configurator, file).start();
			} else {
				ConfigurationObserver.createFileConfigurationObserver(configurator, file).start();
			}
			configurator = configurator.copy();
			PropertiesLoader.readProperties(configurator, properties);
			return configurator;
		} else {
			properties.putAll(System.getProperties());
			return PropertiesLoader.readProperties(properties);
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
		return new Configurator(level, packageLevels, formatPattern, locale, writer, writingThreadDataCopy, maxStackTraceElements);
	}

	/**
	 * Create the configuration.
	 * 
	 * @return The created configuration
	 */
	Configuration create() {
		WritingThread writingThread = writingThreadData == null ? null : new WritingThread(writingThreadData.threadToObserve, writingThreadData.priority);
		return new Configuration(level, packageLevels, formatPattern, locale, writer, writingThread, maxStackTraceElements);
	}

	/**
	 * Data for {@link WritingThread}.
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

		private boolean covers(final WritingThread writingThread) {
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
