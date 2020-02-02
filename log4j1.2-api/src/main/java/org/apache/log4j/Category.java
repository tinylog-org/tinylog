/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.apache.log4j.spi.LoggingEvent;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * <b>This class has been deprecated and replaced by the {@link Logger} <em>subclass</em></b>. It will be kept around to
 * preserve backward compatibility until mid 2003.
 * 
 * <p>
 * {@code Logger} is a subclass of Category, i.e. it extends Category. In other words, a logger <em>is</em> a category.
 * Thus, all operations that can be performed on a category can be performed on a logger. Internally, whenever log4j is
 * asked to produce a Category object, it will instead produce a Logger object. Log4j 1.2 will <em>never</em> produce
 * Category objects but only {@code Logger} instances. In order to preserve backward compatibility, methods that
 * previously accepted category objects still continue to accept category objects.
 * </p>
 * 
 * <p>
 * For example, the following are all legal and will work as expected.
 * </p>
 * 
 * <pre>
 * &nbsp;&nbsp;&nbsp;// Deprecated form:
 * &nbsp;&nbsp;&nbsp;Category cat = Category.getInstance("foo.bar")
 * 
 * &nbsp;&nbsp;&nbsp;// Preferred form for retrieving loggers:
 * &nbsp;&nbsp;&nbsp;Logger logger = Logger.getLogger("foo.bar")
 * </pre>
 * 
 * <p>
 * The first form is deprecated and should be avoided.
 * </p>
 * 
 * <p>
 * <b>There is absolutely no need for new client code to use or refer to the {@code Category} class.</b> Whenever
 * possible, please avoid referring to it or using it.
 * </p>
 * 
 * <p>
 * See the document entitled <a href="http://www.qos.ch/logging/preparingFor13.html">preparing for log4j 1.3</a> for a
 * more detailed discussion.
 * </p>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Anders Kristensen
 */
public class Category {

	protected static final int STACKTRACE_DEPTH = 2;

	protected static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	// @formatter:off
	private static final boolean MINIMUM_LEVEL_COVERS_DEBUG = isCoveredByMinimumLevel(org.tinylog.Level.DEBUG);
	private static final boolean MINIMUM_LEVEL_COVERS_INFO  = isCoveredByMinimumLevel(org.tinylog.Level.INFO);
	private static final boolean MINIMUM_LEVEL_COVERS_WARN  = isCoveredByMinimumLevel(org.tinylog.Level.WARN);
	private static final boolean MINIMUM_LEVEL_COVERS_ERROR = isCoveredByMinimumLevel(org.tinylog.Level.ERROR);
	// @formatter:on

	private final Category parent;
	private final String name;

	private volatile ResourceBundle bundle;

	/**
	 * @param parent
	 *            Parent category ({@code null} for the root category)
	 * @param name
	 *            Name for the category (typically the name of the class that will use this category)
	 */
	Category(final Category parent, final String name) {
		this.parent = parent;
		this.name = name;
	}

	/**
	 * @param name
	 *            Name for the category (typically the name of the class that will use this category)
	 */
	protected Category(final String name) {
		this(LogManager.getParentLogger(name), name);
	}

	/**
	 * Add {@code newAppender} to the list of appenders of this Category instance.
	 * 
	 * <p>
	 * If {@code newAppender} is already in the list of appenders, then it won't be added again.
	 * </p>
	 * 
	 * @param newAppender
	 *            Appender to register
	 */
	public void addAppender(final Appender newAppender) {
		// Ignore
	}

	/**
	 * If {@code assertion} parameter is {@code false}, then logs {@code msg} as an {@link #error(Object) error}
	 * statement.
	 * 
	 * <p>
	 * The {@code assert} method has been renamed to {@code assertLog} because {@code assert} is a language reserved
	 * word in JDK 1.4.
	 * </p>
	 * 
	 * @param assertion
	 *            {@code true} for dropping the passed message, {@code false} for outputting
	 * @param msg
	 *            The message to print if {@code assertion} is false.
	 * 
	 * @since 1.2
	 */
	public void assertLog(final boolean assertion, final String msg) {
		if (!assertion && MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, null, msg, (Object[]) null);
		}
	}

	/**
	 * Call the appenders in the hierarchy starting at {@code this}. If no appenders could be found, emit a warning.
	 * 
	 * <p>
	 * This method calls all the appenders inherited from the hierarchy circumventing any evaluation of whether to log
	 * or not to log the particular log request.
	 * </p>
	 * 
	 * @param event
	 *            the event to log.
	 */
	public void callAppenders(final LoggingEvent event) {
		// Ignore
	}

	/**
	 * Log a message object with the {@link Level#DEBUG DEBUG} level.
	 * 
	 * <p>
	 * This method first checks if this category is {@code DEBUG} enabled by comparing the level of this category with
	 * the {@link Level#DEBUG DEBUG} level. If this category is {@code DEBUG} enabled, then it converts the message
	 * object (passed as parameter) to a string. It then proceeds to call all the registered appenders in this category
	 * and also higher in the hierarchy depending on the value of the additivity flag.
	 * </p>
	 * 
	 * <p>
	 * <b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the {@code Throwable}
	 * but no stack trace. To print a stack trace use the {@link #debug(Object, Throwable)} form instead.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log.
	 */
	public void debug(final Object message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, null, message, (Object[]) null);
		}
	}

	/**
	 * Log a message object with the {@code DEBUG} level including the stack trace of the {@link Throwable} {@code t}
	 * passed as parameter.
	 * 
	 * <p>
	 * See {@link #debug(Object)} form for more detailed information.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void debug(final Object message, final Throwable t) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, t, null, message == t ? null : message, (Object[]) null);
		}
	}

	/**
	 * Log a message object with the {@link Level#ERROR ERROR} Level.
	 * 
	 * <p>
	 * This method first checks if this category is {@code ERROR} enabled by comparing the level of this category with
	 * {@link Level#ERROR ERROR} Level. If this category is {@code ERROR} enabled, then it converts the message object
	 * passed as parameter to a string. It proceeds to call all the registered appenders in this category and also
	 * higher in the hierarchy depending on the value of the additivity flag.
	 * </p>
	 * 
	 * <p>
	 * <b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the {@code Throwable}
	 * but no stack trace. To print a stack trace use the {@link #error(Object, Throwable)} form instead.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log
	 */
	public void error(final Object message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, null, message, (Object[]) null);
		}
	}

	/**
	 * Log a message object with the {@code ERROR} level including the stack trace of the {@link Throwable} {@code t}
	 * passed as parameter.
	 * 
	 * <p>
	 * See {@link #error(Object)} form for more detailed information.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void error(final Object message, final Throwable t) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, t, null, message == t ? null : message, (Object[]) null);
		}
	}

	/**
	 * If the named category exists (in the default hierarchy) then it returns a reference to the category, otherwise it
	 * returns {@code null}.
	 * 
	 * @param name
	 *            Name of the category
	 * @return Logger instance or {@code null}
	 * 
	 * @deprecated Please use {@link LogManager#exists} instead.
	 * @since 0.8.5
	 */
	@Deprecated
	public static Logger exists(final String name) {
		return LogManager.exists(name);
	}

	/**
	 * Log a message object with the {@link Level#FATAL FATAL} Level.
	 * 
	 * <p>
	 * This method first checks if this category is {@code FATAL} enabled by comparing the level of this category with
	 * {@link Level#FATAL FATAL} Level. If the category is {@code FATAL} enabled, then it converts the message object
	 * passed as parameter to a string. It proceeds to call all the registered appenders in this category and also
	 * higher in the hierarchy depending on the value of the additivity flag.
	 * </p>
	 * 
	 * <p>
	 * <b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the Throwable but no
	 * stack trace. To print a stack trace use the {@link #fatal(Object, Throwable)} form instead.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log
	 */
	public void fatal(final Object message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, null, message, (Object[]) null);
		}
	}

	/**
	 * Log a message object with the {@code FATAL} level including the stack trace of the {@link Throwable} {@code t}
	 * passed as parameter.
	 * 
	 * <p>
	 * See {@link #fatal(Object)} for more detailed information.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void fatal(final Object message, final Throwable t) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, t, null, message == t ? null : message, (Object[]) null);
		}
	}

	/**
	 * This method creates a new logging event and logs the event without further checks.
	 * 
	 * @param fqcn
	 *            Fully-qualified class name of the category or logger instance
	 * @param level
	 *            Priority to log
	 * @param message
	 *            Message to log
	 * @param t
	 *            Exception to log
	 */
	protected void forcedLog(final String fqcn, final Priority level, final Object message, final Throwable t) {
		provider.log(fqcn, null, translatePriority(level), t, null, message == t ? null : message, (Object[]) null);
	}

	/**
	 * Get the additivity flag for this Category instance.
	 * 
	 * @return Always {@code true}
	 */
	public boolean getAdditivity() {
		return true;
	}

	/**
	 * Get the appenders contained in this category as an {@link Enumeration}. If no appenders can be found, then a
	 * empty enumeration is returned.
	 * 
	 * @return Enumeration An enumeration of the appenders in this category.
	 */
	@SuppressWarnings("rawtypes")
	public Enumeration getAllAppenders() {
		return Collections.emptyEnumeration();
	}

	/**
	 * Look for the appender named as {@code name}.
	 * 
	 * @param name
	 *            Name of the appender
	 * 
	 * @return Return the appender with that name if in the list. Return {@code null} otherwise.
	 */
	public Appender getAppender(final String name) {
		return null;
	}

	/**
	 * Starting from this category, search the category hierarchy for a non-null level and return it. Otherwise, return
	 * the level of the root category.
	 * 
	 * <p>
	 * The Category class is designed so that this method executes as quickly as possible.
	 * </p>
	 * 
	 * @return Minimum enabled severity level
	 */
	public Level getEffectiveLevel() {
		return translateLevel(provider.getMinimumLevel(null));
	}

	/**
	 * @deprecated Please use the the {@link #getEffectiveLevel} method instead.
	 * 
	 * @return Minimum enabled severity level
	 */
	@Deprecated
	public Priority getChainedPriority() {
		return translateLevel(provider.getMinimumLevel(null));
	}

	/**
	 * Returns all the currently defined categories in the default hierarchy as an {@link java.util.Enumeration
	 * Enumeration}.
	 * 
	 * <p>
	 * The root category is <em>not</em> included in the returned {@link Enumeration}.
	 * </p>
	 * 
	 * @return Enumeration with all existing loggers
	 * 
	 * @deprecated Please use {@link LogManager#getCurrentLoggers()} instead.
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static Enumeration getCurrentCategories() {
		return LogManager.getCurrentLoggers();
	}

	/**
	 * @deprecated Make sure to use {@link Logger#getLogger(String)} instead.
	 * 
	 * @param name
	 *            Name of the category
	 * 
	 * @return Logger instance
	 */
	@Deprecated
	public static Category getInstance(final String name) {
		return LogManager.getLogger(name);
	}

	/**
	 * @deprecated Please make sure to use {@link Logger#getLogger(Class)} instead.
	 * 
	 * @param clazz
	 *            Class to log
	 * 
	 * @return Logger instance
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static Category getInstance(final Class clazz) {
		return LogManager.getLogger(clazz);
	}

	/**
	 * Retrieves the category name.
	 * 
	 * @return Name of this category
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the parent of this category. Note that the parent of a given category may change during the lifetime of
	 * the category.
	 * 
	 * <p>
	 * The root category will return {@code null}.
	 * </p>
	 * 
	 * @return Parent logger instance
	 * 
	 * @since 1.2
	 */
	public final Category getParent() {
		return parent;
	}

	/**
	 * Returns the assigned {@link Level}, if any, for this Category.
	 * 
	 * @return Minimum enabled severity level
	 */
	public final Level getLevel() {
		return translateLevel(provider.getMinimumLevel(null));
	}

	/**
	 * @deprecated Please use {@link #getLevel} instead.
	 * 
	 * @return Minimum enabled severity level
	 */
	@Deprecated
	public final Level getPriority() {
		return translateLevel(provider.getMinimumLevel(null));
	}

	/**
	 * @deprecated Please use {@link Logger#getRootLogger()} instead.
	 * 
	 * @return Root logger instance
	 */
	@Deprecated
	public static final Category getRoot() {
		return LogManager.getRootLogger();
	}

	/**
	 * Return the <em>inherited</em> {@link ResourceBundle} for this category.
	 * 
	 * <p>
	 * This method walks the hierarchy to find the appropriate resource bundle. It will return the resource bundle
	 * attached to the closest ancestor of this category, much like the way priorities are searched. In case there is no
	 * bundle in the hierarchy then {@code null} is returned.
	 * </p>
	 * 
	 * @return Current resource bundle
	 * 
	 * @since 0.9.0
	 */
	public ResourceBundle getResourceBundle() {
		return bundle;
	}

	/**
	 * Log a message object with the {@link Level#INFO INFO} Level.
	 * 
	 * <p>
	 * This method first checks if this category is {@code INFO} enabled by comparing the level of this category with
	 * {@link Level#INFO INFO} Level. If the category is {@code INFO} enabled, then it converts the message object
	 * passed as parameter to a string. It proceeds to call all the registered appenders in this category and also
	 * higher in the hierarchy depending on the value of the additivity flag.
	 * </p>
	 * 
	 * <p>
	 * <b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the Throwable but no
	 * stack trace. To print a stack trace use the {@link #info(Object, Throwable)} form instead.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log
	 */
	public void info(final Object message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, null, message, (Object[]) null);
		}
	}

	/**
	 * Log a message object with the {@code INFO} level including the stack trace of the {@link Throwable} {@code t}
	 * passed as parameter.
	 * 
	 * <p>
	 * See {@link #info(Object)} for more detailed information.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void info(final Object message, final Throwable t) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, t, null, message == t ? null : message, (Object[]) null);
		}
	}

	/**
	 * Is the appender passed as parameter attached to this category?
	 * 
	 * @param appender
	 *            Appender to check
	 * 
	 * @return Always {@code false}
	 */
	public boolean isAttached(final Appender appender) {
		return false;
	}

	/**
	 * Check whether this category is enabled for the {@code DEBUG} Level.
	 *
	 * <p>
	 * This function is intended to lessen the computational cost of disabled log debug statements.
	 * </p>
	 *
	 * <p>
	 * For some {@code cat} Category object, when you write,
	 * </p>
	 * 
	 * <pre>
	 * cat.debug("This is entry number: " + i);
	 * </pre>
	 *
	 * <p>
	 * You incur the cost constructing the message, concatenatiion in this case, regardless of whether the message is
	 * logged or not.
	 * </p>
	 *
	 * <p>
	 * If you are worried about speed, then you should write
	 * </p>
	 * 
	 * <pre>
	 * if (cat.isDebugEnabled()) {
	 * 	cat.debug("This is entry number: " + i);
	 * }
	 * </pre>
	 *
	 * <p>
	 * This way you will not incur the cost of parameter construction if debugging is disabled for {@code cat}. On the
	 * other hand, if the {@code cat} is debug enabled, you will incur the cost of evaluating whether the category is
	 * debug enabled twice. Once in {@code isDebugEnabled} and once in the {@code debug}. This is an insignificant
	 * overhead since evaluating a category takes about 1%% of the time it takes to actually log.
	 * </p>
	 *
	 * @return boolean - {@code true} if this category is debug enabled, {@code false} otherwise.
	 */
	public boolean isDebugEnabled() {
		return MINIMUM_LEVEL_COVERS_DEBUG && provider.isEnabled(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG);
	}

	/**
	 * Check whether this category is enabled for a given {@link Level} passed as parameter.
	 * 
	 * <p>
	 * See also {@link #isDebugEnabled}.
	 * </p>
	 * 
	 * @param level
	 *            Priority to check whether enabled
	 * @return boolean True if this category is enabled for {@code level}.
	 */
	public boolean isEnabledFor(final Priority level) {
		return provider.isEnabled(STACKTRACE_DEPTH, null, translatePriority(level));
	}

	/**
	 * Check whether this category is enabled for the info Level. See also {@link #isDebugEnabled}.
	 * 
	 * @return boolean - {@code true} if this category is enabled for level info, {@code false} otherwise.
	 */
	public boolean isInfoEnabled() {
		return MINIMUM_LEVEL_COVERS_INFO && provider.isEnabled(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO);
	}

	/**
	 * Log a localized message. The user supplied parameter {@code key} is replaced by its localized version from the
	 * resource bundle.
	 *
	 * @param priority
	 *            Priority for log entry
	 * @param key
	 *            Resource key for translation
	 * @param t
	 *            Exception to log
	 * 
	 * @see #setResourceBundle
	 * 
	 * @since 0.8.4
	 */
	public void l7dlog(final Priority priority, final String key, final Throwable t) {
		ResourceBundle bundle = this.bundle;
		String message = bundle == null ? key : bundle.getString(key);

		provider.log(STACKTRACE_DEPTH, null, translatePriority(priority), t, null, message, (Object[]) null);
	}

	/**
	 * Log a localized and parameterized message. First, the user supplied {@code key} is searched in the resource
	 * bundle. Next, the resulting pattern is formatted using {@link java.text.MessageFormat#format(String,Object[])}
	 * method with the user supplied object array {@code params}.
	 * 
	 * @param priority
	 *            Priority for log entry
	 * @param key
	 *            Resource key for translation
	 * @param params
	 *            Arguments for message
	 * @param t
	 *            Exception to log
	 * 
	 * @since 0.8.4
	 */
	public void l7dlog(final Priority priority, final String key, final Object[] params, final Throwable t) {
		ResourceBundle bundle = this.bundle;
		String message = bundle == null ? key : MessageFormat.format(bundle.getString(key), params);

		provider.log(STACKTRACE_DEPTH, null, translatePriority(priority), t, null, message, (Object[]) null);
	}

	/**
	 * This generic form is intended to be used by wrappers.
	 * 
	 * @param priority
	 *            Priority for log entry
	 * @param message
	 *            Message to log
	 * @param t
	 *            Exception to log
	 * 
	 */
	public void log(final Priority priority, final Object message, final Throwable t) {
		provider.log(STACKTRACE_DEPTH, null, translatePriority(priority), t, null, message == t ? null : message, (Object[]) null);
	}

	/**
	 * This generic form is intended to be used by wrappers.
	 * 
	 * @param priority
	 *            Priority for log entry
	 * @param message
	 *            Message to log
	 */
	public void log(final Priority priority, final Object message) {
		provider.log(STACKTRACE_DEPTH, null, translatePriority(priority), null, null, message, (Object[]) null);
	}

	/**
	 * This is the most generic printing method. It is intended to be invoked by <b>wrapper</b> classes.
	 * 
	 * @param callerFQCN
	 *            The wrapper class' fully qualified class name.
	 * @param level
	 *            The level of the logging request.
	 * @param message
	 *            The message of the logging request.
	 * @param t
	 *            The throwable of the logging request, may be null.
	 */
	public void log(final String callerFQCN, final Priority level, final Object message, final Throwable t) {
		provider.log(callerFQCN, null, translatePriority(level), t, null, message == t ? null : message, (Object[]) null);
	}

	/**
	 * Remove all previously added appenders from this Category instance.
	 * 
	 * <p>
	 * This is useful when re-reading configuration information.
	 * </p>
	 */
	public void removeAllAppenders() {
		// Ignore
	}

	/**
	 * Remove the appender passed as parameter form the list of appenders.
	 * 
	 * @param appender
	 *            Appender to remove
	 * 
	 * @since 0.8.2
	 */
	public void removeAppender(final Appender appender) {
		// Ignore
	}

	/**
	 * Remove the appender with the name passed as parameter form the list of appenders.
	 * 
	 * @param name
	 *            Name of appender to remove
	 * 
	 * @since 0.8.2
	 */
	public void removeAppender(final String name) {
		// Ignore
	}

	/**
	 * Set the additivity flag for this Category instance.
	 * 
	 * @param additive
	 *            Additivity flag (will be ignored)
	 * 
	 * @since 0.8.1
	 */
	public void setAdditivity(final boolean additive) {
		// Ignore
	}

	/**
	 * Set the level of this Category. If you are passing any of {@code Level.DEBUG}, {@code Level.INFO},
	 * {@code Level.WARN}, {@code Level.ERROR}, {@code Level.FATAL} as a parameter, you need to case them as Level.
	 * 
	 * <p>
	 * As in
	 * </p>
	 * 
	 * <pre>
	 * logger.setLevel((Level) Level.DEBUG);
	 * </pre>
	 * 
	 * <p>
	 * Null values are admitted.
	 * </p>
	 * 
	 * @param level
	 *            New level (will be ignored)
	 */
	public void setLevel(final Level level) {
		// Ignore
	}

	/**
	 * Set the level of this Category.
	 * 
	 * <p>
	 * Null values are admitted.
	 * </p>
	 * 
	 * @param priority
	 *            New priority (will be ignored)
	 * 
	 * @deprecated Please use {@link #setLevel} instead.
	 */
	@Deprecated
	public void setPriority(final Priority priority) {
		// Ignore
	}

	/**
	 * Set the resource bundle to be used with localized logging methods {@link #l7dlog(Priority,String,Throwable)} and
	 * {@link #l7dlog(Priority,String,Object[],Throwable)}.
	 * 
	 * @param bundle
	 *            New resource bundle for translations
	 * 
	 * @since 0.8.4
	 */
	public void setResourceBundle(final ResourceBundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * Calling this method will <em>safely</em> close and remove all appenders in all the categories including root
	 * contained in the default hierarchy.
	 * 
	 * <p>
	 * The {@code shutdown} method is careful to close nested appenders before closing regular appenders. This is allows
	 * configurations where a regular appender is attached to a category and again to a nested appender.
	 * </p>
	 * 
	 * @deprecated Please use {@link LogManager#shutdown()} instead.
	 * 
	 * @since 1.0
	 */
	@Deprecated
	public static void shutdown() {
		LogManager.shutdown();
	}

	/**
	 * Log a message object with the {@link Level#WARN WARN} Level.
	 * 
	 * <p>
	 * This method first checks if this category is {@code WARN} enabled by comparing the level of this category with
	 * {@link Level#WARN WARN} Level. If the category is {@code WARN} enabled, then it converts the message object
	 * passed as parameter to a string. It proceeds to call all the registered appenders in this category and also
	 * higher in the hierarchy depending on the value of the additivity flag.
	 * </p>
	 * 
	 * <p>
	 * <b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the Throwable but no
	 * stack trace. To print a stack trace use the {@link #warn(Object, Throwable)} form instead.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log.
	 */
	public void warn(final Object message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, null, message, (Object[]) null);
		}
	}

	/**
	 * Log a message with the {@code WARN} level including the stack trace of the {@link Throwable} {@code t} passed as
	 * parameter.
	 * 
	 * <p>
	 * See {@link #warn(Object)} for more detailed information.
	 * </p>
	 * 
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public void warn(final Object message, final Throwable t) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, t, null, message == t ? null : message, (Object[]) null);
		}
	}

	/**
	 * Checks if a given severity level is covered by the logging provider's minimum level.
	 *
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 */
	protected static boolean isCoveredByMinimumLevel(final org.tinylog.Level level) {
		return provider.getMinimumLevel(null).ordinal() <= level.ordinal();
	}

	/**
	 * Translates an Apache Log4j 1.2 priority into a tinylog 2 severity level.
	 * 
	 * @param priority
	 *            Apache Log4j 1.2 priority
	 * @return Corresponding severity level of tinylog 2
	 */
	private static org.tinylog.Level translatePriority(final Priority priority) {
		if (priority.isGreaterOrEqual(Level.ERROR)) {
			return org.tinylog.Level.ERROR;
		} else if (priority.isGreaterOrEqual(Level.WARN)) {
			return org.tinylog.Level.WARN;
		} else if (priority.isGreaterOrEqual(Level.INFO)) {
			return org.tinylog.Level.INFO;
		} else if (priority.isGreaterOrEqual(Level.DEBUG)) {
			return org.tinylog.Level.DEBUG;
		} else {
			return org.tinylog.Level.TRACE;
		}
	}

	/**
	 * Translates a tinylog 2 severity level into an Apache Log4j 1.2 level.
	 * 
	 * @param level
	 *            Severity level of tinylog 2
	 * @return Corresponding level of Apache Log4j 1.2
	 *
	 * @throws IllegalArgumentException
	 *            Unknown tinylog 2 severity level
	 */
	private static Level translateLevel(final org.tinylog.Level level) {
		switch (level) {
			case TRACE:
				return Level.TRACE;
			case DEBUG:
				return Level.DEBUG;
			case INFO:
				return Level.INFO;
			case WARN:
				return Level.WARN;
			case ERROR:
				return Level.ERROR;
			case OFF:
				return Level.OFF;
			default:
				throw new IllegalArgumentException("Unknown tinylog 2 severity level \"" + level + "\"");
		}
	}

}
