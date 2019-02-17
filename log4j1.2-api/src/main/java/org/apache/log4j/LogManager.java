/*
 * Copyright 2019 Martin Winandy
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

package org.apache.log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.spi.LoggerFactory;

/*
 * Allow public utility class constructor for compatibility reasons.
 */
//@checkstyle off: HideUtilityClassConstructor

/**
 * Manager for reusable logger instances.
 */
public class LogManager {

	private static final Logger root = new Logger(null, "root");
	private static final Map<String, Logger> loggers = new HashMap<String, Logger>();
	private static final Object mutex = new Object();

	static {
		loggers.put(root.getName(), root);
	}

	/** */
	public LogManager() {
	}

	/**
	 * Retrieves the root logger.
	 *
	 * @return Root logger
	 */
	public static Logger getRootLogger() {
		return root;
	}

	/**
	 * Retrieves a logger by name.
	 *
	 * @param name
	 *            Name of the logger
	 * @return Logger instance
	 */
	public static Logger getLogger(final String name) {
		synchronized (mutex) {
			return getOrCreateLogger(name);
		}
	}

	/**
	 * Retrieves a logger by name.
	 *
	 * @param name
	 *            Name of the logger
	 * @param factory
	 *            Logger factory (will be ignored)
	 * @return Logger instance
	 */
	public static Logger getLogger(final String name, final LoggerFactory factory) {
		return getLogger(name);
	}

	/**
	 * Retrieves a logger by class.
	 *
	 * @param clazz
	 *            Class to log
	 * @return Logger instance
	 */
	@SuppressWarnings("rawtypes")
	public static Logger getLogger(final Class clazz) {
		return getLogger(clazz.getName());
	}

	/**
	 * Retrieves an already existing logger by name.
	 *
	 * @param name
	 *            Name of the logger
	 * @return Logger instance or {@code null}
	 */
	public static Logger exists(final String name) {
		synchronized (mutex) {
			return loggers.get(name);
		}
	}

	/**
	 * Retrieves all existing loggers.
	 * 
	 * @return Enumeration with all existing loggers
	 */
	@SuppressWarnings("rawtypes")
	public static Enumeration getCurrentLoggers() {
		ArrayList<Logger> copy;
		synchronized (mutex) {
			copy = new ArrayList<Logger>(loggers.values());
		}
		copy.remove(root);
		return Collections.enumeration(copy);
	}

	/**
	 * Asks to shutdown the logging framework down (will be ignored).
	 */
	public static void shutdown() {
		// Ignore
	}

	/**
	 * Asks to reset the logging configuration (will be ignored).
	 */
	public static void resetConfiguration() {
		// Ignore
	}

	/**
	 * Retrieves the parent logger for the requested name.
	 * 
	 * @param name
	 *            Child logger name
	 * @return Parent logger or {@code null} for the root logger
	 */
	static Logger getParentLogger(final String name) {
		return getLogger(reduce(name));
	}

	/**
	 * Retrieves a logger by name.
	 *
	 * @param name
	 *            Name of the logger
	 * @return Logger instance
	 */
	private static Logger getOrCreateLogger(final String name) {
		if (name == null || name.length() == 0) {
			return root;
		} else {
			Logger logger = loggers.get(name);
			if (logger == null) {
				Logger parent = getOrCreateLogger(reduce(name));
				logger = new Logger(parent, name);
				loggers.put(name, logger);
			}
			return logger;
		}
	}

	/**
	 * Gets the parent's logger name for a given logger name.
	 * 
	 * @param name
	 *            Logger name
	 * @return Logger name of parent
	 */
	private static String reduce(final String name) {
		int index = name.lastIndexOf('.');
		return index == -1 ? null : name.substring(0, index);
	}

}
