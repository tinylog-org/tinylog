/*
 * Copyright 2013 Martin Winandy
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

import java.util.HashMap;
import java.util.Map;

/**
 * API to create and find loggers.
 */
public final class LogManager {

	private static final Logger root = new Logger(null, "root");
	private static final Map<String, Logger> loggers = new HashMap<>();
	private static final Object mutex = new Object();

	static {
		loggers.put(root.getName(), root);
	}

	private LogManager() {
	}

	/**
	 * Get the root logger.
	 *
	 * @return Root logger
	 */
	public static Logger getRootLogger() {
		return root;
	}

	/**
	 * Get or create a logger.
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
	 * Get or create a logger.
	 *
	 * @param clazz
	 *            Class to log
	 * @return Logger instance
	 */
	@SuppressWarnings("rawtypes")
	public static Logger getLogger(final Class clazz) {
		return getLogger(clazz.getName());
	}

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

	private static String reduce(final String name) {
		int index = name.lastIndexOf('.');
		return index == -1 ? null : name.substring(0, index);
	}

}
