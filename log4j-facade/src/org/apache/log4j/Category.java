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

package org.apache.log4j;

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.LogEntryForwarder;

public abstract class Category {

	private static final Logger INSTANCE = Logger.getRootLogger();

	protected Category() {
	}

	@Deprecated
	final public static Category getRoot() {
		return INSTANCE;
	}

	@Deprecated
	public static Category getInstance(final Class<?> clazz) {
		return INSTANCE;
	}

	@Deprecated
	public static Category getInstance(final String name) {
		return INSTANCE;
	}

	@Deprecated
	public static Logger exists(final String name) {
		return INSTANCE;
	}

	public Level getLevel() {
		switch (org.pmw.tinylog.Logger.getLoggingLevel()) {
			case ERROR:
				return Level.ERROR;
			case WARNING:
				return Level.WARN;
			case INFO:
				return Level.INFO;
			case DEBUG:
				return Level.DEBUG;
			case TRACE:
				return Level.TRACE;
			case OFF:
				return Level.OFF;
			default:
				return null;
		}
	}

	public Level getEffectiveLevel() {
		return getLevel();
	}

	public void setLevel(final Level level) {
		// Ignore
	}

	@Deprecated
	public Level getPriority() {
		return getLevel();
	}

	public Priority getChainedPriority() {
		return getLevel();
	}

	@Deprecated
	public void setPriority(final Priority priority) {
		// Ignore
	}

	public void log(final Priority priority, final Object message) {
		if (message != null) {
			if (priority.isGreaterOrEqual(Level.ERROR)) {
				org.pmw.tinylog.Logger.error(message.toString());
			} else if (priority.isGreaterOrEqual(Level.WARN)) {
				org.pmw.tinylog.Logger.warn(message.toString());
			} else if (priority.isGreaterOrEqual(Level.INFO)) {
				org.pmw.tinylog.Logger.info(message.toString());
			} else if (priority.isGreaterOrEqual(Level.DEBUG)) {
				org.pmw.tinylog.Logger.debug(message.toString());
			} else if (priority.isGreaterOrEqual(Level.TRACE)) {
				org.pmw.tinylog.Logger.trace(message.toString());
			}
		}
	}

	public void log(final Priority priority, final Object message, final Throwable ex) {
		if (message != null) {
			if (priority.isGreaterOrEqual(Level.ERROR)) {
				org.pmw.tinylog.Logger.error(message.toString(), ex);
			} else if (priority.isGreaterOrEqual(Level.WARN)) {
				org.pmw.tinylog.Logger.warn(message.toString(), ex);
			} else if (priority.isGreaterOrEqual(Level.INFO)) {
				org.pmw.tinylog.Logger.info(message.toString(), ex);
			} else if (priority.isGreaterOrEqual(Level.DEBUG)) {
				org.pmw.tinylog.Logger.debug(message.toString(), ex);
			} else if (priority.isGreaterOrEqual(Level.TRACE)) {
				org.pmw.tinylog.Logger.trace(message.toString(), ex);
			}
		}
	}

	public void log(final String callerFQCN, final Priority level, final Object message, final Throwable ex) {
		log(level, message, ex);
	}

	public void error(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.ERROR, message.toString());
		}
	}

	public void error(final Object message, final Throwable ex) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.ERROR, message.toString(), ex);
		}
	}

	public void fatal(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.ERROR, message.toString());
		}
	}

	public void fatal(final Object message, final Throwable ex) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.ERROR, message.toString(), ex);
		}
	}

	public void warn(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.WARNING, message.toString());
		}
	}

	public void warn(final Object message, final Throwable ex) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.WARNING, message.toString(), ex);
		}
	}

	public void info(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.INFO, message.toString());
		}
	}

	public void info(final Object message, final Throwable ex) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.INFO, message.toString(), ex);
		}
	}

	public void debug(final Object message) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.DEBUG, message.toString());
		}
	}

	public void debug(final Object message, final Throwable ex) {
		if (message != null) {
			LogEntryForwarder.forward(LoggingLevel.DEBUG, message.toString(), ex);
		}
	}

	public boolean isInfoEnabled() {
		return org.pmw.tinylog.Logger.getLoggingLevel().ordinal() <= LoggingLevel.INFO.ordinal();
	}

	public boolean isDebugEnabled() {
		return org.pmw.tinylog.Logger.getLoggingLevel().ordinal() <= LoggingLevel.DEBUG.ordinal();
	}

	public boolean isEnabledFor(final Priority level) {
		return level.isGreaterOrEqual(getLevel());
	}

}
