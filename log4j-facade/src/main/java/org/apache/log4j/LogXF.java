/*
 * Copyright 2015 Martin Winandy
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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Base class for Apache Log4j 1.x compatible parameterized logging classes.
 *
 * @see org.apache.log4j.LogMF
 * @see org.apache.log4j.LogSF
 */
public abstract class LogXF {

	protected LogXF() {
	}

	/**
	 * Create an entering log entry.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param sourceClass
	 *            Name of the class
	 * @param sourceMethod
	 *            Name of the method
	 */
	public static void entering(final Logger logger, final String sourceClass, final String sourceMethod) {
		TinylogBridge.log(Level.DEBUG, sourceClass + "." + sourceMethod + " ENTRY");
	}

	/**
	 * Create an entering log entry.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param sourceClass
	 *            Name of the class
	 * @param sourceMethod
	 *            Name of the method
	 * @param param
	 *            Parameter value
	 */
	public static void entering(final Logger logger, final String sourceClass, final String sourceMethod, final String param) {
		TinylogBridge.log(Level.DEBUG, sourceClass + "." + sourceMethod + " ENTRY " + param);
	}

	/**
	 * Create an entering log entry.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param sourceClass
	 *            Name of the class
	 * @param sourceMethod
	 *            Name of the method
	 * @param param
	 *            Parameter value
	 */
	public static void entering(final Logger logger, final String sourceClass, final String sourceMethod, final Object param) {
		TinylogBridge.log(Level.DEBUG, sourceClass + "." + sourceMethod + " ENTRY " + toString(param));
	}

	/**
	 * Create an entering log entry.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param sourceClass
	 *            Name of the class
	 * @param sourceMethod
	 *            Name of the method
	 * @param params
	 *            Parameter values
	 */
	public static void entering(final Logger logger, final String sourceClass, final String sourceMethod, final Object[] params) {
		TinylogBridge.log(Level.DEBUG, sourceClass + "." + sourceMethod + " ENTRY " + toString(params));
	}

	/**
	 * Create an exiting log entry.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param sourceClass
	 *            Name of the class
	 * @param sourceMethod
	 *            Name of the method
	 */
	public static void exiting(final Logger logger, final String sourceClass, final String sourceMethod) {
		TinylogBridge.log(Level.DEBUG, sourceClass + "." + sourceMethod + " RETURN");
	}

	/**
	 * Create an exiting log entry.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param sourceClass
	 *            Name of the class
	 * @param sourceMethod
	 *            Name of the method
	 * @param param
	 *            Return value
	 */
	public static void exiting(final Logger logger, final String sourceClass, final String sourceMethod, final String param) {
		TinylogBridge.log(Level.DEBUG, sourceClass + "." + sourceMethod + " RETURN " + param);
	}

	/**
	 * Create an exiting log entry.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param sourceClass
	 *            Name of the class
	 * @param sourceMethod
	 *            Name of the method
	 * @param param
	 *            Return value
	 */
	public static void exiting(final Logger logger, final String sourceClass, final String sourceMethod, final Object param) {
		TinylogBridge.log(Level.DEBUG, sourceClass + "." + sourceMethod + " RETURN " + toString(param));
	}

	/**
	 * Create a throwing log entry.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param sourceClass
	 *            Name of the class
	 * @param sourceMethod
	 *            Name of the method
	 * @param throwable
	 *            Throwable to log
	 */
	public static void throwing(final Logger logger, final String sourceClass, final String sourceMethod, final Throwable throwable) {
		TinylogBridge.log(Level.DEBUG, throwable, sourceClass + "." + sourceMethod + " THROW");
	}
	
	protected static String getResourceBundleString(String bundleName, String key) {
		if (bundleName == null) {
			return key;
		} else {
			try {
				return ResourceBundle.getBundle(bundleName).getString(key);
			} catch (MissingResourceException ex) {
				return key;
			}
		}
	}
	
	private static String toString(final Object[] array) {
		if (array == null) {
			return "null";
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append('{');
			for (int i = 0; i < array.length; ++i) {
				if (i > 0) {
					builder.append(',');
			}
				builder.append(toString(array[i]));
			}
			builder.append('}');
			return builder.toString();
		}
	}

	private static String toString(final Object obj) {
		if (obj == null) {
			return "null";
		} else {
			try {
				return obj.toString();
			} catch (Throwable ex) {
				return "?";
			}
		}
	}

}
